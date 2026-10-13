/* global React, MpIcon, MpShell, MpPageHeader */
// Mediport — Admin Overview Dashboard

const AdI = MpIcon;

function AdminDashboard() {
  return (
    <MpShell role="admin"
      crumbs={["Dashboard", "Overview"]}
    >
      <MpPageHeader
        title="Clinic overview"
        subtitle="Mediport General · 23 May 2026"
        actions={
          <>
            <button className="mp-btn mp-btn-secondary"><AdI.filter size={14}/> Last 7 days</button>
            <button className="mp-btn mp-btn-secondary"><AdI.download size={14}/> Export</button>
            <button className="mp-btn mp-btn-primary"><AdI.plus size={14}/> New appointment</button>
          </>
        }
      />

      {/* KPIs */}
      <div style={{ padding: '0 32px 24px', display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: 16 }}>
        <Kpi icon="calendarCheck" label="Appointments today" value="142" delta="+18%" dir="up"/>
        <Kpi icon="stethoscope"   label="Active doctors"     value="9" sub="of 12 on duty"/>
        <Kpi icon="whatsapp"      label="WhatsApp bookings"  value="64%" sub="91 of 142"/>
        <Kpi icon="trending"      label="Avg wait time"      value="11m" delta="−3m" dir="up"/>
      </div>

      {/* Main grid: chart + side panels */}
      <div style={{ padding: '0 32px 24px', display: 'grid', gridTemplateColumns: '2fr 1fr', gap: 24 }}>

        {/* Throughput chart */}
        <div className="mp-card">
          <div className="mp-card-header">
            <div>
              <h3 className="mp-card-title">Appointments by hour</h3>
              <div style={{ fontSize: 12, color: 'var(--text-muted)', marginTop: 2 }}>Today vs same day last week</div>
            </div>
            <div style={{ display: 'flex', gap: 14, fontSize: 12, color: 'var(--text-muted)' }}>
              <Legend dot="var(--brand-primary)" label="Today"/>
              <Legend dot="var(--border-strong)" label="Last Mon"/>
            </div>
          </div>
          <div className="mp-card-body">
            <BarChart/>
          </div>
        </div>

        {/* Booking source */}
        <div className="mp-card">
          <div className="mp-card-header">
            <h3 className="mp-card-title">Booking source</h3>
          </div>
          <div className="mp-card-body">
            {[
              { label: 'WhatsApp',    val: 91, color: 'var(--wa-green)' },
              { label: 'Front desk',  val: 38, color: 'var(--brand-primary)' },
              { label: 'Web portal',  val: 9,  color: 'var(--status-waiting-bar)' },
              { label: 'Phone call',  val: 4,  color: 'var(--text-muted)' },
            ].map(b => (
              <div key={b.label} style={{ marginBottom: 14 }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: 13, marginBottom: 6 }}>
                  <span style={{ color: 'var(--text)', fontWeight: 500 }}>{b.label}</span>
                  <span style={{ fontFamily: 'var(--font-mono)', color: 'var(--text-strong)', fontWeight: 600 }}>{b.val}</span>
                </div>
                <div style={{ height: 6, background: 'var(--bg-muted)', borderRadius: 3, overflow: 'hidden' }}>
                  <div style={{ width: `${(b.val / 91) * 100}%`, height: '100%', background: b.color, borderRadius: 3 }}/>
                </div>
              </div>
            ))}
            <div style={{ paddingTop: 14, borderTop: '1px solid var(--border)', display: 'flex', justifyContent: 'space-between', alignItems: 'baseline' }}>
              <span style={{ fontSize: 12, color: 'var(--text-muted)' }}>Total today</span>
              <span style={{ fontFamily: 'var(--font-display)', fontSize: 22, fontWeight: 700, color: 'var(--text-strong)' }}>142</span>
            </div>
          </div>
        </div>
      </div>

      {/* Doctors + WhatsApp activity */}
      <div style={{ padding: '0 32px 40px', display: 'grid', gridTemplateColumns: '1.4fr 1fr', gap: 24 }}>

        {/* Doctors on duty */}
        <div className="mp-card">
          <div className="mp-card-header">
            <h3 className="mp-card-title">Doctors on duty</h3>
            <button className="mp-btn mp-btn-ghost mp-btn-sm">See all</button>
          </div>
          <div>
            {[
              { name: 'Dr. Anjali Rao',    spec: 'General Medicine',  q: 12, done: 4, pct: 33, st: 'busy' },
              { name: 'Dr. Vikram Mehta',  spec: 'Cardiology',        q: 9,  done: 5, pct: 56, st: 'on' },
              { name: 'Dr. Priya Nair',    spec: 'Pediatrics',        q: 14, done: 6, pct: 43, st: 'busy' },
              { name: 'Dr. Sameer Khan',   spec: 'Dermatology',       q: 7,  done: 3, pct: 43, st: 'on' },
              { name: 'Dr. Tara Iyer',     spec: 'Gynaecology',       q: 0,  done: 0, pct: 0,  st: 'break' },
            ].map((d, i, arr) => (
              <div key={d.name} style={{
                display: 'grid',
                gridTemplateColumns: '40px 1.4fr 1fr 1.4fr 70px',
                alignItems: 'center',
                gap: 14,
                padding: '14px 24px',
                borderBottom: i < arr.length - 1 ? '1px solid var(--border)' : 'none',
              }}>
                <div className="mp-avatar">{d.name.split(' ').slice(1).map(n=>n[0]).join('')}</div>
                <div>
                  <div style={{ fontSize: 14, fontWeight: 600, color: 'var(--text-strong)' }}>{d.name}</div>
                  <div style={{ fontSize: 12, color: 'var(--text-muted)' }}>{d.spec}</div>
                </div>
                <div>
                  {d.st === 'on'    && <span className="mp-pill mp-pill-completed">On duty</span>}
                  {d.st === 'busy'  && <span className="mp-pill mp-pill-consult">In consult</span>}
                  {d.st === 'break' && <span className="mp-pill mp-pill-delayed">On break</span>}
                </div>
                <div>
                  <div style={{ fontSize: 12, color: 'var(--text-muted)', display: 'flex', justifyContent: 'space-between', marginBottom: 4 }}>
                    <span>Queue progress</span>
                    <span style={{ fontFamily: 'var(--font-mono)' }}>{d.done}/{d.q}</span>
                  </div>
                  <div style={{ height: 5, background: 'var(--bg-muted)', borderRadius: 3 }}>
                    <div style={{ width: `${d.pct}%`, height: '100%', background: 'var(--brand-primary)', borderRadius: 3 }}/>
                  </div>
                </div>
                <button className="mp-btn mp-btn-ghost mp-btn-sm" style={{ justifyContent: 'flex-end' }}>
                  <AdI.chevronRight size={14}/>
                </button>
              </div>
            ))}
          </div>
        </div>

        {/* WhatsApp activity */}
        <div className="mp-card">
          <div className="mp-card-header">
            <h3 className="mp-card-title" style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
              <span style={{ color: 'var(--wa-green)', display: 'inline-flex' }}>
                <AdI.whatsapp size={16}/>
              </span>
              WhatsApp activity
            </h3>
            <span className="mp-pill mp-pill-wa" style={{ background: 'var(--wa-green-bg)', color: 'var(--wa-text)' }}>Live</span>
          </div>
          <div>
            {[
              { who: 'Aisha Khan', when: '2 min ago', what: 'Booked appointment with Dr. Mehta · 23 May, 11:30', kind: 'book' },
              { who: 'Devansh Patel (parent)', when: '8 min ago', what: 'Confirmed reminder for tomorrow', kind: 'ack' },
              { who: 'Kabir Sharma', when: '14 min ago', what: 'Did not respond to no-show ping', kind: 'fail' },
              { who: 'Lakshmi Iyer', when: '22 min ago', what: 'Asked to reschedule — front desk picked up', kind: 'reply' },
              { who: 'Sneha Reddy', when: '38 min ago', what: 'Received prescription PDF', kind: 'rx' },
            ].map((e, i, arr) => (
              <div key={i} style={{
                display: 'flex', gap: 12,
                padding: '14px 20px',
                borderBottom: i < arr.length - 1 ? '1px solid var(--border)' : 'none',
              }}>
                <ActivityDot kind={e.kind}/>
                <div style={{ flex: 1, minWidth: 0 }}>
                  <div style={{ fontSize: 13, color: 'var(--text)', lineHeight: 1.4 }}>
                    <strong style={{ color: 'var(--text-strong)' }}>{e.who}</strong> · {e.what}
                  </div>
                  <div style={{ fontSize: 11, color: 'var(--text-muted)', marginTop: 2 }}>{e.when}</div>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>
    </MpShell>
  );
}

function Kpi({ icon, label, value, delta, dir, sub }) {
  const Ico = MpIcon[icon];
  return (
    <div className="mp-stat">
      <div className="mp-stat-label">
        <Ico size={14}/>{label}
      </div>
      <div style={{ display: 'flex', alignItems: 'baseline', gap: 10 }}>
        <div className="mp-stat-value">{value}</div>
        {delta && (
          <span className={`mp-stat-delta ${dir}`}>
            <AdI.trending size={12}/> {delta}
          </span>
        )}
      </div>
      {sub && <div style={{ fontSize: 12, color: 'var(--text-muted)' }}>{sub}</div>}
    </div>
  );
}

function Legend({ dot, label }) {
  return (
    <span style={{ display: 'inline-flex', alignItems: 'center', gap: 6 }}>
      <span style={{ width: 8, height: 8, borderRadius: 2, background: dot, display: 'inline-block' }}/>
      {label}
    </span>
  );
}

function BarChart() {
  const today    = [4, 9, 14, 18, 22, 16, 6, 8, 14, 18, 12, 1];
  const lastWeek = [3, 7, 12, 15, 19, 14, 5, 7, 12, 15, 10, 1];
  const hours = ['9', '10', '11', '12', '1', '2', '3', '4', '5', '6', '7', '8'];
  const max = 24;
  return (
    <div>
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(12, 1fr)', alignItems: 'flex-end', gap: 10, height: 200 }}>
        {hours.map((h, i) => (
          <div key={h} style={{ display: 'flex', alignItems: 'flex-end', gap: 3, height: '100%', position: 'relative' }}>
            <div style={{
              flex: 1,
              height: `${(lastWeek[i] / max) * 100}%`,
              background: 'var(--border-strong)',
              borderRadius: '3px 3px 0 0',
              minHeight: 2,
            }}/>
            <div style={{
              flex: 1,
              height: `${(today[i] / max) * 100}%`,
              background: 'var(--brand-primary)',
              borderRadius: '3px 3px 0 0',
              minHeight: 2,
            }}/>
          </div>
        ))}
      </div>
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(12, 1fr)', marginTop: 8, fontFamily: 'var(--font-mono)', fontSize: 11, color: 'var(--text-muted)', textAlign: 'center' }}>
        {hours.map(h => <div key={h}>{h}</div>)}
      </div>
    </div>
  );
}

function ActivityDot({ kind }) {
  const colors = {
    book:  'var(--brand-primary)',
    ack:   'var(--brand-accent)',
    fail:  'var(--status-missed-bar)',
    reply: 'var(--status-waiting-bar)',
    rx:    'var(--status-consult-bar)',
  };
  return (
    <div style={{
      width: 28, height: 28, borderRadius: 8,
      background: 'var(--bg-muted)',
      color: colors[kind],
      display: 'inline-flex', alignItems: 'center', justifyContent: 'center',
      flexShrink: 0,
    }}>
      <span style={{ width: 8, height: 8, borderRadius: '50%', background: colors[kind] }}/>
    </div>
  );
}

window.MpAdminDashboard = AdminDashboard;
