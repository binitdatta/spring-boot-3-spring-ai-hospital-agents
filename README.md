# curl -X POST localhost:8025/api/rag/ingest

curl -X POST localhost:8025/api/rag/search \
-H 'Content-Type: application/json' \
-d '{"q":"What is STAT EVS target time?"}'

curl -X POST localhost:8025/api/chat/ask \
-H 'Content-Type: application/json' \
-d '{"input":"Create a safe plan to prepare room for new admission on MedSurg."}'

curl -X POST localhost:8025/api/chat/ask \
-H 'Content-Type: application/json' \
-d '{"input":"Create a safe plan to prepare room for new admission on MedSurg."}'

