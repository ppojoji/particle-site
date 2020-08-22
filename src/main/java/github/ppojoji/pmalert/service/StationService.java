package github.ppojoji.pmalert.service;

import java.util.ArrayList;
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
		String [] sido = "서울,대구,인천,광주,대전,울산,경기,강원,충북/충청북도,충남/충청남도,전북/전라북도,전남/전라남도,경북/경상북도,경남/경상남도,제주,세종".split(",");
//		String [][] sidoNames = {
//				{"서울"},
//				{"부산"},
//				{"전북,전라북도"}
//		};
//		String [] sido = "충청북도/충북".split(",");
		for (String sidoName : sido) {
			String [] names = sidoName.split("/");
			//
			List<Station> stations = new ArrayList<>();
			for(String name : names) {
				
				System.out.println(name + ", " + sidoName);
				sc.nextLine(); // 엔터를 쳐야 api 호출되게...
				List<Station> station = pm.listStationsBySido(name);
				stations.addAll(station);
			}
			for (Station station : stations) {
				/*
				 * 실제로 이렇게 단순하지 않음
				 * 
				 *  1) 관측소가 없는거면 새로 넣음
				 *  2) 있으면 업데이트 됐을 수 있기때문에 update 가 되어야 함
				 *  3) 관측소가 없어짐!!!!
				 */
				System.out.println(station);
				stationDao.insertStation(station);
			}
			
		}
	}

	public Station findBySeq(Integer stationSeq) {
		return stationDao.findBySeq(stationSeq);
	}

	public List<Station> findStationsBySido(String sido) {
		return stationDao.findStationsBySido(sido);
	}
}
