
function StationPopup() {
	var wrapper = $('#station-popup');
	
	function readPmData(stationSeq){
		$.ajax({
			url : "/pm/" + stationSeq, // '/pm/12'
			method : 'GET', 
			success(res) {
				
				var chartData = [
				]
				for(var i=0;i<res.pmdata.length;i++){
					res.pmdata[i].time = new Date(res.pmdata[i].time) 
					chartData.push([res.pmdata[i].time,res.pmdata[i].pm25,res.pmdata[i].pm100])
				}
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
		
	
	
	return {
		show(station) {
			console.log('popup!!!', station);
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

