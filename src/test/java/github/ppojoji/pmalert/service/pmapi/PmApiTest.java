package github.ppojoji.pmalert.service.pmapi;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import github.ppojoji.pmalert.dto.PmData;
import github.ppojoji.pmalert.dto.Station;

@SpringBootTest
class PmApiTest {

	@Autowired
	PmApi pm;
	
	@Test
	void test_모든_관측소_가져오기() {
		
//		List<Station> stations = pm.listStationsBySido("서울");
		List<Station> stations = pm.findAllStation();
//		System.out.println(stations);
		for (Station s : stations) {
			System.out.println(s);
		}
		System.out.println(stations.size());
	}
	
	@Test
	void test1() {
		PmApi pm = new PmApi();
		
		List<PmData> pmData = pm.queryHourlyPmData("서울");
//		System.out.println(stations);
		for (PmData p : pmData) {
			System.out.println(p);
		}
	}
}
