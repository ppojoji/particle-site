package github.ppojoji.pmalert.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import github.ppojoji.pmalert.PmException;
import github.ppojoji.pmalert.dao.UserDao;
import github.ppojoji.pmalert.dto.User;

@Service
public class UserService {
	
	@Autowired
	UserDao userDao; 
	
	public User join(String email, String pwd) {
		// FIXME 올바른 email 형식 확인해야함
		// FIXME 비번이 없는 경우 막아야 함(8자리~16자리)
		// FIXME email 중복 여부 확인해야함
		User user = userDao.findByEmail(email);
		
		if(user != null) {
			throw new PmException(400, "EMAIL_DUP");
		}
		if (pwd.length() < 6) {
			throw new PmException(400, "PW_TO_SHORT");
		}
		if (pwd.length() > 12) {
			throw new PmException(400, "PW_TO_LONG");
		}
		System.out.println(email + " , " + pwd); 
		userDao.join(email,pwd);
		return userDao.login(email, pwd);
	}

	public User login(String email, String pwd) {
		return userDao.login(email,pwd);
	}

}
