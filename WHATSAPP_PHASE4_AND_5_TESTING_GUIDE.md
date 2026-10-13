# 🧪 PHASE 4 & 5 Testing Guide - Complete Appointment Booking

## 🚀 QUICK START

### **Prerequisites:**

1. ✅ Application compiled successfully
2. ✅ Database has DOCTOR role and at least one doctor
3. ✅ Patient registration working (Phase 3)

---

## 📋 TEST COMPLETE BOOKING FLOW

### **Setup: Create Test Doctor**

```sql
-- 1. Check if DOCTOR role exists
SELECT * FROM roles WHERE name = 'DOCTOR';

-- If not, create it:
INSERT INTO roles (name) VALUES ('DOCTOR');

-- 2. Create a test doctor
INSERT INTO users (
    username, password, first_name, last_name,
    phone_number, email, specialization, department,
    is_enabled, account_non_locked, created_by
) VALUES (
    'doctor1',
    '$2a$11$dummy.hash.for.testing',
    'Sarah',
    'Wilson',
    '1234567890',
    'sarah@hospital.com',
    'Cardiology',
    'Cardiac Department',
    true,
    true,
    'admin'
);

-- 3. Assign DOCTOR role
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.username = 'doctor1' AND r.name = 'DOCTOR';

-- 4. Create doctor schedule (if your system uses it)
-- Check if this table exists first
INSERT INTO doctor_schedules (doctor_id, day_of_week, start_time, end_time, is_active)
SELECT u.id, 'MONDAY', '09:00:00', '17:00:00', true
FROM users u WHERE u.username = 'doctor1'
UNION ALL
SELECT u.id, 'TUESDAY', '09:00:00', '17:00:00', true
FROM users u WHERE u.username = 'doctor1'
UNION ALL
SELECT u.id, 'WEDNESDAY', '09:00:00', '17:00:00', true
FROM users u WHERE u.username = 'doctor1'
UNION ALL
SELECT u.id, 'THURSDAY', '09:00:00', '17:00:00', true
FROM users u WHERE u.username = 'doctor1'
UNION ALL
SELECT u.id, 'FRIDAY', '09:00:00', '17:00:00', true
FROM users u WHERE u.username = 'doctor1'
UNION ALL
SELECT u.id, 'SATURDAY', '09:00:00', '17:00:00', true
FROM users u WHERE u.username = 'doctor1';

-- Verify
SELECT u.first_name, u.last_name, u.specialization, r.name as role
FROM users u
JOIN user_roles ur ON u.id = ur.user_id
JOIN roles r ON ur.role_id = r.id
WHERE r.name = 'DOCTOR';
```

---

## 🧪 TEST 1: Complete Booking (Happy Path)

### **Steps:**

**1. Start Conversation**
```
WhatsApp: Hi
```

**2. Book Appointment**
```
Bot: Welcome... [Main menu]
WhatsApp: (click "📅 Book Appointment")
```

**3. Patient Check/Registration**
```
# If new patient:
Bot: Enter your name
WhatsApp: Test Patient
Bot: Select gender
WhatsApp: (click Male)
Bot: Enter DOB
WhatsApp: 01/01/2000
Bot: Registration successful!

# If existing patient:
Bot: Welcome back, Test Patient! 👋
```

**4. Doctor Selection**
```
Bot: *Available Doctors:*
     
     1. *Dr. Sarah Wilson*
        Cardiology - Cardiac Department
     
     [Interactive list]

WhatsApp: (select "Dr. Sarah Wilson")
```

**5. Date Selection**
```
Bot: You selected: *Dr. Sarah Wilson*
     ...
     When would you like to book your appointment?
     
     [📅 Today]  [📅 Tomorrow]

WhatsApp: (click "Today")
```

**6. Time Selection**
```
Bot: You selected: *Today* (02/02/2026)
     
     *Available time slots:*
     Morning: 09:00 - 13:00
     Evening: 14:00 - 17:00
     
     [Interactive list with times]

WhatsApp: (select "10:00")
```

