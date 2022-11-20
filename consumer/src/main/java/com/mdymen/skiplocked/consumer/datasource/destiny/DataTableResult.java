package com.mdymen.skiplocked.consumer.datasource.destiny;

import com.mdymen.skiplocked.consumer.datasource.origin.DataTableOutbox;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "datatable_result")
@Data
@AllArgsConstructor
@ToString
public class DataTableResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String data;

    @CreationTimestamp
    private Instant created;

    public DataTableResult() {

    }

    public DataTableResult(DataTableOutbox dataTableOutbox) {
        this.data = dataTableOutbox.getData();
    }
}
