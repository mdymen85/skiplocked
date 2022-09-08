sudo su
yum update -y 
wget https://download.oracle.com/java/17/latest/jdk-17_linux-x64_bin.rpm
rpm -ivh jdk-17_linux-x64_bin.rpm
wget https://kafkalisteners.s3.amazonaws.com/relayerproducer-0.0.1-SNAPSHOT.jar 
java -Dserver.port=8080 -DMYSQL_HOST=${aws_db_instance.relayer_database.address} -Dspring.kafka.producer.bootstrap-servers=${aws_msk_cluster.example1.boostrap_servers.0} -DBOOTSTRAP_SERVER=${aws_msk_cluster.example1.boostrap_servers.0} -jar relayerproducer-0.0.1-SNAPSHOT.jar
