package com.multitenacydemo.config.hibernate;

import com.multitenacydemo.config.TenantContext;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.stereotype.Component;

@Component
public class TenantSchemaResolver implements CurrentTenantIdentifierResolver<String> {
	private static final String DEFAULT_TENANT_ID = "public";

	@Override
	public String resolveCurrentTenantIdentifier() {
		String tenant = TenantContext.getCurrentTenant();
		if (tenant != null) {
			return tenant;
		} else {
			return DEFAULT_TENANT_ID;
		}
	}

	@Override
	public boolean validateExistingCurrentSessions() {
		return true;
	}

	


}
