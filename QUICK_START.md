# 🚀 Quick Start - Phase 1 Testing

## ⚡ 3-Minute Setup

### **Step 1: Database Setup (1 minute)**

Open MySQL and run this:

```sql
-- Make sure you're in your database
USE your_database_name;

-- Run the schema file
SOURCE C:/webapp/database-schema-appointments-mysql.sql;

-- Verify tables created
SHOW TABLES LIKE '%appointments%';
SHOW TABLES LIKE '%doctor_schedules%';
SHOW TABLES LIKE '%prescriptions%';
```

**Expected:** 3 tables listed

---

### **Step 2: Start Application (1 minute)**

```powershell
cd C:\webapp
.\mvnw spring-boot:run
```

**Wait for:** `Started WebappApplication`

---

### **Step 3: Test (1 minute)**

1. **Login as ADMIN** (your existing admin credentials)

2. **Create a Doctor:**
   - Click `User Management` → `Create Doctor`
   - Username: `doctor1`
   - Password: `test123`
   - First Name: `John`
   - Last Name: `Doe`
   - Phone: `1234567890`
   - Specialization: `General`
   - Click **Create**

3. **Logout → Login as Doctor:**
   - Username: `doctor1`
   - Password: `test123`

4. **Test Schedule Page:**
   - Click **"My Schedule"** in sidebar
   - Create schedule:
     - Date: Tomorrow
     - Shift: Morning
     - Slot Duration: 15 min
   - Click **Save**
   - ✅ **Success if:** You see the schedule in the table

5. **Test Appointments Page:**
   - Click **"Today's Appointments"** in sidebar
   - ✅ **Success if:** Page loads showing "No appointments" message

---

## ✅ What Should Work

- [x] Login as doctor
- [x] See doctor menu in sidebar
- [x] Create/view schedules
- [x] View appointments page (empty for now)

---

## ❌ What Won't Work Yet

- No appointments to display (need Attendant booking - Phase 2)
- Can't book appointments yet (Phase 2)
- No prescriptions yet (Phase 3)

---

## 🐛 If Something Breaks

**Issue:** Login redirects to login page  
**Fix:** Check if DOCTOR role exists in database

**Issue:** 404 Page Not Found  
**Fix:** Verify template files exist in `src/main/resources/templates/doctor/`

**Issue:** Database error  
**Fix:** Make sure MySQL is running and schema was executed

**Issue:** Form submit does nothing  
**Fix:** CSRF token issue - already fixed in our code

---

## 📸 Expected Screens

### **My Schedule Page**
- Form with date, shift type, slot duration
- Empty "Upcoming Schedules" table
- After creating schedule: Table shows 1 row

### **Today's Appointments Page**
- Statistics: 0/0/0/0
- Blue info box: "No appointments scheduled"

---

## 📞 Report Format

If you find issues, report like this:

```
ISSUE: [Title]
- What I did: [Steps]
- Expected: [What should happen]
- Actual: [What happened]
- Error: [Copy error message if any]
```

---

**Ready? GO! 🏃‍♂️**
