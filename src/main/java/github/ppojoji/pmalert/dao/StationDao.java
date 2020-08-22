package github.ppojoji.pmalert.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import github.ppojoji.pmalert.dto.Station;

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
}
