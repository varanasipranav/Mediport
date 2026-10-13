/* global React, MpIcon, MpShell, MpPageHeader */
// Mediport — Doctor · Create Prescription. Two-column: form + patient context.

const RxI = MpIcon;

function PrescriptionScreen() {
  return (
    <MpShell role="doctor"
      crumbs={["Dashboard", "Today's Queue", "#3 Meera Joshi", "New prescription"]}
    >
      <MpPageHeader
        title="New prescription"
        subtitle="Will be sent to the patient on WhatsApp once saved"
        actions={
          <>
            <button className="mp-btn mp-btn-secondary"><RxI.cross size={14}/> Cancel</button>
            <button className="mp-btn mp-btn-secondary"><RxI.download size={14}/> Save draft</button>
            <button className="mp-btn mp-btn-success"><RxI.check size={14}/> Save &amp; send via WhatsApp</button>
          </>
        }
      />

      <div style={{ padding: '0 32px 40px', display: 'grid', gridTemplateColumns: '1fr 320px', gap: 24 }}>

        {/* Left: form */}
        <div className="mp-card">
          {/* Patient header */}
          <div style={{
            padding: '20px 24px',
            display: 'flex',
            alignItems: 'center',
            gap: 16,
            borderBottom: '1px solid var(--border)',
            background: 'linear-gradient(180deg, var(--brand-primary-50) 0%, var(--bg-card) 100%)',
          }}>
            <div className="mp-avatar lg" style={{ width: 52, height: 52, fontSize: 17 }}>MJ</div>
            <div style={{ flex: 1 }}>
              <div style={{ fontFamily: 'var(--font-display)', fontSize: 18, fontWeight: 700, color: 'var(--text-strong)' }}>
                Meera Joshi
              </div>
              <div style={{ fontSize: 12, color: 'var(--text-muted)', marginTop: 4, display: 'flex', gap: 12, alignItems: 'center' }}>
                <span style={{ fontFamily: 'var(--font-mono)' }}>#MP-10398</span>
                <span>·</span>
                <span>45 yrs · Female</span>
                <span>·</span>
                <span><RxI.phone size={10}/> +91 99887 76655</span>
              </div>
            </div>
            <div style={{ textAlign: 'right' }}>
              <div style={{ fontSize: 11, color: 'var(--text-muted)' }}>Queue</div>
              <div style={{ fontFamily: 'var(--font-display)', fontSize: 22, fontWeight: 800, color: 'var(--brand-primary)' }}>#3</div>
            </div>
            <div style={{ textAlign: 'right', borderLeft: '1px solid var(--border)', paddingLeft: 16 }}>
              <div style={{ fontSize: 11, color: 'var(--text-muted)' }}>Started</div>
              <div style={{ fontFamily: 'var(--font-mono)', fontSize: 15, fontWeight: 600, color: 'var(--text-strong)' }}>09:34</div>
            </div>
          </div>

          {/* Form sections */}
          <div style={{ padding: 24 }}>
            <FormSection icon="stethoscope" title="Clinical observations &amp; diagnosis" required>
              <textarea className="mp-textarea" rows={4} defaultValue={`Chief complaint: Recurring fatigue + increased thirst, 2 weeks
Vitals: BP 138/86, FBS 168 mg/dL
Diagnosis: Type 2 diabetes — sub-optimally controlled; metformin alone insufficient.`}
                style={{ minHeight: 110, resize: 'vertical' }}/>
            </FormSection>

            <FormSection icon="pill" title="Prescribed medications" required>
              <div style={{ display: 'flex', flexDirection: 'column', gap: 10 }}>
                <MedRow num="01" name="Metformin" strength="500 mg" dosage="1-0-1 after meals" duration="30 days" qty={60}/>
                <MedRow num="02" name="Glimepiride" strength="2 mg" dosage="1-0-0 before breakfast" duration="30 days" qty={30}/>
                <MedRow num="03" name="Atorvastatin" strength="10 mg" dosage="0-0-1 at bedtime" duration="30 days" qty={30}/>
              </div>
              <button className="mp-btn mp-btn-ghost mp-btn-sm" style={{ marginTop: 12, color: 'var(--brand-primary)' }}>
                <RxI.plus size={13}/> Add medication
              </button>
            </FormSection>

            <FormSection icon="prescription" title="Recommended lab tests">
              <div style={{ display: 'flex', flexWrap: 'wrap', gap: 8 }}>
                {['HbA1c', 'Lipid profile', 'Liver function', 'Serum creatinine', 'Urine routine'].map(t => (
                  <span key={t} style={{
                    padding: '6px 12px',
                    background: 'var(--brand-primary-50)',
                    color: 'var(--brand-primary)',
                    borderRadius: 999,
                    fontSize: 12, fontWeight: 600,
                    display: 'inline-flex', alignItems: 'center', gap: 6,
                  }}>
                    {t} <RxI.cross size={10}/>
                  </span>
                ))}
                <button style={{
                  padding: '6px 12px',
                  background: 'transparent',
                  color: 'var(--text-muted)',
                  border: '1px dashed var(--border-strong)',
                  borderRadius: 999,
                  fontSize: 12, fontWeight: 600,
                  display: 'inline-flex', alignItems: 'center', gap: 6,
                }}>
                  <RxI.plus size={10}/> Add test
                </button>
              </div>
            </FormSection>

            <FormSection icon="heart" title="Lifestyle &amp; instructions">
              <textarea className="mp-textarea" rows={3} defaultValue={`• Diet — low carb, no refined sugar; small frequent meals
• Walk 30 minutes daily; light yoga in morning
• Check sugar levels at home before breakfast 2× a week
• Return immediately if FBS > 250 mg/dL`} style={{ minHeight: 92, resize: 'vertical' }}/>
            </FormSection>

            <FormSection icon="calendarCheck" title="Follow-up">
              <div style={{ display: 'flex', gap: 10 }}>
                <input className="mp-input" type="date" defaultValue="2026-06-22" style={{ maxWidth: 200 }}/>
                <select className="mp-select" defaultValue="4w" style={{ maxWidth: 220 }}>
                  <option value="1w">In 1 week</option>
                  <option value="2w">In 2 weeks</option>
                  <option value="4w">In 4 weeks</option>
                  <option value="3m">In 3 months</option>
                </select>
              </div>
              <div className="mp-helper" style={{ marginTop: 10 }}>
                <RxI.whatsapp size={11}/> Patient will get a WhatsApp reminder 1 day before.
              </div>
            </FormSection>
          </div>
        </div>

        {/* Right: context sidebar */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
          <div className="mp-card" style={{ padding: 20 }}>
            <div style={{ fontFamily: 'var(--font-display)', fontSize: 13, fontWeight: 700, color: 'var(--text-strong)', marginBottom: 14, display: 'flex', alignItems: 'center', gap: 8 }}>
              <RxI.clock size={14}/> Recent visits
            </div>
            {[
              { date: '22 Mar 2026', dx: 'Routine diabetes review', dr: 'Dr. Rao' },
              { date: '14 Dec 2025', dx: 'Type 2 diabetes — initial Dx', dr: 'Dr. Rao' },
              { date: '03 Sep 2025', dx: 'Hypertension follow-up', dr: 'Dr. Mehta' },
            ].map((v, i, arr) => (
              <div key={i} style={{
                paddingBottom: 12,
                marginBottom: 12,
                borderBottom: i < arr.length - 1 ? '1px solid var(--border)' : 'none',
                paddingLeft: 14,
                borderLeft: '2px solid var(--brand-primary-100)',
              }}>
                <div style={{ fontFamily: 'var(--font-mono)', fontSize: 11, color: 'var(--text-muted)' }}>{v.date}</div>
                <div style={{ fontSize: 13, fontWeight: 600, color: 'var(--text-strong)', marginTop: 2 }}>{v.dx}</div>
                <div style={{ fontSize: 11, color: 'var(--text-muted)', marginTop: 1 }}>{v.dr}</div>
              </div>
            ))}
            <button className="mp-btn mp-btn-ghost mp-btn-sm" style={{ width: '100%', justifyContent: 'center', color: 'var(--brand-primary)' }}>
              See full history
            </button>
          </div>

          <div className="mp-card" style={{ padding: 20, background: 'var(--bg-muted)' }}>
            <div style={{ fontFamily: 'var(--font-display)', fontSize: 13, fontWeight: 700, color: 'var(--text-strong)', marginBottom: 10, display: 'flex', alignItems: 'center', gap: 8 }}>
              <RxI.shield size={14}/> Allergies &amp; flags
            </div>
            <div style={{ display: 'flex', flexDirection: 'column', gap: 8 }}>
              <FlagItem severity="high" label="Penicillin" note="Hives, 2019"/>
              <FlagItem severity="med" label="Sulpha drugs" note="Mild rash"/>
            </div>
          </div>

          <div className="mp-card" style={{ padding: 18, background: 'var(--wa-green-bg)', border: '1px solid #C7EED9' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: 10, marginBottom: 8 }}>
              <RxI.whatsapp size={18}/>
              <div style={{ fontFamily: 'var(--font-display)', fontSize: 13, fontWeight: 700, color: 'var(--wa-text)' }}>Delivered on WhatsApp</div>
            </div>
            <div style={{ fontSize: 12, color: 'var(--wa-text)', opacity: 0.85, lineHeight: 1.5 }}>
              On save, Meera receives a PDF of this prescription and a follow-up reminder
              on <strong>22 Jun 2026</strong>.
            </div>
          </div>
        </div>

      </div>
    </MpShell>
  );
}

function FormSection({ icon, title, required, children }) {
  const Ico = MpIcon[icon];
  return (
    <div style={{ paddingTop: 18, paddingBottom: 22, borderBottom: '1px solid var(--border)' }}>
      <div style={{ display: 'flex', alignItems: 'center', gap: 10, marginBottom: 12 }}>
        <span style={{
          width: 30, height: 30, borderRadius: 8,
          background: 'var(--brand-primary-50)',
          color: 'var(--brand-primary)',
          display: 'inline-flex', alignItems: 'center', justifyContent: 'center',
        }}>
          <Ico size={15}/>
        </span>
        <div style={{ fontFamily: 'var(--font-display)', fontSize: 14, fontWeight: 700, color: 'var(--text-strong)' }}>
          {title}
          {required && <span style={{ color: 'var(--status-missed)', marginLeft: 4 }}>*</span>}
        </div>
      </div>
      {children}
    </div>
  );
}

function MedRow({ num, name, strength, dosage, duration, qty }) {
  return (
    <div style={{
      display: 'grid',
      gridTemplateColumns: '40px 1.5fr 1fr 1.5fr 1fr 90px 32px',
      alignItems: 'center',
      gap: 10,
      padding: '12px 14px',
      background: 'var(--bg-subtle)',
      border: '1px solid var(--border)',
      borderRadius: 8,
    }}>
      <div style={{ fontFamily: 'var(--font-mono)', fontSize: 12, color: 'var(--text-muted)', fontWeight: 600 }}>{num}</div>
      <div>
        <div style={{ fontSize: 14, fontWeight: 600, color: 'var(--text-strong)' }}>{name}</div>
        <div style={{ fontSize: 11, color: 'var(--text-muted)' }}>Tablet</div>
      </div>
      <div style={{ fontFamily: 'var(--font-mono)', fontSize: 13, color: 'var(--text)' }}>{strength}</div>
      <div style={{ fontSize: 13, color: 'var(--text)' }}>{dosage}</div>
      <div style={{ fontSize: 13, color: 'var(--text-muted)' }}>{duration}</div>
      <div style={{ fontSize: 13, fontWeight: 600, color: 'var(--text)', textAlign: 'right' }}>
        Qty: <span style={{ fontFamily: 'var(--font-mono)' }}>{qty}</span>
      </div>
      <button style={{ color: 'var(--text-muted)', display: 'inline-flex', justifyContent: 'center' }}>
        <RxI.more size={14}/>
      </button>
    </div>
  );
}

function FlagItem({ severity, label, note }) {
  const color = severity === 'high' ? 'var(--status-missed)' : 'var(--status-waiting)';
  const bg    = severity === 'high' ? 'var(--status-missed-bg)' : 'var(--status-waiting-bg)';
  return (
    <div style={{ display: 'flex', alignItems: 'center', gap: 10, padding: '8px 10px', background: bg, borderRadius: 6 }}>
      <RxI.flag size={14}/>
      <div style={{ flex: 1 }}>
        <div style={{ fontSize: 13, fontWeight: 700, color }}>{label}</div>
        <div style={{ fontSize: 11, color, opacity: 0.75 }}>{note}</div>
      </div>
    </div>
  );
}

window.MpPrescription = PrescriptionScreen;
