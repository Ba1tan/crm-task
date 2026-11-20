package com.github.ba1tan.crm_task.utils;

import java.time.*;

public final class WorkingHoursConstraint {

    private static final ZoneId ZONE = ZoneId.of("Asia/Almaty");
    private static final Duration DURATION = Duration.ofMinutes(60);
    private static final LocalTime OPEN_TIME = LocalTime.of(9, 0);
    private static final LocalTime CLOSE_TIME = LocalTime.of(18, 0);

    public WorkingHoursConstraint(){
    }

    public static boolean fits(Instant start)
    {
        ZonedDateTime s = start.atZone(ZONE);
        ZonedDateTime e = start.plus(DURATION).atZone(ZONE);
        DayOfWeek day = s.getDayOfWeek();
        return day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY && s.toLocalDate().equals(e.toLocalDate())
                && !s.toLocalTime().isBefore(OPEN_TIME) && !e.toLocalTime().isAfter(CLOSE_TIME);
    }

    public static ZoneId getZone()
    {
        return ZONE;
    }
    public static Duration getDuration()
    {
        return DURATION;
    }
}
