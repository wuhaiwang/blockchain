#!/bin/bash

username=`whoami`
pname="seasun-management-backend-0.0.1-SNAPSHOT.jar"
pids=`ps -fu $username | grep -w $pname | grep -w -v ps | grep -v ssh |grep -v -w grep |grep -v -w vi|grep -v -w ls|awk '{print $2}'`

if [[ -z "$pid" ]]; then
    echo "pid not found, exit."
else 
    echo "stopping $pname ..."
    for pid in $pids
    do
        kill $pid
        echo "stop process: " $pid
        sleep 1
        cn=`ps -p $pid |wc -l`
        if [ $cn -gt 1 ]; then
            kill -9 $pid
        fi
    done

    pids=`ps -fu $username | grep -w $pname | grep -w -v ps | grep -v ssh |grep -v -w grep |grep -v -w vi|grep -v -w ls|awk '{print $2}'`

    if [[ -z "$pid" ]]; then
        echo "stop failed, please manually check."
    else
        echo "stop successfully."
    fi
fi

