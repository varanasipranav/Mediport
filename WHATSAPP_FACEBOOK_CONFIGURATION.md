# 📱 WhatsApp Webhook Configuration Guide for Facebook Developer Console

**Date:** January 31, 2026  
**Your App:** Hospital Appointment System

---

## 🔑 WHAT TO ENTER IN FACEBOOK DEVELOPER CONSOLE

### **Step 1: Get Your App Secret**

1. Go to: https://developers.facebook.com
2. Select your app from the dashboard
3. Click **Settings** → **Basic** (in left sidebar)
4. Find **App Secret** field
5. Click **Show** button
6. Copy the value

**Update application.properties:**
```properties
whatsapp.webhook.app-secret=<PASTE_YOUR_APP_SECRET_HERE>
```

---

### **Step 2: Configure WhatsApp Webhook**

1. Go to: https://developers.facebook.com
2. Select your app
3. In left sidebar: Click **WhatsApp** → **Configuration**
4. Find section: **Webhooks**
5. Click **Configure** or **Edit** button

---

## 📋 WEBHOOK CONFIGURATION VALUES

### **Callback URL**

This is YOUR server URL where Facebook will send webhook events.

#### **For Development (localhost):**
```
Option 1: Use ngrok (Recommended for testing)
https://abc123.ngrok-free.app/api/whatsapp/webhook

Option 2: Use localtunnel
https://abc123.loca.lt/api/whatsapp/webhook
```

#### **For Production (Your domain):**
```
https://yourdomain.com/api/whatsapp/webhook
```

