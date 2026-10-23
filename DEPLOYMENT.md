# Deploying Mediport

This guide takes a blank Linux server to a running, HTTPS-protected, multi-clinic Mediport. Plan on about an hour,
plus the time Meta takes to approve WhatsApp numbers.

## 1. What you need

| | |
|---|---|
| A server | 2 vCPU / 4 GB RAM is plenty for a few dozen clinics. Docker + Docker Compose v2 installed. |
| A domain | e.g. `app.yourcompany.com`, with an **A/AAAA record pointing at the server** before you start (HTTPS needs it). |
| Open ports | 80 and 443 only. MySQL and the app stay on the private Docker network. |
| A Meta app | developers.facebook.com, with the WhatsApp product added (see section 5). |

## 2. First deployment (Docker Compose)

```bash
git clone <your repository> mediport && cd mediport
cp .env.example .env
# edit .env: DOMAIN, DB_ROOT_PASSWORD, DB_PASSWORD, REMEMBER_ME_KEY, WHATSAPP_* (generate secrets with: openssl rand -base64 32)
docker compose up -d --build
docker compose logs -f app          # wait for "Started WebappApplication"
```

On a brand-new installation the log prints the **platform operator password once**
(`Platform operator created: username=operator generated password=...`). Copy it, or set
`PLATFORM_ADMIN_PASSWORD` in `.env` beforehand. Then:

1. Open `https://<DOMAIN>/platform` and sign in as the operator.
2. Approve a demo request, or choose **New clinic**, to create the first clinic. The admin login is shown **once**.
3. Open `https://<DOMAIN>/login` and sign in as `admin@<clinic code>`.

The start-up log lists anything still set to a development value (lines starting with `CHECK:`). Fix them all.

## 3. Settings (environment variables)

| Variable | Purpose |
|---|---|
| `DOMAIN` | Your public host name. Used for HTTPS and for prescription / WhatsApp links. |
| `DB_ROOT_PASSWORD`, `DB_PASSWORD` | MySQL root and the app's own user. The app user is limited to `platform_master` and `t_*` schemas. |
| `REMEMBER_ME_KEY` | Signs "keep me signed in" cookies. **Keep it stable** or everyone is signed out on each restart. |
| `PLATFORM_ADMIN_USER` / `PLATFORM_ADMIN_PASSWORD` | The operator account for `/platform` (created only on an empty master database). |
| `WHATSAPP_APP_SECRET` | Your Meta app secret. Webhooks are rejected without it. |
| `WHATSAPP_VERIFY_TOKEN` | A random string you also type into Meta when registering the webhook. |
| `WHATSAPP_PHONE_NUMBER_ID`, `WHATSAPP_ACCESS_TOKEN` | Optional platform-wide number. Normally each clinic's own number is set in the console. |
| `LOG_LEVEL` | `INFO` (default). Use `DEBUG` only briefly - it can include message text. |
| `SESSION_COOKIE_SECURE`, `FORWARD_HEADERS` | Already `true` / `framework` in `docker-compose.yml` (HTTPS behind Caddy). |

Without Docker, the same variables work with `java -jar target/webapp-*.jar` (Java 25 + MySQL 8); put a TLS reverse
proxy in front and set `FORWARD_HEADERS=framework` and `SESSION_COOKIE_SECURE=true`.

## 4. Migrating an existing single-clinic installation

If you already run Mediport on a `hospital` database, keep `TENANCY_LEGACY_SCHEMA=hospital` (the default) and start with
`APP_TENANCY_ENABLED=true`. That database becomes the first clinic (site id `hospital`) with no data changes; its users sign
in as `username@hospital` (or just `username`). Old prescription QR codes and links keep working. Back the database up first.

To stay single-clinic, set `APP_TENANCY_ENABLED=false`.

## 5. WhatsApp (Meta Cloud API)

1. In the Meta app, add **WhatsApp** and register a business number **for each clinic**.
2. Webhook: callback URL `https://<DOMAIN>/api/whatsapp/webhook`, verify token = `WHATSAPP_VERIFY_TOKEN`, subscribe to **messages**.
   One webhook serves every clinic; each message is routed by the number it was sent to.
3. In `/platform`, open the clinic and enter its **phone number ID** and **access token**
   (use a permanent System User token, not the 24-hour test token).
4. Free-text messages are only allowed within 24 hours of the patient's last message. Reminders and alerts to patients who have
   not chatted recently need **approved message templates** (Meta Business Manager). Plan this before go-live.
5. Messaging is billed by Meta to each clinic's WhatsApp Business account.

## 6. Backups

Every clinic is its own schema, so restores are per clinic. Back up `platform_master` and every `t_*` schema nightly:

```bash
# /etc/cron.daily/mediport-backup
set -e
D=/var/backups/mediport/$(date +%F); mkdir -p "$D"
for db in $(docker compose -f /opt/mediport/docker-compose.yml exec -T db sh -c \
   "mysql -uroot -p\$MYSQL_ROOT_PASSWORD -N -e \"select schema_name from information_schema.schemata where schema_name='platform_master' or schema_name like 't\\\\_%'\""); do
  docker compose -f /opt/mediport/docker-compose.yml exec -T db sh -c "mysqldump -uroot -p\$MYSQL_ROOT_PASSWORD --single-transaction --routines $db" | gzip > "$D/$db.sql.gz"
done
find /var/backups/mediport -mtime +14 -delete
```

Copy the backups off the server, and **test a restore** before you rely on them.

## 7. Updating

```bash
git pull && docker compose up -d --build
```

Database changes are applied automatically at start-up, clinic by clinic. A clinic whose migration fails stays offline and shows
the error in `/platform` (use **Re-run migrations** after fixing it); other clinics are unaffected.

## 8. Monitoring

* `GET https://<DOMAIN>/health` returns `{"status":"UP"}` (200) or 503 - point your uptime monitor at it.
* Logs: `docker compose logs -f app`. Patient data is not logged at `INFO`.

## 9. Security checklist

- [ ] HTTPS works and `http://` redirects to it (Caddy does this automatically).
- [ ] `.env` is not in git and is readable only by the deploy user (`chmod 600 .env`).
- [ ] All `CHECK:` lines are gone from the start-up log.
- [ ] The operator password is strong and stored in a password manager; rotate it after sharing.
- [ ] Meta tokens are System User tokens; the app secret and tokens that were ever pasted in chat or email are rotated.
- [ ] Firewall allows only 22 (from your IP), 80 and 443.
- [ ] Backups run, are copied off the server and a restore has been tested.
- [ ] Hindi, Telugu and Tamil WhatsApp texts reviewed by a native speaker before going live.

## 10. Good to know

* **One app instance.** Sessions, "keep me signed in" tokens and the sign-in throttle are held in memory. Run a single instance,
  or use sticky sessions. Restarting signs everyone out (except "keep me signed in", which needs a stable `REMEMBER_ME_KEY`).
* **Time zone.** Clinics default to `Asia/Kolkata` (change the `TZ` in the Dockerfile and `app.timezone` for other regions).
* **Sign-in protection.** After 8 failed attempts for the same username from one address (or 40 from one address) sign-in is
  refused for 15 minutes.
* **Not included yet:** billing, per-clinic logos, outgoing email, message-template sending.
