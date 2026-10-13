/* global React, MpIcon, MpShell, MpPageHeader */
// Mediport — Pharmacist · Dispense Queue

const PhI = MpIcon;

const PH_QUEUE = [
  { rx: 'RX-2025-08832', name: 'Sneha Reddy',  age: 28, dr: 'Dr. Rao',  meds: 3, when: '09:42', status: 'ready', wa: true },
  { rx: 'RX-2025-08833', name: 'Arjun Kapoor', age: 34, dr: 'Dr. Mehta', meds: 2, when: '09:55', status: 'ready', wa: true },
  { rx: 'RX-2025-08834', name: 'Devansh Patel', age: 8, dr: 'Dr. Nair',  meds: 4, when: '10:08', status: 'partial', wa: true, note: '1 item out of stock — Azithromycin 100mg/5ml' },
  { rx: 'RX-2025-08835', name: 'Lakshmi Iyer', age: 62, dr: 'Dr. Rao',  meds: 5, when: '10:22', status: 'preparing', wa: true },
  { rx: 'RX-2025-08836', name: 'Aisha Khan',   age: 22, dr: 'Dr. Khan', meds: 1, when: '10:35', status: 'preparing', wa: true },
  { rx: 'RX-2025-08827', name: 'Karthik Bose', age: 41, dr: 'Dr. Mehta', meds: 3, when: '09:10', status: 'dispensed', wa: true },
  { rx: 'RX-2025-08828', name: 'Nisha Verma',  age: 30, dr: 'Dr. Rao',  meds: 2, when: '09:18', status: 'dispensed', wa: true },
];

