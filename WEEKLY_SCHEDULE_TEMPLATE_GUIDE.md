# 📅 Weekly Schedule Template System - Implementation Guide

**Version:** 1.0  
**Date:** January 31, 2026  
**Status:** ✅ Complete & Production-Ready

---

## 🎯 OVERVIEW

This system allows doctors to set **recurring weekly schedule templates** that automatically generate daily schedules. Doctors can:

✅ Set default working hours for each day of the week  
✅ Customize shift times (Morning, Evening, Full Day, or Custom)  
✅ Exclude weekends (Saturday & Sunday) by default  
✅ Add weekend availability when needed  
✅ Override specific dates manually  
✅ Auto-generate schedules for upcoming days

---

## 🏗️ ARCHITECTURE

### **Database Schema**

#### **1. doctor_weekly_templates** (New Table)
```sql
- id: Primary key
- doctor_id: Foreign key to users table
- day_of_week: Integer (1=Monday, 7=Sunday)
- is_active: Boolean (true=working day, false=day off)
- shift_type: MORNING, EVENING, FULL_DAY, CUSTOM
- start_time: TIME
- end_time: TIME
- slot_duration_minutes: Default 15
- max_patients: Optional limit
```

**Purpose:** Stores the recurring weekly pattern for each doctor.

#### **2. doctor_schedules** (Enhanced Table)
```sql
+ is_template_generated: Boolean (auto-generated from template?)
+ is_override: Boolean (manual override of template?)
```

**Purpose:** Tracks if a daily schedule was auto-generated or manually created.

---

## 📦 NEW COMPONENTS

### **Entities**
- `DoctorWeeklyTemplate.java` - Weekly template entity

### **Repositories**
- `DoctorWeeklyTemplateRepository.java` - Data access for templates

### **Services**
- `DoctorWeeklyTemplateService.java` - Business logic for templates
- `ScheduleAutoGenerationService.java` - Scheduled task for auto-generation

### **Controllers**
- `DoctorWeeklyScheduleController.java` - Web controller for doctors

### **UI Templates**
- `weekly-schedule.html` - UI for managing weekly templates

### **Enums**
- `ShiftType` - Enhanced with `FULL_DAY` option

---

## 🔧 USAGE FLOW

### **Step 1: Doctor Sets Up Weekly Template**

1. Doctor navigates to `/doctor/weekly-schedule`
2. Clicks **"Initialize Weekly Schedule"**
3. Selects default shift type:
   - **Morning:** 9 AM - 1 PM
   - **Evening:** 2 PM - 6 PM
   - **Full Day:** 9 AM - 6 PM (with lunch break handled automatically)
   - **Custom:** Set custom hours
4. System creates templates for:
   - **Monday-Friday:** Active (based on selected shift)
   - **Saturday-Sunday:** Inactive (day off)

**Example:**
```
Monday:    09:00 - 13:00 (Morning)  ✅ Active
Tuesday:   09:00 - 13:00 (Morning)  ✅ Active
Wednesday: 09:00 - 13:00 (Morning)  ✅ Active
Thursday:  09:00 - 13:00 (Morning)  ✅ Active
Friday:    09:00 - 13:00 (Morning)  ✅ Active
Saturday:  ---                     ❌ Inactive (Day Off)
Sunday:    ---                     ❌ Inactive (Day Off)
```

---

### **Step 2: Customize Specific Days**

Doctor can edit each day individually:

1. Click **"Edit"** on any day card
2. Modify:
   - **Status:** Active / Inactive
   - **Shift Type:** Morning / Evening / Full Day / Custom
   - **Start Time:** e.g., 08:30
   - **End Time:** e.g., 14:00
   - **Slot Duration:** 10, 15, 20, or 30 minutes
   - **Max Patients:** Optional limit

**Example Customization:**
```
Monday:    09:00 - 13:00 (Morning)    ✅ Active
Tuesday:   14:00 - 18:00 (Evening)    ✅ Active  ← Changed to evening
Wednesday: 09:00 - 18:00 (Full Day)   ✅ Active  ← Full day
Thursday:  09:00 - 13:00 (Morning)    ✅ Active
Friday:    ---                        ❌ Inactive ← Took Friday off
Saturday:  10:00 - 14:00 (Custom)     ✅ Active  ← Added Saturday!
Sunday:    ---                        ❌ Inactive
```

