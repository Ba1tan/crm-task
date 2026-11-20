package com.github.ba1tan.crm_task.repository;

import com.github.ba1tan.crm_task.entity.Manager;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface ManagerRepository extends JpaRepository<Manager, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select m from Manager m where m.id = :id")
    Optional<Manager> findByIdForUpdate(@Param("id") Long id);
}