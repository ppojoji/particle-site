package github.ppojoji.pmalert.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import github.ppojoji.pmalert.PmException;
import github.ppojoji.pmalert.dao.StationDao;
import github.ppojoji.pmalert.dto.Station;
import github.ppojoji.pmalert.dto.User;
import github.ppojoji.pmalert.service.pmapi.PmApi;

@Service
public class StationService {

	@Autowired
	PmApi pm;
	
	@Autowired
	StationDao stationDao;
	
	
	/**
	 * 모든 관측소를 읽어들여서 데이터베이스를 업데이트 합니다.
	 * 1) 새로운 관측소가 생길 수 있음
	 * 2) 관측소가 없어지는 경우(?)가 있는지는 모르겠음
	 */
	public void synchronizeAllStations() {
		List<Station> stations = pm.findAllStation();
		for (Station station : stations) {
			/*
			 * 실제로 이렇게 단순하지 않음
			 * 
			 *  1) 관측소가 없는거면 새로 넣음
			 *  2) 있으면 업데이트 됐을 수 있기때문에 update 가 되어야 함
			 *  3) 관측소가 없어짐!!!!
			 */
			// System.out.println(station);
			stationDao.insertStation(station);
		}
	}
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
	/**
	 * 주어진 시도의 관측소 정보를 가져옴(현재 최신 미세먼지 데이터를 같이 받아옴)
	 * @param sido
	 * @return
	 */
	public Object findStationsWithRecentPm(String sido) {
		return null;
	}
	/**
	 * 주어진 사용자가 관측소를 북마크함. 만일 북마크가 이미 존재하면 북마크를 제거함
	 * @param userSeq - 사용자 seq 
	 * @param stationSeq - 관측소 seq
	 * @return
	 */
	public boolean toggleBookmark(Integer userSeq, Integer stationSeq) {
		// 북마크가 있으면 없앰
		// 북마크 없으면 추가함
		// true(북마크 추가했다) , false(북마크 없앴다)
		
		Map<String, Object> bookMark = stationDao.findBookmark(userSeq, stationSeq);
		System.out.println(bookMark);
		if (bookMark == null) {
			stationDao.insertBookmark(userSeq, stationSeq);
			return true;
		} else {
			stationDao.deleteBookmark(userSeq, stationSeq);
			return false;
		}
		// return false;
	}

	public boolean isBookMarked(Integer userSeq, Integer stationSeq) {
		Map<String, Object> bookMark = stationDao.findBookmark(userSeq, stationSeq);
//		if(bookMark != null) {
//			return true;
//		}else {
//			return false;
//		}
		return bookMark != null;
	}

	public User updatePmData(Integer userSeq, Integer stationSeq, String pmType, Integer pmValue) {
		if (pmValue == null ) {
			throw new PmException(400, "NULL_PM_VALUE");
		}
		if (pmValue < 0) {
			throw new PmException(400, "NEGATIVE_PM_VALUE");
		}
		return stationDao.updatePmData(userSeq,stationSeq,pmType,pmValue);
	}

	public Map<String, Object> findNotification(Integer seq, Integer stationSeq) {
		return stationDao.findNotification(seq,stationSeq);
	}
	/**
	 * 주어진 사용자가 북마크한 관측소 조회
	 * @param userSeq
	 * @return
	 */
	public List<Map<String,Object>> findBookMarkByUser(Integer userSeq) {
		return stationDao.findBookMarkByUser(userSeq);
	}

	public Boolean DeleteBookMark(Integer userSeq, Integer stationSeq) {
		return stationDao.DeleteBookMark(userSeq,stationSeq);
	}

	public void UpdateNotify(Integer UserSeq, Integer stationSeq, Boolean notify) {
		stationDao.UpdateNotify(UserSeq,stationSeq,notify);
		
	}
	
}
