#!/bin/bash

SRC=./dist
DEST=../dcs-webapp-service/src/main/resources/webroot

if [ ! -d node_modules/ ]; then
    echo "Installing Node modules..."
    npm install
fi

echo "Building Angular application..."
ng build --prod --aot

if [ -d "$DEST" ]; then
    echo "Removing contents from folder '$DEST'..."
    rm -r $DEST/*
else
    echo "Creating folder '$DEST'..."
    mkdir -p $DEST
fi

echo "Moving contents from '$SRC' to '$DEST'..."
mv $SRC/dcs-client/* $DEST/

echo "Removing '$SRC'..."
rm -r $SRC
