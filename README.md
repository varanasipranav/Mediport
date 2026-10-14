# Mediport

Clinic management for outpatient clinics: appointment booking with live queues, WhatsApp booking and alerts,
printable prescriptions (PDF + QR verification), and pharmacy inventory with dispensing.

**Roles:** Admin · Doctor · Patient · Front desk (attendant) · Pharmacist

## What it does

| Area | Highlights |
|---|---|
| **Scheduling** | Doctors set a weekly template; daily schedules are generated from it. Each booking gets a queue number and a time worked out from the schedule (`start + (queue − 1) × slot`). Bookings are race-safe (row lock) and can never run past the doctor's hours. |
| **WhatsApp bot** | Patients book, reschedule, cancel, view appointments and ask "where am I in the queue?" — all from WhatsApp. Days and times offered come straight from the doctor's schedule. |
| **Live queue alerts** | WhatsApp messages when 2 patients are ahead, "it's your turn", slot time reached, doctor running late (with a new estimate), missed turn; 24 h and 2 h reminders; prescription-ready message with a PDF link. Each alert is sent once. |
| **Prescriptions** | Printable A4 prescription (clinic letterhead, doctor registration no., medicine table, signature block, QR code), server-side PDF, public QR verification page, expiring share link for WhatsApp. Medicine finder with live stock while writing. |
| **Pharmacy** | Medicine catalogue, batches with expiry, receive/adjust stock, earliest-expiry-first dispensing against a prescription (partial dispensing supported), expiry and low-stock alerts, full movement history. |
| **Security** | Role-based access on every page, signed WhatsApp webhook, CSRF, secrets outside the code, per-record ownership checks (a doctor/patient only sees their own). |

## Run it

Requirements: **JDK 25**, **MySQL 8**.

```bash
# 1. create the database (tables are created automatically on first start)
mysql -uroot -p -e "CREATE DATABASE hospital CHARACTER SET utf8mb4"

# 2. put your secrets in secrets.properties (git-ignored) - see "Configuration"

# 3. run
./mvnw spring-boot:run          # or: ./mvnw package && java -jar target/webapp-0.0.1-SNAPSHOT.jar
```

Open <http://localhost:8081>. On a brand-new database an `admin` account is created; its password is
`APP_ADMIN_PASSWORD` if set, otherwise a random one printed **once** in the console. Change it after signing in.

Database changes are managed by **Flyway** (`src/main/resources/db/migration`). Existing databases are baselined at V1
automatically; new versions apply on startup.

## Configuration

Everything sensitive comes from environment variables or a git-ignored `secrets.properties` next to the jar.

| Setting | Purpose |
|---|---|
| `DB_USERNAME`, `DB_PASSWORD` | Database login |
| `APP_ADMIN_PASSWORD` | First admin password (first start only) |
| `WHATSAPP_ACCESS_TOKEN` | WhatsApp Cloud API token |
| `WHATSAPP_APP_SECRET` | Meta app secret — **required**: webhook signatures are verified with it and everything is rejected without it |
| `WHATSAPP_VERIFY_TOKEN` | Token you enter when registering the webhook in Meta |
| `RX_SIGNING_KEY` | Key that signs prescription QR codes / PDF links (keep it stable, or old QR codes stop verifying) |
| `APP_PUBLIC_URL` | Public base URL (used in QR codes and WhatsApp links), e.g. `https://clinic.example.com` |
| `REMEMBER_ME_KEY` | Key for "keep me signed in" cookies |
| `CORS_ALLOWED_ORIGINS` | Comma-separated origins allowed to call the API from a browser |
| `app.timezone` | Clinic time zone for "today"/reminders (default `Asia/Kolkata`) |
| `app.default-region` | Default country for phone numbers (default `IN`) |
| `clinic.name`, `clinic.address`, `clinic.phone`, `clinic.email`, `clinic.tagline` | Letterhead on prescriptions and the app |

WhatsApp webhook URL: `https://<your-domain>/api/whatsapp/webhook` (subscribe to **messages**).

> **WhatsApp 24-hour rule.** Meta only allows free-text messages to people who messaged the clinic in the last 24 hours.
> Reminders and alerts to patients who booked on the web (and haven't chatted recently) need approved **message templates**.

## Tests

```bash
./mvnw test
```

- Unit tests: schedule slot maths, prescription parsing/signing, inventory (earliest-expiry-first, partial dispensing, quantity suggestion).
- `PageSmokeTest` renders every page as its role against the configured database.

## Project layout

```
src/main/java/com/appointment/webapp
  config/        security, CORS, clinic settings, first-run data
  controller/    pages per role (admin, doctor, patient, attendant, pharma) + prescription documents
  service/       booking, schedules, prescriptions (+PDF/QR), inventory
  whatsapp/      webhook, conversation flow, queue alerts, reminders
src/main/resources
  templates/     Thymeleaf pages (layout + fragments)
  static/css     mediport design system (tokens, components, login, prescription)
  static/vendor  Bootstrap 4, AdminLTE 3, Font Awesome, jQuery, SweetAlert2 (served locally - no CDN needed)
  db/migration   Flyway SQL
```
