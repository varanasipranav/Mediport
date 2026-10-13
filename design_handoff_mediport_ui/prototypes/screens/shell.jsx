/* global React, MpIcon */
// Mediport — shared shell: Sidebar (dark) + Topbar + Page chrome.

const I = MpIcon;

const SIDEBAR_W = 248;

// Role-specific nav configurations
const NAV_CONFIGS = {
  doctor: {
    role: 'Doctor',
    name: 'Dr. Anjali Rao',
    sub: 'General Medicine',
    initials: 'AR',
    items: [
      { id: 'dash',   label: 'Dashboard',         icon: 'dashboard' },
      { id: 'today',  label: "Today's Queue",      icon: 'queue', badge: 12, active: true },
      { id: 'sched',  label: 'Weekly Schedule',   icon: 'calendar' },
      { id: 'rx',     label: 'Prescriptions',     icon: 'prescription' },
      { id: 'pat',    label: 'Patients',          icon: 'users' },
      { id: 'msg',    label: 'WhatsApp Log',      icon: 'whatsapp' },
    ],
  },
  admin: {
    role: 'Administrator',
    name: 'Vikram Shah',
    sub: 'Mediport HQ',
    initials: 'VS',
    items: [
      { id: 'dash',   label: 'Overview',          icon: 'dashboard', active: true },
      { id: 'apt',    label: 'Appointments',      icon: 'calendarCheck' },
      { id: 'docs',   label: 'Doctors',           icon: 'stethoscope' },
      { id: 'pat',    label: 'Patients',          icon: 'users' },
      { id: 'att',    label: 'Staff',             icon: 'userCheck' },
      { id: 'rx',     label: 'Prescriptions',     icon: 'prescription' },
      { id: 'msg',    label: 'WhatsApp Log',      icon: 'whatsapp' },
      { id: 'set',    label: 'Settings',          icon: 'settings' },
    ],
  },
  attendant: {
    role: 'Front Desk',
    name: 'Priya Menon',
    sub: 'Reception · Floor 1',
    initials: 'PM',
    items: [
      { id: 'check',  label: 'Check-in',          icon: 'userCheck', active: true },
      { id: 'queue',  label: 'Live Queue',        icon: 'queue', badge: 9 },
      { id: 'today',  label: "Today's Schedule",   icon: 'calendar' },
      { id: 'pat',    label: 'Patients',          icon: 'users' },
      { id: 'msg',    label: 'WhatsApp Log',      icon: 'whatsapp' },
    ],
  },
  patient: {
    role: 'Patient',
    name: 'Rahul Kumar',
    sub: 'Patient #MP-10421',
    initials: 'RK',
    items: [
      { id: 'dash',   label: 'My Dashboard',      icon: 'dashboard', active: true },
      { id: 'book',   label: 'Book Appointment',  icon: 'calendar' },
      { id: 'apt',    label: 'My Appointments',   icon: 'calendarCheck' },
      { id: 'rx',     label: 'Prescriptions',     icon: 'prescription' },
    ],
  },
  pharma: {
    role: 'Pharmacist',
    name: 'Suresh Iyer',
    sub: 'Mediport Pharmacy',
    initials: 'SI',
    items: [
      { id: 'dash',   label: 'Dispense Queue',    icon: 'pill', active: true },
      { id: 'rx',     label: 'Prescriptions',     icon: 'prescription' },
      { id: 'pat',    label: 'Patients',          icon: 'users' },
    ],
  },
};

