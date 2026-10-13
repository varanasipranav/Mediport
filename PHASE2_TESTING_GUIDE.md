# 🎉 Phase 2 Testing Guide - Appointment Booking & Queue System

## ✅ What We Built in Phase 2

### **Controllers**
- ✅ `AttendantController` - Book appointments, manage queue (finish, delay, not attended)
- ✅ `PatientController` - Self-booking, view dashboard with live queue position

### **Pages**
- ✅ `/attendant/check-in` - Book appointments for patients
- ✅ `/attendant/queue` - Live queue management with all algorithms
- ✅ `/patient/dashboard` - Patient dashboard with today's appointment & queue position
- ✅ `/patient/book-appointment` - Self-service appointment booking
- ✅ `/patient/appointments` - Appointment history

### **Queue Algorithms Implemented**
- ✅ **Auto queue number assignment** - Increments automatically
- ✅ **Auto scheduled time calculation** - Based on queue position × slot duration
- ✅ **Finish** - Marks appointment as completed
- ✅ **Not Attended** - Moves to end of queue (highlighted RED)
- ✅ **Delay** - Pushes down 2 positions
- ✅ **Estimated wait time** - Calculates minutes based on patients ahead

---

## 🧪 TESTING SCENARIO (Complete Flow)

### **📋 Pre-requisites**
1. ✅ Database schema executed (from Phase 1)
2. ✅ Application running
3. ✅ Users created:
   - 1 Doctor
   - 1 Attendant
   - 2-3 Patients

---

## **🎬 Test Flow: Book & Manage Appointments**

### **STEP 1: Create Test Users**

**1.1 Create Doctor (as ADMIN)**
- Login as ADMIN
- Go to: User Management → Create Doctor
  - Username: `doctor1`
  - Password: `test123`
  - First Name: `John`
  - Last Name: `Smith`
  - Phone: `9999999999`
  - Specialization: `Cardiology`
- Click **Create**
- ✅ **Expected**: "Doctor created successfully"

**1.2 Create Attendant (as ADMIN)**
- Go to: User Management → Create Attendant
  - Username: `attendant1`
  - Password: `test123`
  - First Name: `Sarah`
  - Last Name: `Johnson`
  - Phone: `8888888888`
- Click **Create**
- ✅ **Expected**: "Attendant created successfully"

**1.3 Create Patients (as ADMIN)**
Create 3 patients:

**Patient 1:**
- Username: `patient1`
- Password: `test123`
- First Name: `Rajesh`
- Last Name: `Kumar`
- Phone: `7777777777`

**Patient 2:**
- Username: `patient2`
- Password: `test123`
- First Name: `Priya`
- Last Name: `Singh`
- Phone: `6666666666`

**Patient 3:**
- Username: `patient3`
- Password: `test123`
- First Name: `Amit`
- Last Name: `Patel`
- Phone: `5555555555`

---

### **STEP 2: Doctor Sets Schedule**

**2.1 Login as Doctor**
- Logout from ADMIN
- Login with: `doctor1` / `test123`

**2.2 Create Today's Schedule**
- Click: **Appointments** → **My Schedule**
- Create schedule:
  - **Date**: Today (2026-01-25)
  - **Shift Type**: Morning
  - **Slot Duration**: 15 minutes
  - **Max Patients**: Leave empty (auto-calculate)
- Click **Save Schedule**
- ✅ **Expected**: "Schedule saved successfully for 2026-01-25"
- ✅ **Verify**: Table shows schedule with Time: 09:00 - 13:00

**Logout from doctor**

---

### **STEP 3: Attendant Books Appointments**

**3.1 Login as Attendant**
- Login with: `attendant1` / `test123`

**3.2 Book First Appointment**
- Click: **Reception** → **Patient Check-in**
- Fill form:
  - **Patient**: Select `PAT00001 - Rajesh Kumar`
  - **Doctor**: Select `John Smith - Cardiology`
  - **Appointment Date**: Today
- Click **Book Appointment**
- ✅ **Expected**: "Appointment booked successfully! Queue #1 - Scheduled at 09:00"

**3.3 Book Second Appointment**
- Same page, book another:
  - **Patient**: Select `PAT00002 - Priya Singh`
  - **Doctor**: Select `John Smith - Cardiology`
  - **Appointment Date**: Today
- Click **Book Appointment**
- ✅ **Expected**: "Queue #2 - Scheduled at 09:15"

**3.4 Book Third Appointment**
- Same page:
  - **Patient**: Select `PAT00003 - Amit Patel`
  - **Doctor**: Select `John Smith - Cardiology`
  - **Appointment Date**: Today
- Click **Book Appointment**
- ✅ **Expected**: "Queue #3 - Scheduled at 09:30"

