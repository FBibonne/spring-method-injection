package pocaop.otherendpoints;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pocaop.ControllersConfigurationListener;
import pocaop.internal.exceptions.ArgumentException;

@ControllerAdvice("${"+ ControllersConfigurationListener.INTERFACE_CONTROLLERS_PACKAGE_KEY +"}")
public record ExceptionsHandler() {

    @ExceptionHandler({UnsupportedOperationException.class, ArgumentException.class})
    public ResponseEntity<String> handleUnsupportedOperationException(UnsupportedOperationException exception) {
        return ResponseEntity.badRequest().body(exception.getMessage());
    }

}
