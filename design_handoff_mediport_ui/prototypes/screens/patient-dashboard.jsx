/* global React, MpIcon, MpShell, MpPageHeader */
// Mediport — Patient Dashboard. Focus on queue position + upcoming.

const PtI = MpIcon;

function PatientDashboard() {
  return (
    <MpShell role="patient" crumbs={["My care", "Dashboard"]}>
      <MpPageHeader
        title="Hello, Rahul 👋"
        subtitle="Monday, 23 May · Your appointment is today at 09:45"
        actions={<button className="mp-btn mp-btn-secondary"><PtI.whatsapp size={14}/> Open WhatsApp chat</button>}
      />

      <div style={{ padding: '0 32px 40px', display: 'grid', gridTemplateColumns: '1.5fr 1fr', gap: 24 }}>

        {/* Left column */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: 20 }}>

          {/* Hero: live queue */}
          <div style={{
            background: 'linear-gradient(135deg, #0F1B2D 0%, #14274A 60%, var(--brand-primary) 130%)',
            color: '#fff',
            borderRadius: 14,
            padding: 28,
            position: 'relative',
            overflow: 'hidden',
            boxShadow: '0 8px 24px rgba(11, 79, 184, 0.18)',
          }}>
            <div style={{
              position: 'absolute', inset: 0,
              backgroundImage: 'radial-gradient(circle at 90% 10%, rgba(16,185,129,0.12), transparent 50%)',
              pointerEvents: 'none',
            }}/>
            <div style={{ position: 'relative', display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 28 }}>
              <div>
                <div style={{ fontSize: 12, fontWeight: 700, textTransform: 'uppercase', letterSpacing: '0.12em', color: 'var(--brand-accent)', marginBottom: 8 }}>
                  Today · Dr. Anjali Rao
                </div>
                <div style={{ display: 'flex', alignItems: 'baseline', gap: 14 }}>
                  <div style={{ fontFamily: 'var(--font-display)', fontSize: 14, fontWeight: 600, color: 'rgba(255,255,255,0.7)' }}>Your queue position</div>
                </div>
                <div style={{ fontFamily: 'var(--font-display)', fontSize: 96, fontWeight: 800, lineHeight: 1, letterSpacing: '-0.04em', marginTop: 4 }}>
                  #4
                </div>
                <div style={{ fontSize: 14, color: 'rgba(255,255,255,0.75)', marginTop: 12 }}>
                  3 patients ahead of you · estimated wait <strong style={{ color: '#fff' }}>~22 min</strong>
                </div>
              </div>

              <div style={{ display: 'flex', flexDirection: 'column', gap: 14 }}>
                <MiniStat label="Scheduled" value="09:45" mono/>
                <MiniStat label="Doctor's room" value="3rd Floor · Cabin 4"/>
                <MiniStat label="Avg. consult time" value="8 min" mono/>
                <div style={{ marginTop: 4, display: 'flex', gap: 8 }}>
                  <button style={pillBtn} ><PtI.calendar size={12}/> Reschedule</button>
                  <button style={pillBtn}><PtI.cross size={12}/> Cancel</button>
                </div>
              </div>
            </div>

            {/* Queue progress strip */}
            <div style={{ position: 'relative', marginTop: 24 }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: 11, color: 'rgba(255,255,255,0.6)', marginBottom: 8 }}>
                <span>Queue progress</span>
                <span style={{ fontFamily: 'var(--font-mono)' }}>3 of 12 completed</span>
              </div>
              <div style={{ display: 'flex', gap: 4 }}>
                {Array.from({ length: 12 }, (_, i) => (
                  <div key={i} style={{
                    flex: 1,
                    height: 8,
                    borderRadius: 4,
                    background:
                      i < 2 ? 'var(--brand-accent)' :
                      i === 2 ? 'rgba(255,255,255,0.85)' :
                      i === 3 ? 'rgba(16,185,129,0.6)' :
                      'rgba(255,255,255,0.15)',
                  }}/>
                ))}
              </div>
              <div style={{ fontSize: 11, color: 'rgba(255,255,255,0.65)', marginTop: 6 }}>
                ↑ that's you in green — we'll WhatsApp you when it's your turn.
              </div>
            </div>
          </div>

          {/* Upcoming */}
          <div className="mp-card">
            <div className="mp-card-header">
              <h3 className="mp-card-title">Upcoming appointments</h3>
              <button className="mp-btn mp-btn-ghost mp-btn-sm" style={{ color: 'var(--brand-primary)' }}>
                <PtI.plus size={13}/> Book another
              </button>
            </div>
            <div>
              {[
                { dr: 'Dr. Anjali Rao', spec: 'General Medicine', date: 'Today', time: '09:45', q: 4, status: 'today' },
                { dr: 'Dr. Vikram Mehta', spec: 'Cardiology', date: 'Sat, 31 May', time: '10:30', q: '—', status: 'upcoming' },
                { dr: 'Dr. Anjali Rao', spec: 'GM · Follow-up', date: 'Mon, 22 Jun', time: '11:00', q: '—', status: 'upcoming' },
              ].map((a, i, arr) => (
                <div key={i} style={{
                  display: 'grid',
                  gridTemplateColumns: '60px 1.5fr 1fr 1fr 130px',
                  alignItems: 'center', gap: 16,
                  padding: '16px 24px',
                  borderBottom: i < arr.length - 1 ? '1px solid var(--border)' : 'none',
                }}>
                  <DateBlock when={a.date}/>
                  <div>
                    <div style={{ fontSize: 14, fontWeight: 700, color: 'var(--text-strong)' }}>{a.dr}</div>
                    <div style={{ fontSize: 12, color: 'var(--text-muted)' }}>{a.spec}</div>
                  </div>
                  <div style={{ fontFamily: 'var(--font-mono)', fontSize: 14, color: 'var(--text)', fontWeight: 600 }}>{a.time}</div>
                  <div>
                    {a.status === 'today'
                      ? <span className="mp-pill mp-pill-consult">Today · Q#{a.q}</span>
                      : <span className="mp-pill mp-pill-completed" style={{ background: 'var(--bg-muted)', color: 'var(--text-muted)' }}>Confirmed</span>}
                  </div>
                  <button className="mp-btn mp-btn-secondary mp-btn-sm" style={{ justifyContent: 'center' }}>
                    Details <PtI.chevronRight size={12}/>
                  </button>
                </div>
              ))}
            </div>
          </div>
        </div>

        {/* Right column */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>

          {/* Quick actions */}
          <div className="mp-card" style={{ padding: 20 }}>
            <div style={{ fontFamily: 'var(--font-display)', fontSize: 13, fontWeight: 700, color: 'var(--text-strong)', marginBottom: 12 }}>Quick actions</div>
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 10 }}>
              <ActionTile icon="calendar"      label="Book"          sub="appointment"/>
              <ActionTile icon="prescription"  label="My Rx"         sub="3 active"/>
              <ActionTile icon="whatsapp"      label="WhatsApp"      sub="ask a question"/>
              <ActionTile icon="user"          label="Profile"       sub="& records"/>
            </div>
          </div>

          {/* Prescriptions */}
          <div className="mp-card">
            <div className="mp-card-header">
              <h3 className="mp-card-title">Active prescriptions</h3>
              <span className="mp-pill mp-pill-completed">3</span>
            </div>
            <div style={{ padding: 16 }}>
              {[
                { name: 'Paracetamol 500 mg', dose: '1-0-1 after meals', left: '4 days left', urgent: false },
                { name: 'Cetirizine 10 mg', dose: '0-0-1 at bedtime', left: '2 days left', urgent: true },
                { name: 'Vitamin D 60K', dose: 'Once weekly', left: '6 weeks left', urgent: false },
              ].map((m, i, arr) => (
                <div key={i} style={{
                  display: 'flex', alignItems: 'center', gap: 12,
                  padding: '12px 0',
                  borderBottom: i < arr.length - 1 ? '1px solid var(--border)' : 'none',
                }}>
                  <div style={{
                    width: 36, height: 36, borderRadius: 8,
                    background: 'var(--brand-primary-50)',
                    color: 'var(--brand-primary)',
                    display: 'inline-flex', alignItems: 'center', justifyContent: 'center',
                    flexShrink: 0,
                  }}>
                    <PtI.pill size={16}/>
                  </div>
                  <div style={{ flex: 1, minWidth: 0 }}>
                    <div style={{ fontSize: 13, fontWeight: 600, color: 'var(--text-strong)' }}>{m.name}</div>
                    <div style={{ fontSize: 11, color: 'var(--text-muted)' }}>{m.dose}</div>
                  </div>
                  <span style={{
                    fontSize: 11, fontWeight: 600,
                    color: m.urgent ? 'var(--status-missed)' : 'var(--text-muted)',
                  }}>{m.left}</span>
                </div>
              ))}
            </div>
          </div>

          {/* WhatsApp tip */}
          <div className="mp-card" style={{ padding: 18, background: 'var(--wa-green-bg)', border: '1px solid #C7EED9' }}>
            <div style={{ display: 'flex', alignItems: 'flex-start', gap: 10 }}>
              <PtI.whatsapp size={20}/>
              <div>
                <div style={{ fontFamily: 'var(--font-display)', fontSize: 13, fontWeight: 700, color: 'var(--wa-text)' }}>
                  Need to reschedule?
                </div>
                <div style={{ fontSize: 12, color: 'var(--wa-text)', opacity: 0.85, marginTop: 4, lineHeight: 1.5 }}>
                  Just reply "reschedule" to our WhatsApp at +91 80 4000 1234 — front desk
                  will pick a new slot in a few minutes.
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </MpShell>
  );
}

