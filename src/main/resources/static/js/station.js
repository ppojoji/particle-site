$(document).ready(() => {
	
	var url = new URL(location.href)
	var seq = parseInt(url.searchParams.get("station"))
	
	console.log("##seq" + seq);
	
	var statios = null
	//var time = station.find(st => st.time)
	var stationLoaded = false
	var kakaoMap = null
	var marker = null
	
	function resizeTable(){
		var head = $('.head').height();
		$('.pm-table').css('margin-top',head+10);
	}
	/*function loadStations(sido) {
		$.ajax({
			url : '/stations',
			data: {
				sido: sido
			},
			method : 'GET',
			success(res){
				console.log(res);
				res.forEach(station => {
					var $anchor = $(`
						<a href="/station?station=${station.seq}">
							 <span class="station">${station.station_name}</span>
						</a>
						`
					 ).appendTo('.stations')
					
					if(station.seq == seq){
						$anchor.addClass('active');
					}
				})
				resizeTable();
				var targetStation = res.find(s => s.seq === seq)
				var dist = distance(res,targetStation);
				// 정렬!!!!
				dist.sort((a, b) => {
					if(a.dist < b.dist){
						return -1;
					}else if(a.dist > b.dist){
						return 1;
					}else{
						return 0;
					}
				})
				$('.near0').text(dist[1].station.station_name);
				$('.near1').text(dist[2].station.station_name);
				$('.near2').text(dist[3].station.station_name);
				console.log('[출력] ', dist)
				
			}
		})
	}*/
	
	function renderNearestStation(stationSeq) { // [ station, station, ... ]
		var targetStation = stations.find(s => s.seq === stationSeq)
		var dist = distance(stations,targetStation);
		// 정렬!!!!
		dist.sort((a, b) => {
			if(a.dist < b.dist){
				return -1;
			}else if(a.dist > b.dist){
				return 1;
			}else{
				return 0;
			}
		})
		//$('.near0').text(dist[1].station.station_name);
		//$('.near1').text(dist[2].station.station_name);
		//$('.near2').text(dist[3].station.station_name);
		var top3 = [dist[1], dist[2], dist[3]]
		var top3Seq = top3.map(d => d.station.seq)
		// top3[0].station.seq
		// top3[1].station.seq
		// top3[2].station.seq
		var others =  stations.filter(st => {
			/*if(st.seq != top3[0].station.seq && st.seq != top3[1].station.seq && st.seq != top3[2].station.seq){
				return true;
			} else {
				return false;
			}*/
			if (top3Seq.includes(st.seq)) {
				return false
			} else {
				return true
			}
		 })// stations - top3
		 console.log('TOP3 ', top3)
		 console.log('OTHER', others)
		console.log('[출력] ', dist)
		
		$('.stations').empty();
		top3.forEach(d => {
			/*d.dist
			d.station*/
			var $anchor = $(`
				<a href="#" data-seq="${d.station.seq}" class="topk">
					 <span class="station">${d.station.station_name}</span>
				</a>
				`
			 ).appendTo('.stations')
		})
		others.forEach(station => {
			var $anchor = $(`
				<a href="#" data-seq="${station.seq}">
					 <span class="station">${station.station_name}</span>
				</a>
				`
			 ).appendTo('.stations')
			 
			if(station.seq == stationSeq){
				$anchor.addClass('active');
			}
		})
		resizeTable();
		
	}
	function AjaxloadStation(sido, callback){
			$.ajax({
			url : '/stations',
			data: {
				sido: sido
			},
			method : 'GET',
			success(res){
				console.log(res);
				stations = res
				$('.stations').empty();
				res.forEach(station => {
					var $anchor = $(`
						<a href="#" data-seq="${station.seq}">
							 <span class="station">${station.station_name}</span>
						</a>
						`
					 ).appendTo('.stations')
					 
					if(station.seq == seq){
						$anchor.addClass('active');
					}
				})
				resizeTable();
				if (callback) {
					callback()
				}
				
			}
		})
	}
	
	$(document)
	.on('click', '.stations a', (e) => {
		e.preventDefault()
		console.log("###", e.target);
		console.log("###", e.currentTarget)
		var $click = $(e.currentTarget)

		var seq = $click.data('seq');
		loadPmDetail(seq);
		renderNearestStation(seq);

		/*$('.stations a.active').removeClass('active');
		$click.addClass('active');*/
	})
	.on('change', '#sido',(e) => {
		console.log("$$$$" +e.target.value)
		
		var sido = e.target.value
		AjaxloadStation(sido, () => {
			seq = $('.stations a').first().data('seq');
			 
			$('.stations a').first().trigger('click');
			
		})
		
		/*
		$.ajax({
			url: "/stations" ,
			method: "GET",
			data : {sido : sido} ,
			success(res){
				console.log(res);
				loadPmDetail();
			}
		})
		*/
	})
	/**
	 * 관측소 미세먼지 정보
	 */
	function loadPmDetail(stationSeq) {
		$.ajax({
			url : "/station/stationDetail/" + stationSeq , 
			method : "GET" ,
			success(res){
				console.log(res);
				//loadStations(res.station.sido);
				if(stationLoaded == false){
					AjaxloadStation(res.station.sido, () => {
						renderNearestStation(stationSeq)
					});
					stationLoaded = true
				}
				
				renderMap(res.station);
				 var date = res.data[0].time.substring(0,10);
				$('.date').text(date);
				$('.station-name').text(`[${res.station.sido}]` + res.station.station_name);
		 
		 		$('#pm-list').empty();
				res.data.forEach(pm => {
					// 2021-05-22T03:00:00.000+00:00
					var time = pm.time.substring(11,16); 
					$(`<tr>
							<td>${time}</td>
							<td>${pm.pm25}</td>
							<td>${pm.pm100}</td>
						</tr>`).appendTo('#pm-list')
				})
				averageData(res.data);
			}
		})
	}
	
	function distance(stations , station) {
		/*
		stations = {station, station, .... }
		=> 
		
		dist = [{station: .., dist: 0.33333}, {station: .., dist: 0.33333}, {station: .., dist: 0.33333}....]
		
		return dist
		r*/
		var dist = []
		stations.forEach(s => {
			var lat = (s.station_lat - station.station_lat) * (s.station_lat - station.station_lat)
			var lng = (s.station_lng - station.station_lng) * (s.station_lng - station.station_lng)
			
			var sqrt = Math.sqrt(lat + lng);
			dist.push({
				dist: sqrt,
				station: s
			})
		})
		console.log(dist) // [0] 자가 자신
		// [1, 2, 3]
		return dist
	}
	function renderMap(station){
		if(!kakaoMap){
			var container = document.getElementById('map'); 
			var options = { 
					center: new kakao.maps.LatLng(station.station_lat, station.station_lng), level: 7 
			}; 
			kakaoMap = new kakao.maps.Map(container, options);			
		}
		// 지도 중심을 이동시키는 함수를 호출해줘야 함
		setCenter(station);
		renderMarker(station.station_lat, station.station_lng);
	}
	
	function setCenter(station) {            
	    // 이동할 위도 경도 위치를 생성합니다 
	    var moveLatLon = new kakao.maps.LatLng(station.station_lat, station.station_lng);
	    
	    // 지도 중심을 이동 시킵니다
	    kakaoMap.setCenter(moveLatLon);
	}

	console.log(url)
	console.log(seq)
	
	loadPmDetail(seq)
	
	$( window ).resize( function() {
		resizeTable();
	});
	
	function averageData(pmData){
		var pm25 = pmData.map(pm => pm.pm25).filter(v => !isNaN(v+0) );
		var sum25 = pm25.reduce((acc, cur) => acc + cur)
		console.log(sum25/pm25.length)
		var pm100 = pmData.map(pm => pm.pm100).filter(v => !isNaN(v+0))
		var sum100 = pm100.reduce((acc , cur) => acc + cur)
		console.log(sum100/pm100.length);
		console.log(pm100) 
		
		$('.pm25-24h').text((sum25/pm25.length).toFixed(1));
		$('.pm100-24h').text((sum100/pm100.length).toFixed(1));
	}
	
	function renderMarker(lat,lng){
	  
	  if(marker != null){
		marker.setMap(null);
	  }
	    
		marker = new kakao.maps.Marker({ 
			position: new kakao.maps.LatLng(lat, lng)
		});
		marker.setMap(kakaoMap);
	}
	
	
})
	

/*

 $(document).ready ( FUNCTION )
 
 function ready (callbackFunction  )
 
 $.ajax ( option )
*/
