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
	 * 머코 쿨락 아밴투룰 둥록합니다.
	 */
	function markerClick(marker, station) {
		kakao.maps.event.addListener(marker, 'click', function() {
		    // 마커 위에 인포윈도우를 표시합니다
			
			infowin.open(map, marker);
			// TODO INFOWIN 디자인 필요함
			//      이 안에 PM 데이터를 같이 출력하고 싶음
			infowin.setContent(`<h4 class="infowin-title">${station.station_name}</h4>`);
			// console.log('클릭!');
		});
	}
	function renderMarkers() {
		clearMarkers();
		for(var i = 0 ; i < stations.length ; i++) {
			var m = new kakao.maps.Marker({ 
				position: new kakao.maps.LatLng(stations[i].station_lat, stations[i].station_lng)
			});
			m.setMap(map);
			markerClick(m, stations[i]);
			
			// 마커에 클릭이벤트를 등록합니다
			
			
			markers.push(m)
		}
		map.setCenter(new kakao.maps.LatLng(stations[0].station_lat, stations[0].station_lng))
	}
	function loadStationsBySido(sidoName) {
		$.ajax({
			url : "/stations" , 
			method : "GET" , 
			data : {
				sido : sidoName
			},
			success(res){
				console.log(res);
				sido = sidoName;
				stations = res;
				renderMarkers();
			}
		})
	}
	
	$("#sido").change(function(e){
		loadStationsBySido(e.target.value);
	});
	initMap();
	loadStationsBySido('경기');
	//  renderMarkers();
})