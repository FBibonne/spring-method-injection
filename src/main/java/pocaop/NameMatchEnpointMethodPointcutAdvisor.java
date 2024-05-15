package pocaop;

import org.springframework.aop.support.NameMatchMethodPointcutAdvisor;

public class NameMatchEnpointMethodPointcutAdvisor extends NameMatchMethodPointcutAdvisor {

    public NameMatchEnpointMethodPointcutAdvisor(EndPointMethodInjector endPointMethodInjector){
        super(endPointMethodInjector);
    }

}
