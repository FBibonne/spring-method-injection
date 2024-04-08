package pocaop.otherendpoints;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pocaop.ControllersConfigurationListener;

@ControllerAdvice("${"+ ControllersConfigurationListener.INTERFACE_CONTROLLERS_PACKAGE_KEY +"}")
public record ExceptionsHandler() {

    @ExceptionHandler(UnsupportedOperationException.class)
    public ResponseEntity<String> handleUnsupportedOperationException(UnsupportedOperationException exception) {
        return ResponseEntity.badRequest().body(exception.getMessage());
    }

}
