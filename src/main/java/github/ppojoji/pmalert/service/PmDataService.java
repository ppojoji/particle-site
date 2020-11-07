package github.ppojoji.pmalert.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import github.ppojoji.pmalert.dao.PmDataDao;
import github.ppojoji.pmalert.dao.StationDao;
import github.ppojoji.pmalert.dto.PmData;
import github.ppojoji.pmalert.dto.Station;
import github.ppojoji.pmalert.dto.StationBookmark;
import github.ppojoji.pmalert.service.pmapi.PmApi;

@Service
public class PmDataService {
	final static Logger logger = LoggerFactory.getLogger(PmDataService.class);
	
	@Autowired
	PmApi pm;
	
	@Autowired
	PmDataDao pmDataDao;
	
	@Autowired
	StationDao stationDao;
	
	/**
	 * 매 시간마다 미세먼지 정보를 읽어들임
	 * 
	 */
	public void loadHourlyPmData() {
		// Scanner sc = new Scanner(System.in);
		String [] sido = "서울,부산,대구,인천,광주,대전,울산,경기,강원,충북,충남,전북,전남,경북,경남,제주,세종".split(",");
		/*
		 * 현재 관측소별 최근 데이터를 모두 가져옴
		 */
		List<PmData> recentPmList = pmDataDao.findRecentPmList((String)null);
		// recentPmList.get(0).getStation() // stationSeq
		for (String sidoName : sido) {
			System.out.println(sidoName);
			List<PmData> pmDataList = pm.queryHourlyPmData(sidoName); //11:00:00 ~  12:00:00 
			for (PmData pmd : pmDataList) {
				Integer stationSeq = pmd.getStation();
				// if(recentPmList.indexOf(pmData))
				for (PmData recent : recentPmList) {
					if (recent.getStation().equals(stationSeq)) {
						LocalDateTime recentTime = recent.getTime(); //11:00:00
						LocalDateTime nowTime = pmd.getTime(); // 11:00:00
						if(recentTime.isBefore(nowTime)) {
							
							pmDataDao.insertPmDatas(pmd);
							
							// TODO 사용자에게 미세먼지 통보하는 작업을 여기서 해야함
							
							// TODO 메일 전송 인프라 필요함
							/* 
							 *  1. 이전의 최근 미세먼지 데이터 수치 prev.pm25
							 *  2. 새로 읽어들인 미세먼지 데이터 수치 cur.pm25
							 *  
							 *  CASE-1) 메일을 보내는 케이스 미세먼지가 올라갔다
							 *  prev.pm25 <   boookmark.pm25   < cur.pm25
							 *      27          60                     89
							 *      
							 *      
							 *  CASE-2) 메일을 안보내는 케이스
							 *  prev.pm25 <   cur.pm25   < boookmark.pm25
							 *      27          48                60
							 *      
							 *      
							 * CASE-3) 메일을 안보내는 케이스
							 *  boookmark.pm25  < prev.pm25 <   cur.pm25 
							 *      60             74              88
							 *      
							 *  CASE-4) 메일을 안보내는 케이스
							 *  boookmark.pm25  < cur.pm25 <   prev.pm25 
							 *      60             74              88
							 *      
							 *      
							 *  CASE-4) 메일을 보내는 케이스 - 미세먼지가 내려갔다...
							 *   cur.pm25 > bookmark.pm25   >  prev.pm25 
							 *      89             60              51
							 * 
							 */
							List<StationBookmark> bookmarks = pmDataDao.findUsersByBookMarkStation(stationSeq);
							
							logger.info("[HOURLY PM DATA] {}", pmd.toString() );
						} else {
							logger.info("[HOURLY PM DATA] SKIPPED");
						}
						
					}
				}
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public List<PmData> findRecentPmList(String sido) {
		List<PmData> pm = pmDataDao.findRecentPmList(sido);
		return pm; 
	}
	public List<Station> findRecentPmForStation(String sido) {
		List<Station> stations = stationDao.findStationsBySido(sido);
		List<PmData> pm = findRecentPmList(sido);
		
		for(int i =0; i<stations.size(); i++) {
			Station station = stations.get(i);
			for(int j=0; j<pm.size(); j++) {
				PmData pmData = pm.get(j);
				if(pmData != null && 
						pmData.getStation().equals(station.getSeq())) {
//					station.setPmData(Arrays.asList(pmData))
					station.setPmData(pmData);
				}
			}
		}
		return stations;
	}
	
	public void loadHourlyData(Station station, PmData pmd, List<PmData> recentPmList) {
		Integer stationSeq = pmd.getStation();
		// if(recentPmList.indexOf(pmData))
		for (PmData recent : recentPmList) {
			if (recent.getStation().equals(stationSeq)) {
				LocalDateTime recentTime = recent.getTime(); //11:00:00
				LocalDateTime nowTime = pmd.getTime(); // 11:00:00
				if(recentTime.isBefore(nowTime)) {
					pmDataDao.insertPmDatas(pmd);
					logger.info("[HOURLY PM DATA] {}", pmd.toString() );
				} else {
					logger.info("[HOURLY PM DATA] SKIPPED");
				}
				
			}
		}
	}
	/**
	 * 모든 관측소의 측정 정보를 갱신합니다.
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public void synchronizePmData() {
		List<Station> stations = stationDao.findAllStations();
		
		int idx = 0;
		for (Station station : stations) {
			if (station.getSeq() < 3702) {
				// 음성읍
				idx++;
				continue;
			}
			System.out.printf("[%d of %d] %s \n", idx++, stations.size(), station.getStation_name());
			loadPmDataByStation(station);
			try {
				Thread.sleep(1500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * 주어진 관측소 24시간 데이터를 새로 로드함
	 * @param station
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void loadPmDataByStation(Station station) {
		List<PmData> pmList = pm.queryPmDataByStation(station); // 24개..?
		PmData recentPm = pmDataDao.findRecentPmByStation(station.getSeq()); // null
		LocalDateTime rencentTime = LocalDateTime.MIN;
		if(recentPm != null) {
			// recentPm.getTime();
			rencentTime = recentPm.getTime(); // 디비에 들어가 있는 데이터의 최신 시간
		}
		
		for (PmData pm : pmList) {
			try {
				LocalDateTime pmTime = pm.getTime();
				if( pmTime.isAfter(rencentTime) ) {
					pmDataDao.insertPmDatas(pm);
					System.out.println(" INSERT " + pm);
				} else {
					// TODO 이미 이전에 읽어온 데이터라도 NULL인 경우는 지금 새로 읽어온 데이터의 값으로 메꿔 넣어야 함 					
				}
			} catch (Exception e) {
				// 일단은 이렇게 예외를 막아줌
				e.printStackTrace();
			}
			
		}
		
	}
	public List<PmData> PmDataByStation(Integer stationSeq) {
		return pmDataDao.PmDataByStation(stationSeq);
		
	}
	public List<Map<String, Object>> loadUserBookmark() {
		return stationDao.loadUserBookmark();
	}
}

