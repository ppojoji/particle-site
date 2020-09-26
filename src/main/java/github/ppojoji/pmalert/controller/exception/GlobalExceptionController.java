package github.ppojoji.pmalert.controller.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import github.ppojoji.pmalert.PmException;
import github.ppojoji.pmalert.Res;
import github.ppojoji.pmalert.TypeMap;

@ControllerAdvice
public class GlobalExceptionController {

	@ExceptionHandler(PmException.class)
	@ResponseBody
    public Object nullPointerException(PmException e) {
		System.out.println("[ERROR] " + e.toString());
		int responseCode = e.getResponseCode();
		HttpStatus status = codeToStatus(responseCode);
		TypeMap body = Res.fail(
				"cause", e.getErrorCode(),
				"desc", e.getErrorCode());
       return new ResponseEntity<TypeMap>(body, status);
    }
	
	private HttpStatus codeToStatus(int resCode) {
		for (HttpStatus status : HttpStatus.values()) {
			if (status.value() == resCode) {
				return status;
			}
		}
		return HttpStatus.BAD_REQUEST;
	}
}
