package pocaop;

import org.springframework.aop.support.NameMatchMethodPointcutAdvisor;
import org.springframework.beans.factory.annotation.Autowired;

public class NameMatchEnpointMethodPointcutAdvisor extends NameMatchMethodPointcutAdvisor {

    @Autowired
    public NameMatchEnpointMethodPointcutAdvisor(EndPointMethodInjector endPointMethodInjector){
        super(endPointMethodInjector);
    }

}
