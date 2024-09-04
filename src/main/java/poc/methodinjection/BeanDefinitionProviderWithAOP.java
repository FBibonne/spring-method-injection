package poc.methodinjection;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.MethodMetadata;
import org.springframework.util.ClassUtils;
import poc.methodinjection.aop.NameMatchEnpointMethodPointcutAdvisor;

import java.util.List;
import java.util.Optional;

import static poc.methodinjection.RegistrarForBeansWithInjectedMethods.ENDPOINTS_ANNOTATIONS;

@Slf4j
public record BeanDefinitionProviderWithAOP() implements BeanDefinitionProvider {
    @Override
    public Optional<BeanDefinitionWithName> provide(AnnotationMetadata classMetadata) {

        var controllerInterface=RegistrarForBeansWithInjectedMethods.findResolvableType(classMetadata);

        log.atInfo().log(()->STR."Add controller interface : \{classMetadata.getClassName()}");

        List<String> namesOfMethodsToIntercept=getNamesOfMethodsToIntercept(classMetadata);

        log.atTrace().log(()-> STR."Declare advisor to intercept methods \{namesOfMethodsToIntercept}");
        var advisorName = STR."\{classMetadata.getClassName()}_endpointsReplacer";
        log.trace("Created advisor {} for interface {}",advisorName, classMetadata.getClassName());
        return Optional.of(new BeanDefinitionWithName(advisorName, getPointcutBeanDefinition(namesOfMethodsToIntercept)));
    }

    private static List<String> getNamesOfMethodsToIntercept(AnnotationMetadata classMetadata) {
        return classMetadata.getDeclaredMethods().stream()
                .filter(BeanDefinitionProviderWithAOP::isEndpointMethod)
                .map(MethodMetadata::getMethodName)
                .toList();
    }

    private static boolean isEndpointMethod(MethodMetadata method) {
        return ENDPOINTS_ANNOTATIONS.stream().anyMatch(method.getAnnotations()::isDirectlyPresent);
    }

    private BeanDefinition getPointcutBeanDefinition(List<String> namesOfMethodsToIntercept) {
        var advisorBeanDefinition = BeanDefinitionBuilder.genericBeanDefinition(NameMatchEnpointMethodPointcutAdvisor.class)
                .setScope(BeanDefinition.SCOPE_SINGLETON)
                .getBeanDefinition();
        var propertyValues = new MutablePropertyValues();
        propertyValues.addPropertyValue("mappedNames", namesOfMethodsToIntercept);
        advisorBeanDefinition.setPropertyValues(propertyValues);

        return advisorBeanDefinition;
    }

}
