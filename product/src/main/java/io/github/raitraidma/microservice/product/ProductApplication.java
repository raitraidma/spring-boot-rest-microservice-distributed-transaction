package io.github.raitraidma.microservice.product;

import io.github.raitraidma.microservice.common.filter.TransactionAwareRestContainerFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

@SpringBootApplication
public class ProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductApplication.class, args);
    }

    @Bean
    public FilterRegistrationBean<TransactionAwareRestContainerFilter> atomikosFilter1(){
        FilterRegistrationBean<TransactionAwareRestContainerFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new TransactionAwareRestContainerFilter());
        registrationBean.setOrder(HIGHEST_PRECEDENCE);
        return registrationBean;
    }
}
