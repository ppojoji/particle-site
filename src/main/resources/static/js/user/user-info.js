var loginUser = null
var sidos = []
function renderBookmarks(sidos) {
	var template = {
		sido: `<div class="sido">
			<h3>@sido</h3>
			<ul class="stations" data-sido="@sido"></ul>
		</div>`,
		station: `<li>@station<button class="btn btn-default btn-bmk-del" data-seq="@seq" >x</button></li>`
	}
	
	$('.bookmarks').empty()
	sidos.forEach((sido) => {
		var sidoHtml = template.sido.replaceAll('@sido',sido.sidoName);
		
		// $('.bookmarks').append(sido);
		var $sido = $(sidoHtml).appendTo('.bookmarks')
		
		var stations = sido.stations
		stations.forEach((station) => {
			var html = template.station
				.replace('@station',station.station_name)
				.replace('@seq',station.seq)
			$sido.find('.stations').append(html); 
		})
	})
}
	
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
	
	
	loadBookMark()
	
	$('.bookmarks').on('click', '.btn-bmk-del', (e)=> {
		var $del = $(e.target)
		var stationSeq = $del.data('seq');
		//console.log(stationSeq);
		// functioin removeBookmark(stationSeq)
		$.ajax({
			url: '/station/bookmark/' + stationSeq,
			method: 'DELETE',
			success(res){
				// console.log(e);
				if (res.success) {
					// 잘 지워졌음
					var sidoName = $del.closest('.stations').data('sido');
					var sido = sidos.find(si => si.sidoName === sidoName)
					var idx = sido.stations.findIndex(st => st.seq === stationSeq)
					sido.stations.splice(idx, 1)
					
					if(sido.stations.length == 0){
						var i = sidos.findIndex(si => si.sidoName === sidoName)
						sidos.splice(i, 1)
					}
					
					renderBookmarks(sidos)
				} else {
					// 지우지 못했음
				}
			}
		})
	})
})

function loadBookMark(){
	$.ajax({
		url : '/station/bookmarks',
		method : 'GET',
		success(bookmarks) {
			var obj = {}; // var sido = {'서울': [], '경기': [] }
			console.log('[bookmark]', bookmarks)
			var sidoNames = bookmarks.map( bmk => bmk.sido);
			var unames = new Set(sidoNames)
			sidoNames = [ ... unames]
			
			sidos = []
			sidoNames.forEach((sidoName) => {
				var stations = bookmarks.filter( bm => bm.sido === sidoName )
				var data = {
					sidoName: sidoName,
					stations: stations
				}
				sidos.push(data)
			})
			renderBookmarks(sidos)
			
		}
	})
}

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

