/* global React, MpIcon, MpShell, MpPageHeader */
// Mediport — Doctor's Today's Appointments + Queue (hero screen)

const DtI = MpIcon;

const DT_QUEUE = [
  { q: 1, name: 'Sneha Reddy',     id: 'MP-10421', age: 28, gender: 'F', phone: '+91 98765 43210', time: '09:00', actual: '09:02', status: 'completed', note: 'Mild fever, throat congestion · Rx issued', wa: true },
  { q: 2, name: 'Arjun Kapoor',    id: 'MP-10422', age: 34, gender: 'M', phone: '+91 98201 11456', time: '09:15', actual: '09:21', status: 'completed', note: 'Routine BP check', wa: true },
  { q: 3, name: 'Meera Joshi',     id: 'MP-10398', age: 45, gender: 'F', phone: '+91 99887 76655', time: '09:30', actual: '09:34', status: 'consult', note: 'Follow-up — diabetes review', wa: true, current: true },
  { q: 4, name: 'Rahul Kumar',     id: 'MP-10421', age: 31, gender: 'M', phone: '+91 96543 21098', time: '09:45', status: 'waiting', note: 'New patient · headache 5 days', wa: true },
  { q: 5, name: 'Aisha Khan',      id: 'MP-10502', age: 22, gender: 'F', phone: '+91 90876 54321', time: '10:00', status: 'waiting', note: 'Allergy follow-up', wa: true },
  { q: 6, name: 'Devansh Patel',   id: 'MP-10545', age: 8,  gender: 'M', phone: '+91 99876 12340', time: '10:15', status: 'waiting', note: 'Pediatric — recurring cough', wa: true },
  { q: 7, name: 'Lakshmi Iyer',    id: 'MP-10312', age: 62, gender: 'F', phone: '+91 91234 56789', time: '10:30', status: 'delayed', note: 'Hypertension review · arrived late', wa: false },
  { q: 8, name: 'Kabir Sharma',    id: 'MP-10488', age: 27, gender: 'M', phone: '+91 95432 10987', time: '10:45', status: 'missed', note: 'No response to WhatsApp ping', wa: false },
  { q: 9, name: 'Ananya Gupta',    id: 'MP-10612', age: 38, gender: 'F', phone: '+91 98123 45678', time: '11:00', status: 'waiting', note: 'New patient · skin rash', wa: true },
];

const DT_STATUS_META = {
  waiting:   { label: 'Waiting',          pill: 'mp-pill-waiting',   bar: 'var(--status-waiting-bar)' },
  consult:   { label: 'In Consultation',  pill: 'mp-pill-consult',   bar: 'var(--status-consult-bar)' },
  completed: { label: 'Completed',        pill: 'mp-pill-completed', bar: 'var(--status-completed-bar)' },
  missed:    { label: 'Not Attended',     pill: 'mp-pill-missed',    bar: 'var(--status-missed-bar)' },
  delayed:   { label: 'Delayed',          pill: 'mp-pill-delayed',   bar: 'var(--status-delayed-bar)' },
};

