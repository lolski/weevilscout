#!/bin/bash
echo "exporting debug settings in \$SBT_OPTS. run this script as . ./path/to/set_debug.sh"
export SBT_OPTS="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=9999"
