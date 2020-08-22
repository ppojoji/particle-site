package github.ppojoji.pmalert.dao;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;

import github.ppojoji.pmalert.dto.Station;

class SidoInsert {

	@Test
	void test() throws IOException {
		InputStream in = SidoInsert.class.getResourceAsStream("서울.xml");
		Document doc = Jsoup.parse(in, "utf-8", "");
		System.out.println(doc.toString());
	}
	
	
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

}
