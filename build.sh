#!/bin/bash

set -e

cd dcs-client
./build.sh
cd ..

echo "Removing build directories"
find . -maxdepth 2 -type d -name build -exec rm -r -- {} \;

./gradlew clean check test stage
