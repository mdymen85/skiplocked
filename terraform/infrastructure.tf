resource "aws_vpc" "vpc" {
  cidr_block = "192.168.0.0/22"
  enable_dns_hostnames = true
}

data "aws_availability_zones" "azs" {
  state = "available"
}

resource "aws_subnet" "subnet_az1" {
  availability_zone = data.aws_availability_zones.azs.names[0]
  cidr_block        = "192.168.0.0/24"
  vpc_id            = aws_vpc.vpc.id
  map_public_ip_on_launch = true
}

resource "aws_subnet" "subnet_az2" {
  availability_zone = data.aws_availability_zones.azs.names[1]
  cidr_block        = "192.168.1.0/24"
  vpc_id            = aws_vpc.vpc.id
  map_public_ip_on_launch = true
}

resource "aws_subnet" "subnet_az3" {
  availability_zone = data.aws_availability_zones.azs.names[2]
  cidr_block        = "192.168.2.0/24"
  vpc_id            = aws_vpc.vpc.id
  map_public_ip_on_launch = true  
}

resource "aws_internet_gateway" "ig" {
  vpc_id = aws_vpc.vpc.id
}

resource "aws_route_table" "prod-public-crt" {
  vpc_id = aws_vpc.vpc.id
  
  route {
    cidr_block = "0.0.0.0/0" 
    gateway_id = aws_internet_gateway.ig.id
  }    
}

resource "aws_route_table_association" "prod-crta-public-subnet-1"{
    subnet_id = aws_subnet.subnet_az1.id
    route_table_id = aws_route_table.prod-public-crt.id
}

resource "aws_security_group" "sg" {
     vpc_id = aws_vpc.vpc.id
     ingress {
       description      = "SSH"
       from_port        = 22
       to_port          = 22
       protocol         = "tcp"
       cidr_blocks      = ["0.0.0.0/0"]
     }   
     ingress {
       description      = "8080"
       from_port        = 8080
       to_port          = 8080
       protocol         = "tcp"
       cidr_blocks      = ["0.0.0.0/0"]
     }  
     ingress {
       description      = "mysql"
       from_port        = 3306
       to_port          = 3306
       protocol         = "tcp"
       cidr_blocks      = ["0.0.0.0/0"]
     }             
     ingress {
       description      = "kafka"
       from_port        = 9092
       to_port          = 9092
       protocol         = "tcp"
       cidr_blocks      = ["0.0.0.0/0"]
     }       
     egress {
       from_port        = 0
       to_port          = 0
       protocol         = "-1"
       cidr_blocks      = ["0.0.0.0/0"]
       ipv6_cidr_blocks = ["::/0"]
     }                   
     tags = {
        Name = "security_group_tf"
     }
}

#resource "aws_kms_key" "kms" {
#  description = "example"
#}

#resource "aws_cloudwatch_log_group" "test" {
#  name = "msk_broker_logs"
#}

#resource "aws_s3_bucket" "bucket" {
#  bucket = "msk-broker-logs-bucket-mdymen"
#}

#resource "aws_s3_bucket_acl" "bucket_acl" {
#  bucket = aws_s3_bucket.bucket.id
#  acl    = "private"
#}

#resource "aws_iam_role" "firehose_role" {
#  name = "firehose_test_role"
#
#  assume_role_policy = <<EOF
#{
#"Version": "2012-10-17",
#"Statement": [
#  {
#    "Action": "sts:AssumeRole",
#    "Principal": {
#      "Service": "firehose.amazonaws.com"
#    },
#    "Effect": "Allow",
#    "Sid": ""
#  }
#  ]
#}
#EOF
#}

#resource "aws_kinesis_firehose_delivery_stream" "test_stream" {
#  name        = "terraform-kinesis-firehose-msk-broker-logs-stream"
#  destination = "s3"

#  s3_configuration {
#    role_arn   = aws_iam_role.firehose_role.arn
#    bucket_arn = aws_s3_bucket.bucket.arn
#  }

#  tags = {
#    LogDeliveryEnabled = "placeholder"
#  }

#  lifecycle {
#    ignore_changes = [
#      tags["LogDeliveryEnabled"],
#    ]
#  }
#}