function MpSidebar({ role = 'doctor' }) {
  const cfg = NAV_CONFIGS[role];
  return (
    <aside style={{
      width: SIDEBAR_W,
      background: 'var(--sidebar-bg)',
      color: 'var(--sidebar-text)',
      display: 'flex',
      flexDirection: 'column',
      flexShrink: 0,
      borderRight: '1px solid rgba(0,0,0,0.2)',
    }}>
      {/* Brand */}
      <div style={{
        height: 'var(--topbar-h)',
        display: 'flex',
        alignItems: 'center',
        gap: 10,
        padding: '0 22px',
        borderBottom: '1px solid var(--sidebar-border)',
      }}>
        <BrandMark size={28}/>
        <div style={{
          fontFamily: 'var(--font-display)',
          fontSize: 19, fontWeight: 800, color: 'var(--sidebar-text-strong)',
          letterSpacing: '-0.02em',
        }}>
          Mediport
        </div>
      </div>

      {/* Role label */}
      <div style={{
        padding: '18px 22px 10px',
        fontSize: 11, fontWeight: 700, letterSpacing: '0.1em',
        textTransform: 'uppercase',
        color: 'var(--sidebar-text-dim)',
      }}>
        {cfg.role}
      </div>

      {/* Nav */}
      <nav style={{ flex: 1, padding: '0 12px', overflowY: 'auto' }}>
        {cfg.items.map(item => {
          const IconEl = MpIcon[item.icon];
          return (
            <a key={item.id} style={{
              display: 'flex', alignItems: 'center', gap: 12,
              padding: '11px 14px',
              margin: '2px 0',
              borderRadius: 8,
              fontSize: 14,
              fontWeight: 500,
              color: item.active ? 'var(--sidebar-text-strong)' : 'var(--sidebar-text)',
              background: item.active ? 'var(--sidebar-active-bg)' : 'transparent',
              position: 'relative',
              cursor: 'pointer',
            }}>
              {item.active && <span style={{
                position: 'absolute', left: -12, top: '50%', transform: 'translateY(-50%)',
                width: 3, height: 22,
                background: 'var(--sidebar-active-bar)',
                borderRadius: '0 3px 3px 0',
              }}/>}
              <span style={{ color: item.active ? 'var(--brand-accent)' : 'var(--sidebar-text-dim)', display: 'inline-flex' }}>
                <IconEl size={18}/>
              </span>
              <span style={{ flex: 1 }}>{item.label}</span>
              {item.badge && (
                <span style={{
                  background: 'var(--brand-accent)',
                  color: '#fff',
                  fontSize: 11, fontWeight: 700,
                  padding: '2px 7px',
                  borderRadius: 999,
                  letterSpacing: 0,
                }}>{item.badge}</span>
              )}
            </a>
          );
        })}
      </nav>

      {/* User card at bottom */}
      <div style={{
        padding: 14,
        borderTop: '1px solid var(--sidebar-border)',
      }}>
        <div style={{
          display: 'flex', alignItems: 'center', gap: 11,
          padding: '8px 10px',
          borderRadius: 8,
        }}>
          <div style={{
            width: 36, height: 36, borderRadius: '50%',
            background: 'var(--brand-primary)', color: '#fff',
            display: 'inline-flex', alignItems: 'center', justifyContent: 'center',
            fontFamily: 'var(--font-display)', fontWeight: 700, fontSize: 13,
            flexShrink: 0,
          }}>{cfg.initials}</div>
          <div style={{ flex: 1, minWidth: 0 }}>
            <div style={{
              fontSize: 13, fontWeight: 600, color: 'var(--sidebar-text-strong)',
              whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis',
            }}>{cfg.name}</div>
            <div style={{ fontSize: 11, color: 'var(--sidebar-text-dim)' }}>{cfg.sub}</div>
          </div>
          <span style={{ color: 'var(--sidebar-text-dim)', display: 'inline-flex' }}>
            <I.logout size={16}/>
          </span>
        </div>
      </div>
    </aside>
  );
}

