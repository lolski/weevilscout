#!/bin/sh

echo "clearing geolocations and runqueue table and job/workflow instances folders..."
curl localhost:9000/clearAll
echo ""
cd /home/lolski/UVA/weevil/weevil-source/public
rm -r job_running/*
rm -r workflow_results/*
rm -r job_individual_results/*
rm -r profile/*
echo "done."
date
