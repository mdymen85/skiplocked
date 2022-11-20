provider "aws" {
  region                    = "us-east-1"
  shared_credentials_files  = ["$HOME/workspace/credentials"]
  profile                   = "default"
}

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

resource "aws_route_table_association" "prod-crta-public-subnet-2"{
    subnet_id = aws_subnet.subnet_az2.id
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
            script = templatefile("${path.module}/data_skiplocked_producer.sh", 
              { database_origin = aws_db_instance.skiplocked_origin.address, 
                database_destiny = aws_db_instance.skiplocked_destiny.address })
       },
        {
            name = "skiplocked_consumer",
            ip_address = "192.168.0.5",
            depends_on = [],
            script = templatefile("${path.module}/data_skiplocked_consumer.sh", 
              { database_origin = aws_db_instance.skiplocked_origin.address, 
                database_destiny = aws_db_instance.skiplocked_destiny.address })
       }
     ]

}

resource "aws_instance" "terraform_ec2_example" {
    for_each = {
       for index, vm in local.ec2_instances:
       index => vm
    }
    ami = "ami-09d3b3274b6c5d4aa"
    instance_type = "t2.micro"
    private_ip = each.value.ip_address
    vpc_security_group_ids = [
         aws_security_group.sg.id
    ]
    key_name   = "mdymen2"    
    subnet_id = aws_subnet.subnet_az1.id
    tags = {
        Name = each.value.name
    }	
    user_data = each.value.script
    depends_on = [aws_db_instance.skiplocked_origin, aws_db_instance.skiplocked_destiny]
}

resource "aws_db_instance" "skiplocked_destiny" {
  allocated_storage = 8
  engine = "mysql"
  engine_version = "8.0"
  instance_class = "db.t2.micro"
  username = "root"
  password = "mdymen_pass"
  port = 3306
  skip_final_snapshot = true
  db_subnet_group_name = aws_db_subnet_group.db_subnet.id
  vpc_security_group_ids = [aws_security_group.sg.id]
  publicly_accessible = true
  identifier = "skiplocked-destiny"
}

resource "aws_db_instance" "skiplocked_origin" {
  allocated_storage = 8
  engine = "mysql"
  engine_version = "8.0"
  instance_class = "db.t2.micro"
  username = "root"
  password = "mdymen_pass"
  port = 3306
  skip_final_snapshot = true
  db_subnet_group_name = aws_db_subnet_group.db_subnet.id
  vpc_security_group_ids = [aws_security_group.sg.id]
  publicly_accessible = true
  identifier = "skiplocked-origin"
}
