#!/bin/bash

usage() {
    echo "$0 <-a|--authentication|-r|--room|-u|--user|-w|--web-app> [port]" >&2
    exit 1
}

if [ "$#" -lt 1 ]; then
    usage
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
        echo "Invalid option:'$1'" >& 2
        usage
        ;;
    *)  # unknown option
        usage
        ;;
esac

if [ -z ${PORT+x} ]; then
    if [ "$#" != 2 ]; then
        usage
    else
        port=$2
    fi
else
    port=$PORT
fi

java -jar build/dcs-$service-service-1.0.jar $port
