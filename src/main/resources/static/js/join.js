$(document).ready( function() {
	var msg = {
		EMAIL_DUP: '중복된 이메일입니다',
		PW_TO_SHORT: '패스워드가 너무 짧습니다(6~12글자)',
		PW_TO_LONG: '패스워드가 너무 깁니다(6~12글자)'
	}
	$('#member').on('click', function() {
		var error = chkPW() // null '비번이....'
		if(error){
			alert(error);
			return ;
		}
		
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
	
	function checkForm($input, fnValidator) {
		// var $input = $(e.target)
		var $td = $input.parent()
		var error = fnValidator();
		if(error){
			$td.find('.error').remove();
			$td.append(`<label class="error">${error}</label>`)
		} else {
			$td.find('.error').remove();
		}
	}
	$('#pwd').on('input',function(e){
		// var pw = e.target.value
		checkForm($(e.target), chkPW)
	})
	
	$('#email').on('input',function(e){
		checkForm($(e.target), chkEmail)
	})
});

function chkPW(){

	var pw = $("#pwd").val();
	var id = $("#email").val();
		
	var reg = /^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$/;
	var hangulcheck = /[ㄱ-ㅎ|ㅏ-ㅣ|가-힣]/;
	 
	if(pw.search(id) > -1){
		return "비밀번호에 아이디가 포함되었습니다.";
	} else if(pw.search(/\s/) != -1){
		return "비밀번호는 공백 없이 입력해주세요.";
	} else if(false === reg.test(pw)) {
		return '비밀번호는 8자 이상이어야 하며, 숫자/대문자/소문자/특수문자를 모두 포함해야 합니다.';
	} else if(/(\w)\1\1\1/.test(pw)){
		return '같은 문자를 4번 이상 사용하실 수 없습니다.';
	}else if(hangulcheck.test(pw)){
		return "비밀번호에 한글을 사용 할 수 없습니다.";
	}else {
		// alert("회원 가입 되었습니다.");
		return null;
	}
}

function chkEmail(){
	var pw = $("#pwd").val();
	var id = $("#email").val();

	
	if(id.indexOf('@') < 0) {
		return '유효하지 않은 이메일 입니다.'
	}else{
		return null;
	}
}