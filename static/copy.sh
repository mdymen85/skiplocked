#!/bin/bash
export publicDnsNameProducer=$(aws ec2 describe-instances --filters Name=tag:Name,Values=skiplocked_producer --query "Reservations[*].Instances[*].PublicDnsName" --output text)
echo $publicDnsNameProducer
scp -i ~/.ssh/mdymen2.pem  /var/lib/jenkins/workspace/skiplocked/producer/target/producer-0.0.1-SNAPSHOT.jar ec2-user@$test:~/. -y
