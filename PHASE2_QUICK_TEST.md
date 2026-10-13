# 🚀 Phase 2 - Quick Test Reference

## ⚡ **3-Minute Test**

### **1. Create Users (as ADMIN)**
```
Doctor:    doctor1/test123
Attendant: attendant1/test123
Patient:   patient1/test123 (Rajesh Kumar)
Patient:   patient2/test123 (Priya Singh)
```

### **2. Doctor Sets Schedule**
- Login: `doctor1/test123`
- Go to: Appointments → My Schedule
- Create: Today, Morning shift, 15 min
- Logout

### **3. Attendant Books 3 Appointments**
- Login: `attendant1/test123`
- Go to: Reception → Patient Check-in
- Book:
  - Patient1 → Queue #1, Time: 09:00 ✓
  - Patient2 → Queue #2, Time: 09:15 ✓
  - Patient1 (tomorrow) → Queue #1 ✓

### **4. Test Patient Dashboard**
- Logout, Login: `patient1/test123`
- Dashboard shows:
  - Queue Position: #1
  - Time: 09:00
  - Estimated Wait: 0 min ✓

### **5. Test Queue Algorithms (Attendant)**
- Login: `attendant1/test123`
- Go to: Reception → Live Queue
- Select: Doctor1, Today

**Test Delay:**
- Click Delay on #1
- Verify: #1 becomes #3, others shift up ✓

**Test Not Attended:**
- Click Not Attended on #2
- Verify: #2 moves to end, RED background ✓

**Test Finish:**
- Click Finish on #1
- Verify: Status = COMPLETED, green, faded ✓

---

## 🎯 **Expected Results**

| Action | Expected Outcome |
|--------|------------------|
| Book appointment | Queue #X assigned, Time calculated |
| Delay | Moves down 2, status DELAYED (yellow) |
| Not Attended | Moves to end, RED background |
| Finish | Status COMPLETED, green, faded |
| Patient dashboard | Shows queue #, time, wait estimate |

---

## 🐛 **Common Issues**

| Issue | Fix |
|-------|-----|
| Can't book appointment | Check doctor has schedule for that date |
| Queue numbers wrong | Check database for gaps/duplicates |
| Times not updating | Verify `recalculateTimesForSchedule()` called |
| 403 Forbidden | Check user has correct role |
| Page not found | Verify template files exist |

---

## 📸 **What You Should See**

### **Attendant Queue Page**
- Statistics: 3 total, 2 waiting, 1 completed
- Queue cards with colored borders
- Action buttons: Finish, Not Attended, Delay

### **Patient Dashboard**
- Purple gradient "Today's Appointment" card
- Big queue number: #1
- Estimated wait: X minutes

### **Queue After Delay**
```
Before:           After Delay on #1:
#1 Rajesh  09:00  #1 Priya   09:00
#2 Priya   09:15  #2 Amit    09:15
#3 Amit    09:30  #3 Rajesh  09:30 (DELAYED, yellow)
```

### **Queue After Not Attended**
```
Before:           After Not Attended on #2:
#1 Priya   09:00  #1 Priya   09:00
#2 Amit    09:15  #2 Rajesh  09:15
#3 Rajesh  09:30  #3 Amit    09:30 (NOT_ATTENDED, RED)
```

---

## ✅ **Success Checklist**

- [ ] 3 appointments booked successfully
- [ ] Queue numbers: 1, 2, 3
- [ ] Times: 09:00, 09:15, 09:30
- [ ] Delay moves patient down 2 positions
- [ ] Not Attended moves to end (RED)
- [ ] Finish marks as COMPLETED (green, faded)
- [ ] Patient dashboard shows queue #
- [ ] Estimated wait time calculated
- [ ] All status colors working

---

## 📞 **Report Issues**

```
ISSUE: Delay not working
STEP: 5 - Test Delay
EXPECTED: Patient #1 moves to #3
ACTUAL: Nothing happened
ERROR: [paste error if any]
```

---

**Full Testing Guide:** `PHASE2_TESTING_GUIDE.md` (11 detailed steps)

**GO TEST!** 🏃‍♂️
