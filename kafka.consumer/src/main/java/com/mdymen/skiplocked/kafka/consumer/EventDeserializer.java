package com.mdymen.skiplocked.kafka.consumer;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;

public class EventDeserializer implements Deserializer<EventProducer> {

    @Override
    public EventProducer deserialize(String s, byte[] bytes) {
        ObjectMapper mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        EventProducer event = null;
        try {
            event = mapper.readValue(bytes, EventProducer.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return event;
    }
}