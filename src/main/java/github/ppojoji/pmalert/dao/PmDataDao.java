package github.ppojoji.pmalert.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import github.ppojoji.pmalert.dto.PmData;
import github.ppojoji.pmalert.dto.Station;
import github.ppojoji.pmalert.dto.StationBookmark;
import github.ppojoji.pmalert.dto.User;

@Repository
public class PmDataDao {
	
	@Autowired 
	SqlSession session;
	
	public void insertPmDatas(PmData pmData) {
		session.insert("PmDataMapper.insertPmData", pmData);
	}

	public List<PmData> PmDataByStation(Integer stationSeq) {
		// TODO 지금은 쌓인데이터를 다 넘기는데 나중에는 1일치, 일주일치 등으로 기간을 명시하는게 필요할 수 있음
		return session.selectList("PmDataMapper.PmDataByStation", stationSeq);
	}
	/**
	 * 관측소별 최근 데이터 1건씩을 가져옴
	 * @param sido 특정 지역의 관측소, null이면 전국 관측소 전부다 조회함
	 * @return
	 */
	public List<PmData> findRecentPmList(String sido) {
		Map<String, Object> param = new HashMap<>();
		param.put("sido", sido);
		return session.selectList("PmDataMapper.findRecentPmList",param);
	}
	/**
	 * 주어진 관측소의 최근 데이터 1건을 가져옴
	 * @param stationSeq
	 * @return
	 */
	public PmData findRecentPmByStation(Integer stationSeq){
		return session.selectOne("PmDataMapper.findRecentPmByStation", stationSeq);
	}

	public List<StationBookmark> findUsersByBookMarkStation(Integer stationSeq) {
		List<Map<String, Object>> res = session.selectList("PmDataMapper.findByBookMarkUser", stationSeq);
		List<StationBookmark> bookmarks = new ArrayList<StationBookmark>();
		for (Map<String,Object> map : res) {
			// {password=1234, pm25=40, station=3342, pm100=30, user=1, notify=Y, seq=1, email=adm}
			Integer userSeq = (Integer) map.get("seq");
			String email = (String) map.get("email");
			User user = new User();
			user.setSeq(userSeq);
			user.setEmail(email);
//			map.put("user", user);
			long pm25 = (Long) map.get("pm25");
			long pm100 = (Long) map.get("pm100");
			bookmarks.add(new StationBookmark(user, pm25*1.0, pm100*1.0, stationSeq));
		}
		return bookmarks;
	}

	public int loadRecentPm(PmData pm) {
		return session.insert("PmDataMapper.loadRecentPm", pm);
	}
	/**
	 * 메일일 작업을 위해서 이전에 모아둔 관측소별 pm 데이터 가져옴
	 * @return
	 */
	public List<PmData> findPrevPmData() {
		return session.selectList("PmDataMapper.findPrevPmData");
	}
}
