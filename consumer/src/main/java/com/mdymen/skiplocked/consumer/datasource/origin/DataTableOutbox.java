package com.mdymen.skiplocked.consumer.datasource.origin;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "datatable_outbox")
@Data
@ToString
public class DataTableOutbox {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String data;

    public DataTableOutbox(String data) {
        this.data = data;
    }

    public DataTableOutbox() {

    }
}
