#!/usr/bin/env bash

set -e -o pipefail

echo "Regenerating Protocol Buffers"

# Directory to write generated code to (.js and .d.ts files)
JS_OUT_DIR="./build/generated"
TS_OUT_DIR="./generated"

mkdir -p $TS_OUT_DIR
mkdir -p $JS_OUT_DIR

# Generate gRPC web
protoc --proto_path=$PWD/../protocol-buffers/proto \
    --js_out=import_style=commonjs:$JS_OUT_DIR \
    --js_out=import_style=commonjs:$TS_OUT_DIR \
    --grpc-web_out=import_style=commonjs+dts,mode=grpcwebtext:$JS_OUT_DIR \
    --grpc-web_out=import_style=commonjs+dts,mode=grpcwebtext:$TS_OUT_DIR \
    $PWD/../protocol-buffers/proto/*.proto

echo "All done!"