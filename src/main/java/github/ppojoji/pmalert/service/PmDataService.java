package github.ppojoji.pmalert.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import github.ppojoji.pmalert.dto.PmData;
import github.ppojoji.pmalert.service.pmapi.PmApi;

@Service
public class PmDataService {
	
	@Autowired
	PmApi pm;
	
	/**
	 * 매 시간마다 미세먼지 정보를 읽어들임
	 * 
	 */
	public void loadHourlyPmData() {
		String [] sido = "서울,부산,대구,인천,광주,대전,울산,경기,강원,충북,충남,전북,전남,경북,경남,제주,세종".split(",");
		
		List<PmData> pmDataList = pm.queryHourlyPmData("경북");
		
		for (PmData pm : pmDataList) {
			System.out.println(pm);
		}
	}

}
