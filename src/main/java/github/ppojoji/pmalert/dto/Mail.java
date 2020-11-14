package github.ppojoji.pmalert.dto;

public class Mail {
	private String subject; 
	private String content;
	private String sendtime;
	private String sender;
	private Integer sendresult;
	private String receiver;
	private String seq;
	
	public Mail() {
	}
	public Mail(String title, String content, String receiver) {
		this.subject = title;
		this.content = content;
		this.receiver = receiver;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getSendtime() {
		return sendtime;
	}
	public void setSendtime(String sendtime) {
		this.sendtime = sendtime;
	}
	public String getSender() {
		return sender;
	}
	public void setSender(String sender) {
		this.sender = sender;
	}
	public Integer getSendresult() {
		return sendresult;
	}
	public void setSendresult(Integer sendresult) {
		this.sendresult = sendresult;
	}
	public String getReceiver() {
		return receiver;
	}
	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}
	public String getSeq() {
		return seq;
	}
	public void setSeq(String seq) {
		this.seq = seq;
	}
	
	@Override
	public String toString() {
		return "Mail [subject=" + subject + ", content=" + content + ", sendtime=" + sendtime + ", sender=" + sender
				+ ", sendresult=" + sendresult + ", receiver=" + receiver + ", seq=" + seq + "]";
	}
}
