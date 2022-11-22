#!/bin/bash
aws ssm send-command --document-name "AWS-RunDocument" --document-version "1" --targets '[{"Key":"tag:Name","Values":["skiplocked_consumer"]}]' --parameters '{"sourceType":["S3"],"sourceInfo":["{\"path\":\"https://skiplocked-project.s3.amazonaws.com/script-consumer\"}"],"documentParameters":["{}"]}' --timeout-seconds 600 --max-concurrency "50" --max-errors "0" --region us-east-1

aws ssm send-command --document-name "AWS-RunDocument" --document-version "1" --targets '[{"Key":"tag:Name","Values":["skiplocked_producer"]}]' --parameters '{"sourceType":["S3"],"sourceInfo":["{\"path\":\"https://skiplocked-project.s3.amazonaws.com/script-producer\"}"],"documentParameters":["{}"]}' --timeout-seconds 600 --max-concurrency "50" --max-errors "0" --region us-east-1
