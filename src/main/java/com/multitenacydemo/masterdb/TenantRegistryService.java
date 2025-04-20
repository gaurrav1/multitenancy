package com.multitenacydemo.masterdb;

import org.springframework.stereotype.Service;

@Service
public class TenantRegistryService {

    private final TenantRepository tenantRepository;

    public TenantRegistryService(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    public TenantInfo getTenantInfo(String tenantId) {
        TenantEntity tenant = tenantRepository
                .findByTenantIdAndStatus(tenantId, "ACTIVE")
                .orElseThrow(() -> new RuntimeException("Tenant not found or inactive: " + tenantId));
        System.out.println("\n\n\nFound:" + tenant.getJdbcUrl() + "\n\n\n");

        return new TenantInfo(
                tenant.getJdbcUrl(),
                tenant.getUsername(),
                tenant.getPassword()
        );
    }

    public record TenantInfo(String url, String username, String password) {}
}
