/* global React, MpIcon, BrandMark */
// Mediport — Design System artboard (foundations card)

const DsI = MpIcon;

function DesignSystem() {
  return (
    <div style={{
      width: '100%', height: '100%',
      background: '#fff',
      padding: 48,
      fontFamily: 'var(--font-body)',
      color: 'var(--text)',
      overflow: 'auto',
    }}>
      {/* Header */}
      <div style={{ display: 'flex', alignItems: 'center', gap: 18, marginBottom: 8 }}>
        <BrandMark size={48}/>
        <div>
          <div style={{ fontFamily: 'var(--font-display)', fontSize: 32, fontWeight: 800, color: 'var(--text-strong)', letterSpacing: '-0.025em', lineHeight: 1 }}>
            Mediport
          </div>
          <div style={{ fontSize: 14, color: 'var(--text-muted)', marginTop: 6 }}>
            Healthcare, Simplified — Design System v1
          </div>
        </div>
      </div>

      <div className="mp-divider" style={{ margin: '32px 0' }}/>

      {/* ── Colors ── */}
      <SectionLabel>Color</SectionLabel>
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: 16, marginBottom: 36 }}>
        <Swatch name="Primary" hex="#0B4FB8" bg="var(--brand-primary)" big/>
        <Swatch name="Accent / Vital" hex="#10B981" bg="var(--brand-accent)" big/>
        <Swatch name="Navy / Sidebar" hex="#0F1B2D" bg="var(--sidebar-bg)" big/>
        <Swatch name="App Surface" hex="#F7F9FC" bg="var(--bg-app)" big light/>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(5, 1fr)', gap: 12, marginBottom: 12 }}>
        <Swatch name="Waiting" hex="#F59E0B" bg="var(--status-waiting-bar)"/>
        <Swatch name="In Consultation" hex="#2563EB" bg="var(--status-consult-bar)"/>
        <Swatch name="Completed" hex="#10B981" bg="var(--status-completed-bar)"/>
        <Swatch name="Not Attended" hex="#EF4444" bg="var(--status-missed-bar)"/>
        <Swatch name="Delayed" hex="#F97316" bg="var(--status-delayed-bar)"/>
      </div>
      <div style={{ display: 'flex', gap: 10, marginBottom: 36, flexWrap: 'wrap' }}>
        <span className="mp-pill mp-pill-waiting">Waiting</span>
        <span className="mp-pill mp-pill-consult">In Consultation</span>
        <span className="mp-pill mp-pill-completed">Completed</span>
        <span className="mp-pill mp-pill-missed">Not Attended</span>
        <span className="mp-pill mp-pill-delayed">Delayed</span>
        <span className="mp-pill mp-pill-wa">WhatsApp Confirmed</span>
      </div>

      {/* ── Type ── */}
      <SectionLabel>Typography</SectionLabel>
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 32, marginBottom: 36 }}>
        <div>
          <div style={{ fontFamily: 'var(--font-display)', fontSize: 11, fontWeight: 600, color: 'var(--text-muted)', textTransform: 'uppercase', letterSpacing: '0.1em', marginBottom: 8 }}>
            Display · Plus Jakarta Sans
          </div>
          <div style={{ fontFamily: 'var(--font-display)', fontSize: 40, fontWeight: 800, color: 'var(--text-strong)', letterSpacing: '-0.03em', lineHeight: 1.05 }}>
            Care, simply organized.
          </div>
          <div style={{ fontFamily: 'var(--font-display)', fontSize: 22, fontWeight: 700, color: 'var(--text-strong)', letterSpacing: '-0.02em', marginTop: 14 }}>
            H2 — Today's Patient Queue
          </div>
          <div style={{ fontFamily: 'var(--font-display)', fontSize: 16, fontWeight: 700, color: 'var(--text-strong)', marginTop: 10, letterSpacing: '-0.01em' }}>
            H3 — Card Title
          </div>
        </div>
        <div>
          <div style={{ fontFamily: 'var(--font-display)', fontSize: 11, fontWeight: 600, color: 'var(--text-muted)', textTransform: 'uppercase', letterSpacing: '0.1em', marginBottom: 8 }}>
            UI / Body · Manrope
          </div>
          <p style={{ fontSize: 15, color: 'var(--text)', margin: '0 0 10px' }}>
            Body — appointments are organized by queue number. Patients receive
            WhatsApp confirmation immediately after the front desk books.
          </p>
          <div style={{ fontSize: 13, color: 'var(--text-muted)' }}>Secondary — auxiliary copy & captions.</div>
          <div style={{ fontFamily: 'var(--font-mono)', fontSize: 13, color: 'var(--text-strong)', marginTop: 14, background: 'var(--bg-muted)', padding: '8px 12px', borderRadius: 6, display: 'inline-block' }}>
            #MP-10421 · 09:45
          </div>
        </div>
      </div>

      {/* ── Components ── */}
      <SectionLabel>Components</SectionLabel>
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 24, marginBottom: 20 }}>
        {/* Buttons */}
        <div className="mp-card" style={{ padding: 24 }}>
          <div style={{ fontFamily: 'var(--font-display)', fontSize: 13, fontWeight: 700, color: 'var(--text-strong)', marginBottom: 14 }}>Buttons</div>
          <div style={{ display: 'flex', gap: 10, flexWrap: 'wrap' }}>
            <button className="mp-btn mp-btn-primary"><DsI.play size={14}/>Start Consult</button>
            <button className="mp-btn mp-btn-success"><DsI.check size={14}/>Complete</button>
            <button className="mp-btn mp-btn-secondary"><DsI.edit size={14}/>Edit</button>
            <button className="mp-btn mp-btn-danger"><DsI.cross size={14}/>Cancel</button>
            <button className="mp-btn mp-btn-ghost">Skip</button>
          </div>
          <div style={{ display: 'flex', gap: 10, marginTop: 12, flexWrap: 'wrap' }}>
            <button className="mp-btn mp-btn-primary mp-btn-sm">Small</button>
            <button className="mp-btn mp-btn-primary">Default</button>
            <button className="mp-btn mp-btn-primary mp-btn-lg">Large</button>
          </div>
        </div>

        {/* Inputs */}
        <div className="mp-card" style={{ padding: 24 }}>
          <div style={{ fontFamily: 'var(--font-display)', fontSize: 13, fontWeight: 700, color: 'var(--text-strong)', marginBottom: 14 }}>Form fields</div>
          <label className="mp-label">Phone number</label>
          <input className="mp-input" placeholder="10-digit phone" defaultValue="98765 43210"/>
          <div className="mp-helper">Used to look up existing patient records.</div>
          <label className="mp-label" style={{ marginTop: 14 }}>Slot duration</label>
          <select className="mp-select" defaultValue="15"><option>10 minutes</option><option>15 minutes</option></select>
        </div>

        {/* Stat */}
        <div className="mp-stat">
          <div className="mp-stat-label"><DsI.users size={14}/> Total today</div>
          <div className="mp-stat-value">28</div>
          <div className="mp-stat-delta up"><DsI.trending size={12}/> +12% vs last Mon</div>
        </div>

        {/* Patient row */}
        <div className="mp-card" style={{ padding: '18px 22px', display: 'flex', alignItems: 'center', gap: 16 }}>
          <div className="mp-avatar lg">SR</div>
          <div style={{ flex: 1, minWidth: 0 }}>
            <div style={{ fontFamily: 'var(--font-display)', fontSize: 15, fontWeight: 700, color: 'var(--text-strong)' }}>Sneha Reddy</div>
            <div style={{ fontSize: 12, color: 'var(--text-muted)', marginTop: 2 }}>#MP-10421 · 28 yrs · Female</div>
          </div>
          <span className="mp-pill mp-pill-waiting">Waiting</span>
          <span style={{ fontFamily: 'var(--font-mono)', fontSize: 13, color: 'var(--text-muted)' }}>09:45</span>
        </div>
      </div>

      {/* ── Spacing & Radius ── */}
      <SectionLabel>Spacing &amp; Shape</SectionLabel>
      <div style={{ display: 'flex', gap: 16, alignItems: 'flex-end', marginBottom: 14 }}>
        {[4,8,12,16,20,24,32,40,48].map(s => (
          <div key={s} style={{ textAlign: 'center' }}>
            <div style={{ width: s, height: s, background: 'var(--brand-primary)', borderRadius: 2 }}/>
            <div style={{ fontSize: 11, color: 'var(--text-muted)', marginTop: 4 }}>{s}</div>
          </div>
        ))}
      </div>
      <div style={{ display: 'flex', gap: 14 }}>
        {[6,8,10,14,999].map(r => (
          <div key={r} style={{
            width: 64, height: 64,
            background: 'var(--brand-primary-50)',
            border: '2px solid var(--brand-primary)',
            borderRadius: r === 999 ? r : r,
            display: 'flex', alignItems: 'flex-end', justifyContent: 'center',
            paddingBottom: 4,
            fontSize: 11, color: 'var(--brand-primary)', fontWeight: 600,
          }}>{r === 999 ? 'pill' : r + 'px'}</div>
        ))}
      </div>
    </div>
  );
}

function SectionLabel({ children }) {
  return (
    <div style={{
      fontFamily: 'var(--font-display)',
      fontSize: 12, fontWeight: 700,
      textTransform: 'uppercase',
      letterSpacing: '0.12em',
      color: 'var(--text-muted)',
      marginBottom: 14,
    }}>{children}</div>
  );
}

function Swatch({ name, hex, bg, big, light }) {
  return (
    <div>
      <div style={{
        background: bg,
        height: big ? 72 : 56,
        borderRadius: 8,
        border: light ? '1px solid var(--border)' : 'none',
      }}/>
      <div style={{ marginTop: 8, fontSize: 13, fontWeight: 600, color: 'var(--text-strong)' }}>{name}</div>
      <div style={{ fontFamily: 'var(--font-mono)', fontSize: 11, color: 'var(--text-muted)' }}>{hex}</div>
    </div>
  );
}

window.MpDesignSystem = DesignSystem;
