package github.ppojoji.pmalert.service.pmapi;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import github.ppojoji.pmalert.dao.StationDao;
import github.ppojoji.pmalert.dto.PmData;
import github.ppojoji.pmalert.dto.Station;

@Service
public class PmApi {
	@Autowired
	StationDao stationDao;
	
	@Value("${particle.pmdata.apikey}") String apiKey;
	/**
	 * 주어진 시도에 속하는 관측소를 로드합니다.
	 * 
	 * @param sidoName - '서울', '경기' 등 17개 시도 이름
	 * @return 시도별 관측소들
	 */
	final static Logger logger = LoggerFactory.getLogger(PmApi.class);
	
	public List<Station> findAllStation() {
		String urlTemplate = "http://openapi.airkorea.or.kr/openapi/services/rest/MsrstnInfoInqireSvc/getMsrstnList?serviceKey=@apiKey&numOfRows=999&pageNo=1&addr=&stationName=&";
		
		String url = urlTemplate.replace("@apiKey", apiKey);
		
		Connection con = Jsoup.connect(url).ignoreContentType(true);
		try {
			Document doc = con.get();
			System.out.println(doc.toString());
			String selector = "response > body > items > item";
			Elements items = doc.select(selector);
			List<Station> stations = new  ArrayList<>();
			for(int i = 0 ; i < items.size(); i++) {
				Element item = items.get(i);
				String name = item.select("stationName").text();
				String addr = item.select("addr").text();
				Double lat =  asDouble(item.select("dmX").text());
				Double lng =  asDouble(item.select("dmY").text());
				String sidoName = inferSidoName(addr); // 서울,대구,대전
				Station station = new Station(name, addr, lat, lng, sidoName);
				
				stations.add(station);
			}
			return stations;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
	}
	private String inferSidoName(String addr) {
		String sidoName = "서울,부산,대구,인천,광주,대전,울산,경기,강원,충북,충남,전북,전남,경북,경남,제주,세종";
		String sido = addr.substring(0,2); // 전라...		
		if (sidoName.indexOf(sido) >= 0) {
			return sido;
		}
		
		sido = addr.substring(0,4);
		String sidoName4="전라북도,전라남도,경상북도,경상남도,충청북도,충청남도";
		if(sidoName4.indexOf(sido) >= 0) {
			return sido.substring(0, 1) + sido.substring(2,3) ;
		} else {			
			throw new RuntimeException("새로운 시도명 발견: " + addr);
		}
	}
	public List<Station> listStationsBySido(String sidoName) {
		String urlTemplate = "http://openapi.airkorea.or.kr/openapi/services/rest/MsrstnInfoInqireSvc/getMsrstnList?serviceKey=@apiKey&numOfRows=999&pageNo=1&addr=@sido&stationName=&";
		
		String url = urlTemplate
				.replace("@sido", sidoName)
				.replaceAll("@apiKey", apiKey);
		
		Connection con = Jsoup.connect(url).ignoreContentType(true);
		try {
			Document doc = con.get();
			System.out.println(doc.toString());
			String selector = "response > body > items > item";
			Elements items = doc.select(selector);
			List<Station> stations = new  ArrayList<>();
			for(int i = 0 ; i < items.size(); i++) {
				Element item = items.get(i);
				/*
				<item>
		        >>  <stationName>종로</stationName>
		        >>  <addr>서울 종로구 종로 169(종묘주차장 앞)</addr>
		            <year>2008</year>
		            <oper>서울특별시보건환경연구원</oper>
		            <photo>http://www.airkorea.or.kr/airkorea/station_photo/NAMIS/station_images/111125/INSIDE_OTHER_1.bmp</photo>
		            <vrml />
		            <map>http://www.airkorea.or.kr/airkorea/station_map/111125.gif</map>
		            <mangName>도로변대기</mangName>
		            <item>SO2, CO, O3, NO2, PM10, PM2.5</item>
		       >>   <dmX></dmX>
		       >>   <dmY>126.996783</dmY>
		         </item>
				 */
				String name = item.select("stationName").text();
				String addr = item.select("addr").text();
				Double lat =  asDouble(item.select("dmX").text());
				Double lng =  asDouble(item.select("dmY").text());
				Station station = new Station(name, addr, lat, lng, sidoName);
				
				stations.add(station);
			}
			return stations;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
	}

	private Double asDouble(String text) {
		try {
			String coord = text.trim();
			return Double.parseDouble(coord); 			
		} catch(Exception e) {
			return null;
		}
	}
	/**
	 * 주어진 시도(서울, 부산 등)내의 관측소들의 실시간 데이터
	 * @param sidoName
	 * @return
	 */
	public List<PmData> queryHourlyPmData(String sidoName) {
		String urlTemplate = "http://openapi.airkorea.or.kr/openapi/services/rest/ArpltnInforInqireSvc/getCtprvnRltmMesureDnsty?serviceKey=@apiKey&numOfRows=999&pageNo=1&sidoName=@sido&ver=1.3&";
		
		String url = urlTemplate.replace("@sido", sidoName)
				.replace("@apiKey", apiKey);
				;
		
		Connection con = Jsoup.connect(url).timeout(60*1000).ignoreContentType(true);
		try {
			Document doc = con.get();
			// System.out.println(doc.toString());
			String selector = "response > body > items > item";
			Elements items = doc.select(selector);
			List<PmData> dataList = new  ArrayList<>();
			for(int i = 0 ; i < items.size(); i++) {
				Element item = items.get(i);
				
				String name = item.select("stationName").text();
				// System.out.println("[관측소] [" + name + "]");
				Station station = stationDao.findStationByName(name,sidoName);
				if (station == null) {
					// TODO 이렇게 새로운 관측소가 나오면 관측소를 집어넣어야 함
					logger.warn("새로운 관측소 발견: " + name + "(" + sidoName + ")");
					// throw new RuntimeException("새로운 관측소 발견: " + name + "(" + sidoName + ")");
				} else {
					String time = item.select("dataTime").text();
					Double pm100 =  asDouble(item.select("pm10Value").text());
					Double pm25 =  asDouble(item.select("pm25Value").text());
					//LocalDateTime dtime = LocalDateTime.parse(time.replace(' ', 'T') + ":00");
					LocalDateTime dtime = reformatHour(time); 
					PmData pm = new PmData(pm25, pm100, station.getSeq(), dtime);
					dataList.add(pm);					
				}
			}
			return dataList;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
	}
	/**
	 * 주어진 관측소의 하루치 미세먼지 정보를 가져옴
	 * @param station
	 * @return
	 */
	public List<PmData> queryPmDataByStation(Station station) { 
		String urlTemplate = "http://openapi.airkorea.or.kr/openapi/services/rest/ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty?serviceKey=@APIKEY&numOfRows=999&pageNo=1&stationName=@STATION&dataTerm=@TERM&ver=1.3&"; 
		//                                                                       /ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty?
		String url = urlTemplate
				.replace("@APIKEY", apiKey) 
				.replace("@TERM", "DAILY") 
				.replace("@STATION", station.getStation_name()); 
		
		Connection con = Jsoup.connect(url).timeout(60*1000).ignoreContentType(true);
		try {
			Document doc = con.get();
//			System.out.println(doc.toString());
			String selector = "response > body > items > item";
			Elements items = doc.select(selector);
			List<PmData> dataList = new  ArrayList<>();
			for(int i = 0 ; i < items.size(); i++) {
				Element item = items.get(i);
				
				String name = item.select("stationName").text();
//				System.out.println("[관측소] [" + name + "]");
				//Station station = stationDao.findStationByName(name,sidoName);
			
				String time = item.select("dataTime").text();
				Double pm100 =  asDouble(item.select("pm10Value").text());
				Double pm25 =  asDouble(item.select("pm25Value").text());
				LocalDateTime dtime = reformatHour(time); 
				PmData pm = new PmData(pm25, pm100, station.getSeq(), dtime);
				dataList.add(pm);					
				}
			
			return dataList;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 * 
	 * @param pmTime - "2020-10-16 24:00" 이렇게 들어오는 문자열
	 * @return
	 */
	private LocalDateTime reformatHour(String pmTime) {
		//  pmTime = ["2020-01-31", "24:00"] => 2020-02-01
		String [] tokens = pmTime.split(" ");
		LocalDate date = LocalDate.parse(tokens[0]);
		
		// 2020-10-16 : LocalDate
		// 24:00 => LocalDateTime
		String [] hms = (tokens[1] + ":00").split(":"); // "24:00:00" => ["24", "00", "00"]
		if (hms[0].equals("24")) {
			date = date.plusDays(1);
			hms[0] = "00"; // "00:00:00"
		}
		String timeFormat = String.join(":", hms); // "24:00:00
		LocalTime time = LocalTime.parse(timeFormat);
		return LocalDateTime.of(date, time);
	} 
}
