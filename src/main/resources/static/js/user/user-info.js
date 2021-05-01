var loginUser = null

$(document).ready(function() {
	$.ajax({
		url :"/myInfo.do" ,
		method : "GET" ,
		success(res){
			console.log(res);
			if(res.success){
				var html = `<a href="#">${res.user.email}</a> | <a class="btn-logout" href="/logout">LOGOUT</a>`
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

$(document).on('click', '.btn-logout', (e) => {
	e.preventDefault()
	$.ajax({
		url : "/logout",
		method : "POST",
		success(res){
			alert("로그아웃 성공");
			location.reload(true);
		}
	})
	// e.stopPropagation()
})
