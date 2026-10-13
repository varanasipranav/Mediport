# 🚀 Quick Start: Weekly Schedule Templates

## ⚡ Setup (One-Time)

### Step 1: Run Database Migration
```bash
mysql -u root -p hospital < database-schema-weekly-schedules.sql
```

### Step 2: Restart Application
```bash
mvn spring-boot:run
```

### Step 3: Doctor Login
- Navigate to: `http://localhost:8081/doctor/weekly-schedule`
- Login with doctor credentials

---

## 📋 Doctor User Guide

### 1️⃣ **First Time Setup (Initialize Weekly Schedule)**

When you first visit the weekly schedule page, you'll see an initialization form:

1. **Select Default Shift Type:**
   - **Morning:** 9 AM - 1 PM
   - **Evening:** 2 PM - 6 PM
   - **Full Day:** 9 AM - 6 PM
   - **Custom:** Set your own hours

2. **Set Time Preferences:**
   - Morning Start: `09:00`
   - Morning End: `13:00`
   - Evening Start: `14:00`
   - Evening End: `18:00`
   - Slot Duration: `15 minutes` (recommended)

3. Click **"Initialize Weekly Schedule"**

**Result:** You'll now have:
- ✅ Monday-Friday: Active with your selected shift
- ❌ Saturday-Sunday: Inactive (day off)

---

### 2️⃣ **Customize Individual Days**

Each day shows as a card:

```
┌─────────────────────────────────┐
│ MONDAY                    Active│
│ ⏰ 09:00 - 13:00               │
│ Shift: Morning                  │
│ Slot Duration: 15 min           │
│ Max Slots: 16                   │
│ [Edit] [Deactivate]             │
└─────────────────────────────────┘
```

**To Edit a Day:**
1. Click **"Edit"** button
2. Change:
   - Status (Active/Inactive)
   - Shift Type
   - Start/End Time
   - Slot Duration
   - Max Patients (optional)
3. Click **"Save Changes"**

**To Add Weekend Availability:**
1. Find Saturday or Sunday card
2. Click **"Edit"**
3. Change Status to **"Active"**
4. Set your hours (e.g., 10:00 - 14:00)
5. Save

---

### 3️⃣ **Generate Daily Schedules**

Once your weekly template is set:

1. Click **"Generate Daily Schedules"** button
2. Set date range:
   - **Start Date:** Today (or any future date)
   - **End Date:** 30 days from today
3. Click **"Generate Schedules"**

**What Happens:**
- System creates actual daily schedules for each active day
- Patients can now book appointments for these dates
- You'll see a success message: "Generated 23 daily schedules from Feb 1 to Feb 28"

**Note:** If you already have a schedule for a specific date, it won't be overwritten!

---

### 4️⃣ **Override a Specific Date**

**Example:** You have a conference on Friday, Feb 14 and want different hours.

**Option A: Via Daily Schedules Page**
1. Go to `/doctor/schedule` (existing schedule page)
2. Create/edit schedule for Feb 14
3. Set your custom hours (e.g., 15:00 - 19:00)
4. This schedule is now marked as **manual override**
5. Auto-generation will **skip** this date

**Option B: Via Weekly Template**
1. If the date hasn't been generated yet:
2. Edit your Friday template temporarily
3. Generate schedules
4. Change Friday template back to normal

---

### 5️⃣ **Quick Actions**

#### Toggle Day On/Off
- Click **"Activate"** or **"Deactivate"** button on any day card
- Instantly enables/disables that day of the week

#### Reset Templates
- Click **"Reset Templates"** (warning: this deletes all templates)
- Use this to start fresh if needed
- Existing daily schedules are NOT affected

#### Re-generate Future Schedules
- You can run "Generate Schedules" multiple times
- Only creates schedules for dates that don't exist yet
- Safe to run weekly or monthly

---

## 🤖 Automatic Schedule Generation

**Good News:** You don't have to manually generate schedules!

