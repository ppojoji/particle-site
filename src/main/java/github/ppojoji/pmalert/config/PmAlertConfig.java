package github.ppojoji.pmalert.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import github.ppojoji.pmalert.controller.interceptors.LoginCheckInterceptor;

@Configuration
public class PmAlertConfig implements WebMvcConfigurer{

	@Autowired
	LoginCheckInterceptor loginChecker;
	
	@Override
	public void addInterceptors(InterceptorRegistry reg) {
		reg.addInterceptor(loginChecker)
			.addPathPatterns("/**");
	}
	
}
