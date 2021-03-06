# 클러스터 멤버십
> ### 주키퍼
> * 표준 파일 시스템의 디렉터리처럼 계층적인 트리 구조로 데이터를 저장하고 사용
> * 데이터를 저장하는 znode를 사용하고 경로를 사용해서 노드의 위치를 식별
> * 노드 관리는 주키퍼를 사용하는 클라이언트에서 한다. (생성, 삭제, 경로 존재 여부, 노드의 데이터 읽기/쓰기, 자식 노드 가져오기 등)
> * 노드에는 상태와 구성 정보 및 위치 정보 등의 데이터만 저장
> * **임시 노드** : 클라이언트가 연결되어 있을때만 존재
> * **영구 노드** : 클라이언트가 삭제되지 않는한 계쏙 보존
> * **Watch** : 상태 모니터링, 클라이언트가 특정 노드의 Watch 를 설정하면 변경시 콜백을 통한 호출이 일어남
* 주키퍼를 사용해 클러스터 멤버인 브로커들의 메타데이터를 유지 관리
* 브로커 등록 -> 주키퍼에 임시노드를 등록함, 노드 이름으로 자신의 id를 등록하고 이미 존재한다면 에러
* 브로커 추가 삭제 -> 클라이언트가 삭제 또는 연결이 끊겼음으로 임시 노드도 삭제, Watch 하고 있던 카프카 컴포넌트들에게 콜백

# 컨트롤러
* 카프카 브로커 중 하나
* 파티션 리더를 선출하는 책임을 가짐
* 시작하는 첫 번째 브로커가 컨트롤러가 됨, 주키퍼 임시노드인 /controller를 생성
* 다른 브로커들도 시작할때 /controller 노드를 생성하려고 하지만 이미 존재하므로 이미 컨트롤러가 있다는 것을 알게 됨
* 모든 브로커들은 /controller 노드에 주키퍼의 Watch 를 생성, 노드 변경 감지 가능
* 특정 브로커가 떠나면 주키퍼 Watch 를 통해 브로커가 리더로 할당 되었던 모든 파티션들에 새로운 리더가 필요한것을 알게 됨
* 모든 파티션들을 점검하고 새로운 리더가 될 브로컬르 결정

# 복제
* 카프카의 데이터는 토픽으로 구성되며, 각 토픽은 여러 파티션에 저장될 수 있다. 또한 각 파티션은 다수의 리플리카를 가질 수 있다.

> ### 리더 리플리카
> * 각 파티션은 리더로 지정된 하나의 피를리카를 갖는다.
> * 일관성을 보장하기 위해 모든 프로듀서와 컨슈머 클라이언트의 요청은 리더를 통해서 처리된다.

> ### 팔로어 리플리카
> * 리더를 제외한 나머지 리클리카를 팔로어라 부른다.
> * 요청을 서비스하지는 않음
> * 리더의 메시지를 복제하여 리더의 것과 동일하게 유지한다.
> * 리더 리플리카가 중단되는 경우에 팔로어 리플리카 중 해당 파티션의 새로운 리더로 선출 된다.

* 팔로어들은 리더 메시지를 최신화 하지 못하는 경우도 있다.
* 가장 최근 메시지를 복제하지 못했다면, 해당 리플리카는 동기화되지 않는 것으로 간주한다.
* 최신 메시지를 계속 요청하는 팔로어 리플리카를 동기화 리플리카 (ISR)라고 한다.

# 요청 처리
* 카프카 브로커가 하는 일은 대부분 파티션리더에게 전송되는 요청을 처리함
* 메시지 큐처럼 동작할 수 있어서 메시지 순서가 보장
* 브로커에 processor 스레드는  클라이언트 요청을 요청 큐에 넣고, 응답 큐로부터 응답을 가져와서 클라이언트에서게 전송함

> ### 쓰기 요청
> 프로듀서가 전송하며 카프카 브로커에게 쓰려는 메시지를 포함한다.
> ### 읽기 요청
> 카프카 브로커로부터 메시지를 읽을 때 컨슈머와 팔로어 리플리카가 전송한다.

* 쓰기 요청과 읽기 요청은 모두 파티션의 리더 리플리카에게 전송되어야 한다. 
* 요청 위치는 클라이언트의 메타데이터 요청을 통해 알아냄
* 클라이언트는 메타데이터 정보를 캐시하고 올바른 브로커에게 쓰기와 읽기 요청을 전송하는 데 사용한다.

