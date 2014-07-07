#!/bin/zsh

mysql -u weevilscout --password=hopipola -e  'use weevilscout; truncate table geolocations; truncate table runqueue;'
rm -rf public/workflow_results/* && rm -rf public/job_running/*
