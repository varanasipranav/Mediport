# 🏥 Phase 1 Testing Guide - Doctor Schedule & Appointments

## ✅ What We Built

### **Entities**
- ✅ `Appointment.java` - Manages patient appointments in queue
- ✅ `Prescription.java` - Stores prescriptions (will be used in Phase 2)

### **Repositories**
- ✅ `DoctorScheduleRepository` - Query doctor schedules
- ✅ `AppointmentRepository` - Query appointments with queue logic
- ✅ `PrescriptionRepository` - Query prescriptions (Phase 2)

### **Services**
- ✅ `DoctorScheduleService` - Business logic for schedule management
- ✅ `AppointmentService` - Business logic for appointment booking & queue

### **Controllers**
- ✅ `DoctorScheduleController` - `/doctor/schedule` endpoints
- ✅ `DoctorAppointmentController` - `/doctor/appointments/today` endpoints

### **Pages**
- ✅ `/doctor/schedule` - Create/manage doctor's schedule
- ✅ `/doctor/appointments/today` - View today's appointment queue

### **Updated**
- ✅ Sidebar menu - Connected doctor links

---

## 🗄️ STEP 1: Run Database Schema

### **Option A: Using MySQL Workbench / phpMyAdmin**
1. Open your MySQL client
2. Connect to your database (same one where `users` table exists)
3. Open and execute: `database-schema-appointments-mysql.sql`
4. Verify tables created:
   ```sql
   SHOW TABLES;
   -- Should show: doctor_schedules, appointments, prescriptions
   ```

### **Option B: Using Command Line**
```bash
mysql -u your_username -p your_database_name < database-schema-appointments-mysql.sql
```

### **Verify Tables**
Run this query to confirm:
```sql
SELECT TABLE_NAME 
FROM INFORMATION_SCHEMA.TABLES 
WHERE TABLE_SCHEMA = 'your_database_name' 
  AND TABLE_NAME IN ('doctor_schedules', 'appointments', 'prescriptions');
```

You should see all 3 tables listed.

---

## 🚀 STEP 2: Start the Application

```powershell
# From C:\webapp folder
.\mvnw spring-boot:run
```

**Expected Output:**
- No compilation errors
- Server starts on port 8080 (or your configured port)
- Look for: `Started WebappApplication in X.XXX seconds`

---

## 🧪 STEP 3: Test as DOCTOR User

### **3.1: Login as Doctor**

1. **Create a Doctor User (if not exists)**
   - Login as ADMIN
   - Go to: `User Management` → `Create Doctor`
   - Fill in details:
     - Username: `dr.john`
     - Password: `test123`
     - First Name: `John`
     - Last Name: `Smith`
     - Phone: `1234567890`
     - Specialization: `Cardiology`
   - Click **Create**
   - ✅ **Expected**: "Doctor created successfully with ID: DOC00001"

2. **Logout and Login as Doctor**
   - Logout from Admin
   - Login with:
     - Username: `dr.john`
     - Password: `test123`
   - ✅ **Expected**: Redirect to `/dashboard`
   - ✅ **Verify**: Sidebar shows "Doctor" section with:
     - Today's Appointments
     - My Schedule

---

### **3.2: Test My Schedule Page**

**URL:** `http://localhost:8080/doctor/schedule`

#### **Test Case 1: Create Morning Schedule**
1. Click **"My Schedule"** in sidebar
2. ✅ **Expected**: Schedule page loads with:
   - Form to create schedule
   - "Upcoming Schedules" table (empty initially)

3. Fill in the form:
   - **Date:** Tomorrow's date (e.g., 2026-01-26)
   - **Shift Type:** Morning
   - **Slot Duration:** 15 minutes
   - **Max Patients:** Leave empty (auto-calculate)
4. Click **"Save Schedule"**

5. ✅ **Expected Results:**
   - Success message: "Schedule saved successfully for 2026-01-26"
   - New row appears in "Upcoming Schedules" table:
     - Date: 26 Jan 2026
     - Shift: Morning
     - Time: 09:00 - 13:00
     - Slot Duration: 15 min
     - Max Patients: Auto (16) ← *calculated from 4 hours / 15 min*
     - Priority Delay: None
     - Status: Active
     - Actions: "Priority Work" button

#### **Test Case 2: Create Custom Schedule**
1. Create another schedule:
   - **Date:** Day after tomorrow (e.g., 2026-01-27)
   - **Shift Type:** Custom
   - ✅ **Verify**: Two time fields appear (Start Time, End Time)
   - **Start Time:** 14:00
   - **End Time:** 18:00
   - **Slot Duration:** 20 minutes
   - **Max Patients:** 10 (override auto-calculate)
2. Click **"Save Schedule"**

3. ✅ **Expected**: Success message and new row in table

#### **Test Case 3: Add Priority Work**
1. Click **"Priority Work"** button on first schedule
2. ✅ **Verify**: Modal opens with delay input field
3. Enter: `30` minutes
4. Click **"Add Delay"**

