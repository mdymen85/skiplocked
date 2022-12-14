#!/bin/bash
sudo su
yum update -y 
echo export MYSQL_DESTINY=${database_destiny} >> /etc/profile
echo export MYSQL_ORIGIN=${database_origin} >> /etc/profile
wget https://download.oracle.com/java/17/latest/jdk-17_linux-x64_bin.rpm
rpm -ivh jdk-17_linux-x64_bin.rpm
source /etc/profile
wget https://skiplocked-project.s3.amazonaws.com/consumer-0.0.1-SNAPSHOT.jar
source /etc/profile
java -Dserver.port=8080 -jar consumer-0.0.1-SNAPSHOT.jar