**7. Confirmation**
```
Bot: 📋 *Appointment Summary*
     
     👨‍⚕️ *Doctor:* Dr. Sarah Wilson
     📅 *Date:* Today (02/02/2026)
     ⏰ *Time:* 10:00
     
     [✅ Confirm]  [❌ Cancel]

WhatsApp: (click "Confirm")
```

**8. Success**
```
Bot: 🎉 *Appointment Booked Successfully!*
     
     📋 *Appointment Details:*
     • *ID:* #123
     • *Doctor:* Dr. Sarah Wilson
     • *Date:* Today (02/02/2026)
     • *Time:* 10:00
     • *Queue:* #1
     
     ✅ Your appointment is confirmed!
```

---

### **Verification:**

```sql
-- Check appointment was created
SELECT 
    a.id,
    p.first_name as patient,
    d.first_name as doctor,
    a.appointment_date,
    a.scheduled_time,
    a.queue_number,
    a.status,
    a.created_by
FROM appointments a
JOIN users p ON a.patient_id = p.id
JOIN users d ON a.doctor_id = d.id
ORDER BY a.created_at DESC
LIMIT 1;

-- Expected:
-- patient: Test
-- doctor: Sarah
-- appointment_date: 2026-02-02
-- scheduled_time: 10:00:00
-- queue_number: 1 (or next in sequence)
-- status: WAITING
-- created_by: whatsapp-bot
```

---

## 🧪 TEST 2: Multiple Time Slots

### **Book Multiple Appointments:**

```
1. Book appointment at 10:00 (first booking)
2. Start new booking flow
3. Select same doctor, same date
4. Verify time slot list does NOT show 10:00
5. Select different time (10:15)
6. Confirm
7. Check database has both appointments
```

---

## 🧪 TEST 3: Sunday Handling

### **If today is Saturday:**

```
WhatsApp: (select "Tomorrow")

Expected:
❌ Should NOT allow (tomorrow is Sunday)

Bot should show:
📅 Today (Saturday)
📅 Day After Tomorrow (Monday)
```

### **If today is Sunday:**

```
Expected:
Bot should NOT show "Today" option

Bot should show:
📅 Tomorrow (Monday)
📅 Day After (Tuesday)
```

---

## 🧪 TEST 4: All Slots Booked

### **Setup:**

```sql
-- Book all time slots for today
-- Create 28 appointments for doctor_id = 3, date = today
-- (One for each 15-min slot from 09:00-17:00)
```

### **Test:**

```
WhatsApp: (select same doctor and date)

Expected:
❌ Sorry, no time slots available for this date.
   
   Please select a different date.

[Shows date selection again]
```

---

## 🧪 TEST 5: Cancel at Confirmation

### **Steps:**

```
1. Complete flow until confirmation screen
2. Click "❌ Cancel" instead of "Confirm"

Expected:
Appointment booking cancelled.
[Shows main menu]

Database:
No appointment created
```

---

## 🧪 TEST 6: Username Format

### **Verify New Format:**

```sql
-- After registration
SELECT username, whatsapp_phone, first_name
FROM users
WHERE created_by = 'whatsapp-bot'
ORDER BY created_date DESC
LIMIT 1;

-- Expected for +917794093705:
-- username: 7794093705  (NOT wa_917794093705)
-- whatsapp_phone: 917794093705
```

---

## 🧪 TEST 7: Multiple Doctors

### **Setup:**

```sql
-- Create 2-3 doctors
INSERT INTO users (username, password, first_name, last_name, phone_number, email, 
                   specialization, department, is_enabled)
VALUES 
('doctor2', '$2a$11$...', 'Michael', 'Brown', '1234567891', 
 'michael@hospital.com', 'General Medicine', 'OPD', true),
('doctor3', '$2a$11$...', 'Emily', 'Davis', '1234567892',
 'emily@hospital.com', 'Pediatrics', 'Children Ward', true);

-- Assign roles
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.username IN ('doctor2', 'doctor3') AND r.name = 'DOCTOR';
```

### **Test:**

