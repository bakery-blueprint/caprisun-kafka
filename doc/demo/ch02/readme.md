# 소프트웨어 설정

## 주키퍼 설치하기

* 카프카는 컨슈머 클러스터에 관한 메타데이터를 저정하기 위해서 주키퍼를 사용한다.

### 주키퍼 앙상블
* 주키퍼는 분산 처리 시스템의 서버들에 관한 메타데이터를 통합 관리하는데 사용
* 주키퍼의 클러스터를 앙상블이라고 한다.
* 한 서버에서 처리한 결과를 클러스터에 동기화 해서 데이터 안전성을 보장
* 한 서버에서 문제가 생기면 대기중인 서버가 해당 서비스를 이어 받아 처리함
* 빠르고 안정적으로 하기 위해 앙상블은 홀수 개의 서버를 멤버로 갖는다.
* 예를들어 3개중 하나가 장애가 난다면 하나는 서비스, 하나는 대기 상태를 제공하여 안정성을 확보한다.

## 토픽의 기본 설정
* 파티션 개수나 메시지 보존 설정과 같은 값들은 토픽별로 설정할 수 있다.

### num.partitions
* 새로운 토픽이 몇 개의 파티션으로 생성되는지 나타낸다.
* 기본값은 1(하나의 파티션)
* 토픽의 크기가 확장되는 방법은 파티션
* 따라서 클러스터 전체에 걸쳐 메시지가 고르게 저장되도록 파티션 개수를 설정하는것이 중요
* 일반적으로 클러스터으 ㅣ브로커 수와 같게 하거나 배수로 토픽의 파티션 개수를 설정한다.

> ### 파티션 개수의 산정 방법
> * 단위 시간당 토픽의 데이터 처리량
> * 파티션의 데이터 처리 최대 목표
> * 하나의 파티션에 데이터 생성 목표
> * 현재보다는 향후를 기준으로 처리량을 추산
> * 브로커마다 파티션 개수와 디스크 용량 및 네트워크 처리 속도를 고려
> * 파티션 개수가 많다고 좋은건 아님, 리더 선정에 많은 시간이 소요

### log.retention.ms
* 카프카가 얼마 동안 메시지를 보존할지 구성 파일에 설정할때 쓰는 시간 단위 설정값
* hours, minutes도 사용가능, 가급적 ms 사용 권장

### log.retention.bytes
* 저장된 메시지들의 전체 크기를 기준으로 만기 처리할 때 사용되는 설정 값

### log.segment.bytes
* 메시지 보존은 세그먼트 파일로 처리
* 이때 사용되는 세그먼트 파일 크기를 설정
* 크기를 너무 작은 값으로 지정하면 빈번하게 파일이 닫히고 새로 생성되서 디스크 쓰기 효율이 줄어든다.
* 반면 토픽 저장률이 낮다면 세그먼트 파일이 빨리 닫히도록 설정해야 하기 때문에 작은 값으로 지정할 수 있따.

### log.segment.ms
* 로그 세그먼트 파일이 닫히는 시간을 제어하는 프로퍼티

### message.max.bytes
* 카프카 브로커에 write 하는 메시지의 크기 제한
* 프로듀서가 전송을 원하는 메시지는 이보다 클 수 있지만 압축되어 전송될 떄는 해당 크기 이하로 줄여야함

# 하드웨어 선택

## 디스크 처리량
* 로그 세그먼트 저장에 사용되는 브로커 디스크 처리량이 가장 큰 영향을 준다.
* 메시지는 로컬 스토리리지에 저장되고 브로커가 이를 확인해야 한다.

## 디스크 용량
* 보존 기간과 연관성이 많다.

## 메모리
* 컨슈머가 읽는 파티션의 메시지는 시스템 메모리의 페이지 캐시에 최적화되어 저장된다.
* 캐시에 사용할 메모리가 클 수록 컨슈머 클라이언트 성능이 좋아진다.

## 네트워크
* 카프카가 처리할 수 있는 통신 트래픽의 최대량 = 네트워크 처리량

## CPU
* 전체적인 브로커 성능에 조금 영향을 준다.
* 다른 하드웨어 중요도 보다는 떨어진다.

# 카프카 클러스터
* 다수의 브로커서버를 하나의 클러스톨 구성하면 많은 장점이 있다.
* 처리량응ㄹ 분산 시킬 수 있다.
* 데이터 유실을 막을 수 있다.
* 사용중인 카프카 시스템을 중단 시키지 않고 유지 보수 작업을 할 수 있다.

## 브로커 개수
* 적합한 크기는 아래와 같은 요소로 결정 된다.
  * 메시지를 보존하는데 필요한 디스크 용량 과 하나의 브로커에 사용하는 스토리지 크기
  * 하나의 브로커 2TB, 클러스터 보존 필요 10TB -> 최소 5개의 브로커가 필요
