package github.ppojoji.pmalert.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

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
		List<PmData> recentPmList = pmDataDao.findRecentPmList(null);
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
		for (Station station : stations) {
			loadPmDataByStation(station);
			try {
				Thread.sleep(2000);
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
		for (PmData pm : pmList) {
			/*
			 * TODO 무조건 밀어넣으면 안됩니다.
			 * 3700 [ ..   10, 11, 12]
			 * 
			 * 3700: [ 09, 10,11, 12, 13, 14... 21]
			 *          x   x  x   x
			 */
			try {
				pmDataDao.insertPmDatas(pm);				
			} catch (Exception e) {
				// 일단은 이렇게 예외를 막아줌
				e.printStackTrace();
			}
			
		}
		
	}
	public List<PmData> PmDataByStation(Integer stationSeq) {
		return pmDataDao.PmDataByStation(stationSeq);
		
	}
}
