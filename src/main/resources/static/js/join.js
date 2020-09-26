$(document).ready( function() {
	var msg = {
		EMAIL_DUP: '중복된 이메일입니다',
		PW_TO_SHORT: '패스워드가 너무 짧습니다(6~12글자)',
		PW_TO_LONG: '패스워드가 너무 깁니다(6~12글자)'
	}
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
				if(res.success){
					alert("회원가입 성공 했습니다.");
					location.href='/' 
				}
				console.log(res);
				
			},
			error(err) {
				//console.log('[error] ', e, a)
				var cause = err.responseJSON.cause
				var message = msg[cause]
				alert(message)
			}
		})
	})
});