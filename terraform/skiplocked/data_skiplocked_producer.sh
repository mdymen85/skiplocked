#!/bin/bash
sudo su
yum update -y 
echo export MYSQL_HOST=${database_origin} >> /etc/profile
yum install mariadb -y
wget https://skiplocked.s3.us-west-2.amazonaws.com/producer_data.sql
mysql -h ${database_origin} -P 3306 -u root -pmdymen_pass < producer_data.sql
wget https://skiplocked.s3.us-west-2.amazonaws.com/consumer_data.sql
mysql -h ${database_destiny} -P 3306 -u root -pmdymen_pass < consumer_data.sql
wget https://download.oracle.com/java/17/latest/jdk-17_linux-x64_bin.rpm
rpm -ivh jdk-17_linux-x64_bin.rpm
wget https://skiplocked-project.s3.amazonaws.com/producer-0.0.1-SNAPSHOT.jar
java -Dserver.port=8080 -DMYSQL_HOST=${database_origin} -jar producer-0.0.1-SNAPSHOT.jar
