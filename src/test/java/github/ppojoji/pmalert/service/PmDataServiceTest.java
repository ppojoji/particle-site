package github.ppojoji.pmalert.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PmDataServiceTest {

	@Autowired
	PmDataService pmService;
	
	@Test
	void test() {
		pmService.loadHourlyPmData();
	}

}
