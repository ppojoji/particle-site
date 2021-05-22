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
	function renderOverlay(stations) {
		clearMarkers();
		var template = `
			<div class="customoverlay">
				<a class="pm-box" href="#" data-seq="@seq">
					<h5 class="station-name @state">@name</h5>
					<div class="pm-time"><span class="">@time</span></div>
					<ul class="pm">
						<li class="pm25"><span class="data"><img src='/images/pm25.svg'></span><span>@25</span></li>
						<li class= "pm100"><span class="data"><img src='/images/pm100.svg'><span>@100</span></span></li>
					</ul>
				</a>
			</div>`;
		var noDataTemplate = `<div class="customoverlay">
				<a class="pm-box" href="#" data-seq="@seq">
					<h5 class="station-name no-data">@name</h5>
					<span>관측 정보 없음</span>
				</a>
			</div>` 
		for(var i = 0 ; i < stations.length ; i++) {
			
			var pm25Val = ''
			var pm100Val = ''
			var content = ''
			if(stations[i].pmData[0].pm25 !== null){
				pm25Val = stations[i].pmData[0].pm25
			} else {
				pm25Val = '없음'
			}
			
			if(stations[i].pmData[0].pm100 !== null){
				pm100Val = stations[i].pmData[0].pm100
			}else {
				pm100Val = '없음'
			}
			
			if(stations[i].pmData[0].time === null){
				content = noDataTemplate.replace('@seq' , stations[i].seq);
				content = content.replace('@name' , stations[i].station_name);
			} else {
				content = template
							.replace('@time',stations[i].pmData[0].time.substring(11,13) + '시')
							.replace('@seq',stations[i].seq)
							.replace('@name', stations[i].station_name)
							.replace('@25', pm25Val)
							.replace('@100', pm100Val)
							.replace('@state', resolvePm25State(stations[i].pmData[0].pm25))
			}
			
			var overlay = new kakao.maps.CustomOverlay({
				map: map,
				position: new kakao.maps.LatLng(stations[i].station_lat, stations[i].station_lng),
				content: content, 
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
				 //renderMarkers();
				 stationCnt();
				renderOverlay(stations);
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
	
	$('#search-station').on('keyup',function(e){
		// 1. enter를 쳤을때 현재 입력된 값을 가져옴
		if ( e.keyCode === 13) {
			// console.log(e.target.value)
			console.log($(e.target).val())
			var keyword = $(e.target).val()
			var sta = stations.filter(sta => sta.station_name.includes(keyword))
			console.log(sta)
			// stations = sta // 이렇게 덮어씀
			var cnt = sta.length;
			$('.search-cnt').text(cnt+"개");
			renderOverlay(sta)
		}
	})
	
	// aaa(function() { 
	//     	
	// })
/*	function aaa ( callback ) {
		
	}*/
	function stationCnt(){
		//console.log('여기서 갯수 업데이트')
		/*if(pm25 <= 15){
			return "good";
		}else if(pm25 <= 35){
			return "normal";
		}else if(pm25 <= 75){
			return "bed";
		}else {
			return "so-bed";
		}*/
		/*var good = stations.filter(pm25=> pm25.pmData[0].pm25 <= 15);
		var normal = stations.filter(pm25=> pm25.pmData[0].pm25 > 15 && pm25.pmData[0].pm25 <= 35);
		var bad = stations.filter(pm25=> pm25.pmData[0].pm25 > 35 && pm25.pmData[0].pm25 <= 75);
		var soBad = stations.filter(pm25=> pm25.pmData[0].pm25 > 75);*/
		
		var pm25List = stations.map(station => station.pmData[0].pm25)
		
		var good = pm25List.filter(pm25=> pm25 <= 15);
		var normal = pm25List.filter(pm25=> pm25 > 15 && pm25 <= 35);
		var bad = pm25List.filter(pm25=> pm25 > 35 && pm25 <= 75);
		var soBad = pm25List.filter(pm25=> pm25 > 75);
		
		console.log('total', stations.length)
		console.log("good" + good.length);
		console.log("normal" +normal.length);
		console.log("bad" +bad.length);
		console.log("soBad" +soBad.length);
		
		$('.goodCnt').text(good.length);
		$('.normalCnt').text(normal.length);
		$('.badCnt').text(bad.length);
		$('.soBadCnt').text(soBad.length);
	}
})