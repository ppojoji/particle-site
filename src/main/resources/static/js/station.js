$(document).ready(() => {
	var url = new URL(location.href)
	var seq = url.searchParams.get("station")
	
	$.ajax({
		url : "/station/stationDetail/" + seq , 
		method : "GET" ,
		success(res){
			console.log(res);
			res.forEach(pm => {
				$(`<tr>
						<td>${pm.time}</td>
						<td>${pm.pm25}</td>
						<td>${pm.pm100}</td>
					</tr>`).appendTo('#pm-list')
			})
		} 	
	})
})
	
/*

 $(document).ready ( FUNCTION )
 
 function ready (callbackFunction  )
 
 $.ajax ( option )
*/
