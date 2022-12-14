package com.mdymen.skiplocked.producer;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "datatable_outbox")
@Data
public class DataTableOutbox {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String data;

    @CreationTimestamp
    private Instant created;

    public DataTableOutbox(String data) {
        this.data = data;
    }

    public DataTableOutbox() {

    }
}
