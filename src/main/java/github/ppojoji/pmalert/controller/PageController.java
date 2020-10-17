package github.ppojoji.pmalert.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

	@Value("${particle.map.apikey}") String mapApiKey;
	
	@GetMapping("/")
	public String pageMain(Model model) {
		model.addAttribute("mapApiKey", mapApiKey);
		return "index"; // template/index.html
	}
}
