package com.multitenacydemo.security;

import com.multitenacydemo.interceptors.TenantFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecConfig {
    @Bean
    public FilterRegistrationBean<TenantFilter> tenantInterceptorFilter(TenantFilter tenantInterceptor) {
        FilterRegistrationBean<TenantFilter> registrationBean = new FilterRegistrationBean<>(tenantInterceptor);

        registrationBean.setOrder(-100);

        return registrationBean;
    }

    @Bean
    SecurityFilterChain defaultSecuirtyFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests( (req) -> req
                        .anyRequest().permitAll()
                );
    return http.build();
    }


}