5. ✅ **Expected**:
   - Success message: "Added 30 minutes priority work delay"
   - Priority Delay column now shows: "30 min" badge (yellow)

#### **Test Case 4: Update Existing Schedule**
1. Create a schedule for tomorrow again (same date as Test Case 1)
2. Change:
   - **Shift Type:** Evening
   - **Slot Duration:** 20 minutes
3. Click **"Save Schedule"**

4. ✅ **Expected**:
   - Success message (schedule updated, not created again)
   - Table shows updated shift time: 14:00 - 18:00

---

### **3.3: Test Today's Appointments Page**

**URL:** `http://localhost:8080/doctor/appointments/today`

#### **Test Case 5: View Empty Queue**
1. Click **"Today's Appointments"** in sidebar
2. ✅ **Expected**:
   - Page title: "Today's Appointments"
   - Current date displayed
   - Statistics cards:
     - Total Patients: 0
     - Waiting: 0
     - In Consultation: 0
     - Completed: 0
   - Blue info box: "No appointments scheduled for today. You can set your schedule from the My Schedule page."

#### **Test Case 6: Create Today's Schedule (Manual)**

**Important:** To test appointments, we need:
1. A schedule for TODAY
2. Appointments booked (we'll create these in Phase 2 via Attendant)

**For now, let's create today's schedule:**
1. Go back to **"My Schedule"**
2. Create schedule:
   - **Date:** Today's date (2026-01-25)
   - **Shift Type:** Morning
   - **Slot Duration:** 15 minutes
3. Click **"Save Schedule"**

4. ✅ **Expected**: Schedule created for today

**Note:** We can't test appointments yet because we haven't built the Attendant booking feature. That's in the next phase.

---

## 🐛 STEP 4: Report Issues

After testing, report any issues you find:

### **Issue Template:**
```
**Test Case:** [e.g., Create Morning Schedule]
**Expected:** [What should happen]
**Actual:** [What actually happened]
**Error Message:** [If any]
**Screenshots:** [If helpful]
```

### **Common Issues to Check:**

1. **Database Connection Error**
   - ✅ Verify MySQL is running
   - ✅ Check `application.properties` has correct DB credentials
   - ✅ Confirm tables were created

2. **403 Forbidden / Session Expired**
   - ✅ Verify you're logged in as DOCTOR role
   - ✅ Check CSRF token is present in forms

3. **Page Not Found (404)**
   - ✅ Verify template files exist in `src/main/resources/templates/doctor/`
   - ✅ Check controller URLs match sidebar links

4. **Null Pointer Exception**
   - ✅ Check if doctor user has proper roles assigned
   - ✅ Verify database tables have correct foreign key constraints

5. **Validation Errors**
   - ✅ Check if all required fields are filled
   - ✅ Verify date is not in the past

---

## ✅ Success Criteria for Phase 1

**You should be able to:**
- [x] Login as a doctor
- [x] See doctor menu in sidebar
- [x] Navigate to "My Schedule" page
- [x] Create a schedule for any future date
- [x] See schedule in "Upcoming Schedules" table
- [x] Add priority work delay to a schedule
- [x] Update an existing schedule
- [x] Navigate to "Today's Appointments" page
- [x] See empty state (no appointments yet)

**Not expected to work yet:**
- ❌ Actual appointments in the queue (needs Attendant booking - Phase 2)
- ❌ Start/Complete consultation buttons (no appointments yet)
- ❌ Patient booking (Phase 2)
- ❌ Prescription writing (Phase 3)

---

## 📊 Database Queries to Verify Data

After creating schedules, run these queries:

```sql
-- Check doctor schedules
SELECT 
    ds.id, 
    u.first_name, u.last_name,
    ds.schedule_date,
    ds.shift_type,
    ds.start_time,
    ds.end_time,
    ds.slot_duration_minutes,
    ds.max_patients,
    ds.priority_work_minutes,
    ds.is_active
FROM doctor_schedules ds
JOIN users u ON ds.doctor_id = u.id
ORDER BY ds.schedule_date DESC;

-- Check appointments (should be empty for now)
SELECT * FROM appointments;

-- Verify doctor user roles
SELECT 
    u.username, 
    u.first_name, 
    u.last_name,
    r.name as role
FROM users u
JOIN user_roles ur ON u.id = ur.user_id
JOIN roles r ON ur.role_id = r.id
WHERE r.name = 'DOCTOR';
```

---

## 🎯 Next Phase Preview

**Phase 2 will add:**
- Attendant booking interface
- Book appointments for patients
- View live queue
- Queue manipulation (delay, not attended)
- Patient can see their appointment status

**Phase 3 will add:**
- Prescription writing
- PDF generation
- Patient prescription history
- Pharma view

---

## 📝 Notes

- All times are displayed in 24-hour format (HH:mm)
- Queue numbers auto-increment starting from 1
- Scheduled times are calculated automatically based on queue position
- Priority work delays ALL upcoming appointments
- Schedules can be updated but not deleted (only deactivated)

---

**Ready to test! Report back with results.** 🚀
