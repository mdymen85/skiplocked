package com.mdymen.skiplocked.messagerelayer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;

@Component
@RequiredArgsConstructor
public class Processor extends RouteBuilder {

    @Value("${spring.kafka.topic:camel-relayer-topic}")
    private String topic;

    @Value("${spring.kafka.producer.bootstrap-servers:localhost:9092}")
    private String bootstrapServer;

    @Override
    public void configure() throws Exception {

        from("timer:foo?period=1000")
                .setBody(constant("select * from datatable_outbox limit 1000"))
                .to("jdbc:datasource")
                .split(body()).streaming().stopOnException()
                .process(new Process(topic))
                .log("Message relayer : ${in.headers.data}")
                .to("sql:delete from datatable_outbox where uuid = :#data")
                .to("kafka:"+topic+"?brokers=" + bootstrapServer + "&keySerializer=org.apache.kafka.common.serialization.StringSerializer&valueSerializer=com.mdymen.skiplocked.messagerelayer.EventSerializer")
                .end();
    }
}

@RequiredArgsConstructor
@Slf4j
class Process implements org.apache.camel.Processor {

    private final String topic;

    @Override
    public void process(Exchange exchange) throws Exception {

        LinkedHashMap outbox = (LinkedHashMap) exchange.getIn().getBody();

        var data = outbox.get("data").toString();

        var record = new ProducerRecord<String, String>(topic, null, null, data);

        exchange.getIn().setBody(record);
        exchange.getIn().setHeader("data", data);
        exchange.getMessage().setBody(record);
    }
}