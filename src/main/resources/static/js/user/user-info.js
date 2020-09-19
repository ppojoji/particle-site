var loginUser = null

$(document).ready(function() {
	$.ajax({
		url :"/myInfo.do" ,
		method : "GET" ,
		success(res){
			console.log(res);
			if(res.success){
				var html = `<a href="#">${res.user.email}</a> | <a href="/logout">LOGOUT</a>`
				$('.user-info')
					.empty()
					.append(html);
				loginUser = res.user;
			}else{
				$('.user-info .login-link').removeClass("hide-it");
			}
		}
	})
	
})
