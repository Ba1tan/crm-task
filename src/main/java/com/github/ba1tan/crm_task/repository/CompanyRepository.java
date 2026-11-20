package com.github.ba1tan.crm_task.repository;

import com.github.ba1tan.crm_task.entity.Company;
import com.github.ba1tan.crm_task.entity.CompanyStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface CompanyRepository extends JpaRepository<Company, Long> {
    boolean existsByBin(String bin);

    Page<Company> findByStatus(CompanyStatus status, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Company c where c.id = :id")
    Optional<Company> findByIdForUpdate(@Param("id") Long id);

}