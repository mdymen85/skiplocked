package com.mdymen.skiplocked.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class Controller {

    private final DataTableRepository dataTableRepository;
    private final DataTableOutboxRepository dataTableOutboxRepository;

    @RequestMapping(path = "/v1/outbox", method = RequestMethod.POST)
    public ResponseEntity<DataTable> outbox(@RequestBody DataTable data) {

        log.info("Starting outbox flow with data {}", data);

        var result = this.dataTableRepository.save(data);

        log.info("Saved data {} in datatable", result);

        var outbox_result = this.dataTableOutboxRepository.save(data.get());

        log.info("Saved data {} in datatable_outbox", outbox_result);

        return ResponseEntity.ok(data);
    }

}
