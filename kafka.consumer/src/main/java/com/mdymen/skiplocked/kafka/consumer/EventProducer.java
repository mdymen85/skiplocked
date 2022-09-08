package com.mdymen.skiplocked.kafka.consumer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class EventProducer {

    private String data;

    public EventProducer() {

    }

    @Builder
    public EventProducer(String data) {
        this.data = data;
    }

}
