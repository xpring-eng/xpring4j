#!/usr/bin/env bash

set -e -o pipefail

WORKING_DIR=$(pwd)
OUTPUT_DIR=./src/main/resources/

echo "Bundling JS"
cd xpring-common-js

echo ">> Installing Node Dependencies"
npm i
echo ">> Done Installing Node Dependencies"

echo ">> Running Webpack."
npm run webpack
echo ">> Done Running Webpack"

echo "Done Bundling JS"

cd $WORKING_DIR
echo "Copying Artifacts"
mkdir -p $OUTPUT_DIR
cp xpring-common-js/dist/index.js $OUTPUT_DIR
