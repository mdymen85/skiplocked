package com.mdymen.skiplocked.kafka.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional
public class KafkaConsumer {

    private final DataTableResultRepository dataTableResultRepository;

    @KafkaListener(topics = "skiplocked-topic", groupId = "group")
    public void consumer1(ConsumerRecord<String, EventProducer> record) {

        var result = record.value();

        log.info("Consumer is reading message {} from topic {}", result.getData(), record.topic());

        dataTableResultRepository.save(new DataTableResult(result.getData()));

    }

}
