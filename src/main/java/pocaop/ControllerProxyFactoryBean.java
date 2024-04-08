package pocaop;

import org.springframework.aop.framework.ProxyFactoryBean;

public class ControllerProxyFactoryBean extends ProxyFactoryBean {

    public ControllerProxyFactoryBean(Class<?> controllerClass, String advisorName) {
        setSingleton(true);
        setInterfaces(controllerClass);
        setInterceptorNames(advisorName);
    }
}
