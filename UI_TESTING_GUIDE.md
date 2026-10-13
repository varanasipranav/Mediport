# UI Testing Guide - Hospital Management System

## 🧪 Quick Testing Checklist

### Prerequisites
✅ SweetAlert2 library loaded (check browser console for errors)
✅ Font Awesome icons loaded
✅ All pages using layout.html template

---

## 📋 Page-by-Page Testing

### 1. Admin - User Management (`/admin/users/list`)

**Visual Checks:**
- [ ] Purple gradient header visible
- [ ] Filter & Search card displays correctly
- [ ] Role badges are color-coded (Admin=purple, Doctor=green, Patient=blue, Attendant=pink, Pharma=cyan)
- [ ] Status badges show Active/Inactive with icons
- [ ] Table has hover effects
- [ ] All Font Awesome icons visible

**Functional Tests:**
- [ ] Click "Edit" button → navigates correctly
- [ ] Click "Delete" button → SweetAlert2 confirmation dialog appears
- [ ] Confirm delete → SweetAlert2 shows confirmation
- [ ] Click "Disable/Enable" button → SweetAlert2 confirmation appears
- [ ] Search functionality works
- [ ] Filter by role works

**Expected SweetAlert2 Dialogs:**
- Delete: Red warning with "Yes, delete it!" button
- Toggle status: Yellow/Green question with "Yes, disable/enable it!" button
- Success message: Green with timer
- Error message: Red

---

### 2. Doctor - Today's Appointments (`/doctor/appointments/today`)

**Visual Checks:**
- [ ] Purple gradient header
- [ ] Four stat boxes with correct colors (Total=blue, Waiting=yellow, Consultation=purple, Completed=green)
- [ ] Queue cards color-coded by status
- [ ] Action buttons have gradients
- [ ] Modal for notes displays correctly

**Functional Tests:**
- [ ] Click "Start" → Form submits (can add SweetAlert2 confirmation)
- [ ] Click "Write Rx" → Navigates to prescription form
- [ ] Click "Complete" → Form submits (can add SweetAlert2 confirmation)
- [ ] Click "Notes" → Modal opens
- [ ] Page auto-refreshes after 30 seconds

**Queue Card Status Colors:**
- Waiting: Grey background
- In Consultation: Blue gradient
- Completed: Green tint (slightly transparent)
- Not Attended: Red background
- Delayed: Yellow/amber background

---

### 3. Doctor - Create Prescription (`/doctor/prescriptions/create/{id}`)

**Visual Checks:**
- [ ] Purple gradient header
- [ ] Patient info card with blue gradient
- [ ] Form sections clearly separated
- [ ] All icons visible in labels
- [ ] Text areas have proper placeholder examples
- [ ] Buttons have gradient effects

**Functional Tests:**
- [ ] Submit without required fields → SweetAlert2 warning
- [ ] Submit with all fields → Success message
- [ ] Cancel button → Navigates back
- [ ] Form validation works

**Expected SweetAlert2:**
- Missing fields: Yellow warning "Required Fields Missing"
- Success: Green with timer "Prescription created successfully"

---

### 4. Patient - View Prescription (`/patient/prescriptions/view/{id}`)

**Visual Checks:**
- [ ] Hospital header with purple gradient
- [ ] ℞ (Rx) symbol visible
- [ ] Patient and Doctor info in grid layout
- [ ] Diagnosis section with yellow gradient
- [ ] Medicines section with grey gradient and green border
- [ ] Doctor signature section at bottom
- [ ] Print button visible

**Functional Tests:**
- [ ] Click "Print" → Opens print dialog
- [ ] Click "Download PDF" → SweetAlert2 with instructions
- [ ] Print preview looks professional
- [ ] Back button works

**Print Test:**
- [ ] In print preview, action buttons are hidden
- [ ] Prescription has professional layout
- [ ] All sections visible
- [ ] Proper margins

---

### 5. Patient - Prescriptions List (`/patient/prescriptions`)

**Visual Checks:**
- [ ] Purple gradient header
- [ ] Blue summary card with total count
- [ ] Prescription cards with hover effects
- [ ] Doctor icon badges
- [ ] Color-coded sections

**Functional Tests:**
- [ ] Click "View Full Details" → Navigates to prescription
- [ ] Empty state shows when no prescriptions
- [ ] Hover on cards → Lift animation
- [ ] All icons display correctly

---

### 6. Patient - Book Appointment (`/patient/book-appointment`)

**Visual Checks:**
- [ ] Purple gradient header
- [ ] Doctor selection dropdown styled
- [ ] Date picker styled
- [ ] Info box with blue gradient
- [ ] Doctor cards have hover effects

**Functional Tests:**
- [ ] Submit without doctor → SweetAlert2 warning
- [ ] Submit without date → SweetAlert2 warning
- [ ] Successful booking → SweetAlert2 success with timer
- [ ] Cancel button works

---

### 7. Patient - Appointments List (`/patient/appointments`)

