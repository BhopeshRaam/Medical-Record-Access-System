import { NavLink } from "react-router-dom";
import Card from "./Card";
import { IconAudit, IconHome, IconPatients, IconRecords, IconSettings, IconShield, IconUsers } from "./Icons";

function Item({ to, label, icon: Icon }) {
  return (
    <NavLink
      to={to}
      className={({ isActive }) =>
        "group flex items-center gap-3 rounded-2xl px-3 py-2.5 text-sm font-semibold transition focus:outline-none focus:ring-2 focus:ring-blue-200 " +
        (isActive
          ? "bg-blue-50 text-blue-700 border border-blue-100"
          : "text-slate-700 hover:bg-slate-100 border border-transparent")
      }
    >
      <span className="h-9 w-9 rounded-2xl grid place-items-center border border-slate-200 bg-white group-hover:bg-slate-50 group-hover:border-slate-200">
        <Icon className="h-5 w-5" />
      </span>
      {label}
    </NavLink>
  );
}

export default function Sidebar({ user }) {
  const isAdmin = user?.roles?.includes("ADMIN");
  const isDoctor = user?.roles?.includes("DOCTOR");

  return (
    <aside className="hidden md:block w-72">
      <div className="sticky top-4">
        <Card className="p-4">
          <div className="flex items-center gap-3">
            <div className="relative h-12 w-12 rounded-3xl bg-gradient-to-br from-blue-600 to-blue-400 grid place-items-center text-white font-extrabold shadow-sm overflow-hidden">
              <div className="absolute -inset-2 rounded-3xl bg-blue-200/30 blur-2xl" aria-hidden="true" />
              <span className="relative z-10">{(user?.name || user?.email || "U")[0]?.toUpperCase()}</span>
            </div>
            <div className="min-w-0">
              <div className="font-extrabold text-slate-900 truncate">{user?.name || "User"}</div>
              <div className="text-xs text-slate-500 truncate">{user?.email}</div>
            </div>
          </div>

          <div className="mt-4 flex flex-col gap-1">
            <Item to="/dashboard/home" label="Home" icon={IconHome} />
            <Item to="/dashboard/patients" label={isDoctor ? "My Patients" : "Patients"} icon={IconPatients} />
            <Item to="/dashboard/records" label="Records" icon={IconRecords} />
            {isAdmin && <Item to="/dashboard/users" label="Users" icon={IconUsers} />}
            {isAdmin && <Item to="/dashboard/audit" label="Audit Logs" icon={IconAudit} />}
            <Item to="/dashboard/settings" label="Settings" icon={IconSettings} />
          </div>
        </Card>

        <Card className="mt-4 p-4 bg-gradient-to-br from-blue-50 to-white">
          <div className="flex items-start gap-3">
            <div className="h-10 w-10 rounded-2xl border border-blue-100 bg-blue-50 grid place-items-center text-blue-700">
              <IconShield className="h-5 w-5" />
            </div>
            <div>
              <div className="text-sm font-extrabold text-slate-900">Security-first</div>
              <div className="mt-1 text-xs text-slate-600">
                Role-based access control with searchable audit logs.
              </div>
            </div>
          </div>
        </Card>
      </div>
    </aside>
  );
}
