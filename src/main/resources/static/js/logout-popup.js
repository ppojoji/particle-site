$(document).ready(() => {
	const template = {
		logout : `<div class="login-popup">
		<div class="login-container">
			<!--<h3>로그인</h3>-->
			<p>로그아웃 하시겠습니까?</p>
			<div class="item mgt-1rem">
				<button class="login-btn"><span>LOGOUT</span></button>
				<button class="login-btn"><span>CANCEL</span></button>
			</div>
		</div>`
	}
	
	let $wrapper 
	function show() {
		$wrapper = $(template.logout).appendTo("body")
	}
	
	window.logoutPopUp = {
		show : show
	}
})