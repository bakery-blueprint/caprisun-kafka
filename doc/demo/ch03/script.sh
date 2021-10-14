# 카프카 다운로드
curl -O https://dlcdn.apache.org/kafka/3.0.0/kafka_2.13-3.0.0.tgz

# 압축 해제
tar -xzf kafka_2.13-3.0.0.tgz

# 카프카 폴더 이동
cd kafka_2.13-3.0.0

# 토픽 생성
bin/kafka-topics.sh --create --replication-factor 1  --partitions 1 --topic quickstart-events --bootstrap-server localhost:9092

# 토픽에 이벤트 쓰기
bin/kafka-console-producer.sh --topic quickstart-events --bootstrap-server localhost:9092

# 토픽에서 이벤트 읽기
bin/kafka-console-consumer.sh --topic quickstart-events --from-beginning --bootstrap-server localhost:9092