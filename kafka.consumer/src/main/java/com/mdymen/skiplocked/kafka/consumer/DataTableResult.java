package com.mdymen.skiplocked.kafka.consumer;

import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import javax.persistence.*;
import java.time.Instant;

@Data
@ToString
@Entity
@Table(name = "datatable_result")
public class DataTableResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String data;

    @CreationTimestamp
    private Instant created;

    public DataTableResult(){}

    public DataTableResult(String data) {
        this.data = data;
    }
}
