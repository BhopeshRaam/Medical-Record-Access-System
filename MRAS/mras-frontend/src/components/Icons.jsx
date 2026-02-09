// Minimal, dependency-free icon set (inline SVG)
// Keep them 20x20 by default and style via className.

function IconBase({ children, className = "", ...props }) {
  return (
    <svg
      viewBox="0 0 24 24"
      fill="none"
      stroke="currentColor"
      strokeWidth="2"
      strokeLinecap="round"
      strokeLinejoin="round"
      aria-hidden="true"
      className={`h-5 w-5 ${className}`}
      {...props}
    >
      {children}
    </svg>
  );
}

export function IconLogo({ className = "" }) {
  // A simple medical cross in a rounded square
  return (
    <svg viewBox="0 0 40 40" className={`h-10 w-10 ${className}`} aria-hidden="true">
      <defs>
        <linearGradient id="mrasLogoG" x1="0" x2="1" y1="0" y2="1">
          <stop offset="0" stopColor="#2563eb" />
          <stop offset="1" stopColor="#60a5fa" />
        </linearGradient>
      </defs>
      <rect x="2" y="2" width="36" height="36" rx="14" fill="url(#mrasLogoG)" />
      <path d="M20 12v16" stroke="#fff" strokeWidth="4" strokeLinecap="round" />
      <path d="M12 20h16" stroke="#fff" strokeWidth="4" strokeLinecap="round" />
    </svg>
  );
}

export function IconHome(props) {
  return (
    <IconBase {...props}>
      <path d="M3 10.5 12 3l9 7.5" />
      <path d="M5 10v11h14V10" />
      <path d="M10 21v-7h4v7" />
    </IconBase>
  );
}

export function IconPatients(props) {
  return (
    <IconBase {...props}>
      <path d="M16 11a4 4 0 1 0-8 0" />
      <path d="M12 15c-4.4 0-8 2.2-8 5v1h10" />
      <path d="M18 8v6" />
      <path d="M15 11h6" />
    </IconBase>
  );
}

export function IconRecords(props) {
  return (
    <IconBase {...props}>
      <path d="M8 3h6l4 4v14a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2Z" />
      <path d="M14 3v5h5" />
      <path d="M9 13h6" />
      <path d="M9 17h6" />
    </IconBase>
  );
}

export function IconUsers(props) {
  return (
    <IconBase {...props}>
      <path d="M16 11a4 4 0 1 0-8 0" />
      <path d="M2 21c0-4 4-6 8-6" />
      <path d="M22 21c0-3-3-5-6-5" />
      <path d="M20 8a3 3 0 0 0-2-2" />
    </IconBase>
  );
}

export function IconAudit(props) {
  return (
    <IconBase {...props}>
      <path d="M9 3h6l4 4v14a2 2 0 0 1-2 2H9a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2Z" />
      <path d="M9 13h6" />
      <path d="M9 17h6" />
      <path d="M14 3v5h5" />
    </IconBase>
  );
}

export function IconSettings(props) {
  return (
    <IconBase {...props}>
      <path d="M12 15.5A3.5 3.5 0 1 0 12 8.5a3.5 3.5 0 0 0 0 7Z" />
      <path d="M19.4 15a7.8 7.8 0 0 0 .1-2l2-1.3-2-3.4-2.3.7a7.9 7.9 0 0 0-1.7-1l-.3-2.4H9.8l-.3 2.4a7.9 7.9 0 0 0-1.7 1l-2.3-.7-2 3.4L5.5 13a7.8 7.8 0 0 0 .1 2L3.6 16.3l2 3.4 2.3-.7c.5.4 1.1.7 1.7 1l.3 2.4h4.4l.3-2.4c.6-.3 1.2-.6 1.7-1l2.3.7 2-3.4L19.4 15Z" />
    </IconBase>
  );
}

export function IconShield(props) {
  return (
    <IconBase {...props}>
      <path d="M12 2 20 6v7c0 5-3.4 9.4-8 11-4.6-1.6-8-6-8-11V6l8-4Z" />
      <path d="M9.5 12.5 11 14l3.5-4" />
    </IconBase>
  );
}

export function IconSearch(props) {
  return (
    <IconBase {...props}>
      <circle cx="11" cy="11" r="7" />
      <path d="M20 20l-3-3" />
    </IconBase>
  );
}

export function IconUpload(props) {
  return (
    <IconBase {...props}>
      <path d="M12 16V4" />
      <path d="M7 9l5-5 5 5" />
      <path d="M4 20h16" />
    </IconBase>
  );
}

export function IconDownload(props) {
  return (
    <IconBase {...props}>
      <path d="M12 4v12" />
      <path d="M7 11l5 5 5-5" />
      <path d="M4 20h16" />
    </IconBase>
  );
}

export function IconTrash(props) {
  return (
    <IconBase {...props}>
      <path d="M4 7h16" />
      <path d="M10 11v6" />
      <path d="M14 11v6" />
      <path d="M6 7l1 14h10l1-14" />
      <path d="M9 7V4h6v3" />
    </IconBase>
  );
}

export function IconCalendar(props) {
  return (
    <IconBase {...props}>
      <path d="M7 3v3" />
      <path d="M17 3v3" />
      <path d="M4 7h16" />
      <rect x="4" y="5" width="16" height="16" rx="2" />
    </IconBase>
  );
}
