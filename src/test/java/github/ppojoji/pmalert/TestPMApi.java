package github.ppojoji.pmalert;

import java.io.IOException;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class TestPMApi {
	/*
	 * 
	 *
	측정소 목록 조회용 url
	http://openapi.airkorea.or.kr/openapi/services/rest/MsrstnInfoInqireSvc/getMsrstnList?serviceKey=LX%2Bvip8U1cIkZRIYLe%2Fj20f%2F7QAGPO8I3bIF6PRU9ILI05ynseP670tj5oAmkfnaUDKKbMPLRuQNRdosbKDN%2Fg%3D%3D&numOfRows=100&pageNo=1&addr=%EA%B2%BD%EA%B8%B0&stationName=&
	 */
	public static void main(String[] args) throws IOException {
		
		String url = "http://openapi.airkorea.or.kr/openapi/services/rest/ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty?serviceKey=LX%2Bvip8U1cIkZRIYLe%2Fj20f%2F7QAGPO8I3bIF6PRU9ILI05ynseP670tj5oAmkfnaUDKKbMPLRuQNRdosbKDN%2Fg%3D%3D&numOfRows=100&pageNo=1&stationName=%EC%98%81%EB%93%B1%ED%8F%AC%EA%B5%AC&dataTerm=DAILY&ver=1.3&";
		
		Connection con = Jsoup.connect(url).ignoreContentType(true);
		Document doc = con.get();
		
		System.out.println(doc.toString());
		
		
	}
}
