1. docker-compose.yml 먼저 실행
- zookeeper, postgrlsql과 kafka, kafka-ui 실행됨
- docker-compose up -d
- docker ps
- kafka ui 에 접속해서 topic 확인
-  http://localhost:8989/
- postgrlsql 접속으로 DB 확인
- localhost, 5432, database:workflow_db 에서 데이터 확인 SELECT * FROM workflows wf; SELECT * FROM step_histories sh ; 
- 필요 시 docker-compose down 후 docker-compose up -d

3. workflow-acl-service를 실행하면 http://localhost:8081/swagger-ui.html 에 접속 가능함
- 해당 주소를 통해 오더 생성 가능함

4. workflow-service를 실행하면 http://localhost:8080/swagger-ui.html 에 접속 가능함
- kafka ui에 접속해서 각 response queue에 완료 전문 메시지 produce Masseage 후 이력조회나 상태조회하여 진행되는 것 확인 가능함
- {
   "workflowId": "fc3182b5-e812-4261-9b59-0920357e9a4a",
   "stepType": "ACQUISITION",
   "payload": "ACQUISITION completed",
   "timestamp": "2024-12-28T12:01:00"
}

- {
   "workflowId": "1604b69e-6bb8-44de-abb5-cfbf2c37d92a",
   "stepType": "SITE",
   "payload": "SITE completed",
   "timestamp": "2024-12-28T12:01:00"
}
