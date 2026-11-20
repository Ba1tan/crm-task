package com.github.ba1tan.crm_task.service;

import com.github.ba1tan.crm_task.utils.WorkingHoursConstraint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.Instant;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class WorkingHoursPolicyTest {

    // 2026-11-25 - среда, 28-е - суббота, 29-е - воскресенье.
    // Время в таблице локальное (Asia/Almaty), встреча длится 60 минут.
    @ParameterizedTest(name = "{0} -> {1}")
    @CsvSource({
            "2026-11-25T08:59, false",   // до открытия
            "2026-11-25T09:00, true",    // ровно открытие
            "2026-11-25T16:59, true",    // заканчивается в 17:59
            "2026-11-25T17:00, true",    // заканчивается ровно в 18:00
            "2026-11-25T17:01, false",   // не умещается
            "2026-11-25T17:30, false",   // пример из ТЗ: начало в 17:30 - отказ
            "2026-11-25T18:00, false",   // после закрытия
            "2026-11-25T23:30, false",   // переход через полночь
            "2026-11-28T10:00, false",   // суббота
            "2026-11-29T10:00, false"    // воскресенье
    })
    void workingDayBoundaries(String localDateTime, boolean expected) {
        Instant start = LocalDateTime.parse(localDateTime).atZone(WorkingHoursConstraint.getZone()).toInstant();
        assertThat(WorkingHoursConstraint.fits(start)).isEqualTo(expected);
    }

    @Test
    void inputIsUtcButRuleIsAppliedInAlmatyTime() {
        // Almaty = UTC+5
        assertThat(WorkingHoursConstraint.fits(Instant.parse("2026-11-25T04:00:00Z"))).isTrue();   // 09:00
        assertThat(WorkingHoursConstraint.fits(Instant.parse("2026-11-25T03:59:00Z"))).isFalse();  // 08:59
        assertThat(WorkingHoursConstraint.fits(Instant.parse("2026-11-25T12:00:00Z"))).isTrue();   // 17:00
        assertThat(WorkingHoursConstraint.fits(Instant.parse("2026-11-25T12:30:00Z"))).isFalse();  // 17:30
    }
}
