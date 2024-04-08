package pocaop.internal;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import pocaop.internal.exceptions.ArgumentException;

import java.lang.reflect.Method;
import java.util.Optional;

import static java.util.Optional.ofNullable;

@Slf4j
public record GenericRequestProcessor(QueryTemplateSupplier queryTemplateSupplier, QueryExecutor queryExecutor, Unmarshaller unmarshaller) {

    public GenericRequestProcessor(QueryExecutor queryExecutor, Unmarshaller unmarshaller){
        this(new QueryTemplateSupplier(), queryExecutor, unmarshaller);
    }


    public Optional<?> process(@NonNull Method method, @NonNull Object[] arguments) throws ArgumentException{

        String methodName=method.getName();
        log.debug("Process call to {} by EndPointMethodInjector", methodName);
        Optional<QueryTemplate> queryTemplate= queryTemplateSupplier.get(methodName);
        if (queryTemplate.isEmpty()) {
            throw new UnsupportedOperationException(STR."Method \{methodName} not supported");
        }
        String query = queryTemplate.get().format(arguments);
        return ofNullable(unmarshaller.unmarshal(queryExecutor.execute(query), method));
    }
}
