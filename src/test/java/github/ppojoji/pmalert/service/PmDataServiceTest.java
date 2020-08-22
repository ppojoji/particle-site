package github.ppojoji.pmalert.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import github.ppojoji.pmalert.dao.PmDataDao;

@SpringBootTest
class PmDataServiceTest {

	@Autowired
	PmDataService pmService;
	
	@Test
	void test() {
		pmService.loadHourlyPmData();
	}
}
