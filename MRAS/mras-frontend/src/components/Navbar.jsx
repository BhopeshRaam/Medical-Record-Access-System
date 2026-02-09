import { Link, NavLink, useLocation } from "react-router-dom";
import Button from "./Button";
import Chip from "./Chip";
import { useAuth } from "../auth/AuthContext";

function ScrollLink({ href, children }) {
  return (
    <a
      href={href}
      className="text-sm font-semibold text-slate-600 hover:text-slate-900 transition"
      onClick={(e) => {
        // Smooth-scroll for landing page anchors
        const id = href.startsWith("#") ? href.slice(1) : "";
        const el = id ? document.getElementById(id) : null;
        if (el) {
          e.preventDefault();
          el.scrollIntoView({ behavior: "smooth", block: "start" });
        }
      }}
    >
      {children}
    </a>
  );
}

export default function Navbar({ variant = "marketing" }) {
  const { user, logout } = useAuth();
  const loc = useLocation();
  const onLanding = loc.pathname === "/";

  const isAuthed = !!user;

  return (
    <header className={"sticky top-0 z-30 border-b border-slate-100 " + (variant === "dashboard" ? "bg-slate-50/80 backdrop-blur" : "bg-white/80 backdrop-blur")}>
      <div className="mx-auto max-w-6xl px-4 py-3 flex items-center justify-between gap-3">
        <Link to={isAuthed ? "/dashboard/home" : "/"} className="flex items-center gap-2 font-extrabold text-slate-900">
          <span className="h-9 w-9 rounded-2xl bg-blue-600 text-white grid place-items-center shadow-sm">M</span>
          <span className="tracking-tight">MRAS</span>
        </Link>

        {/* Middle nav */}
        <nav className="hidden md:flex items-center gap-6">
          {onLanding ? (
            <>
              <ScrollLink href="#features">Features</ScrollLink>
              <ScrollLink href="#workflow">Workflow</ScrollLink>
              <ScrollLink href="#security">Security</ScrollLink>
              <a
                className="text-sm font-semibold text-slate-600 hover:text-slate-900 transition"
                href="https://github.com/BhopeshRaam/Medical-Record-Access-System"
                target="_blank"
                rel="noreferrer"
                title="GitHub"
              >
                GitHub ↗
              </a>
            </>
          ) : (
            <>
              {isAuthed && (
                <>
                  <NavLink className={({isActive}) => "text-sm font-semibold transition " + (isActive ? "text-blue-700" : "text-slate-600 hover:text-slate-900")} to="/dashboard/home">Dashboard</NavLink>
                  <NavLink className={({isActive}) => "text-sm font-semibold transition " + (isActive ? "text-blue-700" : "text-slate-600 hover:text-slate-900")} to="/dashboard/patients">Patients</NavLink>
                  <NavLink className={({isActive}) => "text-sm font-semibold transition " + (isActive ? "text-blue-700" : "text-slate-600 hover:text-slate-900")} to="/dashboard/records">Records</NavLink>
                </>
              )}
            </>
          )}
        </nav>

        {/* Right actions */}
        <div className="flex items-center gap-2">
          {isAuthed ? (
            <>
              <div className="hidden sm:flex items-center gap-2">
                {user?.roles?.slice(0, 2)?.map((r) => (
                  <Chip key={r} tone={r === "ADMIN" ? "blue" : r === "DOCTOR" ? "green" : "slate"}>{r}</Chip>
                ))}
              </div>
              <Button variant="secondary" onClick={logout}>Log out</Button>
            </>
          ) : (
            <>
              <Link to="/login"><Button variant="ghost">Sign in</Button></Link>
              <Link to="/signup"><Button>Get started</Button></Link>
            </>
          )}
        </div>
      </div>
    </header>
  );
}