* 요청을 처리하기 위한 클러스터의 용량
  * 네트워크 인터페이스 처리 능력
  * 트래픽이 일정하지 않을 때 모든 클라이언트의 트래픽을 처리할 수 있는지
  * 디스크 처리량 또는 시스템 메모리 성능

## 브로커 구성
* 하나의 클러스터에 다수의 브로커를 사용할 때 두가지 고려 사항이 있다
  * 모든 브로커의 구성 파일에 있는 zookeeper.connect 매개변수의 설정 값이 같아야 한다. 주키퍼 앙상블과 경로를 지정하기 때문이다.
  * broker.id 매개변수에는 클러스터의 모든 브로커가 고유한 값을 갖도록 지정해야 한다.

## 운영체제 조정하기
* 카프카 브로커의 성능 향상을 위한 몇ㅌ가지 변경사항이 있다.

### 가상 메모리
* 카프카의 작업량을 조절하기 위해 더티 메모리 페이지나 스와핑 공간의 처리 방법을 수정할 수 있다.
* 메모리의 페이지가 디스크로 스와핑 되면 카프카의 성능에 막대한 영향을 끼치기 때문에 스와핑되는 것을 최대한 막아야 한다.
* 또한 캐시를 많이 사용하기 때문에 스와핑 되는것을 막아야 한다.
* 그렇기 때문에 vm.swappiness 매개변수 값을 아주 낮은 값으로 설정하는 것이 좋다.
* 그리고 카프카는 디스크 입출력 성능에도 의존적이다. 그렇기 때문에 더티 페이지 커널 처리 방법을 조절 할 수도 있다.

### 디스크
* 파일 시스템이 성능에 영향을 끼칠 수 있다.
* XFS > EXT4

### 네트워크
* 각 소켓의 송수신 버퍼로 할당되는 기본과 최대 메모리량을 변경하는 것이 첫 번째 조정 사항
* 이것은 대량의 데이터 전송 성능을 크게 향상 시킨다.
* 소켓당 송수신 버퍼의 기본 크기를 조정하는 매개변수는 net.core.wmen_default와 net.core.rmem_default이며, 이것들의 바람직한 값은 131072(128KiB)이다.
* 또한 송수신 버퍼의 최대 크기는 net.core.wme_max와 net.core.rmem_max 매개변수에 지정, 이것들의 바람직한 값은 2097152(2MiB)이다.
* TCP 소켓의 송수신 버퍼 크기(net.ipv4.tcp_wmem, net.ipv4.tcp_rmem)
* TCP 윈도우 크기 조정
* 동시 연결 허용

# 실제 업무 사용시 구려사항
## 가비지 컬렉션 옵션
* MaxGCPauseMillis : 가비지 컬렉션 작업의 중지 시간을 지정
* InitiatingHeapOccupancyPercent : 전체 힙의 비율
* 카프카 브로커는 힙 메모리를 효율적으로 사용하고 가비지 컬렉션의 대상이 되는 객체의 생성도 적으므로 이 두가지 옵션 값을 더 작게 설정할 수 있다.

## 데이터센터 레이아웃
* 클러스터에 복제를 구성하더라도 실제 브로커 서버들의 물리적인 위치를 고려하는 것도 중요하다.

## 주키퍼 공동 사용하기
* 카프카는 주키퍼를 사용해서 브로커, 토픽, 파티션에 관한 메타데이터 정보를 저장한다.
* 컨슈머 그룹의 멤버십 변경사항이나 카프카 클러스터 자체의 변경사하잉 생길 때만 주키퍼에 쓰기가 수행  따라서 단일의 카프카 클러스터에 주키퍼 앙상블을 사용하는것은 바람직하지 않음
* 컨슈머는 오프셋을 커밋하기 위해 주키퍼나 카프카 중 하나를 선택하다록 구성하는데 그때 컨슈머와 주키퍼가 직접 연관된다.
* 각 컨슈머는 자신이 소비하는 모든 파티션에 대해 해당 간격으로 주키퍼에 오프셋을 쓰게 된다.
* 오프세의 커밋은 주키퍼 트래픽을 많이 발생시킬 수 있으므로 잘 고려해야 한다.
* 주키퍼 앙상블이 커밋 트래픽을 제대로 처리할 수 없다면 커밋 간격을 더 길게 해야 한다.
* 최신 카프카 버전에서는 주키퍼를 직접 사용하지 않고 오프셋을 커밋할 수 있다.
* 다른 애플리케이션이 하나의 주키퍼 앙상블을 공유하는 것은 피해야 한다.


