$(document).ready(() => {
	const template = {
		logout : `<div class="popup-bg">
		<div class="popup-container">
			<!--<h3>로그인</h3>-->
			<p>로그아웃 하시겠습니까?</p>
			<div class="item mgt-1rem">
				<button class="btn-red logout-btn"><span>LOGOUT</span></button>
				<button class="logout-success"><span>로그아웃 성공</span></button>
				<button class="logout-fail"><span>로그아웃 실패</span></button>
			</div>
		</div>`
	}
	
	let $wrapper 
	function show() {
		$wrapper = $(template.logout).appendTo("body")
		$wrapper.find('.logout-success, .logout-fail').hide();
		
		$wrapper.find('.logout-btn').on('click', () => {
			$.ajax({
				url : "/logout",
				method : "POST",
				success(res){
					//alert("로그아웃 성공");
					location.href = '/';
				}
			})
		})
	
		$wrapper.on('click', function(){
			hide();
		})
		
		$wrapper.find('.popup-container').on('click',function(e){
			e.stopPropagation()
		})
	}
	
	function hide() {
		$wrapper.remove()
	}
	
	window.logoutPopUp = {
		show : show
	}
})