---

### **Step 3: Generate Daily Schedules**

Doctor can generate actual daily schedules from templates:

1. Click **"Generate Daily Schedules"** button
2. Select date range:
   - **Start Date:** Today (or any future date)
   - **End Date:** Today + 30 days (or custom)
3. Click **"Generate Schedules"**

**What Happens:**
- System loops through each date in the range
- Checks if date has a weekly template (based on day of week)
- If template is **active** and no schedule exists for that date:
  - Creates a new `DoctorSchedule` with template settings
  - Marks it as `is_template_generated = true`
- If schedule already exists:
  - **Skips** (doesn't overwrite manual overrides)

**Example:**
```
Doctor generates Feb 1-28, 2026:

Feb 1 (Saturday)  → Template active → Create schedule (10:00-14:00)
Feb 2 (Sunday)    → Template inactive → Skip
Feb 3 (Monday)    → Template active → Create schedule (09:00-13:00)
Feb 4 (Tuesday)   → Template active → Create schedule (14:00-18:00)
...and so on
```

---

### **Step 4: Auto-Generation (Background)**

System automatically runs **daily at 2 AM**:

```java
@Scheduled(cron = "0 0 2 * * ?")
public void autoGenerateSchedulesDaily() {
    // For each doctor with weekly templates:
    // - Generate schedules for next 30 days
    // - Only create if doesn't exist
}
```

**Benefits:**
- Doctors don't have to manually generate schedules every week
- System always has 30 days of schedules ready
- Patients can book appointments in advance

---

### **Step 5: Manual Overrides**

Doctor can override a specific date:

1. Go to `/doctor/schedule` (existing daily schedule page)
2. Create/edit schedule for a specific date
3. System marks it as `is_override = true`
4. This schedule **won't be overwritten** by template generation

**Example Use Case:**
- Template says: Friday 9-13 (Morning)
- Doctor has a conference on Feb 7 (Friday)
- Manually creates schedule for Feb 7: 14-18 (Evening only)
- Template generation **skips** Feb 7 (respects manual override)

---

## 🔄 WORKFLOW DIAGRAM

```
┌───────────────────────────────────────────────────────────────┐
│                  Doctor Sets Weekly Template                  │
│  Monday-Friday: 9 AM - 1 PM (Active)                         │
│  Saturday-Sunday: Day Off (Inactive)                          │
└─────────────────────┬─────────────────────────────────────────┘
                      │
                      ▼
┌───────────────────────────────────────────────────────────────┐
│           Auto-Generation (Daily at 2 AM)                     │
│  OR Manual "Generate Schedules" button                        │
└─────────────────────┬─────────────────────────────────────────┘
                      │
                      ▼
┌───────────────────────────────────────────────────────────────┐
│         For each date in range (e.g., Feb 1-28):              │
│                                                                │
│  1. Get day of week (Monday=1, Sunday=7)                      │
│  2. Find template for this day                                │
│  3. If template is ACTIVE and schedule doesn't exist:         │
│     → Create DoctorSchedule                                   │
│     → Copy times from template                                │
│     → Mark as is_template_generated = true                    │
│  4. If schedule already exists:                               │
│     → SKIP (don't overwrite)                                  │
└─────────────────────┬─────────────────────────────────────────┘
                      │
                      ▼
┌───────────────────────────────────────────────────────────────┐
│               Daily Schedules Created!                        │
│  ✅ Feb 3 (Mon): 09:00-13:00 - Auto-generated                │
│  ✅ Feb 4 (Tue): 09:00-13:00 - Auto-generated                │
│  ✅ Feb 5 (Wed): 09:00-13:00 - Auto-generated                │
│  ✅ Feb 6 (Thu): 09:00-13:00 - Auto-generated                │
│  ✅ Feb 7 (Fri): 09:00-13:00 - Auto-generated                │
│  ❌ Feb 8 (Sat): No schedule (template inactive)             │
│  ❌ Feb 9 (Sun): No schedule (template inactive)             │
└───────────────────────────────────────────────────────────────┘
```

---

## 🎨 USER INTERFACE

### **Weekly Schedule Page**

#### **Header Section**
- **Title:** Weekly Schedule Template
- **Buttons:**
  - 🗓️ Daily Schedules (link to existing schedule page)
  - ✅ Generate Daily Schedules
  - 🔄 Reset Templates

#### **Summary Card** (if templates exist)
```
┌────────────────────────────────────────────────────────┐
│ 👨‍⚕️ Doctor: Dr. Sarah Wilson                          │
│ ✅ Active Days: 5 / 7                                  │
│ 💼 Specialization: Cardiology                          │
│ ℹ️ Status: Template Active                             │
└────────────────────────────────────────────────────────┘
```

#### **Day Cards** (for each day of the week)
```
┌─────────────────────────────────┐
│ MONDAY                          │
│ ⏰ 09:00 - 13:00               │
│ Shift: Morning                  │
│ Slot Duration: 15 min           │
│ Max Slots: 16                   │
│ [Edit] [Deactivate]             │
└─────────────────────────────────┘

┌─────────────────────────────────┐
│ SATURDAY            [Weekend]   │
│ Day Off                         │
│ Not available for appointments  │
│ [Edit] [Activate]               │
└─────────────────────────────────┘
```

#### **Initialization Form** (if no templates exist)
```
Setup Your Weekly Schedule
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
Set your default working hours for Monday to Friday.
Saturday and Sunday will be marked as days off.

Default Shift Type: [Morning ▼]
Morning Start: [09:00]
Morning End:   [13:00]
Evening Start: [14:00]
Evening End:   [18:00]
Slot Duration: [15 minutes ▼]

[Initialize Weekly Schedule]
```

---

## 📊 DATABASE EXAMPLES

### **Weekly Template for Dr. Sarah Wilson**

| ID | doctor_id | day_of_week | is_active | shift_type | start_time | end_time | slot_duration |
|----|-----------|-------------|-----------|------------|------------|----------|---------------|
| 1  | 5         | 1 (Mon)     | TRUE      | MORNING    | 09:00      | 13:00    | 15            |
| 2  | 5         | 2 (Tue)     | TRUE      | EVENING    | 14:00      | 18:00    | 15            |
| 3  | 5         | 3 (Wed)     | TRUE      | FULL_DAY   | 09:00      | 18:00    | 20            |
| 4  | 5         | 4 (Thu)     | TRUE      | MORNING    | 09:00      | 13:00    | 15            |
| 5  | 5         | 5 (Fri)     | TRUE      | MORNING    | 09:00      | 13:00    | 15            |
| 6  | 5         | 6 (Sat)     | TRUE      | CUSTOM     | 10:00      | 14:00    | 20            |
| 7  | 5         | 7 (Sun)     | FALSE     | NULL       | NULL       | NULL     | 15            |

### **Generated Daily Schedules**

| ID | doctor_id | schedule_date | shift_type | start_time | end_time | is_template_generated | is_override |
|----|-----------|---------------|------------|------------|----------|----------------------|-------------|
| 101| 5         | 2026-02-03    | MORNING    | 09:00      | 13:00    | TRUE                 | FALSE       |
| 102| 5         | 2026-02-04    | EVENING    | 14:00      | 18:00    | TRUE                 | FALSE       |
| 103| 5         | 2026-02-05    | FULL_DAY   | 09:00      | 18:00    | TRUE                 | FALSE       |
| 104| 5         | 2026-02-06    | MORNING    | 09:00      | 13:00    | TRUE                 | FALSE       |
| 105| 5         | 2026-02-07    | EVENING    | 15:00      | 19:00    | FALSE                | TRUE        |
| 106| 5         | 2026-02-08    | CUSTOM     | 10:00      | 14:00    | TRUE                 | FALSE       |

**Note:** Feb 7 (row 105) was manually overridden - doctor changed evening hours.

---

## 🔐 SECURITY & PERMISSIONS

- ✅ Only doctors with `ROLE_DOCTOR` can access weekly schedule
- ✅ Doctors can only manage their own templates
- ✅ Auto-generation runs as system user (`system-auto`)
- ✅ All actions are logged with `created_by` / `updated_by`

---

## 🚀 API ENDPOINTS

### **Web UI Endpoints**

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/doctor/weekly-schedule` | View weekly template page |
| POST | `/doctor/weekly-schedule/initialize` | Setup default Mon-Fri schedule |
| POST | `/doctor/weekly-schedule/update-day` | Update a specific day |
| POST | `/doctor/weekly-schedule/toggle-day/{day}` | Activate/deactivate a day |
| POST | `/doctor/weekly-schedule/generate-schedules` | Generate daily schedules |
| POST | `/doctor/weekly-schedule/reset` | Delete all templates |

---

## 🧪 TESTING SCENARIOS

### **Test 1: Initialize Default Schedule**
```
1. Doctor logs in
2. Goes to /doctor/weekly-schedule
3. Selects "Morning" shift
4. Clicks "Initialize Weekly Schedule"
5. ✅ Verify: 7 templates created (Mon-Fri active, Sat-Sun inactive)
```

### **Test 2: Add Saturday Availability**
```
1. Find Saturday card
2. Click "Edit"
3. Change status to "Active"
4. Set times: 10:00 - 14:00
5. Click "Save Changes"
6. ✅ Verify: Saturday template now active
```

### **Test 3: Generate Schedules**
```
1. Click "Generate Daily Schedules"
2. Set range: Feb 1 - Feb 28
3. Click "Generate"
4. ✅ Verify: ~20 schedules created (Mon-Fri + Sat)
5. ✅ Verify: Sundays skipped (no schedules)
```

### **Test 4: Manual Override**
```
1. Go to /doctor/schedule (daily schedule page)
2. Create schedule for Feb 15 (Friday): 15:00-19:00
3. Run auto-generation again for February
4. ✅ Verify: Feb 15 NOT overwritten (manual override respected)
```

### **Test 5: Auto-Generation (Scheduled)**
```
1. Wait for 2 AM (or trigger manually)
2. Check logs for "Starting auto-generation..."
3. ✅ Verify: Schedules created for next 30 days
4. ✅ Verify: No duplicates created
```

---

## 📈 BENEFITS FOR YOUR HOSPITAL

### **For Doctors**
✅ **Set once, forget it** - Weekly pattern auto-generates daily schedules  
✅ **Flexibility** - Override specific dates when needed  
✅ **Weekend control** - Easily add/remove weekend availability  
✅ **No manual work** - System handles schedule creation automatically

### **For Patients**
✅ **Always available** - Can book appointments 30 days in advance  
✅ **Predictable** - Know doctor's regular hours  
✅ **More options** - Some doctors work weekends

### **For Hospital Admins**
✅ **Consistency** - All doctors follow template system  
✅ **Automation** - No manual schedule entry needed  
✅ **Reporting** - Easy to see doctor availability patterns

### **For WhatsApp Integration**
✅ **Real-time availability** - WhatsApp bot can query available slots  
✅ **30-day booking window** - Patients can book via WhatsApp anytime  
✅ **Instant slot check** - Bot shows available times based on templates

---

## 🔧 MAINTENANCE

### **Database Migration**
```sql
-- Run this SQL file to set up tables:
source database-schema-weekly-schedules.sql
```

### **Initial Setup for Existing Doctors**
```java
// Run once to create templates for all existing doctors
DoctorWeeklyTemplateService service = ...;
List<User> doctors = userRepository.findByRoleName("DOCTOR");

for (User doctor : doctors) {
    service.setupDefaultWeeklySchedule(
        doctor, 
        ShiftType.MORNING, 
        LocalTime.of(9, 0), 
        LocalTime.of(13, 0),
        LocalTime.of(14, 0),
        LocalTime.of(18, 0),
        15,
        "admin"
    );
}
```

### **Scheduled Task Configuration**
```properties
# application.properties
# Scheduling is enabled via @EnableScheduling in WebappApplication.java

# Default cron: Daily at 2 AM
# You can change this in ScheduleAutoGenerationService.java:
# @Scheduled(cron = "0 0 2 * * ?")
```

---

## 🎯 SUMMARY

This Weekly Schedule Template System transforms your appointment system from:

**❌ Before:** Doctors manually create schedules every day/week  
**✅ After:** Doctors set weekly pattern once, system auto-generates forever

**Key Features:**
- 📅 Recurring weekly templates (Mon-Sun)
- 🚫 Default excludes weekends (can be enabled)
- ⏰ Custom shift times per day
- 🔄 Auto-generation (daily at 2 AM)
- ✏️ Manual overrides for exceptions
- 📱 Ready for WhatsApp integration

**Production-Ready:** ✅  
**Tested:** ✅  
**Documented:** ✅  
**Scalable:** ✅

---

**Created by:** Senior Java Developer  
**Date:** January 31, 2026  
**Status:** Ready for deployment! 🚀
