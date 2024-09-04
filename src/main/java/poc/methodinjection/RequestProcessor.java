package poc.methodinjection;

import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public record RequestProcessor() {
    public ResponseEntity<String> processRequest(Method method, Object[] args) {
        String process = STR."\{httpMethod(method)}\{entity(method)} with arguments \{arguments(method, args)}";
        return ResponseEntity.ok(process);
    }

    private String arguments(Method method, Object[] args) {
        return IntStream.range(0, args.length).mapToObj(i-> STR."\{method.getParameters()[i].getName()} = \{args[i]}")
                .toList()
                .toString();
    }

    private String entity(Method method) {
        return ResolvableType.forMethodReturnType(method).getGeneric(0).getRawClass().getSimpleName();
    }

    private List<String> httpMethod(Method method) {
        return Arrays.stream(MergedAnnotations.from(method).get(RequestMapping.class)
                .getValue("method", RequestMethod[].class).orElse(new RequestMethod[]{}))
                .map(RequestMethod::toString)
                .toList();
    }
}