**Visual Checks:**
- [ ] Purple gradient header
- [ ] Table headers have icons
- [ ] Status badges color-coded
- [ ] Empty state displays correctly
- [ ] Hover effects on table rows

**Functional Tests:**
- [ ] Click "Cancel" on appointment → SweetAlert2 confirmation
- [ ] Confirm cancel → SweetAlert2 success
- [ ] Cancel dialog has "No, keep it" button
- [ ] Book new appointment button works

---

### 8. Attendant - Check-in (`/attendant/check-in`)

**Visual Checks:**
- [ ] Purple gradient header
- [ ] Search button with blue gradient
- [ ] Patient found card with blue gradient
- [ ] New patient card with yellow gradient
- [ ] Form inputs styled correctly

**Functional Tests:**
- [ ] Search patient → SweetAlert2 messages instead of alert boxes
- [ ] Book appointment → SweetAlert2 success/error
- [ ] All messages use SweetAlert2 (not browser alerts)

**SweetAlert2 Messages:**
- Patient found: Blue info
- Patient not found: Blue info
- Missing fields: Yellow warning
- Success: Green with timer
- Error: Red

---

### 9. Pharmacy - View Prescription (`/pharma/prescriptions/view/{id}`)

**Visual Checks:**
- [ ] Same professional design as patient prescription
- [ ] "Pharmacy Department" subtitle
- [ ] Green "Mark as Printed" button
- [ ] Printed status badge when already printed

**Functional Tests:**
- [ ] Click "Mark as Printed" → SweetAlert2 confirmation
- [ ] Confirm → Updates status
- [ ] Print button works
- [ ] Already printed prescriptions show badge

---

## 🎨 Global UI Tests

### Font Awesome Icons
Test these common icons appear correctly:
- `fas fa-users` - Users
- `fas fa-user-md` - Doctor
- `fas fa-user-injured` - Patient
- `fas fa-pills` - Medicines
- `fas fa-calendar-check` - Appointments
- `fas fa-file-prescription` - Prescription
- `fas fa-stethoscope` - Medical
- `fas fa-flask` - Lab tests

### SweetAlert2 Types
Test all appear correctly:
- `icon: 'success'` - Green checkmark
- `icon: 'error'` - Red X
- `icon: 'warning'` - Yellow exclamation
- `icon: 'info'` - Blue i
- `icon: 'question'` - Blue question mark

### Responsive Design
Test at different screen sizes:
- [ ] Desktop (1920px): Full layout
- [ ] Tablet (768px): Responsive grid
- [ ] Mobile (375px): Stacked layout, mobile menu

### Animations & Transitions
- [ ] Cards lift on hover
- [ ] Buttons raise on hover
- [ ] Smooth color transitions
- [ ] No jerky movements

---

## 🐛 Common Issues to Check

### SweetAlert2 Not Working
1. Check browser console for errors
2. Verify `fragments/scripts.html` includes SweetAlert2 CDN
3. Clear browser cache
4. Check if jQuery conflicts exist

### Font Awesome Icons Missing
1. Check if Font Awesome CSS is loaded
2. Verify icon class names (should be `fas fa-icon-name`)
3. Check browser console for 404 errors
4. Clear browser cache

### Styles Not Applying
1. Hard refresh (Ctrl+F5 or Cmd+Shift+R)
2. Check if layout.html is being used
3. Verify CSS is in `<style>` tags in `<head>`
4. Check for CSS conflicts

---

## ✅ Success Criteria

**All pages should have:**
- ✅ SweetAlert2 instead of native alerts
- ✅ All Font Awesome icons visible
- ✅ Consistent gradient headers
- ✅ Hover effects on interactive elements
- ✅ Professional color scheme
- ✅ Proper padding and spacing
- ✅ Responsive design
- ✅ Print-ready prescriptions

**Professional Features:**
- ✅ Medical prescription looks like real prescription
- ✅ Color-coded status indicators
- ✅ Smooth animations
- ✅ Clear visual hierarchy
- ✅ Accessible design

---

## 📱 Browser Testing

Test in these browsers:
- [ ] Chrome/Edge (Chromium)
- [ ] Firefox
- [ ] Safari (if available)

---

## 🚀 Quick Test Commands

```bash
# Clear browser cache in Chrome
Ctrl+Shift+Delete (Windows)
Cmd+Shift+Delete (Mac)

# Hard refresh
Ctrl+F5 (Windows)
Cmd+Shift+R (Mac)

# Open DevTools Console
F12 or Ctrl+Shift+I
```

---

## 📞 If Something Doesn't Work

1. **Check browser console** (F12) for JavaScript errors
2. **Clear cache** and hard refresh
3. **Verify SweetAlert2 is loaded**: In console, type `Swal` - should show object
4. **Check Font Awesome**: In console, type `getComputedStyle(document.querySelector('.fas')).fontFamily` - should show Font Awesome
5. **Restart application** to ensure latest changes are deployed

---

*Last Updated: March 14, 2026*
*Version: 2.0*
