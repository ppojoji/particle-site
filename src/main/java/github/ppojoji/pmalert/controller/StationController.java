package github.ppojoji.pmalert.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import github.ppojoji.pmalert.PmException;
import github.ppojoji.pmalert.Res;
import github.ppojoji.pmalert.dto.PmData;
import github.ppojoji.pmalert.dto.Station;
import github.ppojoji.pmalert.dto.User;
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
		 * N번 루프를 반복함
		 */
		/*
		for (int i = 0; i < list.size(); i++) {
			Station station = list.get(i);
			List<PmData> pmValues = pmDataService.PmDataByStation(station.getSeq());
			station.setPmData(pmValues);
			
		}
		*/
		return list;
	}
	/**
	 * 북마크 추가
	 * @param stationSeq
	 * @param session
	 * @return
	 */
	@PostMapping(value = "/station/bookmark")
	@ResponseBody
	public Object BookMark(@RequestParam Integer stationSeq , HttpSession session) {
		User user = (User)session.getAttribute("LOGIN_USER");
		// FIXME 이렇게 로그인 확인을 사방팔방에서 다 하면 안좋음(인터셉터를 달아서 로그인 확인 로직을 한군데로 몰아넣음)
		if (user == null) {
			throw new PmException(401, "LOGIN_REQUIRED");
		}
		boolean bookmarked = stationService.toggleBookmark(user.getSeq(),stationSeq);
		return Res.success("bookmarked", bookmarked);
	}
	/**
	 * 사용자 북마크 조회
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "/station/bookmarks" ,method = RequestMethod.GET)
	@ResponseBody
	public Object userBookMark(HttpSession session) {
		User user = (User) session.getAttribute("LOGIN_USER");
		
		List<Map<String,Object>> stationList = stationService.findBookMarkByUser(user.getSeq());
		return stationList;
	}
	
	/**
	 * 북마크 삭제
	 * @param session
	 * @param stationSeq
	 * @param pmType
	 * @param pmValue
	 * @return
	 */
	@DeleteMapping("/station/bookmark/{stationSeq}")
	@ResponseBody
	public Object deleteBookMark(HttpSession session ,@PathVariable Integer stationSeq) {
		User user = (User)session.getAttribute("LOGIN_USER");
		Boolean deleted = stationService.DeleteBookMark(user.getSeq(), stationSeq);
		// return deleted;
		return Res.success("success", deleted);
	}
	
	@PostMapping(value = "/station/notification")
	@ResponseBody
	public Object notification(HttpSession session , 
			@RequestParam Integer stationSeq, 
			@RequestParam String pmType , 
			@RequestParam Integer pmValue) {
		User user = (User) session.getAttribute("LOGIN_USER");
		stationService.updatePmData(user.getSeq(),stationSeq,pmType,pmValue);
		return Res.success();
	}
	@RequestMapping(value = "/station/StationSeqDel" , method=RequestMethod.POST)
	@ResponseBody
	public Object DeleteBookMark(@RequestParam Integer StationSeq,HttpSession session) {
		User user = (User)session.getAttribute("LOGIN_USER");
		Boolean Deleted = stationService.DeleteBookMark(user.getSeq() ,StationSeq);
		return Deleted;
	}
	@RequestMapping(value = "/station/UpdateNotify", method = RequestMethod.POST)
	@ResponseBody
	public Object UpdateNotify(
			@RequestParam Integer StationSeq,
			@RequestParam Boolean notify, // false
			HttpSession session) {
		User user = (User)session.getAttribute("LOGIN_USER");
		stationService.UpdateNotify(user.getSeq(),StationSeq, notify);
		
		return notify; 
	}
	
	
	@RequestMapping(value = "/station/stationDetail/{seq}")
	@ResponseBody
	public Object stationDetail(@PathVariable Integer seq) {
		List<Map<String,Object>> pmData = stationService.stationDetail(seq);
		
		Station station = stationService.findBySeq(seq);
		
		// Res.succ
//		Map<String, Object> res = new HashMap<>();
//		res.put("station", statoin);
//		res.put("data", pmData);
		return Res.success("station", station, "data", pmData);
//		return pmData;
	}
}
