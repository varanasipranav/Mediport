/* global React, MpIcon, BrandMark */
// Mediport — Login screen. Calm, two-column. Marketing on left, form on right.

const LgI = MpIcon;

function LoginScreen() {
  return (
    <div className="mp-screen" style={{ background: '#fff' }}>
      {/* — Left: brand panel — */}
      <div style={{
        flex: '1 1 50%',
        background: 'linear-gradient(165deg, #0F1B2D 0%, #14274A 55%, #0B4FB8 130%)',
        color: '#fff',
        padding: '48px 56px',
        display: 'flex',
        flexDirection: 'column',
        justifyContent: 'space-between',
        position: 'relative',
        overflow: 'hidden',
      }}>
        {/* Soft mesh accent */}
        <div style={{
          position: 'absolute',
          inset: 0,
          backgroundImage: `radial-gradient(circle at 80% 10%, rgba(16,185,129,0.10) 0%, transparent 50%),
                            radial-gradient(circle at 10% 100%, rgba(11,79,184,0.22) 0%, transparent 55%)`,
          pointerEvents: 'none',
        }}/>

        <div style={{ position: 'relative', display: 'flex', alignItems: 'center', gap: 12 }}>
          <BrandMark size={36}/>
          <div style={{
            fontFamily: 'var(--font-display)',
            fontSize: 22, fontWeight: 800, letterSpacing: '-0.025em',
          }}>Mediport</div>
        </div>

        <div style={{ position: 'relative' }}>
          <div style={{
            fontFamily: 'var(--font-display)',
            fontSize: 44, fontWeight: 800, letterSpacing: '-0.03em',
            lineHeight: 1.05,
            maxWidth: 480,
          }}>
            Care, simply<br/>organized.
          </div>
          <div style={{
            fontSize: 16, color: 'rgba(255,255,255,0.72)',
            marginTop: 18, maxWidth: 460, lineHeight: 1.55,
          }}>
            Appointment scheduling, queues and prescriptions in one place — with
            WhatsApp notifications your patients already trust.
          </div>

          {/* Feature pills */}
          <div style={{ display: 'flex', gap: 10, marginTop: 30, flexWrap: 'wrap' }}>
            <FeaturePill icon="whatsapp" label="WhatsApp booking"/>
            <FeaturePill icon="queue" label="Live queue"/>
            <FeaturePill icon="prescription" label="Digital Rx"/>
          </div>
        </div>

        <div style={{ position: 'relative', display: 'flex', justifyContent: 'space-between', alignItems: 'center', fontSize: 12, color: 'rgba(255,255,255,0.45)' }}>
          <span>© Mediport 2026 · v2.4</span>
          <span style={{ display: 'flex', gap: 18 }}>
            <span>Privacy</span><span>Status</span><span>Help</span>
          </span>
        </div>
      </div>

      {/* — Right: form — */}
      <div style={{
        flex: '1 1 50%',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        padding: '48px',
        background: '#fff',
      }}>
        <div style={{ width: '100%', maxWidth: 380 }}>
          <div style={{ fontFamily: 'var(--font-display)', fontSize: 11, fontWeight: 700, color: 'var(--brand-primary)', letterSpacing: '0.12em', textTransform: 'uppercase', marginBottom: 14 }}>
            Sign in
          </div>
          <h1 style={{ fontFamily: 'var(--font-display)', fontSize: 28, fontWeight: 700, color: 'var(--text-strong)', letterSpacing: '-0.025em', margin: 0 }}>
            Welcome back
          </h1>
          <div style={{ fontSize: 14, color: 'var(--text-muted)', marginTop: 6 }}>
            Continue to your Mediport dashboard.
          </div>

          {/* Role tabs */}
          <div style={{
            marginTop: 28,
            display: 'flex',
            background: 'var(--bg-muted)',
            padding: 4,
            borderRadius: 10,
            gap: 2,
          }}>
            {[
              { id: 'staff', label: 'Staff' },
              { id: 'doctor', label: 'Doctor' },
              { id: 'patient', label: 'Patient' },
            ].map((r, i) => (
              <button key={r.id} style={{
                flex: 1,
                padding: '8px 12px',
                fontSize: 13,
                fontWeight: 600,
                borderRadius: 7,
                background: i === 0 ? '#fff' : 'transparent',
                color: i === 0 ? 'var(--text-strong)' : 'var(--text-muted)',
                boxShadow: i === 0 ? '0 1px 2px rgba(15,27,45,0.06)' : 'none',
              }}>{r.label}</button>
            ))}
          </div>

          <div style={{ marginTop: 24 }}>
            <label className="mp-label">Email or username</label>
            <input className="mp-input" placeholder="anjali.rao@mediport.io" defaultValue="anjali.rao@mediport.io"/>
          </div>

          <div style={{ marginTop: 14 }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'baseline' }}>
              <label className="mp-label" style={{ marginBottom: 0 }}>Password</label>
              <a style={{ fontSize: 12, color: 'var(--brand-primary)', fontWeight: 600 }}>Forgot?</a>
            </div>
            <input className="mp-input" type="password" placeholder="••••••••" defaultValue="••••••••••" style={{ marginTop: 6 }}/>
          </div>

          <label style={{ marginTop: 16, display: 'flex', alignItems: 'center', gap: 8, fontSize: 13, color: 'var(--text-muted)' }}>
            <span style={{
              width: 16, height: 16, borderRadius: 4,
              border: '1.5px solid var(--border-strong)',
              background: 'var(--brand-primary)',
              display: 'inline-flex', alignItems: 'center', justifyContent: 'center',
              color: '#fff',
            }}>
              <LgI.check size={11}/>
            </span>
            Keep me signed in for 7 days
          </label>

          <button className="mp-btn mp-btn-primary mp-btn-lg" style={{ marginTop: 24, width: '100%', justifyContent: 'center' }}>
            Sign in
            <LgI.chevronRight size={15}/>
          </button>

          <div style={{
            marginTop: 24,
            padding: 14,
            background: 'var(--wa-green-bg)',
            borderRadius: 10,
            display: 'flex', alignItems: 'center', gap: 12,
            border: '1px solid #C7EED9',
          }}>
            <span style={{ color: 'var(--wa-green)', display: 'inline-flex' }}>
              <LgI.whatsapp size={22}/>
            </span>
            <div style={{ flex: 1, fontSize: 13, color: 'var(--wa-text)' }}>
              <strong style={{ display: 'block', fontWeight: 700 }}>Patients book via WhatsApp</strong>
              <span style={{ opacity: 0.85 }}>Send "Hi" to <strong>+91 80 4000 1234</strong></span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

function FeaturePill({ icon, label }) {
  const I = MpIcon[icon];
  return (
    <div style={{
      display: 'inline-flex', alignItems: 'center', gap: 7,
      padding: '6px 12px',
      background: 'rgba(255,255,255,0.08)',
      border: '1px solid rgba(255,255,255,0.12)',
      borderRadius: 999,
      fontSize: 12, fontWeight: 600,
      color: 'rgba(255,255,255,0.9)',
    }}>
      <span style={{ color: 'var(--brand-accent)', display: 'inline-flex' }}>
        <I size={13}/>
      </span>
      {label}
    </div>
  );
}

window.MpLogin = LoginScreen;
