/* global React, MpIcon, MpShell, MpPageHeader */
// Mediport — Attendant · Check-in (book appointment)

const AtI = MpIcon;

function AttendantCheckin() {
  return (
    <MpShell role="attendant"
      crumbs={["Front desk", "Check-in"]}
    >
      <MpPageHeader
        title="Patient check-in"
        subtitle="Look up a patient by phone, or register a new one"
        actions={
          <>
            <button className="mp-btn mp-btn-secondary"><AtI.queue size={14}/> Live queue · 9</button>
            <button className="mp-btn mp-btn-primary"><AtI.userCheck size={14}/> Quick walk-in</button>
          </>
        }
      />

      <div style={{ padding: '0 32px 40px', display: 'grid', gridTemplateColumns: '1.4fr 1fr', gap: 24 }}>

        {/* Left: booking form */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>

          {/* Step 1 — Lookup */}
          <div className="mp-card">
            <div style={{ padding: '20px 24px', borderBottom: '1px solid var(--border)', display: 'flex', alignItems: 'center', gap: 12 }}>
              <StepCircle n="1" active/>
              <div>
                <div style={{ fontFamily: 'var(--font-display)', fontSize: 14, fontWeight: 700, color: 'var(--text-strong)' }}>Look up the patient</div>
                <div style={{ fontSize: 12, color: 'var(--text-muted)' }}>Enter phone number to find existing record</div>
              </div>
            </div>
            <div style={{ padding: 24 }}>
              <div style={{ display: 'flex', gap: 10 }}>
                <div style={{ flex: 1 }}>
                  <label className="mp-label">Phone number</label>
                  <div style={{ display: 'flex' }}>
                    <span style={{
                      padding: '11px 14px',
                      borderTopLeftRadius: 8,
                      borderBottomLeftRadius: 8,
                      background: 'var(--bg-muted)',
                      border: '1px solid var(--border-strong)',
                      borderRight: 'none',
                      fontSize: 14, color: 'var(--text)',
                      fontFamily: 'var(--font-mono)',
                    }}>+91</span>
                    <input className="mp-input" defaultValue="98765 43210" style={{ borderTopLeftRadius: 0, borderBottomLeftRadius: 0, fontFamily: 'var(--font-mono)' }}/>
                  </div>
                </div>
                <div style={{ display: 'flex', alignItems: 'flex-end' }}>
                  <button className="mp-btn mp-btn-primary mp-btn-lg" style={{ height: 44 }}>
                    <AtI.search size={15}/> Search
                  </button>
                </div>
              </div>
            </div>
          </div>

          {/* Step 2 — Found patient */}
          <div className="mp-card" style={{ border: '1px solid var(--brand-primary-100)' }}>
            <div style={{ padding: '20px 24px', borderBottom: '1px solid var(--border)', display: 'flex', alignItems: 'center', gap: 12 }}>
              <StepCircle n="2" active/>
              <div style={{ flex: 1 }}>
                <div style={{ fontFamily: 'var(--font-display)', fontSize: 14, fontWeight: 700, color: 'var(--text-strong)' }}>Patient found</div>
                <div style={{ fontSize: 12, color: 'var(--text-muted)' }}>Existing record · last seen 22 Mar 2026</div>
              </div>
              <span className="mp-pill mp-pill-completed">Match</span>
            </div>
            <div style={{ padding: 24, display: 'flex', alignItems: 'center', gap: 18 }}>
              <div className="mp-avatar lg" style={{ width: 56, height: 56, fontSize: 18 }}>SR</div>
              <div style={{ flex: 1 }}>
                <div style={{ fontFamily: 'var(--font-display)', fontSize: 17, fontWeight: 700, color: 'var(--text-strong)' }}>Sneha Reddy</div>
                <div style={{ fontSize: 13, color: 'var(--text-muted)', marginTop: 4, display: 'flex', gap: 14, flexWrap: 'wrap' }}>
                  <span><strong style={{ color: 'var(--text)' }}>ID</strong>&nbsp;<span style={{ fontFamily: 'var(--font-mono)' }}>MP-10421</span></span>
                  <span><strong style={{ color: 'var(--text)' }}>Age</strong> 28 · Female</span>
                  <span><strong style={{ color: 'var(--text)' }}>DOB</strong> 14 Aug 1997</span>
                </div>
              </div>
              <button className="mp-btn mp-btn-ghost mp-btn-sm">
                <AtI.edit size={13}/> Edit
              </button>
            </div>
          </div>

          {/* Step 3 — Appointment details */}
          <div className="mp-card">
            <div style={{ padding: '20px 24px', borderBottom: '1px solid var(--border)', display: 'flex', alignItems: 'center', gap: 12 }}>
              <StepCircle n="3" active/>
              <div>
                <div style={{ fontFamily: 'var(--font-display)', fontSize: 14, fontWeight: 700, color: 'var(--text-strong)' }}>Appointment details</div>
                <div style={{ fontSize: 12, color: 'var(--text-muted)' }}>Queue number and time will be assigned automatically</div>
              </div>
            </div>
            <div style={{ padding: 24 }}>
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 16 }}>
                <div>
                  <label className="mp-label">Doctor</label>
                  <select className="mp-select" defaultValue="anjali">
                    <option value="anjali">Dr. Anjali Rao — General Medicine</option>
                    <option>Dr. Vikram Mehta — Cardiology</option>
                    <option>Dr. Priya Nair — Pediatrics</option>
                  </select>
                </div>
                <div>
                  <label className="mp-label">Appointment date</label>
                  <input className="mp-input" type="date" defaultValue="2026-05-23"/>
                </div>
              </div>

              <div style={{ marginTop: 16 }}>
                <label className="mp-label">Visit reason (optional)</label>
                <textarea className="mp-textarea" rows={2} placeholder="e.g. Mild fever, throat irritation since Saturday" defaultValue="Mild fever and throat irritation since Saturday"/>
              </div>

              {/* Auto-assigned preview */}
              <div style={{
                marginTop: 18,
                padding: 16,
                background: 'var(--brand-primary-50)',
                borderRadius: 10,
                display: 'flex', alignItems: 'center', gap: 18,
              }}>
                <AssignChip label="Queue #" value="10"/>
                <AssignChip label="Est. time" value="11:30"/>
                <AssignChip label="Est. wait" value="~25 min"/>
                <div style={{ flex: 1, fontSize: 12, color: 'var(--text-muted)' }}>
                  Auto-assigned based on Dr. Rao's morning shift availability.
                </div>
              </div>

              <div style={{ marginTop: 22, display: 'flex', gap: 10, justifyContent: 'flex-end' }}>
                <button className="mp-btn mp-btn-secondary"><AtI.refresh size={13}/> Reset</button>
                <button className="mp-btn mp-btn-success mp-btn-lg">
                  <AtI.check size={14}/> Book &amp; send WhatsApp
                </button>
              </div>
            </div>
          </div>
        </div>

        {/* Right: today snapshot */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
          <div className="mp-card" style={{ padding: 20 }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
              <h3 className="mp-card-title">Today's snapshot</h3>
              <span style={{ fontSize: 12, color: 'var(--text-muted)' }}>23 May</span>
            </div>
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 12 }}>
              <SnapshotTile label="Booked"   value="142" tint="primary"/>
              <SnapshotTile label="Checked in" value="89"  tint="completed"/>
              <SnapshotTile label="Waiting"   value="34"  tint="waiting"/>
              <SnapshotTile label="No-show"   value="3"   tint="missed"/>
            </div>
          </div>

          <div className="mp-card">
            <div className="mp-card-header">
              <h3 className="mp-card-title">Recent check-ins</h3>
              <button className="mp-btn mp-btn-ghost mp-btn-sm">See all</button>
            </div>
            <div>
              {[
                { name: 'Arjun Kapoor', dr: 'Dr. Mehta', q: 4, when: '2m', wa: true },
                { name: 'Devansh Patel', dr: 'Dr. Nair', q: 12, when: '5m', wa: true },
                { name: 'Lakshmi Iyer', dr: 'Dr. Rao', q: 7, when: '11m', wa: false },
                { name: 'Aarav Singh', dr: 'Dr. Khan', q: 3, when: '18m', wa: true },
              ].map((r, i, arr) => (
                <div key={i} style={{
                  display: 'flex', alignItems: 'center', gap: 12,
                  padding: '12px 20px',
                  borderBottom: i < arr.length - 1 ? '1px solid var(--border)' : 'none',
                }}>
                  <div className="mp-avatar sm">{r.name.split(' ').map(n=>n[0]).join('')}</div>
                  <div style={{ flex: 1, minWidth: 0 }}>
                    <div style={{ fontSize: 13, fontWeight: 600, color: 'var(--text-strong)' }}>{r.name}</div>
                    <div style={{ fontSize: 11, color: 'var(--text-muted)' }}>{r.dr} · queue #{r.q}</div>
                  </div>
                  {r.wa && <AtI.whatsapp size={13}/>}
                  <span style={{ fontSize: 11, color: 'var(--text-muted)', fontFamily: 'var(--font-mono)' }}>{r.when}</span>
                </div>
              ))}
            </div>
          </div>

          <div className="mp-card" style={{ padding: 16, background: 'var(--wa-green-bg)', border: '1px solid #C7EED9' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
              <AtI.whatsapp size={18}/>
              <div>
                <div style={{ fontFamily: 'var(--font-display)', fontSize: 13, fontWeight: 700, color: 'var(--wa-text)' }}>91 patients booked via WhatsApp today</div>
                <div style={{ fontSize: 12, color: 'var(--wa-text)', opacity: 0.8 }}>64% of total appointments</div>
              </div>
            </div>
          </div>
        </div>

      </div>
    </MpShell>
  );
}

function StepCircle({ n, active }) {
  return (
    <div style={{
      width: 28, height: 28, borderRadius: '50%',
      background: active ? 'var(--brand-primary)' : 'var(--bg-muted)',
      color: active ? '#fff' : 'var(--text-muted)',
      display: 'inline-flex', alignItems: 'center', justifyContent: 'center',
      fontFamily: 'var(--font-display)', fontWeight: 700, fontSize: 13,
      flexShrink: 0,
    }}>{n}</div>
  );
}

function AssignChip({ label, value }) {
  return (
    <div>
      <div style={{ fontSize: 11, fontWeight: 700, textTransform: 'uppercase', letterSpacing: '0.08em', color: 'var(--brand-primary-700)' }}>{label}</div>
      <div style={{ fontFamily: 'var(--font-display)', fontSize: 20, fontWeight: 700, color: 'var(--brand-primary)', letterSpacing: '-0.02em' }}>{value}</div>
    </div>
  );
}

function SnapshotTile({ label, value, tint }) {
  const colors = {
    primary:   { c: 'var(--brand-primary)', bg: 'var(--brand-primary-50)' },
    completed: { c: 'var(--status-completed)', bg: 'var(--status-completed-bg)' },
    waiting:   { c: 'var(--status-waiting)', bg: 'var(--status-waiting-bg)' },
    missed:    { c: 'var(--status-missed)', bg: 'var(--status-missed-bg)' },
  }[tint];
  return (
    <div style={{ padding: 14, background: colors.bg, borderRadius: 8 }}>
      <div style={{ fontSize: 11, fontWeight: 700, textTransform: 'uppercase', letterSpacing: '0.06em', color: colors.c, opacity: 0.85 }}>{label}</div>
      <div style={{ fontFamily: 'var(--font-display)', fontSize: 24, fontWeight: 700, color: colors.c, marginTop: 4, lineHeight: 1 }}>{value}</div>
    </div>
  );
}

window.MpAttendantCheckin = AttendantCheckin;