The system **automatically runs every day at 2 AM** and:
1. Checks all doctors with weekly templates
2. Generates schedules for the next 30 days
3. Only creates new schedules (doesn't overwrite existing ones)

**This means:** Once you set your weekly template, your schedule is always ready 30 days in advance!

---

## 💡 Common Use Cases

### Use Case 1: Regular Mon-Fri Doctor
```
Setup:
- Monday-Friday: 9 AM - 1 PM (Morning shift)
- Saturday-Sunday: Day off

Result: Schedules auto-generated every weekday, weekends off.
```

### Use Case 2: Doctor with Saturday Clinic
```
Setup:
- Monday-Friday: 9 AM - 5 PM (Full day)
- Saturday: 10 AM - 2 PM (Custom)
- Sunday: Day off

Result: Saturday morning clinic available for bookings!
```

### Use Case 3: Variable Schedule
```
Setup:
- Monday: 9 AM - 1 PM
- Tuesday: 2 PM - 6 PM
- Wednesday: 9 AM - 6 PM (Full day)
- Thursday: 9 AM - 1 PM
- Friday: Day off (personal day)
- Saturday-Sunday: Day off

Result: Flexible weekly pattern that repeats.
```

### Use Case 4: Conference Override
```
Weekly Template: Monday-Friday 9-1
Exception: Feb 15 (Friday) - attending conference

Action:
1. Go to /doctor/schedule
2. Create schedule for Feb 15: 2 PM - 6 PM (evening only)
3. System respects this override
4. Other Fridays remain 9-1 as per template
```

---

## 🔍 How to Check Your Current Setup

### View Weekly Template
1. Go to `/doctor/weekly-schedule`
2. See all 7 days (Monday-Sunday)
3. Check which days are active
4. Review times and shift types

### View Generated Daily Schedules
1. Go to `/doctor/schedule`
2. See actual daily schedules created
3. Filter by date range
4. Check for gaps or issues

### Summary Card (Top of Weekly Schedule Page)
```
Doctor: Dr. Sarah Wilson
Active Days: 5 / 7
Specialization: Cardiology
Status: Template Active
```

---

## ⚠️ Important Notes

### Do NOT Delete Templates Unless:
- You want to completely redesign your weekly schedule
- You're switching to a different schedule system
- **Remember:** Deleting templates doesn't delete existing daily schedules

### Manual Overrides:
- Always respected by auto-generation
- Use for one-time exceptions (conferences, holidays, etc.)
- Clearly marked in the system

### Slot Duration:
- **15 minutes** = 16 patients in a 4-hour morning shift
- **20 minutes** = 12 patients in a 4-hour morning shift
- **30 minutes** = 8 patients in a 4-hour morning shift
- Choose based on your consultation needs

### Max Patients:
- Leave blank = auto-calculated based on time slots
- Set a number = hard limit (useful if you want breaks)

---

## 🎯 Best Practices

1. **Set Template First:** Don't create daily schedules manually before setting template
2. **Run Generate Monthly:** Generate 30-60 days in advance for patient convenience
3. **Use Overrides Sparingly:** Only for genuine exceptions
4. **Keep Weekends Consistent:** If you work Saturdays, keep the same hours weekly
5. **Review Quarterly:** Check if your template still matches your availability

---

## 📞 Support

If you encounter issues:

1. **Check logs:** Look for errors in console/logs
2. **Verify database:** Ensure `doctor_weekly_templates` table exists
3. **Test generation:** Try generating a small date range first (7 days)
4. **Contact admin:** If auto-generation fails

---

## ✅ Checklist for New Doctors

- [ ] Login to system
- [ ] Navigate to `/doctor/weekly-schedule`
- [ ] Click "Initialize Weekly Schedule"
- [ ] Select your default shift type
- [ ] Set time preferences
- [ ] Click "Initialize"
- [ ] Review 7 day cards (Mon-Sun)
- [ ] Edit any days that need customization
- [ ] Click "Generate Daily Schedules"
- [ ] Set range: Today to +30 days
- [ ] Click "Generate"
- [ ] Verify schedules created at `/doctor/schedule`
- [ ] Done! ✅

---

**That's it! Your weekly schedule is now active and auto-generates daily schedules. 🎉**

**Questions? Contact your system administrator.**
