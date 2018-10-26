#!/bin/bash

set -e

SRC=./dist
DEST=../dcs-webapp-service/src/main/resources/webroot

if [ ! -d node_modules/ ]; then
    echo "Installing Node modules..."
    npm install
fi

echo "Building Angular application..."
ng build --prod --aot

if [ -d "$DEST" ]; then
    if [ `ls -1A $DEST | wc -l` -ne 0 ]; then
        echo "Removing contents from folder '$DEST'..."
        rm -r $DEST/*
    fi
else
    echo "Creating folder '$DEST'..."
    mkdir -p $DEST
fi

echo "Moving contents from '$SRC' to '$DEST'..."
mv $SRC/dcs-client/* $DEST/

echo "Removing '$SRC'..."
rm -r $SRC
