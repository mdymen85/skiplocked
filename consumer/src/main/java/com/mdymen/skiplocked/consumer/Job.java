package com.mdymen.skiplocked.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.LockOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import java.sql.SQLException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional
@Profile("!Test")
public class Job {

    private final SkipLockedService skipLockedService;

    @Scheduled(fixedDelayString = "${spring.job.fixedDelay:1000}")
    public void job() throws InterruptedException, SQLException {

        log.info("init job");

        this.skipLockedService.job(false);

        log.info("finish job");
    }

}