function PharmaDashboard() {
  return (
    <MpShell role="pharma" crumbs={["Pharmacy", "Dispense queue"]}>
      <MpPageHeader
        title="Dispense queue"
        subtitle="Prescriptions arriving directly from consulting doctors"
        actions={
          <>
            <button className="mp-btn mp-btn-secondary"><PhI.filter size={14}/> Filter</button>
            <button className="mp-btn mp-btn-secondary"><PhI.download size={14}/> End-of-day report</button>
          </>
        }
      />

      {/* Stats */}
      <div style={{ padding: '0 32px 24px', display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: 16 }}>
        <PhStat icon="prescription" label="Active queue" value="5" tint="primary"/>
        <PhStat icon="pill"          label="Ready to dispense" value="2" tint="completed"/>
        <PhStat icon="clock"         label="Avg prep time"   value="6m" sub="−2m vs avg" deltaUp/>
        <PhStat icon="shield"        label="Out-of-stock alerts" value="3" tint="missed"/>
      </div>

      <div style={{ padding: '0 32px 40px', display: 'grid', gridTemplateColumns: '2fr 1fr', gap: 24 }}>

        {/* Main queue */}
        <div className="mp-card">
          <div className="mp-card-header">
            <h3 className="mp-card-title">Today's prescriptions</h3>
            <div style={{ display: 'flex', gap: 8 }}>
              {['Active', 'Ready', 'Issues', 'Done'].map((l, i) => (
                <button key={l} style={{
                  padding: '6px 12px', borderRadius: 6,
                  fontSize: 13, fontWeight: 600,
                  color: i === 0 ? 'var(--text-strong)' : 'var(--text-muted)',
                  background: i === 0 ? 'var(--bg-muted)' : 'transparent',
                }}>{l}</button>
              ))}
            </div>
          </div>
          <div>
            {PH_QUEUE.map((r, i, arr) => <PhRow key={r.rx} r={r} last={i === arr.length - 1}/>)}
          </div>
        </div>

        {/* Right column */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
          {/* Stock alerts */}
          <div className="mp-card">
            <div className="mp-card-header">
              <h3 className="mp-card-title" style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                <span style={{ color: 'var(--status-missed)' }}><PhI.shield size={14}/></span>
                Stock alerts
              </h3>
              <span className="mp-pill mp-pill-missed">3</span>
            </div>
            <div>
              {[
                { name: 'Azithromycin', strength: '100 mg/5 ml', left: 'Out of stock', critical: true },
                { name: 'Atorvastatin', strength: '20 mg',       left: '4 left',       critical: false },
                { name: 'Salbutamol Inhaler', strength: '100 mcg', left: '2 left',     critical: false },
              ].map((s, i, arr) => (
                <div key={i} style={{
                  display: 'flex', alignItems: 'center', gap: 10,
                  padding: '12px 20px',
                  borderBottom: i < arr.length - 1 ? '1px solid var(--border)' : 'none',
                }}>
                  <div style={{
                    width: 32, height: 32, borderRadius: 8,
                    background: s.critical ? 'var(--status-missed-bg)' : 'var(--status-waiting-bg)',
                    color: s.critical ? 'var(--status-missed)' : 'var(--status-waiting)',
                    display: 'inline-flex', alignItems: 'center', justifyContent: 'center',
                  }}>
                    <PhI.pill size={16}/>
                  </div>
                  <div style={{ flex: 1, minWidth: 0 }}>
                    <div style={{ fontSize: 13, fontWeight: 600, color: 'var(--text-strong)' }}>{s.name}</div>
                    <div style={{ fontSize: 11, color: 'var(--text-muted)' }}>{s.strength}</div>
                  </div>
                  <span style={{
                    fontSize: 11, fontWeight: 700,
                    color: s.critical ? 'var(--status-missed)' : 'var(--status-waiting)',
                  }}>{s.left}</span>
                </div>
              ))}
            </div>
            <div style={{ padding: 14, borderTop: '1px solid var(--border)' }}>
              <button className="mp-btn mp-btn-secondary mp-btn-sm" style={{ width: '100%', justifyContent: 'center' }}>
                <PhI.plus size={13}/> Create reorder
              </button>
            </div>
          </div>

          {/* Recently dispensed */}
          <div className="mp-card">
            <div className="mp-card-header">
              <h3 className="mp-card-title">Recent dispensed</h3>
              <span style={{ fontSize: 12, color: 'var(--text-muted)' }}>Last hour</span>
            </div>
            <div>
              {[
                { name: 'Karthik Bose', rx: 'RX-...8827', when: '09:32' },
                { name: 'Nisha Verma',  rx: 'RX-...8828', when: '09:24' },
                { name: 'Rohit Singh',  rx: 'RX-...8829', when: '09:11' },
              ].map((d, i, arr) => (
                <div key={i} style={{
                  display: 'flex', alignItems: 'center', gap: 12,
                  padding: '12px 20px',
                  borderBottom: i < arr.length - 1 ? '1px solid var(--border)' : 'none',
                }}>
                  <div style={{
                    width: 30, height: 30, borderRadius: '50%',
                    background: 'var(--status-completed-bg)',
                    color: 'var(--status-completed)',
                    display: 'inline-flex', alignItems: 'center', justifyContent: 'center',
                  }}>
                    <PhI.check size={14}/>
                  </div>
                  <div style={{ flex: 1, minWidth: 0 }}>
                    <div style={{ fontSize: 13, fontWeight: 600, color: 'var(--text-strong)' }}>{d.name}</div>
                    <div style={{ fontFamily: 'var(--font-mono)', fontSize: 11, color: 'var(--text-muted)' }}>{d.rx}</div>
                  </div>
                  <span style={{ fontFamily: 'var(--font-mono)', fontSize: 12, color: 'var(--text-muted)' }}>{d.when}</span>
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>
    </MpShell>
  );
}

function PhStat({ icon, label, value, tint, sub, deltaUp }) {
  const Ico = MpIcon[icon];
  return (
    <div className="mp-stat">
      <div className="mp-stat-label">
        <Ico size={14}/>{label}
      </div>
      <div className="mp-stat-value" style={{
        color: tint === 'missed' ? 'var(--status-missed)' :
               tint === 'completed' ? 'var(--status-completed)' :
               'var(--text-strong)',
      }}>{value}</div>
      {sub && (
        <span className={`mp-stat-delta ${deltaUp ? 'up' : 'flat'}`}>
          <PhI.trending size={12}/> {sub}
        </span>
      )}
    </div>
  );
}

function PhRow({ r, last }) {
  const statusMeta = {
    ready:     { pill: 'mp-pill-completed', label: 'Ready' },
    partial:   { pill: 'mp-pill-delayed',   label: 'Partial · stock issue' },
    preparing: { pill: 'mp-pill-consult',   label: 'Preparing' },
    dispensed: { pill: 'mp-pill-completed', label: 'Dispensed' },
  }[r.status];
  const isDispensed = r.status === 'dispensed';

  return (
    <div style={{
      padding: '14px 24px',
      borderBottom: last ? 'none' : '1px solid var(--border)',
      opacity: isDispensed ? 0.55 : 1,
    }}>
      <div style={{
        display: 'grid',
        gridTemplateColumns: '160px 1.4fr 1fr 1fr 80px 200px',
        alignItems: 'center',
        gap: 16,
      }}>
        <div>
          <div style={{ fontFamily: 'var(--font-mono)', fontSize: 12, fontWeight: 600, color: 'var(--text-strong)' }}>{r.rx}</div>
          <div style={{ fontFamily: 'var(--font-mono)', fontSize: 11, color: 'var(--text-muted)' }}>{r.when}</div>
        </div>
        <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
          <div className="mp-avatar">{r.name.split(' ').map(n=>n[0]).join('')}</div>
          <div>
            <div style={{ fontSize: 14, fontWeight: 600, color: 'var(--text-strong)' }}>{r.name}</div>
            <div style={{ fontSize: 12, color: 'var(--text-muted)' }}>{r.age} yrs</div>
          </div>
        </div>
        <div style={{ fontSize: 13, color: 'var(--text)' }}>{r.dr}</div>
        <div>
          <span className={`mp-pill ${statusMeta.pill}`}>{statusMeta.label}</span>
        </div>
        <div style={{ fontSize: 12, color: 'var(--text-muted)', display: 'inline-flex', alignItems: 'center', gap: 4 }}>
          <PhI.pill size={13}/>
          <span style={{ fontFamily: 'var(--font-mono)', fontWeight: 600, color: 'var(--text)' }}>{r.meds}</span>
          <span>items</span>
        </div>
        <div style={{ display: 'flex', gap: 6, justifyContent: 'flex-end' }}>
          {r.status === 'ready' && (
            <>
              <button className="mp-btn mp-btn-success mp-btn-sm"><PhI.check size={12}/> Dispense</button>
              <button className="mp-btn mp-btn-ghost mp-btn-sm" style={{ padding: '7px 8px' }}><PhI.more size={14}/></button>
            </>
          )}
          {r.status === 'preparing' && (
            <button className="mp-btn mp-btn-secondary mp-btn-sm">Mark ready</button>
          )}
          {r.status === 'partial' && (
            <>
              <button className="mp-btn mp-btn-primary mp-btn-sm"><PhI.whatsapp size={12}/> Notify patient</button>
              <button className="mp-btn mp-btn-ghost mp-btn-sm" style={{ padding: '7px 8px' }}><PhI.more size={14}/></button>
            </>
          )}
          {r.status === 'dispensed' && (
            <button className="mp-btn mp-btn-ghost mp-btn-sm">View</button>
          )}
        </div>
      </div>
      {r.note && (
        <div style={{
          marginTop: 10, marginLeft: 176,
          padding: '8px 12px',
          background: 'var(--status-delayed-bg)',
          borderRadius: 6,
          fontSize: 12, color: 'var(--status-delayed)',
          display: 'inline-flex', alignItems: 'center', gap: 8,
        }}>
          <PhI.flag size={12}/> {r.note}
        </div>
      )}
    </div>
  );
}

window.MpPharmaDashboard = PharmaDashboard;
