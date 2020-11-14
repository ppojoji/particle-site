package github.ppojoji.pmalert.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import github.ppojoji.pmalert.dao.StationDao;
import github.ppojoji.pmalert.dto.PmData;
import github.ppojoji.pmalert.service.pmapi.PmApi;

//@MybatisTest
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//@Import({
//	MailingService.class,
//	JavaMailSenderImpl.class
//})

@SpringBootTest
public class TestMail {

	@Autowired
	JavaMailSender mailSender;
	
	@Autowired
	PmNotificationService notifService;
	
	@Test
	public void test_메일전송() throws MessagingException {
		MimeMessage msg = mailSender.createMimeMessage();
		
		MimeMessageHelper messageHelper 
        = new MimeMessageHelper(msg, "UTF-8");

		messageHelper.setFrom("ppojoji@gmail.com");  // 보내는사람 생략하거나 하면 정상작동을 안함
		messageHelper.setTo("ppojoji@naver.com");     // 받는사람 이메일
		
		// 메일 템플릿이 필요합니다.
		messageHelper.setSubject("test"); // 메일제목은 생략이 가능하다
		messageHelper.setText("<b>html로 문서를</b> <font color=\"red\">작성</read>함", true);  // 메일 내용
		
		mailSender.send(msg);
		
	}
	
	@Test
	public void test_미세먼지_메일전송() {
		List<PmData> prevPmData = new ArrayList<PmData>();
		prevPmData.add(new PmData(null, null, 3346, LocalDateTime.now()));
		prevPmData.add(new PmData(35.0, 40.0, 3303, LocalDateTime.now()));
//		prevPmData.add(new PmData(60.0, 60.0, 3305, LocalDateTime.now()));

		List<PmData> currentPmData = new ArrayList<PmData>();
		currentPmData.add(new PmData(null, null, 3346, LocalDateTime.now()));
		currentPmData.add(new PmData(9.0, 70.0, 3303, LocalDateTime.now()));
//		currentPmData.add(new PmData(90.0, 90.0, 3305, LocalDateTime.now()));
		
		notifService.resolveMailingList(prevPmData, currentPmData);
	}
}
