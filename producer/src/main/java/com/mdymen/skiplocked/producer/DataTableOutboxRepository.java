package com.mdymen.skiplocked.producer;

import org.springframework.data.repository.CrudRepository;

public interface DataTableOutboxRepository extends CrudRepository<DataTableOutbox, Long> {
}
