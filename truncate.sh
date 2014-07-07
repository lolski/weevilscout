#!/bin/zsh

mysql -u weevilscout --password=hopipola -e  'use weevilscout; truncate table geolocations; truncate table runqueue;'
