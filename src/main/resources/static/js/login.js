$(document).ready( function() {
	$('#member').on('click', function() {
		//console.log('ok?')
		var email = $('#email').val();
		var pwd = $('#pwd').val();
		$.ajax({
			url : '/login.do' ,
			method : 'POST' ,
			data : {
				email,
				Pwd: pwd
			},
			success(res){
				if(res.success) {
					alert('로그인 성공');
					location.href = '/';
				} else {
					alert('로그인 실패')
				}
				
			}  
		})
	})
});