const pillBtn = {
  padding: '6px 12px',
  background: 'rgba(255,255,255,0.1)',
  border: '1px solid rgba(255,255,255,0.18)',
  borderRadius: 999,
  color: '#fff',
  fontSize: 12, fontWeight: 600,
  display: 'inline-flex', alignItems: 'center', gap: 6,
  fontFamily: 'var(--font-body)',
};

function MiniStat({ label, value, mono }) {
  return (
    <div>
      <div style={{ fontSize: 11, color: 'rgba(255,255,255,0.55)', fontWeight: 600, textTransform: 'uppercase', letterSpacing: '0.08em' }}>{label}</div>
      <div style={{
        fontSize: 17, fontWeight: 700, color: '#fff', marginTop: 3,
        fontFamily: mono ? 'var(--font-mono)' : 'var(--font-display)',
      }}>{value}</div>
    </div>
  );
}

function DateBlock({ when }) {
  // Show as month/day block when not "Today"
  if (when === 'Today') {
    return (
      <div style={{
        width: 52, height: 56, borderRadius: 10,
        background: 'var(--brand-accent)', color: '#fff',
        display: 'flex', flexDirection: 'column',
        alignItems: 'center', justifyContent: 'center',
        fontFamily: 'var(--font-display)',
      }}>
        <div style={{ fontSize: 9, fontWeight: 700, letterSpacing: '0.1em', textTransform: 'uppercase' }}>Now</div>
        <div style={{ fontSize: 17, fontWeight: 800, lineHeight: 1 }}>Today</div>
      </div>
    );
  }
  const [dow, day] = when.split(', ');
  const [d, m] = day.split(' ');
  return (
    <div style={{
      width: 52, height: 56, borderRadius: 10,
      background: 'var(--bg-muted)',
      display: 'flex', flexDirection: 'column',
      alignItems: 'center', justifyContent: 'center',
      fontFamily: 'var(--font-display)',
      border: '1px solid var(--border)',
    }}>
      <div style={{ fontSize: 10, fontWeight: 700, color: 'var(--brand-primary)', letterSpacing: '0.06em', textTransform: 'uppercase' }}>{m}</div>
      <div style={{ fontSize: 20, fontWeight: 800, color: 'var(--text-strong)', lineHeight: 1 }}>{d}</div>
      <div style={{ fontSize: 9, color: 'var(--text-muted)', textTransform: 'uppercase' }}>{dow}</div>
    </div>
  );
}

function ActionTile({ icon, label, sub }) {
  const Ico = MpIcon[icon];
  return (
    <button style={{
      padding: 14,
      borderRadius: 10,
      background: 'var(--bg-subtle)',
      border: '1px solid var(--border)',
      display: 'flex', flexDirection: 'column',
      alignItems: 'flex-start', gap: 10,
      cursor: 'pointer',
      transition: 'border-color 0.15s, background 0.15s',
    }}>
      <span style={{
        width: 34, height: 34, borderRadius: 8,
        background: 'var(--brand-primary-50)',
        color: 'var(--brand-primary)',
        display: 'inline-flex', alignItems: 'center', justifyContent: 'center',
      }}>
        <Ico size={17}/>
      </span>
      <div style={{ textAlign: 'left' }}>
        <div style={{ fontSize: 13, fontWeight: 700, color: 'var(--text-strong)' }}>{label}</div>
        <div style={{ fontSize: 11, color: 'var(--text-muted)' }}>{sub}</div>
      </div>
    </button>
  );
}

window.MpPatientDashboard = PatientDashboard;
