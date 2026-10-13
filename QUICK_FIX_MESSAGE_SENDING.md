# ✅ Quick Fix Checklist - Message Not Sending

## IMMEDIATE TESTS (Do these first!)

### **Test 1: Check Configuration** ⚡

**Open in browser:**
```
http://localhost:8081/api/whatsapp/test/config
```

**What to check:**
- ✅ `accessToken` says "Present" (NOT "MISSING")
- ✅ `phoneNumberId` matches your WhatsApp Business number
- ✅ `messagesEndpoint` looks correct

**❌ If accessToken is MISSING:**
1. Check `application.properties` line 61
2. Make sure it's not empty
3. Restart app after fixing

---

### **Test 2: Send Test Message** ⚡

**Replace `+919876543210` with YOUR phone number!**

**Open in browser:**
```
http://localhost:8081/api/whatsapp/test/send-text?phone=+919876543210&message=Hello
```

**Expected:**
- Browser shows: `{"success":true,...}`
- WhatsApp on your phone receives: "Hello"

**❌ If success=false:**
- Check application logs (Spring Boot console)
- Look for error messages starting with ❌

---

## COMMON ERRORS & QUICK FIXES

### **Error 1: 401 Unauthorized**

**Logs show:**
```
❌ HTTP Client Error while sending message!
   Status: 401 UNAUTHORIZED
   Response Body: {"error":{"message":"Invalid OAuth access token"}}
```

**FIX:**
1. Access token is expired
2. Go to: https://developers.facebook.com
3. Your App → WhatsApp → API Setup
4. Copy the **Temporary access token** (or generate permanent one)
5. Update `application.properties` line 61
6. Restart app

---

### **Error 2: 400 Bad Request - 24 Hour Window**

**Logs show:**
```
Response Body: {"error":{"message":"Message failed to send because more than 24 hours have passed..."}}
```

**FIX:**
1. This is WhatsApp's rule
2. **Solution:** Send a message FROM your phone TO your business number first
3. Then bot can reply for 24 hours

**Steps:**
1. Open WhatsApp
2. Send "Hi" to your business number
3. Wait for bot to reply
4. 24-hour window is now open!

---

### **Error 3: 400 Bad Request - Invalid Phone**

**Logs show:**
```
Response Body: {"error":{"message":"Parameter phone number is not a valid phone number"}}
```

**FIX:**
- Use E.164 format: `+[country code][number]`
- Examples:
  - ✅ `+919876543210` (India)
  - ✅ `+14155551234` (USA)
  - ❌ `9876543210` (missing +91)
  - ❌ `+91 9876543210` (has space)

---

### **Error 4: Connection Refused**

**Logs show:**
```
❌ Network/Connection error while sending message!
   Error: Connection refused
```

**FIX:**
- Check internet connection
- WhatsApp API might be down (unlikely)
- Firewall blocking outgoing HTTPS

---

## STEP-BY-STEP FIX PROCEDURE

### **1. Start App & Check Config**

```bash
# Terminal 1: Start app
cd C:\webapp
.\mvnw.cmd spring-boot:run

# Wait for "Started WebappApplication"

# Browser: Check config
http://localhost:8081/api/whatsapp/test/config
```

---

### **2. Test Direct Message Send**

```bash
# Browser (replace phone number):
http://localhost:8081/api/whatsapp/test/send-text?phone=+919876543210&message=Test123

# Check your WhatsApp - you should receive "Test123"
```

**✅ If you received it:** Message sending works!  
**❌ If you didn't:** Check logs for errors

---

### **3. Fix Common Issues**

**If token error (401):**
```properties
# application.properties line 61
# Replace with fresh token from Facebook
whatsapp.api.access-token=EAAA...your_new_token...
```

**If 24-hour window error:**
```
1. Open WhatsApp
2. Message your business number: "Hi"
3. Bot should now reply
```

**If wrong phone format:**
```
# Must include country code
+919876543210  ✅
9876543210     ❌
```

---

### **4. Test Conversation Flow**

Once test endpoint works:

```
1. Send "Hi" from WhatsApp → Should get welcome message
2. Click "Book Appointment" → Should ask for name
3. Send name → Should ask for gender
```

---

## QUICK DIAGNOSTIC COMMANDS

```bash
# Test config
curl http://localhost:8081/api/whatsapp/test/config

# Test text message (replace phone!)
curl "http://localhost:8081/api/whatsapp/test/send-text?phone=+919876543210&message=Hello"

# Test buttons (replace phone!)
curl "http://localhost:8081/api/whatsapp/test/send-buttons?phone=+919876543210"

# Check webhook health
curl http://localhost:8081/api/whatsapp/webhook/health
```

---

## MOST COMMON CAUSE

**90% of the time it's one of these:**

1. ❌ **Access token expired** → Get new token from Facebook
2. ❌ **24-hour window** → User must message you first
3. ❌ **Wrong phone format** → Must start with `+` and country code

---

## NEXT STEPS

✅ **If test endpoint works:**
- Message sending is fixed!
- Conversation flow should now work
- Test by sending "Hi" from WhatsApp

✅ **If still not working:**
- Share the EXACT error from logs
- Check `WHATSAPP_MESSAGE_SENDING_DEBUG.md` for detailed guide

---

**Start with Test 1 and Test 2 above! 🚀**
