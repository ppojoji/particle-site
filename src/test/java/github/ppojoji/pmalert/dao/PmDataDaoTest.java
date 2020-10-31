package github.ppojoji.pmalert.dao;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import github.ppojoji.pmalert.dto.PmData;
import github.ppojoji.pmalert.dto.StationBookmark;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({
	PmDataDao.class
})

class PmDataDaoTest {

	@Autowired
	PmDataDao pmDao;
	
	@Test
	void test() {
		/*
		 * java.lang.ClassCastException: 
		 * 
		 *    github.ppojoji.pmalert.dto.PmData 
		 *    
		 *       cannot be cast to 
		 *    
		 *    java.util.List
	at github.ppojoji.pmalert.dao.PmDataDao.findRecentPmList(PmDataDao.java:46)
	at github.ppojoji.pmalert.dao.PmDataDaoTest.test(PmDataDaoTest.java:28)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)

		 */
		PmData pmData = pmDao.findRecentPmByStation(3255);
		System.out.println(pmData);
	}
	
	@Test
	public void test_관측소_북마크한_사용자들() {
		List<StationBookmark> res = pmDao.findUsersByBookMarkStation(3342);
		for (StationBookmark user : res) {
			System.out.println(user);
		}
	}

}
