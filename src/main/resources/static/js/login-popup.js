
$(document).ready(() => {
	
	const template = {
		login: `<div class="popup-bg">
		<div class="popup-container">
			<!--<h3>로그인</h3>-->
			<div class="item">username</div>
			<div class="item">
				<img class="form-icon" src="/images/person.svg">
				<input type="text" class="id">
			</div>
			<div class="item mgt-1rem">passwd</div> 
			<div class="item">
				<img class="form-icon" src="/images/password.svg">
				<input type="password" class="pwd">
			</div>
			<div class="item mgt-1rem">
				<button class="btn-red login-btn"><span>LOGIN</span><img class="spin" src="/images/spin.svg"></button>
				<span class="login-success">로그인 성공</span>
				<span class="login-fail">로그인 실패</span> 
			</div>
		</div>`,
		
	}
	let $wrapper
	function show() {
		$wrapper = $(template.login).appendTo("body")
		
		// $wrapper.find('.spin').hide();
		$wrapper.find('.spin, .login-success, .login-fail').hide();
		
		$wrapper.find('.id , .pwd').on("keyup",(e) =>{
			if(e.keyCode == 13){
				$('.login-btn').trigger('click');
			}else if(e.keyCode == 27){
				hide();
			}
		})
		
		$wrapper.find('.login-btn').on('click', () => {
			$(".id").attr("disabled",true);
			$(".pwd").attr("disabled",true);
			
			$wrapper.find('.spin').show();
			
			$wrapper.find('.login-btn').addClass('pending');
			
			var id = $wrapper.find('.id').val();
			var pwd = $wrapper.find('.pwd').val();
			
			$.ajax({
				url : '/login.do' ,
				method : 'POST' ,
				data : {
					email: id,
					Pwd: pwd
				},
				success(res){
					$wrapper.find('.spin').hide();
					$wrapper.find('.login-btn').removeClass('pending');
					$(".id, .pwd").attr("disabled",false);
					
					if(res.success) {
						$wrapper.find('.login-success').show();
						location.href = '/';
					} else {
						$wrapper.find('.login-fail').show();
					}
				}  
			})
		})
	
		$wrapper.on('click', function(){
			hide();
		})
		
		$wrapper.find('.popup-container').on('click',function(e){
			e.stopPropagation()
		})
		
		$wrapper.find('.id , .pwd').on('keyup', (e) => {
			$wrapper.find('.spin, .login-success, .login-fail').hide();
		})
	}
	
	function hide() {
		$wrapper.remove()
	}
	
	window.loginPopup = {
		show: show,
		//showLogout: showLogout,
		hide: hide
	}
	
	/*$(document).ready(() => {
		$('.id , .pwd').on("keyup",(e) => {
			console.log('[ESC]', e.keyCode)
			if(e.keyCode == 27) {
				hide();
			}
		})
	})*/
})