## 쓰기 요청
* acks 구성 매개변수에 따라서 리더만 받을지, 전부 동기화 할지, 수신 응답을 기다리지 않을지에 따라 쓰기 성공 조건이 달라짐
* 브로커는 로컬 디스크에 새로 받은 메시지를 쓴다.
* 카프카는 디스크에 데이터 쓰기를 기다리지 않으며, 메시지의 내구성 보장을 위해 복제에 의존한다.

## 읽기 요청
* 클라이언트는 읽기를 원하는 토픽과 파티션 및 오프셋에 있는 메시지들의 읽기 요청을  브로커에게 전송한다.
* 브로커가 반환할 수 있는 데이터 크기를 제한할 수 있다. -> 응답 저장 메모리를 위해
* 카프카는 제로카피 기법을 사용해서 클라이언트에게 메시지를 전송해서 버퍼 관리 부담을 제거하여 성능을 향상 시킨다.
* 클라이언트 반환 데이터 하한 크기도 설정할 수 있음 -> 너무 많은 트래픽 사용을 막기 위해
* 클라이언트는 모든 동기화 리플리카에 쓴 메시지들만 읽을 수 있다.
* 리플리카들에게 아직 복제되지 않은 메시지들은 '불안 전한' 것으로 간주

# 스토리지
* 기본적인 스토리지 단위는 파티션 리플리카.

## 파티션 할당
* 파티션 리프릴카들은 브로커 간에 고르게 분산시킨다.
* 각 파티션의 리플리카는 다른 브로커에 할당.
* 랙 정보를 갖고 있다면 서로 다른 랙에 리플리카 할당 가능
* 파티션에 사용될 디렉토리는 디렉토리에 할당된 파티션 수를 구하고, 할당이 가장 적은 디렉토리에 파티션을 할당한다.
* 파티션 개수로 나누는 거라서 한 디렉토리에 한개의 파티션만 할당 되더라도 용량이 클 수가 있다.

## 파일 관리
* 토픽별 보존 구성
* 보존시간, 데이터 크기에 따라 보존 구성 설정
* 각 파티션을 세그먼트(로그 세그먼트) 로 나누어서 보존
* 파티션에 데이터를 쓸 때 세그먼트의 제한 크기나 보존 기간에 도달하면 해당 파일을 닫고 새로운 세그먼트 파일에 계속 씀
* 메시지를 쓰기 위해 사용중인 세금너트를 액티브 세그먼트라고 함
* 카프카 브로커는 몯느 파티션의 모든 세그먼트에 대해 하나의 열린 파일 핸들을 유지

## 파일 형식
* 세그먼트는 카프카 메시지와 오프셋들이 저장
* 디스크 수록되는 데이터의 형식은 메시지의 형식과 동일, 그렇기 때문에 제로 카페 기법을 사용하요 최적화 가능

## 인덱스
* 컨슈머가 특정 오프셋 부터 읽을 수 있게 해줌
* 카프카는 각 파티션의 인덱슬르 유지 관리
* 인덱스도 세그먼트 파일로 관리
* 메시지가 삭제되면 연관된 인덱스 항목도 삭제

## 압축
* 각 키의 가장 최근 값만 토픽에 저장
* 키와 값을 갖는 메시지를 생성하는 애플리케이션의 토픽에만 적용(키가 있어야 가능)
* 여기서 압축은 compression 이 아닌 compaction 이다.

## 압축 처리 방법
* 로그 세그먼트는 compact 된 메시지만 남아 있는 클린 부분과 아직 compact되지 않은 메시지가 저장된 더티 부분으로 나누어 진다.
* 더티 메시지들을 읽어 들여서 오프셋 MAP 을 생성
* 그리고 로그 compact는 항상 클린 부분에서만 실행되며 생성된 오프셋 map을 기준으로 실행

## 삭제된 메시지
* 삭제하고 싶은 메시지의 키에 value 값을 null로 셋팅
* 그렇게 하면 compact 이후에는 key - null 형태로 존재할 것이며 컨슈머는 해당 메시지를 읽게 된다.
* null 이라면 삭제된 데이터라고 알 수 있고 삭제 해야 하는 데이터인것을 알 수 있다.
* 이러한 메시지를 톰스톤 이라고 한다.

## 토픽은 언제 압축될까?
* 현재 사용중인 세그먼트는 압축하지 않음
* 보통 토픽의 50%가 더티 레코드 일때 압축을 시작