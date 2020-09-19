package github.ppojoji.pmalert.dto;

public class User {
	private Integer seq; 
	private String email; 
	private String password;
	public Integer getSeq() {
		return seq;
	}
	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	@Override
	public String toString() {
		return "User [seq=" + seq + ", email=" + email + ", password=" + password + "]";
	} 
}
