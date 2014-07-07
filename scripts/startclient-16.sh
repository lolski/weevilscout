#!/bin/sh

SERVER=localhost:9000
BROWSER=firefox
#killall $BROWSER
for i in {0..15}; do
	$BROWSER $SERVER;
done