---

### **STEP 4: Test Patient Self-Booking**

**4.1 Logout and Login as Patient**
- Logout from Attendant
- Login with: `patient1` / `test123`

**4.2 View Dashboard**
- Click: **My Dashboard** (or it loads automatically)
- ✅ **Expected**: You should see:
  - **Today's Appointment** card (purple gradient)
  - Queue Position: **#1**
  - Scheduled Time: **09:00**
  - Doctor: **John Smith**
  - Specialization: **Cardiology**
  - **Estimated Wait: 0 minutes** (since you're first)

**4.3 Try Booking Another Appointment (Should Fail)**
- Click: **Book New Appointment**
- Select:
  - Doctor: `John Smith`
  - Date: Today
- Click **Confirm Booking**
- ✅ **Expected**: Error: "Patient already has an appointment on 2026-01-25"

**4.4 Book Future Appointment**
- Same page:
  - Doctor: `John Smith`
  - Date: **Tomorrow** (2026-01-26)
- Click **Confirm Booking**
- ✅ **Expected**: "Appointment booked successfully!"

**Logout from patient1**

---

### **STEP 5: Test Queue Management (Attendant)**

**5.1 Login as Attendant**
- Login with: `attendant1` / `test123`

**5.2 View Live Queue**
- Click: **Reception** → **Live Queue**
- Select:
  - **Doctor**: John Smith
  - **Date**: Today
- ✅ **Expected**: You should see 3 appointments:
  - **#1** - Rajesh Kumar - 09:00 - **WAITING** (gray border)
  - **#2** - Priya Singh - 09:15 - **WAITING**
  - **#3** - Amit Patel - 09:30 - **WAITING**
- ✅ **Statistics**: Total: 3, Waiting: 3, In Consultation: 0, Completed: 0

---

### **STEP 6: Test DELAY Algorithm**

**6.1 Delay Patient #1 (Rajesh Kumar)**
- On queue page, find **#1 Rajesh Kumar**
- Click **Delay (−2)** button
- Confirm the prompt
- ✅ **Expected**: "Appointment delayed to position #3"
- ✅ **Verify Queue Order Changed**:
  - **#1** - Priya Singh - 09:00 (moved up from #2)
  - **#2** - Amit Patel - 09:15 (moved up from #3)
  - **#3** - Rajesh Kumar - 09:30 (delayed, status: DELAYED, yellow border)

✅ **Algorithm Verified:**
- Rajesh moved from #1 → #3 (down 2 positions)
- Priya & Amit shifted up
- Times recalculated correctly

---

### **STEP 7: Test NOT ATTENDED Algorithm**

**7.1 Mark Patient #2 as NOT ATTENDED (Amit Patel)**
- Find **#2 Amit Patel**
- Click **Not Attended** button
- Confirm: "Mark as NOT ATTENDED? Patient will be moved to end of queue."
- ✅ **Expected**: "Patient marked as NOT ATTENDED and moved to end of queue (position #3)"
- ✅ **Verify Queue Order**:
  - **#1** - Priya Singh - 09:00 - **WAITING**
  - **#2** - Rajesh Kumar - 09:15 - **DELAYED** (moved up from #3)
  - **#3** - Amit Patel - 09:30 - **NOT_ATTENDED** (RED background)

✅ **Algorithm Verified:**
- Amit moved to end
- Status changed to NOT_ATTENDED
- Highlighted in RED
- Everyone else shifted up

---

### **STEP 8: Test FINISH Algorithm**

**8.1 Mark Patient #1 as Finished (Priya Singh)**
- Find **#1 Priya Singh**
- Click **Finish** button
- Confirm
- ✅ **Expected**: "Appointment marked as finished"
- ✅ **Verify**:
  - **#1** - Priya Singh - Status: **COMPLETED** (green border, faded)
  - Statistics: Completed: 1, Waiting: 1

✅ **Algorithm Verified:**
- Status changed to COMPLETED
- Still in queue but visually faded
- Queue numbers remain same (not shifted)

---

### **STEP 9: Test Patient Dashboard Updates**

**9.1 Logout and Login as Patient 1 (Rajesh Kumar)**
- Logout from Attendant
- Login with: `patient1` / `test123`

**9.2 Check Dashboard**
- Click: **My Dashboard**
- ✅ **Expected**:
  - Queue Position: **#2** (updated from original #1)
  - Scheduled Time: **09:15** (updated from 09:00)
  - Estimated Wait: **0 minutes** (since #1 is completed)

✅ **Live Update Verified:**
- Patient sees updated queue position
- Scheduled time recalculated
- Estimated wait time accurate

---

### **STEP 10: Test Patient Appointment History**

**10.1 View All Appointments**
- Click: **My Appointments** → **View Appointments**
- ✅ **Expected**: Table shows:
  - Today's appointment (status: DELAYED)
  - Tomorrow's appointment (status: WAITING)

**10.2 Cancel Future Appointment**
- Find tomorrow's appointment
- Click **Cancel** button
- Confirm
- ✅ **Expected**: "Appointment cancelled successfully"
- ✅ **Verify**: Status changed to CANCELLED

---

### **STEP 11: Test Doctor's View**

**11.1 Logout and Login as Doctor**
- Logout from Patient
- Login with: `doctor1` / `test123`

**11.2 View Today's Appointments**
- Click: **Appointments** → **Today's Appointments**
- ✅ **Expected**: Same queue as attendant saw:
  - **#1** - Priya Singh - COMPLETED
  - **#2** - Rajesh Kumar - DELAYED
  - **#3** - Amit Patel - NOT_ATTENDED (red)

**11.3 Start Consultation (Rajesh Kumar)**
- Find **#2 Rajesh Kumar**
- Click **Start** button
- ✅ **Expected**: "Consultation started"
- ✅ **Verify**:
  - Status changed to **IN_CONSULTATION** (blue border, blue background)
  - Statistics: In Consultation: 1

**11.4 Complete Consultation**
- Click **Complete** button
- ✅ **Expected**: "Consultation completed"
- ✅ **Verify**:
  - Status: COMPLETED
  - Statistics: Completed: 2

---

## ✅ **Success Criteria Checklist**

### **Booking System**
- [x] Attendant can book appointments for patients
- [x] Patient can book own appointments
- [x] Queue number auto-assigned (1, 2, 3...)
- [x] Scheduled time auto-calculated
- [x] Cannot book duplicate on same date
- [x] Can book future dates

### **Queue Algorithms**
- [x] **Delay**: Moves patient down 2 positions
- [x] **Not Attended**: Moves to end, highlighted RED
- [x] **Finish**: Marks as COMPLETED
- [x] Queue numbers update correctly
- [x] Scheduled times recalculate correctly

### **Live Updates**
- [x] Patient dashboard shows live queue position
- [x] Estimated wait time calculated correctly
- [x] Queue updates reflect in all views (attendant, doctor, patient)

### **UI/UX**
- [x] Status color coding works (green, blue, red, yellow, gray)
- [x] Statistics cards show correct counts
- [x] Action buttons show/hide based on status
- [x] Confirmation prompts before actions
- [x] Success/error messages display correctly

---

## 🐛 **Known Issues to Report (If Found)**

1. **Queue calculation errors** - Wrong queue numbers after delay/not attended
2. **Time calculation errors** - Scheduled times don't update
3. **Database errors** - Foreign key constraint violations
4. **UI bugs** - Status colors not showing
5. **Permission errors** - 403 Forbidden when accessing pages

---

## 📊 **Database Verification Queries**

After testing, run these to verify data:

```sql
-- Check all appointments for today
SELECT 
    a.id,
    a.queue_number,
    a.scheduled_time,
    p.first_name as patient_name,
    d.first_name as doctor_name,
    a.status
FROM appointments a
JOIN users p ON a.patient_id = p.id
JOIN users d ON a.doctor_id = d.id
WHERE a.appointment_date = CURDATE()
ORDER BY a.queue_number;

-- Check queue integrity (should have no gaps if no NOT_ATTENDED at end)
SELECT queue_number FROM appointments 
WHERE appointment_date = CURDATE() 
ORDER BY queue_number;
-- Expected: 1, 2, 3 (no gaps)
```

---

## 🎯 **What's Working (Expected)**

✅ Appointment booking (attendant & patient)  
✅ Queue number auto-assignment  
✅ Time auto-calculation  
✅ Delay algorithm (move down 2)  
✅ Not Attended algorithm (move to end, red)  
✅ Finish algorithm (mark completed)  
✅ Patient dashboard with live queue position  
✅ Estimated wait time calculation  
✅ Appointment history  
✅ Cancel future appointments  

---

## ❌ **What's NOT Working Yet (Expected)**

❌ Prescription writing (Phase 3)  
❌ PDF generation (Phase 3)  
❌ Prescription history for patients (Phase 3)  
❌ Pharma view (Phase 3)  
❌ Real-time WebSocket updates (Future enhancement)  
❌ SMS notifications (Future enhancement)  

---

**Ready to test! Follow the flow above and report any issues.** 🚀

**Report format:**
```
ISSUE: [Title]
STEP: [Which test step]
EXPECTED: [What should happen]
ACTUAL: [What happened]
ERROR: [Error message if any]
```
