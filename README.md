`# Mediport
`
`Clinic management for outpatient clinics: appointment booking with live queues, WhatsApp booking and alerts,
`printable prescriptions (PDF + QR verification), and pharmacy inventory with dispensing.
`
`**Roles:** Admin · Doctor · Patient · Front desk (attendant) · Pharmacist
`
`## What it does
`
`| Area | Highlights |
`|---|---|
`| **Scheduling** | Doctors set a weekly template; daily schedules are generated from it. Each booking gets a queue number and a time worked out from the schedule (`start + (queue − 1) × slot`). Bookings are race-safe (row lock) and can never run past the doctor's hours. |
`| **WhatsApp bot** | Patients book, reschedule, cancel, view appointments and ask "where am I in the queue?" — all from WhatsApp. Days and times offered come straight from the doctor's schedule. |
`| **Live queue alerts** | WhatsApp messages when 2 patients are ahead, "it's your turn", slot time reached, doctor running late (with a new estimate), missed turn; 24 h and 2 h reminders; prescription-ready message with a PDF link. Each alert is sent once. |
`| **Languages** | The WhatsApp assistant speaks **English, Hindi, Telugu and Tamil**. New chats start with a language picker; patients can switch from the menu. The choice is saved on the patient (and on family members sharing the number), so reminders, queue alerts and the prescription message arrive in the same language. Adding a language = one enum value + one `i18n/whatsapp_xx.properties` file (a test checks the keys, placeholders and WhatsApp length limits). |
`| **Medical record** | Per patient: blood group, allergies, ongoing conditions, vitals per visit (entered by the doctor or front desk) and the full visit timeline with every prescription - from all doctors. When a patient books with a different doctor, that doctor sees the earlier prescriptions, can repeat medicines in one click, and gets an allergy warning. An "At a glance" summary is generated from the record (rule-based; swap in an AI summariser via the `RecordSummarizer` interface). Every access and change is logged. |
`| **Prescriptions** | Printable A4 prescription (clinic letterhead, doctor registration no., medicine table, signature block, QR code), server-side PDF, public QR verification page, expiring share link for WhatsApp. Medicine finder with live stock while writing. |
`| **Pharmacy** | Medicine catalogue, batches with expiry, receive/adjust stock, earliest-expiry-first dispensing against a prescription (partial dispensing supported), expiry and low-stock alerts, full movement history. |
`| **Security** | Role-based access on every page, signed WhatsApp webhook, CSRF, secrets outside the code, per-record ownership checks (a doctor/patient only sees their own). |
`
`## Run it
`
`Requirements: **JDK 25**, **MySQL 8**.
`
````bash
`# 1. create the database (tables are created automatically on first start)
`mysql -uroot -p -e "CREATE DATABASE hospital CHARACTER SET utf8mb4"
`
`# 2. put your secrets in secrets.properties (git-ignored) - see "Configuration"
`
`# 3. run
`./mvnw spring-boot:run          # or: ./mvnw package && java -jar target/webapp-0.0.1-SNAPSHOT.jar
````
`
`Open <http://localhost:8081>. On a brand-new database an `admin` account is created; its password is
``APP_ADMIN_PASSWORD` if set, otherwise a random one printed **once** in the console. Change it after signing in.
`
`Database changes are managed by **Flyway** (`src/main/resources/db/migration`). Existing databases are baselined at V1
`automatically; new versions apply on startup.
`
`## Deploying

See **[DEPLOYMENT.md](DEPLOYMENT.md)**: Docker Compose with MySQL and automatic HTTPS, settings, WhatsApp webhook, backups, updates and a security checklist.
`GET /health` is the health check for load balancers.

## Configuration
`
`Everything sensitive comes from environment variables or a git-ignored `secrets.properties` next to the jar.
`
`| Setting | Purpose |
`|---|---|
`| `DB_USERNAME`, `DB_PASSWORD` | Database login |
`| `APP_ADMIN_PASSWORD` | First admin password (first start only) |
`| `WHATSAPP_ACCESS_TOKEN` | WhatsApp Cloud API token |
`| `WHATSAPP_APP_SECRET` | Meta app secret — **required**: webhook signatures are verified with it and everything is rejected without it |
`| `WHATSAPP_VERIFY_TOKEN` | Token you enter when registering the webhook in Meta |
`| `RX_SIGNING_KEY` | Key that signs prescription QR codes / PDF links (keep it stable, or old QR codes stop verifying) |
`| `APP_PUBLIC_URL` | Public base URL (used in QR codes and WhatsApp links), e.g. `https://clinic.example.com` |
`| `REMEMBER_ME_KEY` | Key for "keep me signed in" cookies |
`| `CORS_ALLOWED_ORIGINS` | Comma-separated origins allowed to call the API from a browser |
`| `app.timezone` | Clinic time zone for "today"/reminders (default `Asia/Kolkata`) |
`| `app.default-region` | Default country for phone numbers (default `IN`) |
`| `clinic.name`, `clinic.address`, `clinic.phone`, `clinic.email`, `clinic.tagline` | Letterhead on prescriptions and the app |
`
`WhatsApp webhook URL: `https://<your-domain>/api/whatsapp/webhook` (subscribe to **messages**).
`
`> **WhatsApp 24-hour rule.** Meta only allows free-text messages to people who messaged the clinic in the last 24 hours.
`> Reminders and alerts to patients who booked on the web (and haven't chatted recently) need approved **message templates**.
`
`## Multi-clinic mode (one app, one database per clinic)
`
`Set `APP_TENANCY_ENABLED=true` to serve many clinics from one running app. Each clinic gets its **own MySQL schema** (`t_<code>`);
`a small master database (`platform_master`) only lists the clinics, who can sign in where, demo requests and platform operators.
`With the setting off (default) the app is a single clinic in the `hospital` database, exactly as before.
`
`- **Public page** at `/` (also `/site`): product page with a "Request a demo" form (CSRF, honeypot, timing check, 5 requests/hour per IP).
`- **Operator console** at `/platform` (its own login and session): review requests, **Approve** to create the clinic - schema, migrations, roles,
`  first admin and optional sample doctors - and see the admin login **once**. Also suspend/activate, re-run migrations, reset the admin password,
`  set the clinic's WhatsApp number. First operator: `PLATFORM_ADMIN_USER` / `PLATFORM_ADMIN_PASSWORD` (random one is logged once if blank).
`- **Sign-in**: everyone uses `/login`. Type `username@clinic` (e.g. `admin@sunrise`); a plain `username` works when only one clinic has it.
`  The clinic (site id) is stored in the session; **every request without a site id is blocked**, and the database connection is chosen from it.
`- **WhatsApp**: each clinic registers its own WhatsApp Business number (phone number id + token) in the console. The single webhook URL
`  `https://<domain>/api/whatsapp/webhook` routes each message to the clinic that owns the number it was sent to; replies go out from that number.
`  Clinics without their own number can't receive messages. The number in `application.properties` keeps serving the first clinic.
`- **First clinic**: the existing `hospital` database is registered as clinic `hospital` automatically (`TENANCY_LEGACY_SCHEMA`, `TENANCY_LEGACY_SITE`).
`- **Prescription links** carry the clinic (`/rx/pdf/s/<clinic>/...`); every clinic signs with its own key. Old links keep working for the first clinic.
- MySQL user needs `CREATE` on the clinic schemas, e.g. ``GRANT ALL ON `t\_%`.* TO 'app'@'%'`` plus `platform_master.*` and `hospital.*`.
`  Roughly 30 active clinics fit in MySQL's default 151 connections (each clinic uses at most 5, idle pools close).
`- Background jobs (schedule generation, reminders, queue alerts) run once per active clinic.
`
`## Languages
`
`- Texts live in `src/main/resources/i18n/whatsapp_<code>.properties` (UTF-8). `{0}`, `{1}`... are filled in by the code.
`- Hindi, Telugu and Tamil were written for this project - **have a native speaker (ideally a medical professional) review them before going live.** Doctor names and specialities are shown as entered (English).
`- Button and list texts must stay within WhatsApp's limits (buttons 20, row titles 24, row descriptions 72 characters); `MessageCatalogTest` enforces this.
`- The web screens are English only for now.
`
`## Medical record and privacy
`
`- A doctor can open the record of any patient who has (or had) an appointment with them. Admins and pharmacists do not see clinical records.
`- Opening a record, saving the profile and recording vitals are written to `record_access_log`.
`- The summary never leaves the server unless you plug in an AI summariser - if you do, get patient consent and a data-processing agreement first.
`
`## Tests
`
````bash
`./mvnw test
````
`
`- Unit tests: schedule slot maths, prescription parsing/signing, inventory (earliest-expiry-first, partial dispensing, quantity suggestion).
`- `PageSmokeTest` renders every page as its role against the configured database.
`
`## Project layout
`
````
`src/main/java/com/appointment/webapp
`  config/        security, CORS, clinic settings, first-run data
`  controller/    pages per role (admin, doctor, patient, attendant, pharma) + prescription documents
`  service/       booking, schedules, prescriptions (+PDF/QR), inventory
`  whatsapp/      webhook, conversation flow, queue alerts, reminders
`src/main/resources
`  templates/     Thymeleaf pages (layout + fragments)
`  static/css     mediport design system (tokens, components, login, prescription)
`  static/vendor  Bootstrap 4, AdminLTE 3, Font Awesome, jQuery, SweetAlert2 (served locally - no CDN needed)
`  db/migration   Flyway SQL
````
