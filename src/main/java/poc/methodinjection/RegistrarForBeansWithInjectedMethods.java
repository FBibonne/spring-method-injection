package poc.methodinjection;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.ResolvableType;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.*;

import java.lang.annotation.Annotation;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;


@Slf4j
public class RegistrarForBeansWithInjectedMethods implements BeanDefinitionRegistryPostProcessor, EnvironmentAware {

    public static final String INTERFACE_CONTROLLERS_PACKAGE_KEY = "pocaop.interface-controllers.package";

    public static final Set<Class<? extends Annotation>> ENDPOINTS_ANNOTATIONS = Set.of(RequestMapping.class,
            GetMapping.class, PostMapping.class, PutMapping.class, DeleteMapping.class, PatchMapping.class);

    private String interfaceControllerPackage;
    private Environment environment;
    private final BeanDefinitionProvider beanDefinitionProvider;

    public RegistrarForBeansWithInjectedMethods(BeanDefinitionProvider beanDefinitionProvider) {
        this.beanDefinitionProvider = beanDefinitionProvider;
    }


    @Override
    public void postProcessBeanDefinitionRegistry(@NonNull BeanDefinitionRegistry registry) throws BeansException {
        interfaceControllerPackage = Objects.requireNonNull(environment.getProperty(INTERFACE_CONTROLLERS_PACKAGE_KEY),
                STR."Must provide a package to search for controllers with property \{INTERFACE_CONTROLLERS_PACKAGE_KEY}");
        controllerInterfacesToImplement().forEach(
                metadata -> registerControllerImplementation(metadata, registry)
        );
    }

    private void registerControllerImplementation(AnnotationMetadata classMetadata, BeanDefinitionRegistry registry) {
        log.debug("Declaration of interface controller {} as a bean", classMetadata.getClassName());
        var beanDefinitionWithName = this.beanDefinitionProvider.provide(classMetadata);
        if (beanDefinitionWithName.isEmpty()) {
            log.warn("Unable to create bean definition for {} : no bean for this controller will be registred", classMetadata.getClassName());
            return;
        }
        registry.registerBeanDefinition(beanDefinitionWithName.get().name(), beanDefinitionWithName.get().beanDefinition());
    }

    private Stream<? extends AnnotationMetadata> controllerInterfacesToImplement() {
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

    public static ResolvableType findResolvableType(AnnotationMetadata metadata) {
        try {
            // LIKE org.springframework.data.util.AnnotatedTypeScanner.findTypes  !!
            return ResolvableType.forClass(ClassUtils.forName(Objects.requireNonNull(metadata).getClassName(), null));
        } catch (ClassNotFoundException e) {
            log.error(STR."Unable to find the class for \{metadata.getClassName()}", e);
            return null;
        }
    }

    @Override
    public void setEnvironment(@NonNull Environment environment) {
        this.environment = environment;
    }
}
