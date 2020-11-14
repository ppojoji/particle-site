package github.ppojoji.pmalert.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import github.ppojoji.pmalert.dao.PmDataDao;
import github.ppojoji.pmalert.dao.StationDao;
import github.ppojoji.pmalert.dao.UserDao;
import github.ppojoji.pmalert.dto.Mail;
import github.ppojoji.pmalert.dto.PmData;
import github.ppojoji.pmalert.dto.Station;
import github.ppojoji.pmalert.dto.User;
import github.ppojoji.pmalert.service.pmapi.PmApi;

@Service
public class PmNotificationService {

	@Autowired
	PmDataDao pmDao;
	
	@Autowired
	PmDataService pmService;
	
	@Autowired
	StationDao stationDao;
	
	@Autowired
	PmApi api;
	
	@Autowired
	MailingService mailService;
	
	@Autowired
	UserDao userDao;
	
	@Scheduled(cron = "0 0 * * * *")
	public void prepareJob0_1_2() {
		System.out.println("[메일링 사전 작업]");
		clearNotificationData();
		loadRecentPm();
		loadUserBookmark();
		
	}
	/**
	 * Job3 - 최신 데이터 로드해서 메일 전송 및 업데이트
	 * 5분마다 5/10/15/20/25/30
	 */
	@Scheduled(cron = "30 5,10,15,20,21,22,23,24,25 * * * *")
	public void startJob3() {
		System.out.println("[START NOTIFICATION]");
		startNotification();
		System.out.println("[START NOTIFICATION]");
	}
	
	/**
	 * 이전 작업에서 남은 데이터 정리
	 * 
	 */
	public void clearNotificationData() {
		pmDao.clearNotificationData();
	}
	public Object loadRecentPm() {
		List<PmData> pmList = pmService.findRecentPmList(null);
		for( PmData pm:pmList) {
			pmDao.loadRecentPm(pm);
		}
		return null;
	}
	/**
	 * 사용자의 관측소 북마크를 로드함
	 * 
	 * @param userSeq
	 * @param stationSeq
	 */
	public void loadUserBookmark() {
		List<Map<String, Object>> mailingList = pmService.loadUserBookmark();
		System.out.println(mailingList);
		
	}
	/**
	 * 최신 pm 데이터 로드 후 메일을 보냄 ( 5/10/15/20...)
	 */
	public void startNotification() {
		// 1. api call => 관측소별 `
		// String [] sidoList = "경기,서울,부산,대구,인천,광주,대전,울산,강원,충북,충남,전북,전남,경북,경남,제주,세종".split(",");
		String [] sidoList = "경기,서울".split(",");
		List<PmData> currentPmData = new ArrayList<>(); // [{station:13223}, {], {], {station: 322}]
		for (String sido : sidoList) {
			List<PmData> sidoPmData = api.queryHourlyPmData(sido);
			System.out.println("[SIDO] " + sido);
			currentPmData.addAll(sidoPmData);
			try {
				Thread.sleep(2*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		List<PmData> prevPmData = pmDao.findPrevPmData();
		resolveMailingList(prevPmData, currentPmData);
		
	}
	
	public void resolveMailingList(List<PmData> prevPmData, List<PmData> currentPmData) {
		List<Map<String, Object>> mailing = stationDao.findPmMailingList();
		for (Map<String, Object> boookmark : mailing) { // 1000*3 = 3000
			Integer userSeq = (Integer) boookmark.get("user");
			Integer pm25 = (Integer) boookmark.get("pm25");
			Integer pm100 = (Integer) boookmark.get("pm100");
			Integer stationSeq = (Integer) boookmark.get("station");
			
			PmData prev = null;
			// FIXME 이렇게 하면 시간이 많이 걸립니다. 5만개*550 = 2750만번 반복함
			for (PmData data : prevPmData) { // 550
				if (stationSeq.intValue() == data.getStation().intValue()) {
					prev = data;
					break;
				}
			}
			PmData current = null;
			// FIXME 이렇게 하면 시간이 많이 걸립니다. 5만개*550 = 2750만번 반복함
			for (PmData data : currentPmData) { // 550
				if (stationSeq.intValue() == data.getStation().intValue()) {
					current = data;
					break;
				}
			}
			// pmData 는 null이 아니어야 함!!!
			Double prevPm25 = prev.getPm25(0.0);
			Double prevPm100 = prev.getPm100(0.0);
			Double curPm25 =current.getPm25(0.0);
			Double curPm100= current.getPm100(0.0);
			Mail mail = new Mail();
			
			String template = "종로구 @time시 미세먼지  PM10: @pm100, PM2.5: @pm25";
			if(prevPm25 <= pm25.doubleValue() && 
				pm25.doubleValue() <= curPm25 ||
				prevPm100 <= pm100.doubleValue() && 
				pm100.doubleValue() <= curPm100) {
				// TODO 메일 전송 작업을 해야함
				System.out.println("[메일 전송] 올라감: " + boookmark);
				
				Station station = stationDao.findBySeq(stationSeq);
				
				mail.setSubject("[" + station.getStation_name() +"] 미세먼지 상승 알림");
				String content = template
						.replace("@time", "12")
						.replace("@pm100", curPm100.toString())
						.replace("@pm25", curPm25.toString());
				mail.setContent(content);
				
				User user = new User();
				user = userDao.findBySeq(userSeq);
				mail.setReceiver(user.getEmail());
				
				mailService.SendMail(mail);
				
				stationDao.updateMailingSent(userSeq,stationSeq);
				
				// TODO 메일을 보낸 후에 pm_mailing.sent = 'Y'로 변경해야합니다.
			}
			// TODO 미세먼지가 떨어지는 경우도 위와같이 처리를 해야함
			// prev25 >=  pm25   => cur25
			//  50         40        33        60 
			else if(pm25 >= curPm25 && prevPm25 >= pm25) {
				System.out.println("[메일 전송] 내려감: " + boookmark);

				Station station = stationDao.findBySeq(stationSeq);
				
				mail.setSubject("[" + station.getStation_name() +"] 미세먼지 하강 알림");
				String content = template
						.replace("@time", "12")
						.replace("@pm100", curPm100.toString())
						.replace("@pm25", curPm25.toString());
				mail.setContent(content);
				
				User user = new User();
				user = userDao.findBySeq(userSeq);
				mail.setReceiver(user.getEmail());
				
				mailService.SendMail(mail);

			}
			
			
		}
		// 2.  메일 전송 인프라 필요함
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
	}
}
