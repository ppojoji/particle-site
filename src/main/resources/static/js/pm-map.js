$(document).ready(function() {
	var sido = '서울';
	/**
	 * 현재 시도의 관측소
	 */
	var stations = [{
		seq: 1119 ,
		sido: "서울",
		station_addr: "서울 중구 덕수궁길 15시청서소문별관 3동",
		station_lat: 37.564639,
		station_lng: 126.975961,
		station_name: "중구"
	}, {
		seq: 1120 , 
		sido: "서울" ,
		station_addr: "서울 용산구 한강대로 405(서울역 앞)" ,
		station_lat: 37.549389 ,
		station_lng: 126.971519 ,
		station_name: "한강대로"
	}];
	/*
	var station = {
		seq: 1119 ,
		sido: "서울",
		station_addr: "서울 중구 덕수궁길 15시청서소문별관 3동",
		station_lat: 37.564639,
		station_lng: 126.975961,
		station_name: "중구"
	};
	*/
	
	// kakao 지도 객체
	var map;
	/**
	 * 관측소를 화면에 찍어준 마커
	 */
	var markers = [];
	/**
	 * kakao infowindow
	 */
	var infowin;
	/**
	 * 지도를 초기화함
	 */
	function initMap() {
		var container = document.getElementById('map'); 
		var options = { 
				center: new kakao.maps.LatLng(stations[0].station_lat, stations[0].station_lng), level: 7 
		}; 
		map = new kakao.maps.Map(container, options);	
		
		var sidoNames = "서울,부산,대구,인천,광주,대전,울산,경기,강원,충북,충남,전북,전남,경북,경남,제주,세종".split(",");
		for(var i = 0 ; i < sidoNames.length ; i++) {
			$(`<option value="${sidoNames[i]}">${sidoNames[i]}</option>`).appendTo('#sido');
		}
		// infowin 초기회
		infowin = new kakao.maps.InfoWindow({
		    map: map,
		    position: map.getCenter(),
		    content: 'dddddd'
		});
		infowin.close();
	}
	/**
	 * 현재 지도 위의 마커를 모두 제거함
	 */
	function clearMarkers() {
		for(var i=0; i<markers.length; i++){
			markers[i].setMap(null);
		}
		markers = []
		// markers.splice(0, markers.length);
	}
	/**
	 * 마크 클릭 이벤트를 등록합니다.
	 */
	function markerClick(marker, station) {
		kakao.maps.event.addListener(marker, 'click', function() {
		    // 마커 위에 인포윈도우를 표시합니다
			
			infowin.open(map, marker);
			// TODO INFOWIN 디자인 필요함
			//      몇시 데이터인지도 html로 같이 조립해줘야 함
			if(station.pmData.length === 0){
				station.pmData.push({
					pm25: '--',
					pm100: '--'
				})
			}
			var recentData = station.pmData[station.pmData.length-1];
			infowin.setContent(`
			<div class="pm-box" data-seq="${station.seq}">
				<h4 class="station-name">${station.station_name}</h4>
				<ul class="pm-data">
					<li class="pm-25">${recentData.pm25}</li>
					<li class="pm-100">${recentData.pm100}</li>
				</ul>
			</div>
			`);
			infowin.setZIndex(1);
			 //console.log('클릭!');
			map.panTo(new kakao.maps.LatLng(station.station_lat,station.station_lng));
		});
	}
	
	//지도 이동 이벤트 핸들러
	function moveKakaoMap(self){
	    
	    var center = map.getCenter(), 
	        lat = center.getLat(),
	        lng = center.getLng();

	    self.href = 'https://map.kakao.com/link/map/' + encodeURIComponent(`${station.station_name}`) + ',' + lat + ',' + lng; 
	}
	function resolvePm25State(pm25) {
		/*
			‘좋음‘은 ‘0∼15㎍/㎥’, "good"
			
			‘보통’은 ‘16∼35㎍/㎥’, "normal"
			
			‘나쁨’은 ‘36∼75㎍/㎥’, "bed"
			
			‘매우 나쁨’은 ‘76㎍/㎥ 이상’ "so-bed"

		 */
		if(pm25 <= 15){
			return "good";
		}else if(pm25 <= 35){
			return "normal";
		}else if(pm25 <= 75){
			return "bed";
		}else {
			return "so-bed";
		}
	}
	function renderOverlay() {
		clearMarkers();
		var template = `
			<div class="customoverlay">
				<a href="https://map.kakao.com/link/map/11394059" target="_blank">
					<h5 class="station-name @state">@name</h5>
					<ul class="pm">
						<li class="pm25">PM2.5 <span class="data">@25</span></li>
						<li class="pm100">PM10.0: <span class="data">@100</span></li>
					</ul>
				</a>
			</div>`; 
		for(var i = 0 ; i < stations.length ; i++) {
			var overlay = new kakao.maps.CustomOverlay({
				map: map,
				position: new kakao.maps.LatLng(stations[i].station_lat, stations[i].station_lng),
				content: template
							.replace('@name', stations[i].station_name)
							.replace('@25', stations[i].pmData[0].pm25)
							.replace('@100', stations[i].pmData[0].pm100)
							.replace('@state', resolvePm25State(stations[i].pmData[0].pm25)), 
							yAnchor: 1
			});
			markers.push(overlay)
		}
		map.setCenter(new kakao.maps.LatLng(stations[0].station_lat, stations[0].station_lng))
	}
	function renderMarkers() {
		clearMarkers();
		for(var i = 0 ; i < stations.length ; i++) {
			/*
			 *  pm25: 10
				pm100: 27
				seq: 15578
				station: 3342
				time: "2020-10-17T12:00:00"
			 */
			var m = new kakao.maps.Marker({ 
				position: new kakao.maps.LatLng(stations[i].station_lat, stations[i].station_lng)
			});
			m.setMap(map);
			markerClick(m, stations[i], i);
			
			// 마커에 클릭이벤트를 등록합니다
			
			
			markers.push(m)
		}
		map.setCenter(new kakao.maps.LatLng(stations[0].station_lat, stations[0].station_lng))
	}
	function loadStationsBySido(sidoName) {
		$.ajax({
			url : "/pm/findRecentPmList" , 
			method : "GET" , 
			data : {
				sido : sidoName
			},
			success(res){
				console.log(res);
				sido = sidoName;
				stations = res;
				// renderMarkers();
				renderOverlay();
			}
		})
	}
	
	$("#sido").change(function(e){
		loadStationsBySido(e.target.value);
	});
	initMap();
	loadStationsBySido('경기');
	
	$(document).on('click', '.pm-box', (e) => {
		var seq = $(e.target).closest('.pm-box').data('seq');
		var target = stations.find((station) => {
			if (station.seq === seq) {
				return true;
			} else {
				return false;
			}
		});
		// var target = stations.find((station) => station.seq === seq);
		// console.log(target);
		window.popup.show(target);
	});
	//  renderMarkers();
})