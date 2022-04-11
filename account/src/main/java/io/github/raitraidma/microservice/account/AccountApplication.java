package io.github.raitraidma.microservice.account;

import io.github.raitraidma.microservice.common.filter.TransactionAwareRestContainerFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

@SpringBootApplication
public class AccountApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccountApplication.class, args);
    }

    @Bean
    public FilterRegistrationBean<TransactionAwareRestContainerFilter> atomikosFilter1(){
        FilterRegistrationBean<TransactionAwareRestContainerFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new TransactionAwareRestContainerFilter());
        registrationBean.setOrder(HIGHEST_PRECEDENCE);
        return registrationBean;
    }
}
