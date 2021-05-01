package github.ppojoji.pmalert.service;

import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.Rollback;

import github.ppojoji.pmalert.dao.StationDao;
import github.ppojoji.pmalert.service.pmapi.PmApi;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
// @SpringBootTest
@Import({
	StationService.class,
	PmApi.class,
	StationDao.class
})
class StationServiceTest {

	@Autowired
	StationService stationService;
	
	@Test
	@Rollback(true)
	void test_관측소_정보_업데이트() {
		stationService.synchronizeAllStations();
	}
	
	@Test
	@Rollback(false)
	void test_synchronizedStation() {
		stationService.synchronizedStation();
	}

}
