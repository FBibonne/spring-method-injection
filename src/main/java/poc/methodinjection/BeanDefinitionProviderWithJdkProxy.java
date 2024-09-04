package poc.methodinjection;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.http.ResponseEntity;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Optional;

@Slf4j
public record BeanDefinitionProviderWithJdkProxy(RequestProcessor requestProcessor) implements BeanDefinitionProvider {

    @Override
    public Optional<BeanDefinitionWithName> provide(AnnotationMetadata classMetadata) {
        String endpointName = STR."proxy_\{classMetadata.getClassName()}";
        var controllerInterface = RegistrarForBeansWithInjectedMethods.findResolvableType(classMetadata);
        if (controllerInterface == null) {
            return Optional.empty();
        }
        InvocationHandler handler = this::processRequest;
        BeanDefinition beanDefinition = BeanDefinitionBuilder.rootBeanDefinition(controllerInterface,
                () -> Proxy.newProxyInstance(BeanDefinitionProviderWithJdkProxy.class.getClassLoader(), new Class[]{controllerInterface.getRawClass()}, handler)
        ).setScope(BeanDefinition.SCOPE_SINGLETON)
                .getBeanDefinition();
        return Optional.of(new BeanDefinitionWithName(endpointName, beanDefinition));
    }

    private Object processRequest(Object ignored, Method method, Object[] args) {
        ResponseEntity<String> response = requestProcessor.processRequest(method, args);
        log.info(response.getBody());
        return response;
    }


    /*
    Objet :
       1. abstraction du modèle métier (donner du sens)
       2. factorisation/généricité du code : ne pas se répéter => polymorphisme (ex: comparable)
       3. encapsulation du code : masquer la complexité, couplage faible
       4. structuration du code : lisibilité du code
     */
}
