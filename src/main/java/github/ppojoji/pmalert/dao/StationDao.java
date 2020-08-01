package github.ppojoji.pmalert.dao;

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
}
