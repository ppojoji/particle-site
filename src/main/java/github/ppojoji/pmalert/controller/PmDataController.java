package github.ppojoji.pmalert.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import github.ppojoji.pmalert.dto.PmData;
import github.ppojoji.pmalert.dto.Station;
import github.ppojoji.pmalert.dto.StationBookmark;
import github.ppojoji.pmalert.dto.User;
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
	public Object PmDataByStation(@PathVariable Integer stationSeq , HttpSession session) {
		Map<String, Object> res = new HashMap<String, Object>();
		/**
		 * FIXME - 현재 관측소의 모든 데이터를 전부 읽어들임
		 *       - 그런데 화면에서는 최근 데이터 1건만 사용해서 INFOWIN에 렌더링함
		 *       - 최근 데이터 1건만 보내면 됨
		 *       
		 *      PmDataByStation(stationSeq, 1);
		 */
		User user = (User)session.getAttribute("LOGIN_USER"); // user == null
		/*
		 * 로그인이 안되어 있으면
		 * 북마크 여부도 판별할 수 없음
		 */
		boolean bookMarked = false;
		if (user != null) {
			// StationBookmark bmk = stationService.findBookmark(user.getSeq(), stationSeq);
			Integer userSeq = user.getSeq(); // null.getSeq() Null Pointer Exception
			bookMarked = stationService.isBookMarked(userSeq, stationSeq);
			Map<String, Object> notifData = stationService.findNotification(user.getSeq(), stationSeq);
			System.out.println("[notif data]" + notifData);
			res.put("pm25", notifData == null ? 0 : notifData.get("pm25"));
			res.put("pm100",notifData == null ? 0 : notifData.get("pm100"));
		} else {
			res.put("pm25", 0);
			res.put("pm100", 0);
		}
		
		List<PmData> list = pmDataService.PmDataByStation(stationSeq);
		Station station = stationService.findBySeq(stationSeq);
		res.put("station", station);
		res.put("pmdata", list);
//		res.put("pm25",0);
//		res.put("pm100",0);
		res.put("success", true);
		
		res.put("bookmarked", bookMarked); // 일단 false로 해둠
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
	@RequestMapping(value = "/pm/findRecentPmList")
	@ResponseBody
	public Object findRecentPmList(@RequestParam String sido){
		
//		List<Station> stations = stationService.findStationsBySido(sido);
//		List<PmData> pm = pmDataService.findRecentPmList(sido);
//		
//		for(int i =0; i<stations.size(); i++) {
//			Station station = stations.get(i);
//			for(int j=0; j<pm.size(); j++) {
//				PmData pmData = pm.get(j);
//				if(pmData != null && 
//						pmData.getStation().equals(station.getSeq())) {
////					station.setPmData(Arrays.asList(pmData))
//					station.setPmData(pmData);
//				}
//			}
//		}
		List<Station> stations = pmDataService.findRecentPmForStation(sido);
		return stations;
	}
}