```
WhatsApp: (click "Book Appointment")

Expected:
Bot shows list with all 3 doctors:

*Available Doctors:*

1. *Dr. Sarah Wilson*
   Cardiology - Cardiac Department

2. *Dr. Michael Brown*
   General Medicine - OPD

3. *Dr. Emily Davis*
   Pediatrics - Children Ward

[Interactive list with all 3]
```

---

## 📊 DATABASE CHECKS

### **After Each Booking:**

```sql
-- 1. Appointment exists
SELECT COUNT(*) FROM appointments
WHERE patient_id = (SELECT id FROM users WHERE username = '7794093705')
AND appointment_date = CURDATE();
-- Should return: 1 (or more if multiple bookings)

-- 2. Correct scheduled_time
SELECT scheduled_time FROM appointments
WHERE id = (SELECT MAX(id) FROM appointments);
-- Should match selected time slot

-- 3. Queue number assigned
SELECT queue_number FROM appointments
WHERE id = (SELECT MAX(id) FROM appointments);
-- Should be a positive integer

-- 4. Status is correct
SELECT status FROM appointments
WHERE id = (SELECT MAX(id) FROM appointments);
-- Should be: WAITING

-- 5. created_by is whatsapp-bot
SELECT created_by FROM appointments
WHERE id = (SELECT MAX(id) FROM appointments);
-- Should be: whatsapp-bot
```

---

## ✅ SUCCESS CHECKLIST

After testing, verify:

**Phase 4 (Doctor Selection):**
- [ ] Doctor list displays correctly
- [ ] Can select doctor from list
- [ ] Doctor name and specialization shown
- [ ] Multiple doctors handled
- [ ] No doctors shows error message
- [ ] Doctor saved in conversation context

**Phase 5 (Date & Time):**
- [ ] Today/Tomorrow buttons shown
- [ ] Sundays excluded automatically
- [ ] Can select date
- [ ] Time slots generated (15-min intervals)
- [ ] Morning slots: 09:00-13:00
- [ ] Evening slots: 14:00-17:00
- [ ] Lunch break excluded
- [ ] Booked slots hidden
- [ ] Only available slots shown
- [ ] Can select time slot
- [ ] Confirmation shows all details
- [ ] Confirm button books appointment
- [ ] Cancel button returns to menu
- [ ] Appointment created in database
- [ ] Queue number assigned
- [ ] scheduled_time correct
- [ ] Status is WAITING
- [ ] created_by is whatsapp-bot

**Username Fix:**
- [ ] New patients have username format: 7794093705
- [ ] No "wa_" prefix
- [ ] Country code removed

---

## 🐛 Common Issues

### **Issue: No doctors shown**

**Check:**
```sql
SELECT * FROM users u
JOIN user_roles ur ON u.id = ur.user_id
JOIN roles r ON ur.role_id = r.id
WHERE r.name = 'DOCTOR' AND u.is_enabled = true;
```

**If empty:** Run the doctor creation SQL above

---

### **Issue: All slots show as unavailable**

**Check:**
```sql
SELECT COUNT(*) FROM appointments
WHERE doctor_id = X AND appointment_date = 'YYYY-MM-DD';
```

**If 28+:** All slots booked, select different date

---

### **Issue: Appointment not saved**

**Check logs for:**
```
💾 BOOKING APPOINTMENT
❌ Error booking appointment
```

**Possible causes:**
- Doctor schedule not found
- Database constraint violation
- Missing required fields

---

## 📝 Test Report Template

```
Test Date: 02/02/2026
Tested By: _____________

✅ PASSED Tests:
[ ] Complete booking flow
[ ] Doctor selection
[ ] Date selection
[ ] Time slot selection
[ ] Confirmation and booking
[ ] Multiple time slots
[ ] Sunday handling
[ ] Cancel at confirmation
[ ] Username format
[ ] Multiple doctors
[ ] Database verification

❌ FAILED Tests:
(List any failures with logs/screenshots)

📊 Statistics:
Total appointments created: ___
Different doctors tested: ___
Different time slots: ___
```

---

**Phases 4 & 5 are production-ready! Test thoroughly! 🚀**
