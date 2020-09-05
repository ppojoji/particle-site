package github.ppojoji.pmalert.service.pmapi;

import java.io.IOException;
import java.time.LocalDateTime;
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
import org.springframework.stereotype.Service;

import github.ppojoji.pmalert.dao.StationDao;
import github.ppojoji.pmalert.dto.PmData;
import github.ppojoji.pmalert.dto.Station;

@Service
public class PmApi {
	@Autowired
	StationDao stationDao;
	/**
	 * 주어진 시도에 속하는 관측소를 로드합니다.
	 * 
	 * @param sidoName - '서울', '경기' 등 17개 시도 이름
	 * @return 시도별 관측소들
	 */
	final static Logger logger = LoggerFactory.getLogger(PmApi.class);
	
	public List<Station> listStationsBySido(String sidoName) {
		String urlTemplate = "http://openapi.airkorea.or.kr/openapi/services/rest/MsrstnInfoInqireSvc/getMsrstnList?serviceKey=RHJDYwDpuFiFtejEpchEWBkx8Uy8XrZVPSkmTOd%2BVk2qUO7t8HOUGnBHj63GhpiHfWxgxEYQy0MeQFK2Ysh6kg%3D%3D&numOfRows=999&pageNo=1&addr=@sido&stationName=&";
		
		String url = urlTemplate.replace("@sido", sidoName);
		
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

	public List<PmData> queryHourlyPmData(String sidoName) {
		String urlTemplate = "http://openapi.airkorea.or.kr/openapi/services/rest/ArpltnInforInqireSvc/getCtprvnRltmMesureDnsty?serviceKey=RHJDYwDpuFiFtejEpchEWBkx8Uy8XrZVPSkmTOd%2BVk2qUO7t8HOUGnBHj63GhpiHfWxgxEYQy0MeQFK2Ysh6kg%3D%3D&numOfRows=999&pageNo=1&sidoName=@sido&ver=1.3&";
		
		String url = urlTemplate.replace("@sido", sidoName);
		
		Connection con = Jsoup.connect(url).ignoreContentType(true);
		try {
			Document doc = con.get();
			System.out.println(doc.toString());
			String selector = "response > body > items > item";
			Elements items = doc.select(selector);
			List<PmData> dataList = new  ArrayList<>();
			for(int i = 0 ; i < items.size(); i++) {
				Element item = items.get(i);
				/*
				<item>
				>>  <stationName>장흥동</stationName>
					<mangName>도시대기</mangName>
				>>  <dataTime>2020-08-01 12:00</dataTime>
					<so2Value>0.003</so2Value>
					<coValue>0.3</coValue>
					<o3Value>0.024</o3Value>
					<no2Value>0.011</no2Value>
				>>  <pm10Value>54</pm10Value>
					<pm10Value24>28</pm10Value24>
				>>  <pm25Value>7</pm25Value>
					<pm25Value24>7</pm25Value24>
					<khaiValue>47</khaiValue>
					<khaiGrade>1</khaiGrade>
					<so2Grade>1</so2Grade>
					<coGrade>1</coGrade>
					<o3Grade>1</o3Grade>
					<no2Grade>1</no2Grade>
					<pm10Grade>1</pm10Grade>
					<pm25Grade>1</pm25Grade>
					<pm10Grade1h>2</pm10Grade1h>
					<pm25Grade1h>1</pm25Grade1h>
					</item>
				 */
				String name = item.select("stationName").text();
				System.out.println("[관측소] [" + name + "]");
				Station station = stationDao.findStationByName(name,sidoName);
				if (station == null) {
					// TODO 이렇게 새로운 관측소가 나오면 관측소를 집어넣어야 함
					logger.warn("새로운 관측소 발견: " + name + "(" + sidoName + ")");
					// throw new RuntimeException("새로운 관측소 발견: " + name + "(" + sidoName + ")");
				} else {
					String time = item.select("dataTime").text();
					Double pm100 =  asDouble(item.select("pm10Value").text());
					Double pm25 =  asDouble(item.select("pm25Value").text());
					LocalDateTime dtime = LocalDateTime.parse(time.replace(' ', 'T') + ":00");
					PmData pm = new PmData(pm25, pm100, station.getSeq(), dtime);
					dataList.add(pm);					
				}
			}
			return dataList;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
	}
	
	public List<PmData> queryPmDataByStation(Station station) { 
		String urlTemplate = "http://openapi.airkorea.or.kr/openapi/services/rest/ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty?serviceKey=@APIKEY&numOfRows=999&pageNo=1&stationName=@STATION&dataTerm=@TERM&ver=1.3&"; 
		
		String url = urlTemplate.replace("@APIKEY", "RHJDYwDpuFiFtejEpchEWBkx8Uy8XrZVPSkmTOd%2BVk2qUO7t8HOUGnBHj63GhpiHfWxgxEYQy0MeQFK2Ysh6kg%3D%3D") 
				.replace("@TERM", "DAILY") 
				.replace("@STATION", station.getStation_name()); 
		
		Connection con = Jsoup.connect(url).ignoreContentType(true);
		try {
			Document doc = con.get();
			System.out.println(doc.toString());
			String selector = "response > body > items > item";
			Elements items = doc.select(selector);
			List<PmData> dataList = new  ArrayList<>();
			for(int i = 0 ; i < items.size(); i++) {
				Element item = items.get(i);
				
				String name = item.select("stationName").text();
				System.out.println("[관측소] [" + name + "]");
				//Station station = stationDao.findStationByName(name,sidoName);
			
					String time = item.select("dataTime").text();
					Double pm100 =  asDouble(item.select("pm10Value").text());
					Double pm25 =  asDouble(item.select("pm25Value").text());
					String formattedTime = reformatHour(time.replace(' ', 'T') + ":00"); 
					LocalDateTime dtime = LocalDateTime.parse(formattedTime); 
					PmData pm = new PmData(pm25, pm100, station.getSeq(), dtime);
					dataList.add(pm);					
				}
			
			return dataList;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private String reformatHour(String string) {
		// TODO Auto-generated method stub
		return null;
	} 
}
