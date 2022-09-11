#!/bin/bash
sudo su
yum update -y 
wget https://download.oracle.com/java/17/latest/jdk-17_linux-x64_bin.rpm
rpm -ivh jdk-17_linux-x64_bin.rpm
wget https://skiplocked.s3.us-west-2.amazonaws.com/consumer-0.0.1-SNAPSHOT.jar
java -Dserver.port=8080 -DMYSQL_HOST_ORIGIN=${aws_db_instance.skiplocked_origin.address} -DMYSQL_HOST_DESTINY=${aws_db_instance.skiplocked_destiny.address} -jar consumer-0.0.1-SNAPSHOT.jar
