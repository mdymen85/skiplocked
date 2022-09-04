package com.mdymen.skiplocked.producer;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "datatable")
@Data
@ToString
public class DataTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String data;

    @CreationTimestamp
    private Instant created;

    public DataTable(String data) {
        this.data = data;
    }

    public DataTable() {

    }

    public DataTableOutbox get() {
        return new DataTableOutbox(this.data);
    }
}
