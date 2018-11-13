#!/bin/bash

set -e

while [[ $# -gt 0 ]]; do
    case $1 in
        angular)
            ANGULAR=1
            shift # past argument
            ;;
        clean)
            CLEAN=1
             shift # past argument
            ;;
        check)
            CHECK=1
             shift # past argument
            ;;
        stage)
            STAGE=1
            shift # past argument
            ;;
        test)
            TEST=1
            shift # past argument
            ;;
        *)    # unknown option
            echo "Unknown option: '$1'"
            usage
            exit 1
            ;;
    esac
done

if [ ! -z ${ANGULAR+x} ]; then
    cd dcs-client
    ./build.sh
    cd ..
fi

if [ ! -z ${CLEAN+x} ]; then
    echo "Removing build directories"
    for dir in `find . -maxdepth 2 -type d -name "build"`; do
        # `rm -r` fails if the directory to delete is empty.
        rm -r -- $dir || rmdir $dir
    done
    ./gradlew clean
fi

if [ ! -z ${CHECK+x} ]; then
    ./gradlew check
fi

if [ ! -z ${TEST+x} ]; then
    ./gradlew test
fi

if [ ! -z ${STAGE+x} ]; then
    ./gradlew stage
fi
