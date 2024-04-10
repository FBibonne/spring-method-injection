package pocaop.otherendpoints;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pocaop.internal.exceptions.ArgumentException;

@ControllerAdvice()
public record ExceptionsHandler() {

    @ExceptionHandler({UnsupportedOperationException.class, ArgumentException.class, HttpMessageNotWritableException.class})
    // cf. excellent article about Spring MVC errors handling : https://spring.io/blog/2013/11/01/exception-handling-in-spring-mvc
    public ResponseEntity<String> handleException(Exception exception) {
        return switch (exception){
            case HttpMessageNotWritableException _ ->  ResponseEntity.badRequest().body("Specify a valid value for Accept and Accept-Content : application/json or application/xml");
            default -> ResponseEntity.badRequest().body(exception.getMessage());
        };
    }

}
