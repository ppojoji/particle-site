package github.ppojoji.pmalert.service;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
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
	PmNotificationService.class,
	StationDao.class
})

class NotificationServiceTest {
	@Autowired
	PmNotificationService NotifServce;
	
	@Autowired
	StationDao stationDao;
	
	@Test
	@Rollback(false)
	void 관측소() {
		NotifServce.loadRecentPm();
	}
	
	@Test
	@Rollback(false)
	void mailList() {
		NotifServce.loadUserBookmark();
	}
	
	@Test
	void 메일통보_결정하는_코드() {
		List<PmData> prevPmData = new ArrayList<PmData>();
		prevPmData.add(new PmData(null, null, 3346, LocalDateTime.now()));
		prevPmData.add(new PmData(5.0, 40.0, 3303, LocalDateTime.now()));
		prevPmData.add(new PmData(60.0, 60.0, 3305, LocalDateTime.now()));

		List<PmData> currentPmData = new ArrayList<PmData>();
		currentPmData.add(new PmData(null, null, 3346, LocalDateTime.now()));
		currentPmData.add(new PmData(40.0, 70.0, 3303, LocalDateTime.now()));
		currentPmData.add(new PmData(90.0, 90.0, 3305, LocalDateTime.now()));
		
		
		// NotifServce.startNotification();
		NotifServce.resolveMailingList(prevPmData, currentPmData);
	}
}