function DoctorToday() {
  return (
    <MpShell role="doctor"
      crumbs={["Dashboard", "Today's Queue"]}
      topbarActions={
        <button className="mp-btn mp-btn-secondary mp-btn-sm">
          <DtI.refresh size={14}/> Auto-refresh · 30s
        </button>
      }
    >
      <MpPageHeader
        title="Today's Queue"
        subtitle="Monday, 23 May 2026 · Morning shift 09:00–13:00"
        actions={
          <>
            <button className="mp-btn mp-btn-secondary"><DtI.calendar size={14}/> View week</button>
            <button className="mp-btn mp-btn-primary"><DtI.plus size={14}/> Add walk-in</button>
          </>
        }
      />

      {/* Stats */}
      <div style={{ padding: '0 32px 24px', display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: 16 }}>
        <Stat icon="users"        label="Total today"      value="12" delta="2 vs last Mon" deltaDir="up"/>
        <Stat icon="clock"        label="Waiting"          value="6"  hint="Avg wait 8 min" tint="waiting"/>
        <Stat icon="stethoscope"  label="In consultation"  value="1"  hint="Started 09:34" tint="consult"/>
        <Stat icon="check"        label="Completed"        value="2"  hint="On time" tint="completed"/>
      </div>

      {/* Queue */}
      <div style={{ padding: '0 32px 40px' }}>
        <div className="mp-card">
          <div className="mp-card-header">
            <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
              <h3 className="mp-card-title">Patient Queue</h3>
              <span style={{ fontSize: 12, color: 'var(--text-muted)' }}>
                Auto-updates as patients check in
              </span>
            </div>
            <div style={{ display: 'flex', gap: 8 }}>
              <FilterTab label="All" count={12} active/>
              <FilterTab label="Waiting" count={6}/>
              <FilterTab label="Done" count={2}/>
              <FilterTab label="Issues" count={2}/>
            </div>
          </div>
          <div>
            {DT_QUEUE.map((p, i) => <QueueRow key={p.q} p={p} last={i === DT_QUEUE.length - 1}/>)}
          </div>
        </div>
      </div>
    </MpShell>
  );
}

function FilterTab({ label, count, active }) {
  return (
    <button style={{
      padding: '6px 12px',
      borderRadius: 6,
      fontSize: 13, fontWeight: 600,
      color: active ? 'var(--text-strong)' : 'var(--text-muted)',
      background: active ? 'var(--bg-muted)' : 'transparent',
      display: 'inline-flex', alignItems: 'center', gap: 6,
    }}>
      {label}
      <span style={{
        fontSize: 11, fontWeight: 700,
        padding: '0 6px', borderRadius: 999,
        background: active ? 'var(--brand-primary)' : 'var(--bg-muted)',
        color: active ? '#fff' : 'var(--text-muted)',
        minWidth: 18, textAlign: 'center',
      }}>{count}</span>
    </button>
  );
}

function Stat({ icon, label, value, delta, deltaDir, hint, tint }) {
  const Ico = MpIcon[icon];
  const tintBar = tint ? `var(--status-${tint}-bar)` : 'var(--brand-primary)';
  return (
    <div className="mp-stat" style={{ position: 'relative', overflow: 'hidden' }}>
      <span style={{
        position: 'absolute', top: 0, left: 0, bottom: 0,
        width: 3, background: tintBar, opacity: 0.85,
        borderRadius: '10px 0 0 10px',
      }}/>
      <div className="mp-stat-label">
        <Ico size={14}/>
        {label}
      </div>
      <div style={{ display: 'flex', alignItems: 'baseline', justifyContent: 'space-between', gap: 8 }}>
        <div className="mp-stat-value">{value}</div>
        {delta && (
          <span className={`mp-stat-delta ${deltaDir || 'flat'}`}>
            <MpIcon.trending size={12}/>{delta}
          </span>
        )}
        {hint && !delta && (
          <span style={{ fontSize: 12, color: 'var(--text-muted)' }}>{hint}</span>
        )}
      </div>
    </div>
  );
}

