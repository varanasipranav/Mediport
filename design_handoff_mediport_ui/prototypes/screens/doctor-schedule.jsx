/* global React, MpIcon, MpShell, MpPageHeader */
// Mediport — Doctor Weekly Schedule. Template view with day cards.

const DsI2 = MpIcon;

const DOCTOR_DAYS = [
  { day: 'Monday',    short: 'Mon', active: true,  shift: 'Full day', start: '09:00', end: '18:00', slot: 15, slots: 36, booked: 28 },
  { day: 'Tuesday',   short: 'Tue', active: true,  shift: 'Full day', start: '09:00', end: '18:00', slot: 15, slots: 36, booked: 31 },
  { day: 'Wednesday', short: 'Wed', active: true,  shift: 'Morning',  start: '09:00', end: '13:00', slot: 15, slots: 16, booked: 12 },
  { day: 'Thursday',  short: 'Thu', active: true,  shift: 'Full day', start: '09:00', end: '18:00', slot: 15, slots: 36, booked: 22 },
  { day: 'Friday',    short: 'Fri', active: true,  shift: 'Full day', start: '09:00', end: '18:00', slot: 15, slots: 36, booked: 30 },
  { day: 'Saturday',  short: 'Sat', active: true,  shift: 'Morning',  start: '09:00', end: '13:00', slot: 20, slots: 12, booked: 9, weekend: true },
  { day: 'Sunday',    short: 'Sun', active: false, weekend: true },
];

function DoctorSchedule() {
  return (
    <MpShell role="doctor"
      crumbs={["Dashboard", "Schedule", "Weekly template"]}
    >
      <MpPageHeader
        title="Weekly schedule template"
        subtitle="Default working hours · generates daily schedules automatically"
        actions={
          <>
            <button className="mp-btn mp-btn-secondary"><DsI2.calendar size={14}/> Daily view</button>
            <button className="mp-btn mp-btn-primary"><DsI2.refresh size={14}/> Generate next 30 days</button>
          </>
        }
      />

      {/* Doctor summary */}
      <div style={{ padding: '0 32px 24px' }}>
        <div className="mp-card" style={{ padding: 20, display: 'flex', alignItems: 'center', gap: 20 }}>
          <div className="mp-avatar lg" style={{ width: 56, height: 56, fontSize: 18 }}>AR</div>
          <div style={{ flex: 1 }}>
            <div style={{ fontFamily: 'var(--font-display)', fontSize: 17, fontWeight: 700, color: 'var(--text-strong)' }}>Dr. Anjali Rao</div>
            <div style={{ fontSize: 13, color: 'var(--text-muted)', marginTop: 2 }}>General Medicine · MBBS, MD · 12 yrs experience</div>
          </div>
          <SummaryStat label="Active days" value="6 / 7"/>
          <SummaryStat label="Weekly hours" value="46 h"/>
          <SummaryStat label="Avg booked" value="78%"/>
          <span className="mp-pill mp-pill-completed" style={{ marginLeft: 8 }}>Template active</span>
        </div>
      </div>

      {/* Week grid */}
      <div style={{ padding: '0 32px 40px' }}>
        <div style={{
          display: 'grid',
          gridTemplateColumns: 'repeat(7, 1fr)',
          gap: 14,
        }}>
          {DOCTOR_DAYS.map(d => <DayCard key={d.day} d={d}/>)}
        </div>

        {/* Time visualization */}
        <div className="mp-card" style={{ marginTop: 28, padding: 24 }}>
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 18 }}>
            <h3 className="mp-card-title">Working hours overview</h3>
            <div style={{ fontSize: 12, color: 'var(--text-muted)' }}>Mon · Tue · Thu · Fri 9–6 · Wed &amp; Sat morning only</div>
          </div>
          <WeekVisualization/>
        </div>
      </div>
    </MpShell>
  );
}

function SummaryStat({ label, value }) {
  return (
    <div style={{ paddingLeft: 24, borderLeft: '1px solid var(--border)' }}>
      <div style={{ fontSize: 11, fontWeight: 700, textTransform: 'uppercase', letterSpacing: '0.08em', color: 'var(--text-muted)' }}>{label}</div>
      <div style={{ fontFamily: 'var(--font-display)', fontSize: 20, fontWeight: 700, color: 'var(--text-strong)', marginTop: 4 }}>{value}</div>
    </div>
  );
}

