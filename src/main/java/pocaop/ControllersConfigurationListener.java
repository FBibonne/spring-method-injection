package pocaop;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.boot.context.event.ApplicationContextInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.MethodMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Controller;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.*;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;


@Slf4j
public class ControllersConfigurationListener implements ApplicationListener<ApplicationContextInitializedEvent> {

    public static final String INTERFACE_CONTROLLERS_PACKAGE_KEY = "pocaop.interface-controllers.package";

    private static final Set<Class<? extends Annotation>> endpointsAnnotations = Set.of(RequestMapping.class,
            GetMapping.class, PostMapping.class, PutMapping.class, DeleteMapping.class, PatchMapping.class);

    private String interfaceControllerPackage;


    @Override
    public void onApplicationEvent(@NonNull ApplicationContextInitializedEvent event) {
        GenericApplicationContext genericApplicationContext = (GenericApplicationContext) event.getApplicationContext();
        interfaceControllerPackage = Objects.requireNonNull(genericApplicationContext.getEnvironment().getProperty(INTERFACE_CONTROLLERS_PACKAGE_KEY),
                STR."Must provide a package to search for controllers with property \{INTERFACE_CONTROLLERS_PACKAGE_KEY}");
        controllerInterfacesForEndpointImplementation().forEach(
                metadata -> lookupEnpointsImplementation(metadata, genericApplicationContext)
        );
    }


    private void lookupEnpointsImplementation(AnnotationMetadata classMetadata, GenericApplicationContext genericApplicationContext) {
        log.debug("Declaration of interface controller {} as a bean", classMetadata.getClassName());

        Class<?> controllerInterface;
        try {
            // LIKE org.springframework.data.util.AnnotatedTypeScanner.findTypes  !!
            controllerInterface = ClassUtils.forName(classMetadata.getClassName(), null);
        } catch (ClassNotFoundException e) {
            log.error(STR."Unable to find the class for \{classMetadata.getClassName()}", e);
            return;
        }

        log.atInfo().log(()->STR."Add controller interface : \{classMetadata.getClassName()}");

        List<String> namesOfMethodsToIntercept=getNamesOfMethodsToIntercept(classMetadata);

        log.atTrace().log(()-> STR."Declare advisor to intercept methods \{namesOfMethodsToIntercept}");
        var advisorName = STR."\{classMetadata.getClassName()}_endpointsReplacer";
        genericApplicationContext.registerBeanDefinition(advisorName, getPointcutBeanDefinition(namesOfMethodsToIntercept));
        log.trace("Advisor {} registred",advisorName);
        genericApplicationContext.registerBean(classMetadata.getClassName(), ControllerProxyFactoryBean.class, controllerInterface, advisorName);
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

    private static List<String> getNamesOfMethodsToIntercept(AnnotationMetadata classMetadata) {
        return classMetadata.getDeclaredMethods().stream()
                .filter(ControllersConfigurationListener::isEndpointMethod)
                .map(MethodMetadata::getMethodName)
                .toList();
    }

    private static boolean isEndpointMethod(MethodMetadata method) {
        return endpointsAnnotations.stream().anyMatch(method.getAnnotations()::isDirectlyPresent);
    }

    private Stream<? extends AnnotationMetadata> controllerInterfacesForEndpointImplementation() {
        return findAllInterfacesRestControllerInPackage(interfaceControllerPackage);
    }

    private Stream<? extends AnnotationMetadata> findAllInterfacesRestControllerInPackage(String interfaceControllerPackage) {
        var scanner = new ClassPathScanningCandidateComponentProvider(false) {
            @Override
            protected boolean isCandidateComponent(@NonNull AnnotatedBeanDefinition beanDefinition) {
                var metadata = beanDefinition.getMetadata();
                return metadata.isIndependent() && metadata.isInterface();
            }
        };
        scanner.addIncludeFilter(new AnnotationTypeFilter(Controller.class));
        var beandefs = scanner.findCandidateComponents(interfaceControllerPackage);
        if (beandefs.isEmpty()) {
            log.warn("No controllers found in package {}", interfaceControllerPackage);
        }
        return beandefs.stream().map(ScannedGenericBeanDefinition.class::cast)
                .map(ScannedGenericBeanDefinition::getMetadata);
    }


}
