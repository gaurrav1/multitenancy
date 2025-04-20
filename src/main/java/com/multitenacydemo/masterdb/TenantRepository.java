package com.multitenacydemo.masterdb;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TenantRepository extends JpaRepository<TenantEntity, Long> {
    Optional<TenantEntity> findByTenantIdAndStatus(String tenantId, String status);

    boolean existsByTenantId(String tenantId);
}
