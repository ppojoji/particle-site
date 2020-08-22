$(document).ready(() => {
	console.log('차트 여기서 그림')
	var qs = location.search
	var token = qs.split('=')
	var seq = token[1]
	
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
	
	function loadSido(){
		$.ajax({
			url : `/pm/loadSido/${seq}` , 
			method : "GET" , 
			success(res){
				for(var i=0;i<res.sido.length;i++){
					$("#sido").append(
					`<option value="${res.sido[i]}">${res.sido[i]}</option>`
							)
				}
				for(var i =0; i < res.stations.length; i++) {
					$('#station').append(
						`<option value="${res.stations[i].seq}">${res.stations[i].station_name}</option>`
					)
				}
				
				$("#sido").val(res.stations[0].sido).prop("selected", true);
				$("#station").val(seq)
				
				console.log(res);
				readPmData(seq)
			}
		})
	}
	
	loadSido()
	
})