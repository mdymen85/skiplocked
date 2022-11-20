package com.mdymen.skiplocked.consumer;

import com.mdymen.skiplocked.consumer.datasource.destiny.DataTableResultRepository;
import com.mdymen.skiplocked.consumer.datasource.origin.DataTableOutbox;
import com.mdymen.skiplocked.consumer.datasource.origin.DataTableOutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Component
@RequiredArgsConstructor
public class ProxySkipLockedServiceAsync {

    private final SkipLockedService skipLockedService;

    private final DataTableOutboxRepository dataTableOutboxRepository;
    private final DataTableResultRepository dataTableResultRepository;

    public void job() {

        try {

            this.skipLockedService.job(true);

        }
        catch (Exception e) {
            throw new RuntimeException();
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createData(String... args) {

        List<String> s = Arrays.stream(args).toList();

        s.forEach(x -> {
            var dataTableOutbox1 = new DataTableOutbox(x);
            this.dataTableOutboxRepository.save(dataTableOutbox1);
        });

        var it = this.dataTableOutboxRepository.findAll().iterator();

        int size = 0;
        while (it.hasNext()) {
            it.next();
            size++;
        }
        assertEquals(size, 2);

    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteAll() {
        dataTableResultRepository.deleteAll();
        dataTableOutboxRepository.deleteAll();
    }

}
