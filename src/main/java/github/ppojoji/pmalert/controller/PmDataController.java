package github.ppojoji.pmalert.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import github.ppojoji.pmalert.dto.PmData;
import github.ppojoji.pmalert.dto.Station;
import github.ppojoji.pmalert.service.PmDataService;
import github.ppojoji.pmalert.service.StationService;
	
@Controller
public class PmDataController {
	@Autowired
	PmDataService pmDataService;
	
	@Autowired
	StationService stationService;
	
	final String [] sidoNames = "서울,부산,대구,인천,광주,대전,울산,경기,강원,충북,충남,전북,전남,경북,경남,제주,세종".split(",");
	
	@GetMapping(value = "/pm/{stationSeq}")
	@ResponseBody
	public Object PmDataByStation(@PathVariable Integer stationSeq) {
		Map<String, Object> res = new HashMap<String, Object>();
		/**
		 * FIXME - 현재 관측소의 모든 데이터를 전부 읽어들임
		 *       - 그런데 화면에서는 최근 데이터 1건만 사용해서 INFOWIN에 렌더링함
		 *       - 최근 데이터 1건만 보내면 됨
		 *       
		 *      PmDataByStation(stationSeq, 1);
		 */
		/**
		 * FIXME - 주어진 관측소가 북마크되어있는지를 나타내는 값을 추가해줘야 합니다.
		 */
		List<PmData> list = pmDataService.PmDataByStation(stationSeq);
		Station station = stationService.findBySeq(stationSeq);
		res.put("station", station);
		res.put("pmdata", list);
		res.put("success", true);
		res.put("bookmared", false); // 일단 false로 해둠
		return res; 
	}
	@GetMapping(value = "/pm/loadSido/{Seq}")
	@ResponseBody
	public Object LoadSido(@PathVariable Integer Seq) {
		Station station = stationService.findBySeq(Seq);
		String sido = station.getSido();
		List<Station> stations = stationService.findStationsBySido(sido);
		Map<String, Object> res = new HashMap<String, Object>();
		res.put("stations", stations);
		res.put("sido", sidoNames);
		return res;
		 
	}
}
