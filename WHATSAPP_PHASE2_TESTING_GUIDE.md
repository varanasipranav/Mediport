# 🧪 PHASE 2 Testing Guide

## Quick Start Testing

### **Step 1: Run Database Migration**

```bash
# Connect to MySQL
mysql -u root -p

# Select database
use hospital;

# Run migration script
source C:/webapp/database-schema-whatsapp-conversations.sql;

# Verify tables created
SHOW TABLES LIKE 'whatsapp%';

# Expected output:
# +--------------------------------+
# | Tables_in_hospital (whatsapp%) |
# +--------------------------------+
# | whatsapp_conversations          |
# | whatsapp_messages               |
# +--------------------------------+

# Check table structure
DESC whatsapp_conversations;

exit;
```

---

### **Step 2: Start Application**

```bash
cd C:\webapp
.\mvnw.cmd spring-boot:run
```

**Wait for:**
```
Started WebappApplication in X.XXX seconds
```

---

### **Step 3: Start Ngrok**

```bash
# In another terminal:
ngrok http 8081
```

**Copy the HTTPS URL** (e.g., `https://abc123.ngrok-free.app`)

---

### **Step 4: Update Facebook Webhook** (if ngrok URL changed)

1. Go to: https://developers.facebook.com
2. Your App → WhatsApp → Configuration
3. Webhooks → Edit
4. Update Callback URL: `https://NEW_NGROK_URL.ngrok-free.app/api/whatsapp/webhook`
5. Click "Verify and save"

---

### **Step 5: Send Test Messages**

Open WhatsApp on your phone and send to your business number:

#### **Test 1: Initial Contact**

```
📱 You: Hi
```

**Expected Response:**
```
🤖 Bot: 🏥 Welcome to City Hospital!

      Hello [Your Name]! 👋

      I'm your virtual assistant. I can help you:

      📅 Book appointments
      👁️ View your appointments
      ❌ Cancel appointments
      🔄 Reschedule appointments

      Let's get started!

🤖 Bot: What would you like to do?

      Please choose an option:

      [📅 Book Appointment]  [👁️ View Appointments]  [❌ Cancel]
```

---

#### **Test 2: Click "📅 Book Appointment"**

```
📱 You: (click "📅 Book Appointment" button)
```

**Expected Response:**
```
🤖 Bot: Let's create your patient profile first.

      Please enter your *full name*:
```

---

#### **Test 3: Enter Name**

```
📱 You: John Smith
```

**Expected Response:**
```
🤖 Bot: Thanks, *John Smith*! 👍

      Please select your gender:

      [👨 Male]  [👩 Female]  [⚧ Other]
```

---

#### **Test 4: Select Gender**

```
📱 You: (click "👨 Male")
```

**Expected Response:**
```
🤖 Bot: Please enter your *date of birth*

      Format: DD/MM/YYYY
      Example: 15/05/1990
```

---

#### **Test 5: Enter DOB**

```
📱 You: 15/05/1990
```

**Expected Response:**
```
🤖 Bot: ✅ Patient registration will be completed in Phase 3!

      What would you like to do?

      Please choose an option:

      [📅 Book Appointment]  [👁️ View Appointments]  [❌ Cancel]
```

---

## 📊 Verify in Database

```sql
-- Check conversation was created
SELECT * FROM whatsapp_conversations 
WHERE whatsapp_phone = '+919876543210'  -- Replace with your number
ORDER BY created_at DESC LIMIT 1;

-- Check context data
SELECT 
    whatsapp_phone,
    current_state,
    context_data,
    sender_name,
    last_message_at
FROM whatsapp_conversations 
WHERE whatsapp_phone = '+919876543210';

-- Expected context_data:
-- {"tempPatientName":"John Smith","tempPatientGender":"MALE"}

-- Check message log
SELECT 
    direction,
    message_type,
    content,
    created_at
FROM whatsapp_messages wm
JOIN whatsapp_conversations wc ON wm.conversation_id = wc.id
WHERE wc.whatsapp_phone = '+919876543210'
ORDER BY created_at DESC
LIMIT 10;
```

---

## 🔍 Check Application Logs

Look for these log messages:

```
📱 Received message from: +919876543210 (John Doe)
   Type: TEXT
   Text: Hi
🔄 Processing message from: +919876543210
Current state: INITIAL
Handling initial state
State transition for +919876543210: INITIAL → MAIN_MENU
✅ Message sent successfully
✅ Message processed successfully
```

---

## ✅ Success Checklist

- [ ] Database tables created successfully
- [ ] Application started without errors
- [ ] Ngrok running and webhook verified
- [ ] Sent "Hi" → Received welcome message
- [ ] Main menu buttons displayed
- [ ] Clicked "Book Appointment" → Asked for name
- [ ] Entered name → Saved in context
- [ ] Gender buttons displayed
- [ ] Clicked gender → Saved in context
- [ ] DOB prompt displayed
- [ ] Entered DOB → Back to main menu
- [ ] Database shows conversation record
- [ ] Context data contains name and gender
- [ ] No errors in application logs

---

## 🐛 Common Issues

### **Issue: Bot doesn't respond**

**Check:**
```bash
# 1. Is app running?
# Terminal should show Spring Boot running

# 2. Is ngrok running?
# Should show "Forwarding https://..."

# 3. Test webhook directly:
curl https://YOUR_NGROK_URL.ngrok-free.app/api/whatsapp/webhook/health

# Expected: "WhatsApp Webhook is UP ✓"
```

---

### **Issue: Database error**

```
Error: Table 'hospital.whatsapp_conversations' doesn't exist
```

**Fix:**
```bash
mysql -u root -p hospital < C:/webapp/database-schema-whatsapp-conversations.sql
```

---

### **Issue: Buttons not showing**

**Possible causes:**
1. WhatsApp API credentials incorrect
2. Access token expired
3. Phone number not verified

**Check logs for:**
```
❌ Failed to send message. Status: 403
```

**Fix:** Verify WhatsApp API access token in application.properties

---

### **Issue: State not transitioning**

**Check database:**
```sql
SELECT current_state, previous_state 
FROM whatsapp_conversations 
WHERE whatsapp_phone = '+919876543210';
```

**Should show state changes after each interaction**

---

## 📈 Next: Phase 3

Once Phase 2 tests pass, you're ready for **Phase 3: Patient Identification & Complete Registration**!

Phase 3 will:
- ✅ Check if phone number exists in database
- ✅ Handle multiple patients with same phone
- ✅ Validate date of birth format
- ✅ Create patient record in `users` table
- ✅ Link WhatsApp conversation to patient

---

**Happy Testing!** 🎉
