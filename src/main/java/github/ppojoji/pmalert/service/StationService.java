package github.ppojoji.pmalert.service;

import java.util.List;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import github.ppojoji.pmalert.dao.StationDao;
import github.ppojoji.pmalert.dto.Station;
import github.ppojoji.pmalert.service.pmapi.PmApi;

@Service
public class StationService {

	@Autowired
	PmApi pm;
	
	@Autowired
	StationDao stationDao;
	
	/**
	 * 시도별 관측소를 조회해서 디비를 업데이트 합니다.
	 * 
	 * 1) 관측소가 새로 생김
	 * 2) 기존 관측소가 없어짐(이전)
	 * 
	 */
	public void synchronizedStation() {
		Scanner sc = new Scanner(System.in);
//		String [] sido = "대구,인천,광주,대전,울산,경기,강원,충북,충남,전북,전남,경북,경남,제주,세종".split(",");
		String [] sido = "경상북도".split(",");
		for (String sidoName : sido) {
			System.out.println(sidoName);
			sc.hasNextLine(); // 엔터를 쳐야 api 호출되게...
			List<Station> stations = pm.listStationsBySido(sidoName);
			for (Station station : stations) {
				/*
				 * 실제로 이렇게 단순하지 않음
				 * 
				 *  1) 관측소가 없는거면 새로 넣음
				 *  2) 있으면 업데이트 됐을 수 있기때문에 update 가 되어야 함
				 *  3) 관측소가 없어짐!!!!
				 */
				stationDao.insertStation(station);
			}
			
		}
	}
}