function QueueRow({ p, last }) {
  const meta = DT_STATUS_META[p.status];
  return (
    <div style={{
      display: 'grid',
      gridTemplateColumns: '56px 56px 1fr 1.2fr 1fr 220px',
      alignItems: 'center',
      gap: 16,
      padding: '18px 24px',
      borderBottom: last ? 'none' : '1px solid var(--border)',
      background: p.current ? 'var(--brand-primary-50)' : 'transparent',
      position: 'relative',
    }}>
      {/* Active indicator */}
      {p.current && (
        <span style={{
          position: 'absolute', left: 0, top: 8, bottom: 8, width: 3,
          background: 'var(--brand-primary)', borderRadius: '0 3px 3px 0',
        }}/>
      )}

      {/* Queue # */}
      <div style={{
        width: 44, height: 44, borderRadius: 10,
        background: p.current ? 'var(--brand-primary)' : 'var(--bg-muted)',
        color: p.current ? '#fff' : 'var(--text-strong)',
        display: 'inline-flex', alignItems: 'center', justifyContent: 'center',
        fontFamily: 'var(--font-display)', fontWeight: 800, fontSize: 18,
        letterSpacing: '-0.02em',
      }}>{p.q}</div>

      {/* Avatar */}
      <div className="mp-avatar lg" style={{
        background: p.current ? 'var(--brand-primary-100)' : 'var(--brand-primary-50)',
      }}>{p.name.split(' ').map(n=>n[0]).slice(0,2).join('')}</div>

      {/* Patient */}
      <div style={{ minWidth: 0 }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
          <span style={{ fontFamily: 'var(--font-display)', fontSize: 15, fontWeight: 700, color: 'var(--text-strong)' }}>{p.name}</span>
          {p.wa && (
            <span title="WhatsApp confirmed" style={{ color: 'var(--wa-green)', display: 'inline-flex' }}>
              <DtI.whatsapp size={14}/>
            </span>
          )}
        </div>
        <div style={{ fontSize: 12, color: 'var(--text-muted)', marginTop: 2, display: 'flex', alignItems: 'center', gap: 10 }}>
          <span style={{ fontFamily: 'var(--font-mono)' }}>#{p.id}</span>
          <span>·</span>
          <span>{p.age} yrs · {p.gender === 'F' ? 'Female' : 'Male'}</span>
        </div>
      </div>

      {/* Note */}
      <div style={{ fontSize: 13, color: 'var(--text-muted)', minWidth: 0, lineHeight: 1.4, paddingRight: 8 }}>
        {p.note}
      </div>

      {/* Time + status */}
      <div>
        <div style={{ fontFamily: 'var(--font-mono)', fontSize: 13, fontWeight: 600, color: 'var(--text-strong)' }}>
          {p.time}
          {p.actual && <span style={{ color: 'var(--text-soft)', fontWeight: 500 }}> → {p.actual}</span>}
        </div>
        <div style={{ marginTop: 6 }}>
          <span className={`mp-pill ${meta.pill}`}>{meta.label}</span>
        </div>
      </div>

      {/* Actions */}
      <div style={{ display: 'flex', gap: 6, justifyContent: 'flex-end', flexWrap: 'wrap' }}>
        {p.status === 'waiting' && (
          <button className="mp-btn mp-btn-primary mp-btn-sm"><DtI.play size={12}/> Start</button>
        )}
        {p.status === 'consult' && (
          <>
            <button className="mp-btn mp-btn-primary mp-btn-sm"><DtI.prescription size={12}/> Write Rx</button>
            <button className="mp-btn mp-btn-success mp-btn-sm"><DtI.check size={12}/> Complete</button>
          </>
        )}
        {p.status === 'completed' && (
          <button className="mp-btn mp-btn-ghost mp-btn-sm"><DtI.prescription size={12}/> View Rx</button>
        )}
        {p.status === 'delayed' && (
          <>
            <button className="mp-btn mp-btn-primary mp-btn-sm"><DtI.play size={12}/> Start</button>
            <button className="mp-btn mp-btn-ghost mp-btn-sm"><DtI.phone size={12}/></button>
          </>
        )}
        {p.status === 'missed' && (
          <>
            <button className="mp-btn mp-btn-secondary mp-btn-sm">Reschedule</button>
            <button className="mp-btn mp-btn-ghost mp-btn-sm"><DtI.whatsapp size={12}/></button>
          </>
        )}
        <button className="mp-btn mp-btn-ghost mp-btn-sm" style={{ padding: '7px 8px' }}><DtI.more size={14}/></button>
      </div>
    </div>
  );
}

window.MpDoctorToday = DoctorToday;
