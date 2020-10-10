$(document).ready(function() {
	$.ajax({
		url : '/user/bookMark',
		method : 'GET',
		success(res){
			var $tbody = $('#bookmark tbody')
			$tbody.empty()
			//console.log(res);f
			for(var i = 0 ; i < res.length; i++) {
				var html = `<tr>
					<td>${res[i].station_name}</td>
					<td><input type="number" value="${res[i].pm25}" data-seq="${res[i].station}" data-pm="pm25"></td>
					<td><input type="number" value="${res[i].pm100}" data-seq="${res[i].station}" data-pm="pm100"></td>
					<td><input type="checkbox" data-seq="${res[i].station}"></td>
					<td><a href="#" class="btn-del" data-seq="${res[i].station}" >삭제</a></td>
				</tr>`
				var $tr = $(html).appendTo($tbody)
				
				$tr.find('input[type="checkbox"]').prop('checked', res[i].notify === 'Y')
				/*
				if(res[i].notify == 'Y'){
					$tr.find('input[type="checkbox"]').prop('checked', true)
				}else{
					$tr.find('input[type="checkbox"]').prop('checked', false)
				}
				*/
			}
		}
	});
	
	$('#bookmark')
	.on('click', '.btn-del', (e) => {
		//console.log(e.target)
		var StationSeq = $(e.target).data('seq');
		//console.log(StationSeq);
		StationSeqDel(StationSeq, (res) => {
			if (res) {
				$(e.target).closest('tr').remove()				
			} else {
				alert('못지웠다')
			}
		});
	})
	.on('change', 'input[type="checkbox"]', (e) => {
		//console.log(e.target)
		var notify = $(e.target).prop('checked')
		UpdateNotify($(e.target).data('seq') ,notify , (e)=> {
			console.log(e);
		})
	})
	.on('keyup', 'input[data-pm]', (e) => {
		if (e.keyCode === 13) {
			var StationSeq = $(e.target).data("seq"); 
			var PmType = $(e.target).data("pm");
			var PmValue = $(e.target).val();
			
			UpdatePm(StationSeq ,PmType , PmValue);
		}
	})
	
	function StationSeqDel(StationSeq, callback){
		$.ajax({
			url : '/station/StationSeqDel',
		    method : 'POST',
			data:{
				StationSeq : StationSeq
			}, 
			success(res){
				callback(res)
			}
		})
	}
	function UpdateNotify(StationSeq, notify , callback){
		$.ajax({
			url : '/station/UpdateNotify',
		    method : 'POST',
			data:{
				StationSeq : StationSeq,
				notify : notify
			}, 
			success(res){
				callback(res)
			}
		})
	}
	
	function UpdatePm(stationSeq , pmType, pmValue){
		$.ajax({
			url : '/station/notification',
		    method : 'POST',
			data:{
				stationSeq,
				pmType, 
				pmValue
			}, 
			success(res){
				console.log(res)
			}
		})
	}
})