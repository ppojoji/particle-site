package github.ppojoji.pmalert.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import github.ppojoji.pmalert.dto.PmData;
import github.ppojoji.pmalert.dto.Station;

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
	 * @return
	 */
	public List<PmData> findRecentPmList() {
		return session.selectList("PmDataMapper.findRecentPmList");
	}
}
