#!/bin/bash
sudo su
yum update -y 
wget https://download.oracle.com/java/17/latest/jdk-17_linux-x64_bin.rpm
rpm -ivh jdk-17_linux-x64_bin.rpm
wget https://skiplocked-project.s3.amazonaws.com/consumer-0.0.1-SNAPSHOT.jar
java -Dserver.port=8080 -DMYSQL_HOST_ORIGIN=${database_origin} -DMYSQL_HOST_DESTINY=${database_destiny} -jar consumer-0.0.1-SNAPSHOT.jar
echo export MYSQL_HOST_DESTINY=${database_destiny} >> /etc/profile
echo export MYSQL_HOST_ORIGIN=${database_origin} >> /etc/profile
