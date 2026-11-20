package com.github.ba1tan.crm_task.service;

import com.github.ba1tan.crm_task.dto.ScheduleResponse;
import com.github.ba1tan.crm_task.exception.ApiException;
import com.github.ba1tan.crm_task.repository.ManagerRepository;
import com.github.ba1tan.crm_task.repository.MeetingRepository;
import com.github.ba1tan.crm_task.utils.WorkingHoursConstraint;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;


@Service
@RequiredArgsConstructor
public class ManagerService {
    private final ManagerRepository managerRepo;
    private final MeetingRepository meetingRepo;
    @Transactional(readOnly = true)
    public ScheduleResponse schedule(Long managerId, LocalDate date)
    {
        if(!managerRepo.existsById(managerId))
        {
            throw ApiException.notFound("Менеджер не найден");
        }
        Instant from = date.atStartOfDay(WorkingHoursConstraint.getZone()).toInstant();
        Instant to = date.plusDays(1).atStartOfDay(WorkingHoursConstraint.getZone()).toInstant();
        var items = meetingRepo.findSchedule(managerId, from, to).stream()
                .map(m -> new ScheduleResponse.Item(m.getId(), m.getCompany().getId(), m.getCompany().getName(), m.getStartAt(), m.getEndAt(), m.getInitialStartAt())).toList();
        return new ScheduleResponse(managerId, date, WorkingHoursConstraint.getZone().getId(), items);
    }

}
