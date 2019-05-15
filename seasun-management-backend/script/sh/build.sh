#!/bin/bash

cd /var/lib/jenkins/workspace/qa-seasun-management/code/seasun-management-backend

echo "==================== START BUILD ========================="
gradle build -x test
echo "==================== AFTER BUILD ========================="

cp /var/lib/jenkins/workspace/qa-seasun-management/code/seasun-management-backend/build/libs/seasun-management-backend-0.0.1-SNAPSHOT.jar /var/lib/jenkins/workspace/qa-seasun-management/code/seasun-management-backend/run/seasun-management-backend-0.0.1-SNAPSHOT.jar

cd /var/lib/jenkins/workspace/qa-seasun-management/code/seasun-management-backend/run
username=`whoami`
pname="seasun-management-backend-0.0.1-SNAPSHOT.jar"
pids=`ps -fu $username | grep -w $pname | grep -w -v ps | grep -v ssh |grep -v -w grep |grep -v -w vi|grep -v -w ls|awk '{print $2}'`

echo "====================  KILL ALL SYSTEM PROCCESSES =========================="
echo "stopping all system [jar]..."
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

echo "====================  RESTART SYSTEM =========================="
echo "ready to start system in " `pwd`
nohup java -Xms1024m -Xmx2048m -XX:NewSize=1024m -XX:MaxNewSize=1024m -Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=8000,suspend=n -jar ./seasun-management-backend-0.0.1-SNAPSHOT.jar --server.port=31238  &
sleep 1
echo "starting.....  please view log"
