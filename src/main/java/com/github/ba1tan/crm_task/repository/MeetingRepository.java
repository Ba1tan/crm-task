package com.github.ba1tan.crm_task.repository;

import com.github.ba1tan.crm_task.entity.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {
    @Query("""
            select count(m) from Meeting m
            where m.manager.id = :managerId
              and m.status = com.github.ba1tan.crm_task.entity.MeetingStatus.SCHEDULED
              and m.startAt < :endAt and m.endAt > :startAt
              and m.id <> :excludeId""")
    long countManagerOverlaps(@Param("managerId") long managerId, @Param("startAt") Instant startAt,
                             @Param("endAt") Instant endAt, @Param("excludeId") long excludeId);

    @Query("""
            select count(m) from Meeting m
            where m.company.id = :companyId
              and m.status = com.github.ba1tan.crm_task.entity.MeetingStatus.SCHEDULED
              and m.startAt < :endAt and m.endAt > :startAt
              and m.id <> :excludeId""")
    long countCompanyOverlaps(@Param("companyId") long companyId, @Param("startAt") Instant startAt,
                              @Param("endAt") Instant endAt, @Param("excludeId") long excludeId);

    @Query("""
            select m from Meeting m join fetch m.company
            where m.manager.id = :managerId
              and m.status = com.github.ba1tan.crm_task.entity.MeetingStatus.SCHEDULED
              and m.startAt >= :from and m.startAt < :to
            order by m.startAt""")
    List<Meeting> findSchedule(@Param("managerId") long managerId,
                                   @Param("from") Instant from, @Param("to") Instant to);
}
