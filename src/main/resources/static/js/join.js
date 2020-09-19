$(document).ready( function() {
	$('#member').on('click', function() {
		//console.log('ok?')
		var email = $('#email').val();
		var pwd = $('#pwd').val();
		$.ajax({
			url : '/join.do' ,
			method : 'POST' ,
			data : {
				email,
				Pwd: pwd
			},
			success(res){
				// FIXME 가입 성공 후 뭔가를 해야함
				console.log(res);
				
			}  
		})
	})
});