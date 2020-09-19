package github.ppojoji.pmalert.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import github.ppojoji.pmalert.Res;
import github.ppojoji.pmalert.dto.User;
import github.ppojoji.pmalert.service.UserService;

@Controller
public class UserController {
	@Autowired
	UserService userService;
	
	@RequestMapping(value="/join.do" ,method = RequestMethod.POST)
	@ResponseBody
	public Object Join(@RequestParam String email , @RequestParam String Pwd) {
		System.out.println(email + " , " + Pwd); 
		userService.join(email,Pwd);
//		Map map = new HashMap<String, Object>();
//		map.put("email", email);
//		map.put("Pwd", Pwd);
//		map.put("SUCCESS", true);
		
		return Res.success("email", email, "Pwd", Pwd); 
	}
	
	@RequestMapping(value="/login.do" ,method = RequestMethod.POST)
	@ResponseBody
	public Object Login(@RequestParam String email , @RequestParam String Pwd , HttpSession session) {
		User user = userService.login(email , Pwd);
		
		if(user == null) {
			return Res.fail();
		} else {
			user.setPassword("******");
			session.setAttribute("LOGIN_USER", user);
			return Res.success("user" , user);
		}
	}
	@RequestMapping(value="/myInfo.do" ,method = RequestMethod.GET)
	@ResponseBody
	public Object myInfo(HttpSession session) {
		User user = (User) session.getAttribute("LOGIN_USER");
		
		if(user == null) {
			return Res.fail();
		} else {
			return Res.success("user" , user);
		}
	}
}
