package github.ppojoji.pmalert.dto;

public class StationBookmark {

	
	/*
	 * {password=1234, pm25=40, station=3342, pm100=30, user=1, notify=Y, seq=1, email=adm}
	 */
	private User user;
	private Integer stationSeq;
	private Double pm25;
	private Double pm100;
	public StationBookmark(User user, Double pm25, Double pm100, Integer stationSeq) {
		this.user = user;
		this.pm25 = pm25;
		this.pm100 = pm100;
		this.stationSeq = stationSeq;
		
	}
	public User getUser() {
		return user;
	}
	public Integer getStationSeq() {
		return stationSeq;
	}
	public Double getPm25() {
		return pm25;
	}
	public Double getPm100() {
		return pm100;
	}
	@Override
	public String toString() {
		return "StationBookmark [user=" + user + ", stationSeq=" + stationSeq + ", pm25=" + pm25 + ", pm100=" + pm100
				+ "]";
	}
	
}
