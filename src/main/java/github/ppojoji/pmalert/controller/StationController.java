package github.ppojoji.pmalert.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import github.ppojoji.pmalert.dto.PmData;
import github.ppojoji.pmalert.dto.Station;
import github.ppojoji.pmalert.service.PmDataService;
import github.ppojoji.pmalert.service.StationService;

@Controller
public class StationController {
	
	@Autowired
	StationService stationService;
	@Autowired 
	PmDataService pmDataService;
	
	/**
	 * 특정 시도의 관측소 목록을 반환합니다.
	 * 제주 => [station, station, station....]
	 * 
	 * @param sido
	 * @param stationSeq
	 * @return
	 */
	@GetMapping(value = "/stations")
	@ResponseBody 
	public Object ListStations(@RequestParam String sido) {
		
		System.out.println("SIDO: " + sido); // 인코딩이 깨졌는지 확인해야함
		List<Station> list = stationService.findStationsBySido(sido);
		/*
		 * FIXME 굉장히 비효율적인 구현입니다.
		 * 쿼리를 join해서 구현해야 함
		 * 
		 * 
		 */
		for (int i = 0; i < list.size(); i++) {
			Station station = list.get(i);
			List<PmData> pmValues = pmDataService.PmDataByStation(station.getSeq());
			station.setPmData(pmValues);
			
		}
		return list;
	}

}
