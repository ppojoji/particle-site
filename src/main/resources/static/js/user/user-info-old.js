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
			var obj = {};
			console.log('[bookmark]', bookmarks)
			for(var i =0 ; i<bookmarks.length ; i++){
				//var h3 = '<h3><a href="#">@html</a></h3>'
				var sn = '<li><a href="#">@name</a></li>' // bookmarks[i].station_name
				var li = sn.replace('@name',bookmarks[i].station_name);
				//var html = h3.replace('@html',bookmarks[i].sido);
				//console.log(html);
				//var sido = bookmarks[i].sido
				var sido = bookmarks.find(bm => bm.sido)
				var stationName = bookmarks.filter(sn => 
					sn.station_name
				);
				
				//var 지역 = bookmarks[i].sido;
				console.log(stationName);
				
				
				
				console.log(sido)
				//console.log("stationName" + stationName)
				//console.log("stationName" + JSON.stringify(stationName))
				 //$(html).append("<h3>"+지역 +"</h3>")
				 //$(li).appendTo($('.bookMark'))
			}
			$.each(bookmarks,function(i,item){
				console.log(i,item);
				if(!obj[bookmarks[i].sido]){
					obj[bookmarks[i].sido] = [];
				}
				
				obj[bookmarks[i].sido].push(bookmarks[i].station_name);
			});
			console.log(obj);
			
			var keys = Object.keys(obj);
			console.log(keys);
			
			for(var i=0; i<keys.length; i++){
				$('.bookMark').append("<h3>"+keys[i]+"</h3>");
				for(var j=0; j<obj[keys[i]].length; j++){
					$('.bookMark').append("<li>"+obj[keys[i]][j]+"</li>");
				}
				//$(li).appendTo($('.bookMark'))
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
