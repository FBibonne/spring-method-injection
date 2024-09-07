package poc.methodinjection.jdkproxy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.type.AnnotationMetadata;
import poc.methodinjection.BeanDefinitionProvider;
import poc.methodinjection.BeanDefinitionWithName;

import java.util.Optional;

@Slf4j
public record BeanDefinitionProviderWithJdkProxy() implements BeanDefinitionProvider {

    @Override
    public Optional<BeanDefinitionWithName> provide(AnnotationMetadata classMetadata) {
        String endpointName = STR."proxy_\{classMetadata.getClassName()}";
        var controllerInterface = RegistrarForBeansWithInjectedMethods.findResolvableType(classMetadata);
        if (controllerInterface == null) {
            return Optional.empty();
        }

        ConstructorArgumentValues arguments = new ConstructorArgumentValues();

        RootBeanDefinition beanDefinition = new RootBeanDefinition();
        beanDefinition.setTargetType(controllerInterface);
        beanDefinition.setScope(BeanDefinition.SCOPE_SINGLETON);
        beanDefinition.setFactoryMethodName("proxyProviderForJdkProxy");
        beanDefinition.setFactoryBeanName(RegistrarForBeansWithInjectedMethods.BEAN_NAME);
        arguments.addGenericArgumentValue(controllerInterface);
        beanDefinition.setConstructorArgumentValues(arguments);
        return Optional.of(new BeanDefinitionWithName(endpointName, beanDefinition));
    }


    /*
    Objet :
       1. abstraction du modèle métier (donner du sens)
       2. factorisation/généricité du code : ne pas se répéter => polymorphisme (ex: comparable)
       3. encapsulation du code : masquer la complexité, couplage faible
       4. structuration du code : lisibilité du code
     */
}
