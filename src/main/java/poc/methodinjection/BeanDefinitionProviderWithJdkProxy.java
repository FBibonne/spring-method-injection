package poc.methodinjection;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public record BeanDefinitionProviderWithJdkProxy(RequestProcessor requestProcessor) implements BeanDefinitionProvider {

    @Override
    public Optional<BeanDefinitionWithName> provide(AnnotationMetadata classMetadata) {
        String endpointName = STR."proxy_\{classMetadata.getClassName()}";
        var controllerInterface = RegistrarForBeansWithInjectedMethods.findResolvableType(classMetadata);
        if (controllerInterface == null) {
            return Optional.empty();
        }
        final Set<String> controllerMethods = extractEndpointsMethods(controllerInterface);
        InvocationHandler handler = new InvocationHandler() {

            final Object support = new Object();

            @Override
            public Object invoke(Object proxy, Method method, Object[] args) {
                if (controllerMethods.contains(method.getName())) {
                    ResponseEntity<String> response = requestProcessor.processRequest(method, args);
                    log.info(response.getBody());
                    return response;
                }
                return switch (method.getName()){
                    case "toString" -> STR."\{controllerInterface.getRawClass().getName()}&\{proxy.getClass().getName()}";
                    case "hashCode" -> System.identityHashCode(proxy);
                    case "equals" -> args.length>0 && proxy==args[0];
                    default -> invokeOriginalMethod(support, method, args);
                };
            }
        };
        BeanDefinition beanDefinition = BeanDefinitionBuilder.rootBeanDefinition(controllerInterface,
                () -> Proxy.newProxyInstance(BeanDefinitionProviderWithJdkProxy.class.getClassLoader(), new Class[]{controllerInterface.getRawClass()}, handler)
        ).setScope(BeanDefinition.SCOPE_SINGLETON)
                .getBeanDefinition();
        return Optional.of(new BeanDefinitionWithName(endpointName, beanDefinition));
    }

    private static Set<String> extractEndpointsMethods(ResolvableType controllerInterface) {
        return Arrays.stream(controllerInterface.getRawClass().getMethods())
                .filter(method -> MergedAnnotations.from(method).isPresent(RequestMapping.class))
                .map(Method::getName)
                .collect(Collectors.toSet());
    }

    private static Object invokeOriginalMethod(Object proxy, Method method, Object[] args) {
        try {
            return method.invoke(proxy, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.debug(STR."Error while invoking \{method.getName()}", e);
            return null;
        }
    }


    /*
    Objet :
       1. abstraction du modèle métier (donner du sens)
       2. factorisation/généricité du code : ne pas se répéter => polymorphisme (ex: comparable)
       3. encapsulation du code : masquer la complexité, couplage faible
       4. structuration du code : lisibilité du code
     */
}
