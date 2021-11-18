package com.github.bakery.blueprint.caprisunkafka.core.group;

/**
 * @see kafka.coordinator.group.GroupCoordinator
 * 1. 첫 번째 단계에서는 JoinGroup 요청을 GroupCoordinator로 보내 그룹에 참여한다. 이후 리더(leader)로 선정된 컨슈머는 그룹 내 파티션을 할당한다. 모든 컨슈머는 Synchronization barrier를 넘어가기 전에 메시지 처리를 중지하고 오프셋을 커밋해야 한다.
 * 2. 두 번째 단계에서 모든 컨슈머는 SyncGroup 요청을 보낸다. 리더는 SyncGroup 요청을 보낼 때 파티션 할당 결과를 요청에 포함시킨다. GroupCoordinator는 파티션 할당 결과를 SyncGroup의 응답으로 준다. 이후 오프셋 초기화 과정을 끝낸 후 컨슈머는 브로커에서 데이터를 가져올 수 있다.
 */
public class MyGroupCoordinator {
}
