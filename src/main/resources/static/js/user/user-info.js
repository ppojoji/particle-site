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
	
	$.ajax({
		url : '/user/bookMark',
		method : 'GET',
		success(bookmarks) {
			console.log('[bookmark]', bookmarks)
			for(var i =0 ; i<bookmarks.length ; i++){
				var sn = '<li><a href="#">@name</a></li>' // bookmarks[i].station_name
				var li = sn.replace('@name',bookmarks[i].station_name);
				$(li).appendTo($('.bookMark'))
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
