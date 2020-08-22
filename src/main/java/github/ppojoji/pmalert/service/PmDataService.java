package github.ppojoji.pmalert.service;

import java.util.List;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import github.ppojoji.pmalert.dao.PmDataDao;
import github.ppojoji.pmalert.dao.StationDao;
import github.ppojoji.pmalert.dto.PmData;
import github.ppojoji.pmalert.dto.Station;
import github.ppojoji.pmalert.service.pmapi.PmApi;

@Service
public class PmDataService {
	
	@Autowired
	PmApi pm;
	
	@Autowired
	PmDataDao pmDataDao;
	/**
	 * 매 시간마다 미세먼지 정보를 읽어들임
	 * 
	 */
	public void loadHourlyPmData() {
		Scanner sc = new Scanner(System.in);
		String [] sido = "서울,부산,대구,인천,광주,대전,울산,경기,강원,충북,충남,전북,전남,경북,경남,제주,세종".split(",");
//		String [] sido = "서울,강원,충북,충남,전북,전남,경북,경남,제주,세종".split(",");
		//List<PmData> pmDataList = pm.queryHourlyPmData("서울");
		
		for (String sidoName : sido) {
			System.out.println(sidoName);
			sc.nextLine(); // 엔터를 쳐야 api 호출되게...
			List<PmData> pmDataList = pm.queryHourlyPmData(sidoName);
			for (PmData pmd : pmDataList) {
				pmDataDao.insertPmDatas(pmd);
			}
		}
	}
	public List<PmData> PmDataByStation(Integer stationSeq) {
		return pmDataDao.PmDataByStation(stationSeq);
		
	}
}