**⚠️ IMPORTANT:**
- URL must be **HTTPS** (not HTTP) - Facebook requires secure connection
- URL must be publicly accessible (localhost won't work without tunnel)
- Port 8081 is your local dev server - you need a tunnel or domain

---

### **Verify Token**

This is the token YOU created in application.properties.

**Copy this exact value:**
```
hospital_webhook_secure_token_2026_xyz123
```

**Or from your application.properties:**
```properties
whatsapp.webhook.verify-token=hospital_webhook_secure_token_2026_xyz123
```

**⚠️ MUST MATCH EXACTLY** - case-sensitive, no spaces!

---

## 🚀 STEP-BY-STEP FACEBOOK CONFIGURATION

### **Step 1: Setup Ngrok (For Local Testing)**

```bash
# Download ngrok from: https://ngrok.com/download
# Install and authenticate

# Start your Spring Boot app first
cd C:\webapp
.\mvnw.cmd spring-boot:run

# In another terminal, start ngrok
ngrok http 8081
```

**You'll get output like:**
```
Forwarding  https://abc123.ngrok-free.app -> http://localhost:8081
```

**Your Callback URL is:**
```
https://abc123.ngrok-free.app/api/whatsapp/webhook
```

---

### **Step 2: Configure in Facebook**

1. **Go to Facebook Developer Console:**
   - URL: https://developers.facebook.com
   - Login with your account

2. **Navigate to WhatsApp Configuration:**
   - Click your app name
   - Left sidebar → **WhatsApp** → **Configuration**

3. **Find Webhooks Section:**
   - Scroll to **Webhooks** section
   - Click **Configure** (or **Edit** if already configured)

4. **Enter Callback URL:**
   ```
   Field: Callback URL
   Value: https://abc123.ngrok-free.app/api/whatsapp/webhook
   
   (Replace abc123.ngrok-free.app with your ngrok URL)
   ```

5. **Enter Verify Token:**
   ```
   Field: Verify token
   Value: hospital_webhook_secure_token_2026_xyz123
   
   (Must match exactly what's in application.properties)
   ```

6. **Click "Verify and save"**

---

## ✅ VERIFICATION PROCESS

When you click "Verify and save", Facebook will:

1. Send a GET request to your Callback URL:
   ```
   GET https://abc123.ngrok-free.app/api/whatsapp/webhook?hub.mode=subscribe&hub.verify_token=hospital_webhook_secure_token_2026_xyz123&hub.challenge=123456789
   ```

2. Your server will:
   - Check if `hub.mode` = "subscribe" ✓
   - Check if `hub.verify_token` matches ✓
   - Return the `hub.challenge` value

3. If successful:
   - ✅ **"Successfully verified webhook"** message appears
   - Green checkmark shows next to webhook URL

4. If fails:
   - ❌ Error message appears
   - Check your logs for details

---

## 🔍 TROUBLESHOOTING

### **Error: "The URL couldn't be validated"**

**Possible causes:**
1. ❌ Your app is not running (start it first!)
2. ❌ Using `localhost` instead of ngrok URL
3. ❌ Using `http://` instead of `https://`
4. ❌ Ngrok session expired (restart ngrok)
5. ❌ Firewall blocking incoming requests

**Fix:**
```bash
# 1. Ensure app is running
cd C:\webapp
.\mvnw.cmd spring-boot:run

# 2. Start ngrok in another terminal
ngrok http 8081

# 3. Use the HTTPS URL from ngrok
```

---

### **Error: "Verify token mismatch"**

**Possible causes:**
1. ❌ Token in Facebook doesn't match application.properties
2. ❌ Extra spaces or wrong case
3. ❌ App needs restart after config change

**Fix:**
```bash
# 1. Check application.properties
whatsapp.webhook.verify-token=hospital_webhook_secure_token_2026_xyz123

# 2. Use EXACT same value in Facebook

# 3. Restart your app if you changed the config
```

---

### **Error: "Connection refused" or "Timeout"**

**Possible causes:**
1. ❌ App not running on port 8081
2. ❌ Ngrok not running
3. ❌ Security Configuration blocking webhook endpoint

**Fix:**
```bash
# Check if app is running
# Open browser: http://localhost:8081/api/whatsapp/webhook/health
# Should see: "WhatsApp Webhook is UP ✓"

# Check ngrok
# It should show incoming requests in the terminal
```

---

## 📊 SUBSCRIBE TO WEBHOOK FIELDS

After verification succeeds, you need to **subscribe to events**:

1. In the same Webhooks section
2. Find **Webhook fields** section
3. Check these boxes:
   - ☑️ **messages** (Required - incoming messages)
   - ☑️ **message_deliveries** (Optional - delivery receipts)
   - ☑️ **message_reads** (Optional - read receipts)

4. Click **Save**

---

## 🧪 TEST YOUR WEBHOOK

### **Method 1: Check Health Endpoint**

```bash
# Open browser or use curl:
https://abc123.ngrok-free.app/api/whatsapp/webhook/health

# Expected response:
"WhatsApp Webhook is UP ✓"
```

### **Method 2: Send Test Message**

1. Save your WhatsApp Business number to your phone
2. Send "Hi" from your personal WhatsApp
3. Check your Spring Boot logs:
   ```
   📱 Received message from: 919876543210 (Your Name)
      Type: TEXT
      Text: Hi
   ```

### **Method 3: Check Ngrok Dashboard**

1. Open: http://localhost:4040 (ngrok web UI)
2. You'll see all incoming webhook requests
3. Can inspect request/response details

---

## 📝 PRODUCTION DEPLOYMENT

When deploying to production (AWS, Heroku, etc.):

### **Update Callback URL to:**
```
https://yourdomain.com/api/whatsapp/webhook

Examples:
- https://hospital.example.com/api/whatsapp/webhook
- https://api.hospital.com/api/whatsapp/webhook
```

### **Get App Secret:**
```
1. Go to Facebook Developer Console
2. Settings → Basic
3. Copy App Secret
4. Update application.properties (or use environment variables)
```

### **Environment Variables (Recommended for Production):**

Instead of hardcoding in application.properties:

```bash
# Set as environment variables:
export WHATSAPP_PHONE_NUMBER_ID=1016183291559791
export WHATSAPP_ACCESS_TOKEN=EAAROE0BPEMUBQt...
export WHATSAPP_VERIFY_TOKEN=hospital_webhook_secure_token_2026_xyz123
export WHATSAPP_APP_SECRET=your_actual_app_secret_here

# Update application.properties to:
whatsapp.api.phone-number-id=${WHATSAPP_PHONE_NUMBER_ID}
whatsapp.api.access-token=${WHATSAPP_ACCESS_TOKEN}
whatsapp.webhook.verify-token=${WHATSAPP_VERIFY_TOKEN}
whatsapp.webhook.app-secret=${WHATSAPP_APP_SECRET}
```

---

## ✅ CONFIGURATION CHECKLIST

- [ ] Spring Boot app is running (port 8081)
- [ ] Ngrok is running and shows HTTPS URL
- [ ] application.properties has verify token set
- [ ] Callback URL in Facebook uses ngrok HTTPS URL
- [ ] Verify token in Facebook matches application.properties
- [ ] Clicked "Verify and save" in Facebook
- [ ] Verification succeeded (green checkmark)
- [ ] Subscribed to "messages" webhook field
- [ ] Sent test message from WhatsApp
- [ ] Saw message in Spring Boot logs
- [ ] App Secret copied from Facebook (for production)

---

## 🎯 QUICK COPY-PASTE VALUES

**For Your Reference:**

```
Phone Number ID:
1016183291559791

Access Token (already in your config):
EAAROE0BPEMUBQt... (your full token)

Verify Token:
hospital_webhook_secure_token_2026_xyz123

Callback URL (replace with your ngrok URL):
https://YOUR_NGROK_URL.ngrok-free.app/api/whatsapp/webhook

Webhook Fields to Subscribe:
☑️ messages
☑️ message_deliveries (optional)
```

---

## 📞 SUPPORT

If you encounter issues:

1. **Check Spring Boot logs:** Look for webhook-related errors
2. **Check ngrok dashboard:** http://localhost:4040 - see incoming requests
3. **Check Facebook webhook logs:** In Configuration → Webhooks → View logs
4. **Test health endpoint:** /api/whatsapp/webhook/health

---

**Configuration Complete!** 🎉

Once verified, you can receive WhatsApp messages in your Spring Boot application!
