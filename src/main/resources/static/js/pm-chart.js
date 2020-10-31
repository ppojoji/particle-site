
function StationPopup() {
	var wrapper = $('#station-popup');
	var curStation = null
	
	function readPmData(stationSeq){
		$.ajax({
			url : "/pm/" + stationSeq, // '/pm/12'
			method : 'GET', 
			success(res) {
				if(res.pmdata.length > 0){
					var chartData = [
					]
					for(var i=0;i<res.pmdata.length;i++){
						res.pmdata[i].time = new Date(res.pmdata[i].time) 
						chartData.push([res.pmdata[i].time,res.pmdata[i].pm25,res.pmdata[i].pm100])
					}
					chartData.reverse();
					var chartLabel = ['시간', 'PM2.5' , 'PM10']
					$('#pmchart').css('width', $('#station-popup').width()-10);
					var chartDiv = $('#pmchart')[0]
					
					var graph = new Dygraph(
					  chartDiv,
					  chartData,
					  {
					    label: chartLabel ,
					    valueRange: [0, 260]
					  }
					)
					
					$("#stationName").text(`[${res.station.sido}/${res.station.station_name}]`);
					if (res.bookmarked) {
						 wrapper.find('.bookmark-on').removeClass('hide-it');
						 wrapper.find('.bookmark-off').addClass('hide-it');
					} else {
						 wrapper.find('.bookmark-on').addClass('hide-it');
						 wrapper.find('.bookmark-off').removeClass('hide-it');
					}
					// FIXME 로그인 했을때(AND 북마크 했을때)에만 PM 설정 양식을 보여줘야 합니다.
					wrapper.find('#notif-pm25').val(res.pm25);
					wrapper.find('#notif-pm100').val(res.pm100);
				} else {
					$('#pmchart').append('<h4>데이터가 없습니다</h4>');
				}
				
				// var $(".bookmark-icon").val();
//				if(loginUser) {
//					
//				} else {
//					
//				}
				console.log(res);
			}			
		})
	}
	function toggleBookmark() {
		
		$.ajax({
			url: '/station/bookmark',
			method : "POST" , 
			data : {
				stationSeq: curStation.seq , 
			},
			success(res) {
				console.log(res) // res.bookmarked
				if(res.bookmarked){
					wrapper.find(".bookmark-on").removeClass("hide-it");
					wrapper.find(".bookmark-off").addClass("hide-it");
				} else {
					wrapper.find(".bookmark-off").removeClass("hide-it");
					wrapper.find(".bookmark-on").addClass("hide-it");
				}
			}
		})
	}
	
	function updateNofif(pmType , pmValue){
		$.ajax({
			url : '/station/notification' ,
			method : 'POST' , 
			data: {
				stationSeq : curStation.seq
				,pmType : pmType
				,pmValue : pmValue
			} ,
			success(res){
				console.log(res);
			}
		})
	}
	
	function close(){
		wrapper.addClass('hide-it')
	}
	wrapper.find('.btn-close').click(function(){
		close();
	})
	
	wrapper.find('.bookmark-on, .bookmark-off').on('click', function(e){
		if(loginUser != null){
			toggleBookmark()
		}else {
			alert("로그인 하세요.");
		}
		//console.log('ok????')
		
	})
	
	wrapper.find('#notif-pm25').on('change',function(e){
		//console.log(e.target.value);
		updateNofif("pm25",e.target.value); 
	})
	wrapper.find('#notif-pm100').on('change',function(e){
		//console.log(e.target.value);
		updateNofif("pm100",e.target.value); 
	})
	return {
		show(station) {
			console.log('popup!!!', station);
			curStation = station
			// 1. popup을 띄우고
			wrapper.removeClass('hide-it');
			// 2. target(관측소)를 이용해서 select를 채움
			readPmData(station.seq);
			// 3. 차트를 렌더링함
		}
	}
}

$(document).ready((e) => {
	
	window.popup = new StationPopup();
});

