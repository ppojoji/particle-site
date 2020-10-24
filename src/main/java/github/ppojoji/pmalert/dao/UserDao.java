package github.ppojoji.pmalert.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import github.ppojoji.pmalert.Res;
import github.ppojoji.pmalert.TypeMap;
import github.ppojoji.pmalert.dto.User;

@Repository
public class UserDao {
	@Autowired
	SqlSession sqlSession;
	public void join(String email, String pwd) {
		Map map = new HashMap<String, Object>();
		map.put("email", email);
		map.put("pwd", pwd);
		sqlSession.insert("UserMapper.join",map);
		
		
	}
	public User login(String email, String pwd) {
		return sqlSession.selectOne(
				"UserMapper.login", 
				Res.success("email", email, "pwd", pwd));
	}
	public User findByEmail(String email) {
		return sqlSession.selectOne("UserMapper.findByEmail",email);
	}
	public void updatePass(Integer userSeq, String newPass) {
		Map<String ,Object> map = new HashMap<String, Object>(); 
		map.put("userSeq", userSeq);
		map.put("newPass", newPass);
		
		sqlSession.update("UserMapper.updatePass",map);
	}
	public User findByUserSeqAndPw(Integer userSeq, String curPass) {
		Map<String ,Object> map = new HashMap<String, Object>(); 
		map.put("userSeq", userSeq);
		map.put("curPass", curPass);
		
		return sqlSession.selectOne("UserMapper.findByUserSeqAndPw", map);
	}

}
