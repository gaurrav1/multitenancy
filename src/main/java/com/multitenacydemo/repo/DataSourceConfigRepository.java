package com.multitenacydemo.repo;

import com.multitenacydemo.model.DBDetails;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
@Lazy
public interface DataSourceConfigRepository extends JpaRepository<DBDetails, String> {
}
