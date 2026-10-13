# 🔍 WhatsApp Message Sending - Debug Guide

## Problem: Bot receives messages but doesn't reply

---

## Step 1: Verify Configuration

### **Test Configuration Endpoint**

```bash
# Open in browser or use curl:
http://localhost:8081/api/whatsapp/test/config
```

**Check Output:**
```json
{
  "baseUrl": "https://graph.facebook.com/v22.0",
  "phoneNumberId": "1016183291559791",
  "accessToken": "Present (length: XXX)",
  "messagesEndpoint": "https://graph.facebook.com/v22.0/1016183291559791/messages"
}
```

**✅ Verify:**
- `baseUrl` is correct
- `phoneNumberId` matches your WhatsApp Business number
- `accessToken` shows "Present" (not "MISSING")
- `messagesEndpoint` looks correct

---

## Step 2: Test Message Sending Directly

### **Test 1: Send Simple Text Message**

```bash
# Replace +919876543210 with YOUR phone number
curl "http://localhost:8081/api/whatsapp/test/send-text?phone=+919876543210&message=Hello%20from%20test"
```

**Or open in browser:**
```
http://localhost:8081/api/whatsapp/test/send-text?phone=+919876543210&message=Hello
```

**Expected Response:**
```json
{
  "success": true,
  "phone": "+919876543210",
  "message": "Hello",
  "result": "{\"messaging_product\":\"whatsapp\",\"contacts\":[...],\"messages\":[...]}"
}
```

**Check Your Phone:** You should receive "Hello" from your WhatsApp Business number

---

### **Test 2: Send Button Message**

```bash
curl "http://localhost:8081/api/whatsapp/test/send-buttons?phone=+919876543210"
```

**Expected:** You should receive a message with 3 buttons

---

## Step 3: Check Application Logs

When you run the tests above, check logs for:

### **✅ Successful Send:**
```
📤 Sending message to WhatsApp API
   Endpoint: https://graph.facebook.com/v22.0/1016183291559791/messages
   Payload: {"messaging_product":"whatsapp",...}
✅ Message sent successfully!
   Response: {"messaging_product":"whatsapp",...}
```

### **❌ Error Examples:**

#### **Error 1: Invalid Access Token**
```
❌ HTTP Client Error while sending message!
   Status: 401 UNAUTHORIZED
   Response Body: {"error":{"message":"Invalid OAuth access token","type":"OAuthException"}}
```

**Fix:** 
- Access token expired or invalid
- Generate new token from Facebook Business Manager
- Update `application.properties`

---

#### **Error 2: Invalid Phone Number**
```
❌ HTTP Client Error while sending message!
   Status: 400 BAD_REQUEST
   Response Body: {"error":{"message":"Parameter phone number is not a valid phone number"}}
```

**Fix:**
- Use E.164 format: `+[country code][number]`
- Example: `+919876543210` (India), `+14155551234` (US)

---

#### **Error 3: Phone Number Not Registered**
```
❌ HTTP Client Error while sending message!
   Status: 400 BAD_REQUEST
   Response Body: {"error":{"message":"Message failed to send because more than 24 hours have passed since the customer last replied to this number"}}
```

**Fix:**
- This is WhatsApp's 24-hour rule
- User must message YOU first
- Or use an approved template message

---

#### **Error 4: Invalid Phone Number ID**
```
❌ HTTP Client Error while sending message!
   Status: 404 NOT_FOUND
   Response Body: {"error":{"message":"Invalid parameter"}}
```

**Fix:**
- Phone Number ID in `application.properties` is wrong
- Get correct ID from: Facebook Business Manager → WhatsApp → API Setup

---

#### **Error 5: Network/Connection Error**
```
❌ Network/Connection error while sending message!
   Error: Connection refused
```

**Fix:**
- No internet connection
- WhatsApp API is down
- Firewall blocking outgoing requests

---

## Step 4: Verify WhatsApp API Setup

### **Check Phone Number Status**

1. Go to: https://business.facebook.com
2. WhatsApp Manager → Phone Numbers
3. Check status: Should be **"Connected"** (green)
4. Check API Access: Should show **"Business API"**

---

### **Check Access Token**

