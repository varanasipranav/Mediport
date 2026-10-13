/* global React */
// Mediport — shared icons. Stroke-based, 20×20 viewBox, currentColor.
// Single small file. Keep visual style consistent.

const Icon = ({ d, size = 18, stroke = 1.75, fill = 'none' }) => (
  <svg width={size} height={size} viewBox="0 0 20 20" fill={fill}
       stroke="currentColor" strokeWidth={stroke}
       strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
    {d}
  </svg>
);

const I = {
  dashboard: (p) => <Icon {...p} d={<>
    <rect x="2.5" y="2.5" width="6" height="7" rx="1.2"/>
    <rect x="11.5" y="2.5" width="6" height="4" rx="1.2"/>
    <rect x="2.5" y="11.5" width="6" height="6" rx="1.2"/>
    <rect x="11.5" y="9.5" width="6" height="8" rx="1.2"/>
  </>}/>,
  calendar: (p) => <Icon {...p} d={<>
    <rect x="2.5" y="3.5" width="15" height="14" rx="2"/>
    <path d="M2.5 7.5h15M6.5 1.5v4M13.5 1.5v4"/>
  </>}/>,
  calendarCheck: (p) => <Icon {...p} d={<>
    <rect x="2.5" y="3.5" width="15" height="14" rx="2"/>
    <path d="M2.5 7.5h15M6.5 1.5v4M13.5 1.5v4M7 12l2 2 4-4"/>
  </>}/>,
  users: (p) => <Icon {...p} d={<>
    <circle cx="7.5" cy="7" r="3"/>
    <path d="M2 16c0-2.5 2.5-4.5 5.5-4.5S13 13.5 13 16"/>
    <circle cx="14" cy="6" r="2.3"/>
    <path d="M14 11c2.5 0 4 1.7 4 3.7"/>
  </>}/>,
  user: (p) => <Icon {...p} d={<>
    <circle cx="10" cy="7" r="3.2"/>
    <path d="M3.5 17c0-3 3-5.2 6.5-5.2S16.5 14 16.5 17"/>
  </>}/>,
  userCheck: (p) => <Icon {...p} d={<>
    <circle cx="8" cy="7" r="3"/>
    <path d="M2 17c0-2.8 2.7-5 6-5s6 2.2 6 5"/>
    <path d="M14 9l1.5 1.5L18 8"/>
  </>}/>,
  stethoscope: (p) => <Icon {...p} d={<>
    <path d="M5 2v6a3 3 0 0 0 6 0V2"/>
    <path d="M8 11v2.5a4 4 0 0 0 8 0V11"/>
    <circle cx="16" cy="9" r="1.5"/>
  </>}/>,
  pill: (p) => <Icon {...p} d={<>
    <rect x="1.7" y="6" width="16.6" height="8" rx="4" transform="rotate(-30 10 10)"/>
    <path d="M6.4 4.3l7 7"/>
  </>}/>,
  prescription: (p) => <Icon {...p} d={<>
    <path d="M5 2.5h4a3 3 0 0 1 0 6H5V2.5z"/>
    <path d="M5 8.5v9M9 8.5l8 9M12 12l5-3.5"/>
  </>}/>,
  queue: (p) => <Icon {...p} d={<>
    <path d="M3 5.5h14M3 10h14M3 14.5h9"/>
    <circle cx="16" cy="14.5" r="2.5"/>
  </>}/>,
  whatsapp: (p) => <Icon {...p} d={<>
    <path d="M3 17l1.3-3.7A7 7 0 1 1 7 17l-4 0z"/>
    <path d="M7.5 8c.3 1.5 1.2 2.6 2.5 3 .6.2 1 .1 1.3-.3l.5-.6c.2-.2.5-.3.8-.1l1.3.7c.3.2.4.5.3.8a2 2 0 0 1-2.4 1.3c-2.7-.6-4.4-2.4-5-5a1.5 1.5 0 0 1 1.3-1.8c.3 0 .6 0 .8.3l.7 1.3c.1.3 0 .6-.1.8z" fill="currentColor" stroke="none"/>
  </>}/>,
  bell: (p) => <Icon {...p} d={<>
    <path d="M10 2.5a5 5 0 0 0-5 5v3l-1.5 3h13L15 10.5v-3a5 5 0 0 0-5-5z"/>
    <path d="M8 16a2 2 0 0 0 4 0"/>
  </>}/>,
  search: (p) => <Icon {...p} d={<>
    <circle cx="9" cy="9" r="5.5"/>
    <path d="M13 13l4 4"/>
  </>}/>,
  chevronRight: (p) => <Icon {...p} d={<path d="M7.5 4l6 6-6 6"/>}/>,
  chevronDown: (p) => <Icon {...p} d={<path d="M4 7.5l6 6 6-6"/>}/>,
  chevronLeft: (p) => <Icon {...p} d={<path d="M12.5 4l-6 6 6 6"/>}/>,
  play: (p) => <Icon {...p} d={<path d="M5 3v14l11-7z" fill="currentColor" stroke="none"/>}/>,
  check: (p) => <Icon {...p} d={<path d="M4 10.5l4 4 8-9"/>}/>,
  plus: (p) => <Icon {...p} d={<><path d="M10 4v12M4 10h12"/></>}/>,
  edit: (p) => <Icon {...p} d={<>
    <path d="M3 17h4l9-9-4-4-9 9v4z"/>
    <path d="M12 4l4 4"/>
  </>}/>,
  phone: (p) => <Icon {...p} d={<>
    <path d="M3.5 4.5a2 2 0 0 1 2-2h2l1.5 4-2 1.3a10 10 0 0 0 5.2 5.2L13.5 11l4 1.5v2a2 2 0 0 1-2 2A11 11 0 0 1 3.5 4.5z"/>
  </>}/>,
  clock: (p) => <Icon {...p} d={<>
    <circle cx="10" cy="10" r="7.5"/>
    <path d="M10 5.5V10l3 2"/>
  </>}/>,
  refresh: (p) => <Icon {...p} d={<>
    <path d="M17 3v4h-4M3 17v-4h4"/>
    <path d="M16.5 7.5A7 7 0 0 0 4.5 6M3.5 12.5A7 7 0 0 0 15.5 14"/>
  </>}/>,
  logout: (p) => <Icon {...p} d={<>
    <path d="M12 5V3.5a1 1 0 0 0-1-1H4.5a1 1 0 0 0-1 1v13a1 1 0 0 0 1 1H11a1 1 0 0 0 1-1V15"/>
    <path d="M8 10h10M15 7l3 3-3 3"/>
  </>}/>,
  settings: (p) => <Icon {...p} d={<>
    <circle cx="10" cy="10" r="2.5"/>
    <path d="M10 1.5v2.5M10 16v2.5M3.5 10h-2M18.5 10h-2M4.6 4.6l1.5 1.5M14 14l1.5 1.5M4.6 15.4L6 14M14 6l1.5-1.5"/>
  </>}/>,
  trending: (p) => <Icon {...p} d={<>
    <path d="M2.5 13.5l5-5 3 3 6-6"/>
    <path d="M12 5.5h4.5V10"/>
  </>}/>,
  bedHospital: (p) => <Icon {...p} d={<>
    <path d="M2.5 14V6"/>
    <path d="M2.5 10h15v4"/>
    <path d="M17.5 14v2M2.5 14v2"/>
    <circle cx="6.5" cy="8.5" r="1.5"/>
  </>}/>,
  shield: (p) => <Icon {...p} d={<>
    <path d="M10 1.5l7 2.5v5c0 4-3 7.5-7 9-4-1.5-7-5-7-9v-5l7-2.5z"/>
    <path d="M7 10l2.2 2.2L13.5 8"/>
  </>}/>,
  filter: (p) => <Icon {...p} d={<path d="M2.5 4.5h15l-6 7v5l-3-1.5v-3.5l-6-7z"/>}/>,
  download: (p) => <Icon {...p} d={<>
    <path d="M10 3v10M5.5 9.5L10 14l4.5-4.5"/>
    <path d="M3 16.5h14"/>
  </>}/>,
  flag: (p) => <Icon {...p} d={<>
    <path d="M4 3v14"/>
    <path d="M4 3h10l-2 3.5L14 10H4"/>
  </>}/>,
  send: (p) => <Icon {...p} d={<>
    <path d="M3 10l14-7-4 14-3-6-7-1z"/>
  </>}/>,
  more: (p) => <Icon {...p} d={<>
    <circle cx="5" cy="10" r="1.2" fill="currentColor" stroke="none"/>
    <circle cx="10" cy="10" r="1.2" fill="currentColor" stroke="none"/>
    <circle cx="15" cy="10" r="1.2" fill="currentColor" stroke="none"/>
  </>}/>,
  cross: (p) => <Icon {...p} d={<path d="M5 5l10 10M15 5L5 15"/>}/>,
  heart: (p) => <Icon {...p} d={<path d="M10 17s-7-4.3-7-9.2A3.8 3.8 0 0 1 10 5.5 3.8 3.8 0 0 1 17 7.8C17 12.7 10 17 10 17z"/>}/>,
};

window.MpIcon = I;
