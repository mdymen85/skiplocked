package com.mdymen.skiplocked.consumer;

import com.mdymen.skiplocked.consumer.datasource.destiny.DataTableResultRepository;
import com.mdymen.skiplocked.consumer.datasource.origin.DataTableOutboxRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Iterator;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
class ConsumerApplicationTests {

	@Autowired
	private SkipLockedService skipLockedService;

	@Autowired
	private ProxySkipLockedServiceAsync proxySkipLockedServiceAsync;

	@Autowired
	private DataTableResultRepository dataTableResultRepository;

	@Autowired
	private DataTableOutboxRepository dataTableOutboxRepository;

	@BeforeEach
	public void before() {
		proxySkipLockedServiceAsync.deleteAll();
	}

	@Test
	void contextLoads() throws InterruptedException {

		this.proxySkipLockedServiceAsync.createData("Testing1", "Testing2");

		Iterator<DataTableOutbox> it = dataTableOutboxRepository.findAll().iterator();
		int size = 0;
		while (it.hasNext()) {
			it.next();
			size++;
		}
		assertEquals(size, 2);

		var future = CompletableFuture.runAsync(() -> proxySkipLockedServiceAsync.job());

		Thread.sleep(1000);

		skipLockedService.job(false);

		Iterator<DataTableOutbox> it2 = dataTableOutboxRepository.findAll().iterator();
		size = 0;
		while (it2.hasNext()) {
			var dataR = it2.next();
			assertEquals(dataR.getData(), "Testing2");
			size++;
		}

		assertEquals(1, size);

	}

}
