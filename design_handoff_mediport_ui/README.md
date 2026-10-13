# Handoff: Mediport UI Redesign

> **For Claude Code working inside the Mediport Spring Boot + Thymeleaf codebase.**
> The user has already approved this design direction. Implement it in the existing webapp.

---

## 1. What this is

A complete UI redesign of **Mediport** — a Spring Boot + Thymeleaf + AdminLTE healthcare appointment management system with WhatsApp integration. The design was produced as **HTML/React prototypes for visual reference only** — they are NOT meant to be shipped, and you should NOT replace the Thymeleaf templates with React.

**Your job:** take the visual language, color tokens, components, and layouts from the prototypes and re-skin the existing Thymeleaf templates in the user's webapp project. The Java/Spring controllers, models, routes, and existing workflows must remain unchanged.

## 2. Fidelity

**High-fidelity.** Final colors, typography, spacing, component shapes, and interaction states are all locked. Match them pixel-perfectly. If you find a gap (e.g. a screen that exists in the app but wasn't designed), extend the system using the documented tokens and component patterns — do not invent new colors or radii.

## 3. Tech stack — what's in play

- **Backend:** Spring Boot, Spring Security, MyBatis/JPA
- **View:** Thymeleaf + Thymeleaf Layout Dialect (`layout:decorate`, `layout:fragment`)
- **CSS:** AdminLTE 3.2 + Bootstrap 4 (already loaded). Custom CSS lives in `src/main/resources/static/css/`
- **JS:** jQuery + SweetAlert2 + AdminLTE bundle. Already imported via `layout.html`
- **Icons:** Font Awesome (loaded). Keep using FA classes — do NOT replace with inline SVG icon components.

**Do not** introduce React, Vue, Tailwind, or any new framework. Do not migrate away from AdminLTE/Bootstrap. The design overrides them in a focused way.

## 4. The plan — work in this order

### Phase 1 — Drop the design system into the codebase
1. Copy `tokens.css` into `src/main/resources/static/css/mediport-tokens.css`
2. Include it in `templates/layout.html` AFTER AdminLTE and Bootstrap so it overrides them. Example:
   ```html
   <link rel="stylesheet" th:href="@{/resources/lte3dot2/dist/css/adminlte.min.css}"/>
   <link rel="stylesheet" th:href="@{/css/mediport-tokens.css}"/>
   ```
3. Add Google Fonts to `<head>` of `layout.html` (already in tokens.css `@import`, but a preconnect is faster):
   ```html
   <link rel="preconnect" href="https://fonts.googleapis.com"/>
   <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin/>
   ```
4. In tokens.css, override Bootstrap's `.btn`, `.card`, `.badge`, `.form-control`, AdminLTE's `.small-box`, `.main-sidebar`, `.content-header`, `.nav-link` etc. Map them to the Mediport visual rules — see Section 6. The user's existing template markup should not need to change for the global re-skin.

### Phase 2 — Re-skin the shell
- `templates/layout.html` — the base shell. Re-skin the sidebar (`.main-sidebar`), top navbar, content header. Use the dark navy `#0F1B2D` from tokens.
- `templates/fragments/main-sidebar.html` — already has its own `<style>` block; replace it with classes that pull from tokens.css. Match the design in `screens/shell.jsx` (BrandMark + role label + nav items with active green accent bar + bottom user card).
- `templates/fragments/header.html` — the top bar. Add a search input, notification bell, user menu styled per design.

### Phase 3 — Re-skin pages one by one
Map each design screen to the corresponding Thymeleaf template. See Section 7 for the full map.

For each page:
1. Open the existing `.html` template
2. Delete the inline `<style>` block — DO NOT keep page-specific gradient cards
3. Update class names on existing elements to use Mediport classes (`mp-card`, `mp-btn-primary`, `mp-pill-waiting`, etc.) defined in tokens.css. Keep all `th:` attributes and Thymeleaf logic intact.
4. Match the layout and structure of the corresponding prototype
5. Confirm controllers/models still feed the page correctly — no Java changes should be needed

### Phase 4 — Polish
- Remove the colorful gradient backgrounds (`linear-gradient(135deg, #667eea 0%, #764ba2 100%)`) wherever you find them. They were the main source of "distorted" feel.
- Replace SweetAlert2 success/error theming colors with `var(--brand-primary)` and `var(--status-missed)`.
- Verify all status badges across the app use the 5 status pills, not Bootstrap's `badge-info` etc.

## 5. Design tokens — single source of truth

All values are defined in `tokens.css`. The most important ones:

### Color
```
--brand-primary:        #0B4FB8   (deep medical blue — buttons, links, focus)
--brand-primary-50:     #EAF1FC   (light tint backgrounds)
--brand-accent:         #10B981   (vital green — success, active state bar, WhatsApp)
--sidebar-bg:           #0F1B2D   (navy, NOT pure black)
--bg-app:               #F7F9FC   (warm cool-white page background)
--bg-card:              #FFFFFF
--bg-muted:             #F2F5FA
--border:               #E5EAF2
--text-strong:          #0F1B2D
--text:                 #1F2A40
--text-muted:           #5B6B85

/* Status — soft bg + solid bar + text triplet per status */
--status-waiting-bg:    #FEF3DC    waiting bar: #F59E0B    text: #B5651D
--status-consult-bg:    #E1ECFB    consult bar: #2563EB    text: #0A47A6
--status-completed-bg:  #D7F5E6    completed bar: #10B981  text: #047857
--status-missed-bg:     #FDE3E3    missed bar:   #EF4444   text: #B91C1C
--status-delayed-bg:    #FFEDD5    delayed bar:  #F97316   text: #92400E

/* WhatsApp — used as confirmation accent only */
--wa-green:             #25D366
--wa-green-bg:          #E6F9EF
--wa-text:              #075E54
```

### Typography
```
--font-display:  'Plus Jakarta Sans', system-ui, sans-serif    (headings, big numbers)
--font-body:     'Manrope', system-ui, sans-serif              (UI, paragraphs)
--font-mono:     'JetBrains Mono', monospace                   (patient IDs, times, queue #)
```

Scale (px):
- H1 page title: 26 / 700 / -0.025em
- H2 section: 22 / 700 / -0.02em
- H3 card title: 16 / 700 / -0.01em
- Body: 14 / 400 / -0.005em
- Small/caption: 12 / 500
- Stat numbers: 32–48 / 700–800

### Shape
- Card radius: 10px
- Button radius: 8px
- Pill radius: 999px
- Small chips: 6px

### Shadow (restrained)
```
--shadow-card:  0 1px 2px rgba(15,27,45,0.04), 0 1px 1px rgba(15,27,45,0.02)
--shadow-pop:   0 8px 24px rgba(15,27,45,0.08), 0 2px 6px rgba(15,27,45,0.04)
```

### Spacing scale (4-based)
`4, 8, 12, 16, 20, 24, 32, 40, 48` — accessible as `--s-1`…`--s-12`.

## 6. Component vocabulary

These classes are defined in `tokens.css`. Add them to the existing Thymeleaf markup. Most map onto AdminLTE/Bootstrap equivalents, so you can co-apply.

### Buttons
```html
<!-- replaces .btn .btn-primary -->
<button class="mp-btn mp-btn-primary"><i class="fas fa-play"></i> Start consult</button>
<button class="mp-btn mp-btn-success">Complete</button>
<button class="mp-btn mp-btn-secondary">Edit</button>
<button class="mp-btn mp-btn-danger">Cancel</button>
<button class="mp-btn mp-btn-ghost">Skip</button>

<!-- Size modifiers -->
<button class="mp-btn mp-btn-primary mp-btn-sm">Small</button>
<button class="mp-btn mp-btn-primary mp-btn-lg">Large</button>
```

### Cards (replaces AdminLTE `.card`)
```html
<div class="mp-card">
  <div class="mp-card-header">
    <h3 class="mp-card-title">Patient Queue</h3>
    <div><!-- card tools --></div>
  </div>
  <div class="mp-card-body">…</div>
</div>
```

### Status pills (replaces Bootstrap `.badge`)
```html
<span class="mp-pill mp-pill-waiting">Waiting</span>
<span class="mp-pill mp-pill-consult">In Consultation</span>
<span class="mp-pill mp-pill-completed">Completed</span>
<span class="mp-pill mp-pill-missed">Not Attended</span>
<span class="mp-pill mp-pill-delayed">Delayed</span>
<span class="mp-pill mp-pill-wa">WhatsApp Confirmed</span>
```

Map the existing `AppointmentStatus` enum's `badgeClass()` getter to return these instead of `badge-warning` etc.

### Inputs
```html
<label class="mp-label">Phone number</label>
<input class="mp-input" type="tel"/>
<div class="mp-helper">10-digit number used to look up patient.</div>
```

### Stat tile
```html
<div class="mp-stat">
  <div class="mp-stat-label"><i class="fas fa-users"></i> Total today</div>
  <div class="mp-stat-value">28</div>
  <span class="mp-stat-delta up">+12% vs last Mon</span>
</div>
```
Use this to replace AdminLTE's gradient `.small-box`.

### Avatar
```html
<div class="mp-avatar lg">AR</div>   <!-- sm | lg, defaults medium -->
```

## 7. Screen-by-screen mapping

| Design screen (prototype file) | Replaces this Thymeleaf template | Notes |
|---|---|---|
| `screens/login.jsx` | `templates/login.html` | Two-column. Left: dark navy → blue gradient panel with brand + tagline + 3 feature pills. Right: tabbed sign-in (Staff/Doctor/Patient), email+password, "Keep me signed in", primary CTA, WhatsApp callout at bottom. Remove the current decorative blobs. |
| `screens/design-system.jsx` | _(reference only — not a real page)_ | Use as your color/type/component reference card. |
| `screens/doctor-today.jsx` | `templates/doctor/appointments-today.html` | **HERO SCREEN.** 4 stat cards top (Total / Waiting / In Consult / Completed) using `mp-stat` not gradient small-boxes. Queue as a single `mp-card` containing rows. Each row: 56px queue number block + avatar + name (with WhatsApp ✓ if `appointment.whatsappConfirmed`) + patient meta + notes + scheduled-time/status + status-aware action buttons. Active "In Consultation" row gets a `--brand-primary-50` background + 3px left bar. Drop the auto-refresh 30s — keep behaviour but remove the visual flicker. |
| `screens/doctor-schedule.jsx` | `templates/doctor/weekly-schedule.html` | Doctor summary card at top with avatar + 3 stat columns (Active days / Weekly hours / Avg booked). Then a 7-column grid of day cards (each 200px+ tall, showing time range, slot duration, booked-vs-total fill bar, edit button). Replace the existing `summary-card` gradient. Inactive days render greyed with "Day off / Activate" button. Below the grid: optional horizontal week visualization. |
| `screens/prescription.jsx` | `templates/doctor/create-prescription.html` | Two-column layout (form + 320px context sidebar). Patient header strip at top of form card with avatar/ID/queue number. 5 form sections each with rounded blue-tinted icon: Diagnosis (textarea), Medications (one row per medicine — see `MedRow` component), Lab tests (pill-chip selector), Lifestyle (textarea), Follow-up (date + relative selector). Sidebar: Recent visits timeline, Allergies/flags card, "Delivered on WhatsApp" callout. |
| `screens/admin-dashboard.jsx` | `templates/dashboard/index.html` (when user is ROLE_ADMIN) | 4 KPI tiles. Then 2-col grid: by-hour bar chart (today vs last week) + booking source breakdown. Below: doctors on duty table + live WhatsApp activity feed. |
| `screens/attendant-checkin.jsx` | `templates/attendant/check-in.html` | 3-step card flow on left (each card has a numbered step circle): Lookup → Patient found → Appointment details. Right column: today's snapshot tiles + recent check-ins list + WhatsApp summary. Keep ALL the existing JS — `searchPatient()`, `bookAppointment()`, the multiple-patients selector, CSRF token handling. Just restyle. |
| `screens/patient-dashboard.jsx` | `templates/patient/dashboard.html` | Big dark hero card showing live queue position (#4) with progress strip of 12 blocks (completed=green, current=white, you=tinted green). Right column: Quick actions grid (Book / My Rx / WhatsApp / Profile) + Active prescriptions list + WhatsApp reschedule tip. |
| `screens/pharma-dashboard.jsx` | `templates/pharma/dashboard.html` | 4 stat tiles. Then 2-col: Active Rx queue (status-aware rows: ready / preparing / partial / dispensed, with a stock-issue inline note) + sidebar with Stock alerts + Recently dispensed. |

### Sidebar nav contents (per role)

Already designed — see `screens/shell.jsx`'s `NAV_CONFIGS`. Map by Spring Security role:

- `ROLE_DOCTOR` → Dashboard, Today's Queue (active badge with live count), Weekly Schedule, Prescriptions, Patients, WhatsApp Log
- `ROLE_ADMIN` → Overview, Appointments, Doctors, Patients, Staff, Prescriptions, WhatsApp Log, Settings
- `ROLE_ATTENDANT` → Check-in, Live Queue (count badge), Today's Schedule, Patients, WhatsApp Log
- `ROLE_PATIENT` → My Dashboard, Book Appointment, My Appointments, Prescriptions
- `ROLE_PHARMA` → Dispense Queue, Prescriptions, Patients

The badge counts (e.g. "12" on Today's Queue) should be fed by the existing controller's model attributes — wire them through `layout:fragment` parameters or a `@ModelAttribute` on the base controller.

## 8. Interaction & state notes

Most behaviour already exists in the codebase — preserve it. Visual changes only.

- **Auto-refresh on doctor queue (every 30s):** keep the JS `setTimeout(location.reload, 30000)`. Show the refresh button labelled "Auto-refresh · 30s" using `mp-btn-secondary mp-btn-sm`.
- **SweetAlert2 toasts:** still in use. Update confirm button colors to `--brand-primary` (`#0B4FB8`) for primary actions, `--brand-accent` (`#10B981`) for success, `--status-missed` (`#EF4444`) for danger.
- **Multiple-patient selection in check-in:** keep the existing JS that builds cards via DOM. Apply `mp-card` class and use the patient row pattern from `doctor-today.jsx` (avatar + name + meta).
- **CSRF tokens:** every existing form submission already injects them. No change.
- **Whatsapp ✓ badge on queue rows:** new visual element. If you have a `whatsappConfirmed` boolean on the Appointment entity, gate the FA `fab fa-whatsapp` icon on it (color `var(--wa-green)`). If the field doesn't exist yet, ask the user before adding.

## 9. Files in this bundle

```
design_handoff_mediport_ui/
├── README.md                         (this file)
├── tokens.css                        — drop into src/main/resources/static/css/
└── prototypes/
    ├── index.html                    — design canvas entry (open to browse all screens)
    ├── design-canvas.jsx             — canvas runtime, ignore
    ├── tweaks-panel.jsx              — tweaks runtime, ignore
    └── screens/
        ├── icons.jsx                 — for visual reference; use Font Awesome in the real app
        ├── shell.jsx                 — sidebar + topbar reference (NAV_CONFIGS per role)
        ├── design-system.jsx         — token + component reference card
        ├── login.jsx
        ├── doctor-today.jsx          — HERO SCREEN — start here after the shell is reskinned
        ├── doctor-schedule.jsx
        ├── prescription.jsx
        ├── admin-dashboard.jsx
        ├── attendant-checkin.jsx
        ├── patient-dashboard.jsx
        └── pharma-dashboard.jsx
```

To browse the prototypes locally, open `prototypes/index.html` in a browser — it's the same pannable canvas the user reviewed.

## 10. Definition of done

- [ ] `mediport-tokens.css` is loaded after AdminLTE in `layout.html`
- [ ] No `linear-gradient(135deg, #667eea 0%, #764ba2 100%)` (or similar purple/violet) anywhere in `templates/` or inline `<style>` blocks
- [ ] All 8 mapped screens visually match the prototypes
- [ ] Sidebar matches the dark navy design across all 5 roles
- [ ] Buttons use `mp-btn-*` classes (or AdminLTE buttons overridden to look identical)
- [ ] Status badges use the 5 `mp-pill-*` variants, fed from the existing `AppointmentStatus` enum
- [ ] App runs and all existing flows still work — booking, check-in, start/complete consult, prescription save, WhatsApp delivery — no Java changes required
- [ ] On a 1440×900 viewport, every screen looks like its corresponding prototype with no horizontal scrollbar

## 11. Things NOT to do

- ❌ Don't migrate to React/Vue/Tailwind
- ❌ Don't replace Thymeleaf templates with the JSX prototype files — they're references
- ❌ Don't change controller signatures, route paths, or DB schema
- ❌ Don't add new Maven dependencies unless absolutely necessary
- ❌ Don't reintroduce the colorful gradient cards — they're the main thing the user wanted removed
- ❌ Don't ship the prototypes folder. It's reference material only.

---

**Questions for the user** if anything is ambiguous: ask them in chat before guessing. Especially around the WhatsApp confirmation badge field name and the role enum names in their Spring Security config.
