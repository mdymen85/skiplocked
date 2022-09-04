package com.mdymen.skiplocked.producer;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "datatable_outbox")
@Data
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
