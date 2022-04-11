package io.github.raitraidma.microservice.order;

import com.atomikos.remoting.spring.rest.TransactionAwareRestClientInterceptor;
import io.github.raitraidma.microservice.common.filter.TransactionAwareRestContainerFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

@SpringBootApplication
public class OrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setInterceptors(List.of(new TransactionAwareRestClientInterceptor()));
        return restTemplate;
    }

    @Bean
    public FilterRegistrationBean<TransactionAwareRestContainerFilter> atomikosFilter1(){
        FilterRegistrationBean<TransactionAwareRestContainerFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new TransactionAwareRestContainerFilter());
        registrationBean.setOrder(HIGHEST_PRECEDENCE);
        return registrationBean;
    }
}
