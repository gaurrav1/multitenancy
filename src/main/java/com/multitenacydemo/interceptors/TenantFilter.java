package com.multitenacydemo.interceptors;


import com.multitenacydemo.config.TenantContext;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class TenantFilter implements Filter {
	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
		HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
		String tenant = httpServletRequest.getHeader("X-TenantID");
		TenantContext.setCurrentTenant(tenant);
		System.out.println("\n\n\n" + tenant + "\n\n\n");

		try {
			filterChain.doFilter(servletRequest, servletResponse);
		} finally {
			TenantContext.clear();
		}
	}
}