resource "aws_msk_cluster" "example1" {
  cluster_name           = "clusterExampleKafka1"
  kafka_version          = "3.2.0"
  number_of_broker_nodes = 3

  encryption_info {
    encryption_in_transit {
      client_broker = "TLS_PLAINTEXT"
    }
  }

  broker_node_group_info {
    instance_type = "kafka.m5.large"
    client_subnets = [
      aws_subnet.subnet_az1.id,
      aws_subnet.subnet_az2.id,
      aws_subnet.subnet_az3.id,
    ]
    storage_info {
      ebs_storage_info {
        volume_size = 1000
      }      
    }
    security_groups = [aws_security_group.sg.id]
  }

 # encryption_info {
 #   encryption_at_rest_kms_key_arn = aws_kms_key.kms.arn
 # }

  open_monitoring {
    prometheus {
      jmx_exporter {
        enabled_in_broker = true
      }
      node_exporter {
        enabled_in_broker = true
      }
    }
  }

  #logging_info {
  #  broker_logs {
  #    cloudwatch_logs {
  #      enabled   = true
  #      log_group = aws_cloudwatch_log_group.test.name
  #    }
      #firehose {
      #  enabled         = true
      #  delivery_stream = aws_kinesis_firehose_delivery_stream.test_stream.name
      #}
      #s3 {
      #  enabled = true
      #  bucket  = aws_s3_bucket.bucket.id
      #  prefix  = "logs/msk-"
      #}
    #}
  #}

 # tags = {
 #   foo = "bar"
 # }
}

output "zookeeper_connect_string" {
  value = aws_msk_cluster.example1.zookeeper_connect_string
}

output "bootstrap_brokers_tls" {
  description = "TLS connection host:port pairs"
  value       = aws_msk_cluster.example1.bootstrap_brokers_tls
}

resource "aws_db_subnet_group" "db_subnet" {
  name = "dbsubnet"
  subnet_ids = [aws_subnet.subnet_az1.id, aws_subnet.subnet_az2.id]
}


locals {

     ec2_instances = [
       {
            name = "skiplocked_producer",
            ip_address = "192.168.0.4",
            depends_on = [],
            script = file("${path.module}/data_skiplocked_producer.sh")  
       },
        {
            name = "skiplocked_consumer",
            ip_address = "192.168.0.5",
            depends_on = [],
            script = file("${path.module}/data_skiplocked_consumer.sh")  
       },
        {
            name = "kafka_producer",
            ip_address = "192.168.0.6",
            depends_on = [],
            script = file("${path.module}/data_kafka_producer.sh")  
       },
        {
            name = "kafka_consumer",
            ip_address = "192.168.0.7",
            depends_on = [],
            script = file("${path.module}/data_kafka_consumer.sh")  
       }
     ]

}

resource "aws_instance" "terraform_ec2_example" {
    for_each = {
       for index, vm in local.ec2_instances:
       index => vm
    }
    ami = "ami-0c2ab3b8efb09f272"
    instance_type = "t2.micro"
    private_ip = each.value.ip_address
    vpc_security_group_ids = [
         aws_security_group.sg.id
    ]
    key_name   = "mdymenOregonKey"    
    subnet_id = aws_subnet.subnet_az1.id
    tags = {
        Name = each.value.name
    }	
    user_data = each.value.script  
}

locals {

     databases = [
       {
            db_name = "skiplocked_prodcer"
       },
       {
            db_name = "skiplocked_consumer"
       },
       {
            db_name = "kafka_producer"
       },
       {
            db_name = "kafka_consumer"
       }
     ]

}

resource "aws_db_instance" "relayer_database" {
  for_each = {
      for index, vm in local.databases:
      index => vm
  }
  allocated_storage = 8
  engine = "mysql"
  engine_version = "8.0"
  instance_class = "db.t2.micro"
  db_name = each.value.db_name
  username = "root"
  password = "mdymen_pass"
  port = 3306
  skip_final_snapshot = true
  db_subnet_group_name = aws_db_subnet_group.db_subnet.id
  vpc_security_group_ids = [aws_security_group.sg.id]
  publicly_accessible = true
}