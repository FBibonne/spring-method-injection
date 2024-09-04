package poc.methodinjection.aop;

import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.stereotype.Component;
import poc.methodinjection.RequestProcessor;
import poc.methodinjection.internal.exceptions.ArgumentException;

@Slf4j
@Component
public record EndPointMethodInjector(RequestProcessor requestProcessor) implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation invocation) throws ArgumentException {
        return requestProcessor.processRequest(invocation.getMethod(), invocation.getArguments());
    }
}