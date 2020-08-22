package github.ppojoji.pmalert.dto;

import java.time.LocalDateTime;
import java.util.List;

public class PmData {
	private int seq;
	private Double pm25;
	private Double pm100;
	private Integer station;
//	private Station station;
	private LocalDateTime time;
	
	public PmData() {
	}
	public PmData(Double pm25, Double pm100, Integer station, LocalDateTime time) {
		super();
		this.pm25 = pm25;
		this.pm100 = pm100;
		this.station = station;
		this.time = time;
	}

	public int getSeq() {
		return seq;
	}
	public void setSeq(int seq) {
		this.seq = seq;
	}
	public Double getPm25() {
		return pm25;
	}
	public void setPm25(Double pm25) {
		this.pm25 = pm25;
	}
	public Double getPm100() {
		return pm100;
	}
	public void setPm100(Double pm100) {
		this.pm100 = pm100;
	}
	public Integer getStation() {
		return station;
	}
	public void setStation(Integer station) {
		this.station = station;
	}
	public LocalDateTime getTime() {
		return time;
	}
	public void setTime(LocalDateTime time) {
		this.time = time;
	}
	@Override
	public String toString() {
		return "PmData [seq=" + seq + ", pm25=" + pm25 + ", pm100=" + pm100 + ", station=" + station + ", time=" + time
				+ "]";
	}
}
