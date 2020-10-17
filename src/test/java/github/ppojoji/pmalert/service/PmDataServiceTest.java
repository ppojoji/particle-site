package github.ppojoji.pmalert.service;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.Rollback;

import github.ppojoji.pmalert.dao.PmDataDao;
import github.ppojoji.pmalert.dao.StationDao;
import github.ppojoji.pmalert.dto.PmData;
import github.ppojoji.pmalert.dto.Station;
import github.ppojoji.pmalert.service.pmapi.PmApi;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
// @SpringBootTest
@Import({
	PmApi.class,
	PmDataService.class,
	PmDataDao.class,
	StationDao.class
})

class PmDataServiceTest {

	@Autowired
	PmDataService pmService;
	
	@Autowired
	PmDataDao pmDataDao;
	
	// @Test
	@Rollback(false)
	void test_특정_관측소_데이터_갱신() {
		// pmService.loadHourlyPmData();
		Station station = new Station("동홍동", "ㅇㅇㅇㅇ", null, null, "제주");
		station.setSeq(3301);
		pmService.loadPmDataByStation(station);
	}
	
	// @Test
	@Rollback(false)
	void test_모든관측소_데이터_로드() {
		System.out.println("done");
		pmService.synchronizePmData();
	}
	
	@Test
	void test_관측소별_최신_데이터_가져오기() {
		List<PmData> pmList = pmDataDao.findRecentPmList();
		for (PmData pm : pmList) {
			System.out.println(pm);
		}
	}
	
	@Test
	@Rollback(false)
	void test_1시간마다_최신_데이터_가져오기() {
		pmService.loadHourlyPmData();
	}
}
