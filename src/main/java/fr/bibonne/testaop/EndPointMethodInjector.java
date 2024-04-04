package fr.bibonne.testaop;

import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;

@Slf4j
@Component
@Scope(SCOPE_SINGLETON)
public record EndPointMethodInjector implements MethodInterceptor {


    @Override
    public Object invoke(MethodInvocation invocation) {
        var methodName=invocation.getMethod().getName();

        log.debug("Process call to {} by AOP", invocation.getMethod().getName());
        try {
            Optional<String> body = readBody(webRequest, invocation.getMethod(), invocation.getArguments());
            HttpHeaders headers = headers(webRequest);
            return passePlatUtility.allRequest(HttpMethod.valueOf(webRequest.getMethod()), webRequest.getServletPath(), headers, body);
        } catch (IOException e) {
            log.error("While preparing request {} {} for remote call", webRequest.getMethod(), webRequest.getServletPath(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}