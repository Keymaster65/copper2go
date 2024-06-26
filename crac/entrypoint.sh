#!/bin/bash

export PATH="$PATH:$JAVA_HOME/bin"
typeset logPrefix="============================="

# this might not be enough
typeset lastDumpFile="cr/stats-dump"

_checkPoint(){
  export PATH="$PATH:$JAVA_HOME/bin"
  jcmd io.github.keymaster65.copper2go.Main JDK.checkpoint
}

_wait(){
  typeset RC="1"
  echo "Waiting for service ..."
  while [ "$RC" != "0" ]; do
    curl --data Wolf http://localhost:19666/copper2go/3/api/twoway/2.0/Hello 2>&1 | fgrep "Hello Wolf"
    RC="$?"
  done
  echo "$logPrefix Service found at  $(date +%H:%M:%S.%N)"
}

if [ ! -f "$lastDumpFile" ]; then
  echo "$logPrefix Starting to warm up container at $(date +%H:%M:%S.%N)"
  export COPPER2GO_APPLICATION_OPTS="-XX:CRaCCheckpointTo=cr"
  time _wait &
  bin/copper2go-application &
  _wait
  typeset noOfRequest="100"
  echo "Now warming up with $noOfRequest requests"
  for i in {1..$noOfRequest}; do
    curl --data Wolf http://localhost:19666/copper2go/3/api/twoway/2.0/Hello 2>&1 | fgrep -q "Hello Wolf"
  done
  sleep 1 # wait for warmup finished
  _checkPoint
  while [ ! -f "$lastDumpFile" ]; do
    echo "Wait for checkpoint"
    sleep 1
   done
fi

echo "$logPrefix Starting with checkpoint at $(date +%H:%M:%S.%N)"
time _wait &

# Add restarts as suggested in
# https://docs.azul.com/core/crac/crac-debugging
# for example in case of
# Error (criu/cr-restore.c:1518): Can't fork for 12: File exists
# Error (criu/cr-restore.c:2605): Restoring FAILED.
java -XX:CRaCRestoreFrom=cr \
|| echo "1st restart due to error $?." ; java -XX:CRaCRestoreFrom=cr \
|| echo "2nd restart due to error $?." ; java -XX:CRaCRestoreFrom=cr \
|| echo "3rd restart due to error $?." ; java -XX:CRaCRestoreFrom=cr \
|| echo "4th restart due to error $?." ; java -XX:CRaCRestoreFrom=cr \
|| echo "5th and last restart due to error $?." ; java -XX:CRaCRestoreFrom=cr