function MpTopbar({ crumbs = [], title, subtitle, actions, search = true }) {
  return (
    <header style={{
      height: 'var(--topbar-h)',
      background: '#fff',
      borderBottom: '1px solid var(--border)',
      display: 'flex',
      alignItems: 'center',
      padding: '0 32px',
      gap: 24,
      flexShrink: 0,
    }}>
      <div style={{ flex: 1, display: 'flex', alignItems: 'center', gap: 14 }}>
        {crumbs.length > 0 ? (
          <div style={{ display: 'flex', alignItems: 'center', gap: 8, fontSize: 13, color: 'var(--text-muted)' }}>
            {crumbs.map((c, i) => (
              <React.Fragment key={i}>
                <span style={{ color: i === crumbs.length - 1 ? 'var(--text-strong)' : 'var(--text-muted)', fontWeight: i === crumbs.length - 1 ? 600 : 500 }}>{c}</span>
                {i < crumbs.length - 1 && <I.chevronRight size={13}/>}
              </React.Fragment>
            ))}
          </div>
        ) : (
          <div>
            <div style={{ fontFamily: 'var(--font-display)', fontSize: 18, fontWeight: 700, color: 'var(--text-strong)', letterSpacing: '-0.015em' }}>{title}</div>
            {subtitle && <div style={{ fontSize: 12, color: 'var(--text-muted)' }}>{subtitle}</div>}
          </div>
        )}
      </div>
      {search && (
        <div style={{
          display: 'flex', alignItems: 'center', gap: 8,
          padding: '8px 12px',
          width: 280,
          background: 'var(--bg-muted)',
          borderRadius: 8,
          color: 'var(--text-muted)',
          fontSize: 13,
        }}>
          <I.search size={15}/>
          <span style={{ flex: 1 }}>Search patients, appointments…</span>
          <span style={{
            fontFamily: 'var(--font-mono)', fontSize: 11,
            padding: '1px 5px', borderRadius: 4,
            background: '#fff', border: '1px solid var(--border)',
          }}>⌘K</span>
        </div>
      )}
      <button style={{
        position: 'relative',
        width: 38, height: 38, borderRadius: 8,
        display: 'inline-flex', alignItems: 'center', justifyContent: 'center',
        background: 'var(--bg-muted)', color: 'var(--text)',
      }}>
        <I.bell size={17}/>
        <span style={{
          position: 'absolute', top: 7, right: 9,
          width: 7, height: 7, borderRadius: '50%',
          background: 'var(--status-missed-bar)',
          border: '2px solid var(--bg-muted)',
        }}/>
      </button>
      {actions}
    </header>
  );
}

function MpPageHeader({ title, subtitle, actions }) {
  return (
    <div style={{
      display: 'flex',
      alignItems: 'flex-end',
      justifyContent: 'space-between',
      padding: '28px 32px 20px',
      gap: 24,
    }}>
      <div>
        <h1 style={{
          fontFamily: 'var(--font-display)',
          fontSize: 26, fontWeight: 700,
          color: 'var(--text-strong)',
          letterSpacing: '-0.025em',
          margin: 0,
        }}>{title}</h1>
        {subtitle && (
          <div style={{ fontSize: 14, color: 'var(--text-muted)', marginTop: 4 }}>{subtitle}</div>
        )}
      </div>
      {actions && (
        <div style={{ display: 'flex', gap: 10, alignItems: 'center' }}>{actions}</div>
      )}
    </div>
  );
}

// Mediport mark — abstract cross + pulse
function BrandMark({ size = 28 }) {
  return (
    <div style={{
      width: size, height: size,
      borderRadius: 8,
      background: 'linear-gradient(135deg, var(--brand-primary) 0%, var(--brand-primary-600) 100%)',
      display: 'inline-flex', alignItems: 'center', justifyContent: 'center',
      flexShrink: 0,
    }}>
      <svg width={size*0.6} height={size*0.6} viewBox="0 0 20 20" fill="none">
        <path d="M10 3v14M3 10h14" stroke="#fff" strokeWidth="2.2" strokeLinecap="round"/>
        <circle cx="10" cy="10" r="2.3" fill="var(--brand-accent)"/>
      </svg>
    </div>
  );
}

// Full app shell — used by every artboard except Login.
function MpShell({ role, crumbs, topbarActions, children }) {
  return (
    <div className="mp-screen">
      <MpSidebar role={role}/>
      <div style={{ flex: 1, display: 'flex', flexDirection: 'column', minWidth: 0 }}>
        <MpTopbar crumbs={crumbs} actions={topbarActions}/>
        <main style={{ flex: 1, overflowY: 'auto' }}>
          {children}
        </main>
      </div>
    </div>
  );
}

Object.assign(window, { MpSidebar, MpTopbar, MpPageHeader, MpShell, BrandMark });
