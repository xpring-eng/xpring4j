docker stop hermes-envoy
docker rm hermes-envoy
docker build -t hermes-envoy -f ./Dockerfile .
docker run -d -p 9091:9091 --name hermes-envoy hermes-envoy