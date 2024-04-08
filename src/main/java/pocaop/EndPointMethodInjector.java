package pocaop;

import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import pocaop.internal.GenericRequestProcessor;
import pocaop.internal.exceptions.ArgumentException;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;

@Slf4j
@Component
@Scope(SCOPE_SINGLETON)
public record EndPointMethodInjector(GenericRequestProcessor genericRequestProcessor) implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation invocation) throws ArgumentException {
        var entityToReturn = genericRequestProcessor.process(invocation.getMethod(), invocation.getArguments());
        return entityToReturn.map(ResponseEntity.ok()::body).orElse(ResponseEntity.notFound().build());
    }
}