# Deploy the Mediport demo on Railway

About 15 minutes. You get an `https://<something>.up.railway.app` address with a private MySQL, no server to manage.
Expect roughly **$15–25 a month** for the app + MySQL (Railway bills CPU, memory and storage by usage; check railway.com/pricing).

## 1. Put the code on GitHub
Railway builds from a GitHub repository (this repo already has a `Dockerfile` and `railway.json`).
Push your branch, then in Railway choose that branch.

## 2. Create the project
1. railway.com → **New Project** → **Deploy from GitHub repo** → pick the repository and branch.
   Railway reads `railway.json` and builds with the `Dockerfile`. The first build takes a few minutes (Maven downloads).
2. In the same project: **+ New** → **Database** → **Add MySQL**. Wait until it shows as running.
3. Open the MySQL service → **Settings → Volumes** and confirm a volume is attached (it is by default), so data survives restarts.

## 3. Variables for the app service
Open the **app** service → **Variables** → **Raw editor** and paste (replace the `MySQL` word if you renamed that service):

```
APP_TENANCY_ENABLED=true
MYSQLHOST=${{MySQL.MYSQLHOST}}
MYSQLPORT=${{MySQL.MYSQLPORT}}
MYSQLUSER=${{MySQL.MYSQLUSER}}
MYSQLPASSWORD=${{MySQL.MYSQLPASSWORD}}
TENANCY_LEGACY_SCHEMA=
APP_PUBLIC_URL=https://${{RAILWAY_PUBLIC_DOMAIN}}
FORWARD_HEADERS=framework
SESSION_COOKIE_SECURE=true
REMEMBER_ME_KEY=<run: openssl rand -base64 32>
PLATFORM_ADMIN_USER=operator
PLATFORM_ADMIN_PASSWORD=<choose a strong password>
WHATSAPP_APP_SECRET=<Meta app secret>
WHATSAPP_VERIFY_TOKEN=<random string you also give to Meta>
LOG_LEVEL=INFO
JAVA_OPTS=-Xms128m -Xmx450m -XX:+UseSerialGC -XX:+ExitOnOutOfMemoryError
```

Notes
* `TENANCY_LEGACY_SCHEMA=` is left **empty** on purpose: a new install has no earlier single-clinic database.
* The app creates `platform_master` and one `t_<clinic>` schema per clinic inside Railway's MySQL, using the MySQL root login Railway provides.
* If you don't have WhatsApp credentials yet, leave `WHATSAPP_*` out; the site and demo clinics work, only WhatsApp messages wait.
* Keep **one replica** (`railway.json` already says so): sessions are held in memory.

## 4. Public address
App service → **Settings → Networking → Generate Domain**. Railway gives HTTPS automatically.
Optional custom domain: add it under the same page and create the CNAME Railway shows; then change `APP_PUBLIC_URL` to it.

## 5. Check it
* `https://<your domain>/health` → `{"status":"UP"}`
* `https://<your domain>/` → the landing page (the demo form works)
* `https://<your domain>/platform` → sign in as `PLATFORM_ADMIN_USER` with your password
  (if you left the password blank, the generated one is printed once in the app's **Deploy Logs**).
* In `/platform` create the demo clinic (tick **sample doctors**). Copy the admin login shown; sign in at `/login` as `admin@<code>`.

## 6. WhatsApp for the demo (optional)
1. Meta developers → your app → WhatsApp → Configuration: callback URL `https://<your domain>/api/whatsapp/webhook`, verify token = `WHATSAPP_VERIFY_TOKEN`, subscribe to **messages**.
2. In `/platform`, set the demo clinic's **phone number ID** and **access token** (Meta's test number works for a demo; it can message only the numbers you add as test recipients).
3. Message the number from a test phone: `Hi`.

## 7. Backups (do this even for a demo you care about)
Railway's MySQL is a normal MySQL. From your computer:
```
mysqldump -h <public host> -P <public port> -uroot -p --single-transaction --databases platform_master t_<clinic> > backup.sql
```
(Find the public host/port under the MySQL service → **Connect → Public network**; enable TCP proxy if needed.)

## 8. Cost control
* Memory is capped by `JAVA_OPTS` (≈ 450 MB heap). Don't raise replicas.
* Delete the project (or pause services) when you're not demoing.
* Watch **Usage** in the Railway dashboard during the first week.

## 9. Updating
Push to the connected branch; Railway rebuilds and redeploys. Database changes apply automatically at start-up.

## Troubleshooting
| Symptom | Likely cause |
|---|---|
| Deploy fails the health check | Open **Deploy Logs**; usually a wrong `MYSQL*` reference (service name is not `MySQL`) or MySQL not running yet. |
| `Access denied` / `Communications link failure` in the log | The `MYSQL*` variables are missing or reference the wrong service. |
| Out of memory restarts | Raise `-Xmx` a little (e.g. 600m) and watch cost. |
| Links in WhatsApp/QR codes point to localhost | `APP_PUBLIC_URL` not set to the https address. |
| Everyone is signed out after each deploy | Expected (sessions are in memory). "Keep me signed in" survives only if `REMEMBER_ME_KEY` is set. |