1. Go to: https://developers.facebook.com
2. Your App → WhatsApp → API Setup
3. **Temporary Access Token:**
   - Valid for 24 hours (for testing)
   - Copy and use in `application.properties`
4. **Permanent Access Token:**
   - Settings → Users → System Users
   - Generate token with `whatsapp_business_messaging` permission

---

## Step 5: Test with Curl (Direct API Call)

```bash
# Replace these values:
# - YOUR_ACCESS_TOKEN
# - YOUR_PHONE_NUMBER_ID
# - YOUR_RECIPIENT_PHONE

curl -X POST \
  'https://graph.facebook.com/v22.0/YOUR_PHONE_NUMBER_ID/messages' \
  -H 'Authorization: Bearer YOUR_ACCESS_TOKEN' \
  -H 'Content-Type: application/json' \
  -d '{
    "messaging_product": "whatsapp",
    "to": "YOUR_RECIPIENT_PHONE",
    "type": "text",
    "text": {
      "body": "Direct API test"
    }
  }'
```

**If this works:** Problem is in your application code  
**If this fails:** Problem is with WhatsApp API credentials

---

## Step 6: Common Fixes

### **Fix 1: Update Access Token**

```properties
# application.properties
# Get new token from: https://developers.facebook.com → Your App → WhatsApp → API Setup
whatsapp.api.access-token=EAA...NEW_TOKEN_HERE...
```

**Restart application after changing**

---

### **Fix 2: Check Phone Number Format**

```java
// Correct formats:
+919876543210  // India
+14155551234   // USA
+447975777666  // UK

// Wrong formats:
9876543210     // Missing country code
+91 9876543210 // Has space
+91-9876543210 // Has dash
```

---

### **Fix 3: Test with YOUR Number First**

Send message to YOUR OWN phone number (the one you're testing with):

```bash
# This should work if everything is configured correctly
curl "http://localhost:8081/api/whatsapp/test/send-text?phone=+919876543210&message=Test"
```

---

### **Fix 4: Check 24-Hour Window**

WhatsApp has a 24-hour messaging window rule:

**Solution 1:** Have the user message YOU first
```
📱 User: Hi
🤖 Bot: (can now reply for next 24 hours)
```

**Solution 2:** Use approved template messages
- Go to Facebook Business Manager
- WhatsApp → Message Templates
- Create and get approved templates
- Use template message API

---

## Step 7: Enable Debug Logging

```properties
# application.properties
# Add these lines for detailed HTTP logging:

logging.level.org.springframework.web.client.RestTemplate=DEBUG
logging.level.org.apache.http=DEBUG
logging.level.com.appointment.webapp.whatsapp=DEBUG
```

**Restart app and check logs for full HTTP request/response**

---

## Step 8: Check Facebook Developer Console

1. Go to: https://developers.facebook.com
2. Your App → WhatsApp → Insights
3. Check for errors or warnings
4. Look at message delivery stats

---

## Quick Diagnosis Checklist

Run through this checklist:

- [ ] Configuration endpoint shows all values correctly
- [ ] Access token is not "MISSING"
- [ ] Phone Number ID matches Facebook dashboard
- [ ] Test endpoint can send message to YOUR phone
- [ ] You received the test message
- [ ] Logs show "✅ Message sent successfully"
- [ ] No errors in logs (401, 400, 404)
- [ ] WhatsApp Business phone is "Connected" in dashboard
- [ ] User messaged you first (24-hour window)

---

## If Still Not Working

### **Collect This Info:**

1. **Logs from test endpoint**
2. **Error message (exact text)**
3. **Response from curl test**
4. **Screenshots from Facebook dashboard**

### **Common Root Causes:**

| Issue | Symptom | Fix |
|-------|---------|-----|
| Expired token | 401 error | Generate new permanent token |
| Wrong phone ID | 404 error | Copy correct ID from dashboard |
| 24-hour window | Cannot send | User must message first |
| Wrong format | 400 error | Use E.164 format (+919876543210) |
| Network issue | Connection refused | Check internet/firewall |

---

## Next Steps

Once test endpoint works:

1. ✅ Test direct sending works
2. ✅ Fix configuration if needed
3. ✅ Restart application
4. ✅ Test conversation flow again

The message sending should now work in your conversation flow!

---

**Need Help?** Check logs first, then configuration, then API credentials. 🔍
