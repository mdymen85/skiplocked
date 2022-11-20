package com.mdymen.skiplocked.consumer.datasource.origin;

import org.springframework.data.repository.CrudRepository;

public interface DataTableOutboxRepository extends CrudRepository<DataTableOutbox, Long> {

//    String SKIP_LOCKED = "-2";
//    @QueryHints(@QueryHint(name = AvailableSettings.JPA_LOCK_TIMEOUT, value = SKIP_LOCKED))
//    @Lock(LockModeType.PESSIMISTIC_WRITE)
//    @Query(value = "SELECT * FROM datatable_outbox FOR UPDATE SKIP LOCKED", nativeQuery = true)
//    List<DataTableOutbox> findTopByData(String a);

}
