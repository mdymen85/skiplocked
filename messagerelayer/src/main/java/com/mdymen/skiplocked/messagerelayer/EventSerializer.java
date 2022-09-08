package com.mdymen.skiplocked.messagerelayer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.Serializer;

public class EventSerializer implements Serializer<ProducerRecord> {

    @Override
    public byte[] serialize(String s, ProducerRecord event) {
        byte[] retVal = null;
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        try {
            retVal = objectMapper.writeValueAsString(event.value()).getBytes();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retVal;
    }

}