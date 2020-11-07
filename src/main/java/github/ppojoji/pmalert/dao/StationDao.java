package github.ppojoji.pmalert.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import github.ppojoji.pmalert.PmException;
import github.ppojoji.pmalert.Res;
import github.ppojoji.pmalert.dto.Station;
import github.ppojoji.pmalert.dto.StationBookmark;
import github.ppojoji.pmalert.dto.User;

@Repository
public class StationDao {

	@Autowired SqlSession session;
	
	public void insertStation(Station station) {
		session.insert("StationMapper.insertStation", station);
	}

	public Station findStationByName(String name,String sido) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("name", name);
		map.put("sido", sido);
		
		return session.selectOne("StationMapper.findStationByName", map);
	}

	public Station findBySeq(Integer stationSeq) {
		return session.selectOne("StationMapper.findBySeq", stationSeq);
	}

	public List<Station> findStationsBySido(String sido) {
		return session.selectList("StationMapper.findStationsBySido", sido);
	}

	public List<Station> findAllStations() {
		return session.selectList("StationMapper.findStationsBySido");
	}

	public Map<String, Object> findBookmark(Integer userSeq, Integer stationSeq) {
		return session.selectOne("StationMapper.findBookmark", Res.success("userSeq" , userSeq , "stationSeq",stationSeq));
	}

	public void insertBookmark(Integer userSeq, Integer stationSeq) {
		session.insert("StationMapper.insertBookmark", Res.success("userSeq" , userSeq , "stationSeq",stationSeq));
	}

	public void deleteBookmark(Integer userSeq, Integer stationSeq) {
		session.delete("StationMapper.deleteBookmark", Res.success("userSeq" , userSeq , "stationSeq",stationSeq));
		
	}

	public User updatePmData(Integer userSeq, Integer stationSeq, String pmType, Integer pmValue) {
		
		String pmColumn = checkPmColumn(pmType);
		System.out.printf("user: %d, station: %d, type: %s, value: %d\n", userSeq, stationSeq, pmType, pmValue);
		session.update("StationMapper.updatePmData" ,Res.success(
			"userSeq",userSeq,
			"stationSeq",stationSeq,
			"pmType",pmColumn,
			"pmValue",pmValue)
		);
		
		return null;
	}

	private String checkPmColumn(String pmType) {
		if (pmType.equals("pm100") || pmType.equals("pm25")) {
			return pmType;
		} else {
			throw new PmException(400, "PM_TYPE_ERROR");
		}
	}

	public Map<String, Object> findNotification(Integer seq, Integer stationSeq) {
		return session.selectOne("StationMapper.findNotification", Res.success("seq",seq , "stationSeq",stationSeq));
	}

	public List<Map<String, Object>> findBookMarkByUser(Integer userSeq) {
		return session.selectList("StationMapper.findBookMarkByUser", userSeq);
	}

	public Boolean DeleteBookMark(Integer userSeq, Integer stationSeq) {
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("userSeq", userSeq);
		map.put("stationSeq", stationSeq);
		int cnt = session.delete("StationMapper.DeleteBookMark", map);
		
		return cnt == 1;
	}

	public void UpdateNotify(Integer userSeq, Integer stationSeq, Boolean notify) {
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("userSeq", userSeq);
		map.put("stationSeq", stationSeq);
		map.put("notify",notify ? "Y" : "N");
		
		session.update("StationMapper.UpdateNotify",map);
		
	}

	public List<Map<String, Object>> loadUserBookmark() {
		session.insert("StationMapper.loadUserBookmark");
		return session.selectList("StationMapper.findPmMailingList"); 
	}
	/**
	 * 메일 통보받을 사용자북마크 정보 가져옴
	 * @return
	 */
	public List<Map<String, Object>> findPmMailingList() {
		return session.selectList("StationMapper.findPmMailingList");
	}

}
