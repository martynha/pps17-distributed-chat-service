#!/bin/bash

usage() {
    echo "$0 <> [port]" >&2
}

if [ "$#" -lt 1 ]; then
    usage
    exit 1
fi

case $1 in
    -a|--authentication)
        service=authentication
        ;;
    -r|--room)
        service=room
        ;;
    -u|--user)
        service=user
        ;;
    -w|--web-app)
        service=webapp
        ;;
    -*|--*)
        echo "Invalid option: \"$1\"" >& 2
        usage
        exit 1
        ;;
    *)    # unknown option
        usage
        exit 1
        ;;
esac

if [ -n ${PORT+x} ]; then
    if [ "$#" != 2 ]; then
        usage
        exit 1
    else
        port=$2
    fi
else
    usage
    exit 1
fi

java -jar build/dcs-$service-service-1.0.jar $port
