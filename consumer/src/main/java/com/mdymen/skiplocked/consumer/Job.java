package com.mdymen.skiplocked.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.LockOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional
public class Job {

    private static int scheduler_times = 0;

    private final DataTableOutboxRepository dataTableOutboxRepository;
    private final DataTableResultRepository dataTableResultRepository;

    private final EntityManager entityManager;

    @Value("${spring.jpa.maxResults:1000}")
    private int maxResults;

    @Value("${spring.jpa.skipedlock.enabled:true}")
    private boolean skipedLock;

    @Value("${spring.jpa.testingMode.enabled:false}")
    private boolean testingMode;

    @Value("${spring.jpa.testingMode.sleep:90000}")
    private long sleep;

    @Scheduled(fixedDelayString = "${spring.job.fixedDelay:1000}")
    public void job() throws InterruptedException {

        scheduler_times = scheduler_times + 1;

        log.trace("scheduler times {}", scheduler_times);

        List<DataTableOutbox> dataTableOutboxList = entityManager
                .createQuery("select d from DataTableOutbox d ", DataTableOutbox.class)
                .setMaxResults(maxResults)
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .setHint("javax.persistence.lock.timeout", getLockOption())
                .getResultList();

        this.testingMode();

        log.trace("processing...");

        dataTableOutboxList
                .forEach(datatable -> {
                    var result = new DataTableResult(datatable);

                    log.trace("Row {}", datatable);

//                    this.dataTableResultRepository.save(result);

//                    this.dataTableOutboxRepository.delete(datatable);
                });

        log.trace("ended scheduler times {}", scheduler_times);
    }

    private int getLockOption() {
        return skipedLock ? LockOptions.SKIP_LOCKED : LockOptions.WAIT_FOREVER;
    }

    private void testingMode() throws InterruptedException {
        if (testingMode) {
            log.trace("Sleep mode in {}", sleep/1000);
            Thread.sleep(sleep);
        } else {
            log.trace("Sleep mode disabled");
        }
    }

}
