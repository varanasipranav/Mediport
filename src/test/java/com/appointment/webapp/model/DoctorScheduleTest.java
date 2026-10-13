package com.appointment.webapp.model;

import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DoctorScheduleTest {

    private DoctorSchedule schedule(String start, String end, int slot, int priority) {
        DoctorSchedule s = new DoctorSchedule();
        s.setStartTime(LocalTime.parse(start));
        s.setEndTime(LocalTime.parse(end));
        s.setSlotDurationMinutes(slot);
        s.setPriorityWorkMinutes(priority);
        return s;
    }

    @Test
    void timeFollowsQueueNumber() {
        DoctorSchedule s = schedule("09:00", "13:00", 15, 0);
        assertEquals(LocalTime.of(9, 0), s.calculateScheduledTime(1));
        assertEquals(LocalTime.of(9, 45), s.calculateScheduledTime(4));
    }

    @Test
    void priorityWorkPushesEveryoneLater() {
        DoctorSchedule s = schedule("09:00", "13:00", 15, 30);
        assertEquals(LocalTime.of(9, 30), s.calculateScheduledTime(1));
    }

    @Test
    void lastSlotMustFinishInsideWorkingHours() {
        DoctorSchedule s = schedule("09:00", "10:00", 15, 0);
        assertTrue(s.hasSlotFor(4));   // 09:45 - 10:00
        assertFalse(s.hasSlotFor(5));  // would run past 10:00
    }

    @Test
    void priorityWorkReducesCapacity() {
        DoctorSchedule s = schedule("09:00", "10:00", 15, 10);
        assertTrue(s.hasSlotFor(3));   // 09:40 - 09:55
        assertFalse(s.hasSlotFor(4));  // 09:55 - 10:10
    }
}
