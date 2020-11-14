package github.ppojoji.pmalert.service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import github.ppojoji.pmalert.dto.Mail;
 
@Service
public class MailingService {
	
	@Autowired 
	private JavaMailSender mailSender;
	
	@Value("${spring.mail.username}")
	private String senderEmail;
	
	public boolean SendMail(Mail mail) {
		// FIXME 여기서 메일을 바로 보내지 않고 테이블에 INSERT함. 그리고 별도의 스케쥴링으로 메일 보내는 메소드를 따로 작성함
		String receiverEmail = mail.getReceiver(); 
		String title = mail.getSubject();
		String content = mail.getContent();
		
		// FIXME 위에 3개 파라미터 말고 MailDto 를 만들어서 넣어야 나중에 편함
		MimeMessage msg = mailSender.createMimeMessage();
		
		MimeMessageHelper messageHelper = new MimeMessageHelper(msg, "UTF-8");

		try {
			
			messageHelper.setFrom(senderEmail);
			messageHelper.setTo(receiverEmail);
			messageHelper.setSubject(title); // 메일제목은 생략이 가능하다
			messageHelper.setText(content, true);  // 메일 내용
			mailSender.send(msg);
			System.out.println("메일 보냈음 " + receiverEmail);
			return true;
 		} catch (MessagingException e) {
			e.printStackTrace();
			return false;
		}  
	}
}