function DayCard({ d }) {
  const fillPct = d.active ? Math.round((d.booked / d.slots) * 100) : 0;
  return (
    <div style={{
      background: d.active ? 'var(--bg-card)' : 'var(--bg-muted)',
      border: `1px solid ${d.active ? 'var(--border)' : 'var(--border)'}`,
      borderRadius: 'var(--r-md)',
      padding: 16,
      opacity: d.active ? 1 : 0.7,
      position: 'relative',
      minHeight: 200,
      display: 'flex',
      flexDirection: 'column',
    }}>
      {/* Header */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
        <div>
          <div style={{ fontFamily: 'var(--font-display)', fontSize: 13, fontWeight: 700, color: 'var(--text-strong)', letterSpacing: '-0.005em' }}>
            {d.short}
          </div>
          <div style={{ fontSize: 11, color: 'var(--text-muted)' }}>{d.day}</div>
        </div>
        {d.weekend && (
          <span style={{ fontSize: 10, fontWeight: 700, color: 'var(--status-delayed)', background: 'var(--status-delayed-bg)', padding: '2px 7px', borderRadius: 999, letterSpacing: '0.04em' }}>WKND</span>
        )}
      </div>

      {d.active ? (
        <>
          <div style={{ marginTop: 14, fontFamily: 'var(--font-mono)', fontSize: 14, fontWeight: 600, color: 'var(--text-strong)' }}>
            {d.start} → {d.end}
          </div>
          <div style={{ fontSize: 11, color: 'var(--text-muted)', marginTop: 2 }}>{d.shift} · {d.slot} min slots</div>

          {/* Fill bar */}
          <div style={{ marginTop: 14 }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: 11, color: 'var(--text-muted)' }}>
              <span>Booked</span>
              <span style={{ fontWeight: 600, color: 'var(--text)' }}>{d.booked}/{d.slots}</span>
            </div>
            <div style={{
              height: 5, background: 'var(--bg-muted)', borderRadius: 3,
              marginTop: 5, overflow: 'hidden',
            }}>
              <div style={{
                width: `${fillPct}%`, height: '100%',
                background: fillPct > 90 ? 'var(--status-missed-bar)'
                         : fillPct > 70 ? 'var(--status-waiting-bar)'
                         : 'var(--brand-accent)',
                borderRadius: 3,
              }}/>
            </div>
          </div>

          <div style={{ marginTop: 'auto', paddingTop: 14, display: 'flex', gap: 6 }}>
            <button className="mp-btn mp-btn-secondary mp-btn-sm" style={{ flex: 1, justifyContent: 'center', padding: '6px 8px', fontSize: 12 }}>
              <DsI2.edit size={11}/> Edit
            </button>
          </div>
        </>
      ) : (
        <div style={{
          flex: 1,
          display: 'flex', alignItems: 'center', justifyContent: 'center',
          flexDirection: 'column',
          color: 'var(--text-muted)',
          gap: 8,
        }}>
          <div style={{ fontSize: 13, fontWeight: 600 }}>Day off</div>
          <button className="mp-btn mp-btn-ghost mp-btn-sm">Activate</button>
        </div>
      )}
    </div>
  );
}

function WeekVisualization() {
  // 9am to 6pm, marked hours
  const hours = ['9', '10', '11', '12', '1', '2', '3', '4', '5', '6'];
  return (
    <div>
      {/* Hour scale */}
      <div style={{ display: 'grid', gridTemplateColumns: '60px repeat(10, 1fr)', alignItems: 'center', marginBottom: 8, fontFamily: 'var(--font-mono)', fontSize: 11, color: 'var(--text-muted)' }}>
        <span/>
        {hours.map(h => <span key={h} style={{ borderLeft: '1px solid var(--border)', paddingLeft: 6 }}>{h}{h === '12' ? '' : ''}</span>)}
      </div>
      {DOCTOR_DAYS.map(d => (
        <div key={d.day} style={{ display: 'grid', gridTemplateColumns: '60px 1fr', alignItems: 'center', height: 30, gap: 0 }}>
          <span style={{ fontSize: 12, fontWeight: 600, color: d.active ? 'var(--text)' : 'var(--text-soft)' }}>{d.short}</span>
          <div style={{ position: 'relative', height: 20, background: 'var(--bg-muted)', borderRadius: 4, overflow: 'hidden' }}>
            {d.active && (
              d.shift === 'Morning' ? (
                <Block start={0} end={4 / 9} accent/>
              ) : (
                <>
                  <Block start={0} end={4 / 9} accent/>
                  <Block start={5 / 9} end={1} accent/>
                </>
              )
            )}
          </div>
        </div>
      ))}
    </div>
  );
}

function Block({ start, end, accent }) {
  return (
    <div style={{
      position: 'absolute',
      left: `${start * 100}%`,
      width: `${(end - start) * 100}%`,
      top: 0, bottom: 0,
      background: accent ? 'var(--brand-primary)' : 'var(--text-muted)',
      borderRadius: 3,
      opacity: 0.92,
    }}/>
  );
}

window.MpDoctorSchedule = DoctorSchedule;
