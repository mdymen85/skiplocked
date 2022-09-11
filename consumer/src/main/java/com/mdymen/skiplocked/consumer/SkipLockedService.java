package com.mdymen.skiplocked.consumer;

import com.mdymen.skiplocked.consumer.datasource.destiny.DataTableResultRepository;
import com.mdymen.skiplocked.consumer.datasource.origin.DataTableOutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.LockOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class SkipLockedService {

    private static int scheduler_times = 0;

    private final DataTableOutboxRepository dataTableOutboxRepository;
    private final DataTableResultRepository dataTableResultRepository;

    private final EntityManager entityManager;

    @Value("${spring.jpa.maxResults:1000}")
    private int maxResults;

    @Value("${spring.jpa.skipedlock.enabled:true}")
    private boolean skipedLock;

    @Value("${spring.jpa.testingMode.sleep:90000}")
    private long sleep;

    public void job(Boolean testingPorpuse) throws InterruptedException {

        scheduler_times = scheduler_times + 1;

        log.info("scheduler {}", scheduler_times);

        List<DataTableOutbox> dataTableOutboxList = entityManager
                .createQuery("select d from DataTableOutbox d ", DataTableOutbox.class)
                .setMaxResults(maxResults)
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .setHint("javax.persistence.lock.timeout", getLockOption())
                .getResultList();

        this.testingMode(testingPorpuse);

        log.info("processing...");

        dataTableOutboxList
                .forEach(datatable -> {
                    var result = new DataTableResult(datatable);

                    log.info("processing row {}", datatable);

                    this.dataTableResultRepository.save(result);

                    log.info("removing from outbox table: {}", datatable);

                    this.dataTableOutboxRepository.delete(datatable);
                });

        log.info("ended scheduler {}", scheduler_times);
    }

    private int getLockOption() {
        return skipedLock ? LockOptions.SKIP_LOCKED : LockOptions.WAIT_FOREVER;
    }

    private void testingMode(Boolean testingMode) throws InterruptedException {
        if (testingMode != null && testingMode) {
            log.info("sleep mode in {}", sleep/1000);
            Thread.sleep(sleep);
        } else {
            log.info("sleep mode disabled");
        }
    }
}
