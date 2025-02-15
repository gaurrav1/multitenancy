package com.multitenacydemo.config.intialConfig;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.multitenacydemo.config.TenantContext;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class MultiTenantDatasource extends AbstractRoutingDataSource {


    private final Map<Object, Object> targetDataSources = new ConcurrentHashMap<>();


	public MultiTenantDatasource() {
		super.setTargetDataSources(targetDataSources);
	}

    @Override
    protected Object determineCurrentLookupKey() {
        return TenantContext.getCurrentTenant();
    }

}
