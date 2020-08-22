package github.ppojoji.pmalert.service.pmapi;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import github.ppojoji.pmalert.dto.PmData;
import github.ppojoji.pmalert.dto.Station;

// @SpringBootTest
class PmApiTest {

	@Test
	void test() {
		PmApi pm = new PmApi();
		
		List<Station> stations = pm.listStationsBySido("서울");
//		System.out.println(stations);
		for (Station s : stations) {
			System.out.println(s);
		}
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
