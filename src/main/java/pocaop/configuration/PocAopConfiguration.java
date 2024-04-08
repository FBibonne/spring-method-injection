package pocaop.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import pocaop.internal.GenericRequestProcessor;
import pocaop.internal.QueryExecutor;
import pocaop.internal.Unmarshaller;

@Component
public class PocAopConfiguration {

    @Bean
    public GenericRequestProcessor getRequestProcessor(QueryExecutor queryExecutor, Unmarshaller unmarshaller) {
        return new GenericRequestProcessor(queryExecutor, unmarshaller);
    }

}
