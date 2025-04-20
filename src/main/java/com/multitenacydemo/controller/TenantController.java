package com.multitenacydemo.controller;

import com.multitenacydemo.masterdb.TenantEntity;
import com.multitenacydemo.service.TenantService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tenant")
public class TenantController {

    private final TenantService tenantService;

    public TenantController(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    @GetMapping("/")
    public List<TenantEntity> tenants() {
        return tenantService.getAllTenants();
    }

    @PostMapping("/")
    public TenantEntity create(@RequestBody Map<String, String> body) {
        return tenantService.createTenant(body.get("tenantId"), body.get("name"));
    }
}
