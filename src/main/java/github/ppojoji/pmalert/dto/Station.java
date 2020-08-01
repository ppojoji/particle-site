package github.ppojoji.pmalert.dto;

public class Station {
	private Integer seq;
	private String station_name;
	private String station_addr;
	private Double station_lat;
	private Double station_lng;
	private String sido;
	
	public Station() {
	}
	public Station(String station_name, String station_addr, Double station_lat, Double station_lng, String sido) {
		super();
		this.station_name = station_name;
		this.station_addr = station_addr;
		this.station_lat = station_lat;
		this.station_lng = station_lng;
		this.sido = sido;
	}


	public Integer getSeq() {
		return seq;
	}
	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	public String getStation_name() {
		return station_name;
	}
	public void setStation_name(String station_name) {
		this.station_name = station_name;
	}
	public String getStation_addr() {
		return station_addr;
	}
	public void setStation_addr(String station_addr) {
		this.station_addr = station_addr;
	}
	public Double getStation_lat() {
		return station_lat;
	}
	public void setStation_lat(Double station_lat) {
		this.station_lat = station_lat;
	}
	public Double getStation_lng() {
		return station_lng;
	}
	public void setStation_lng(Double station_lng) {
		this.station_lng = station_lng;
	}
	public String getSido() {
		return sido;
	}
	public void setSido(String sido) {
		this.sido = sido;
	}
	
	@Override
	public String toString() {
		return "Station [seq=" + seq + ", station_name=" + station_name + ", station_addr=" + station_addr
				+ ", station_lat=" + station_lat + ", station_lng=" + station_lng + ", sido=" + sido + "]";
	}
}
