import { useMemo } from "react";
import { useAuth } from "../auth/AuthContext";
import Chip from "./Chip";
import Button from "./Button";
import { IconCalendar } from "./Icons";

function todayStr() {
  const d = new Date();
  return d.toISOString().slice(0, 10);
}

export default function Topbar({ title }) {
  const { user, logout } = useAuth();
  const date = useMemo(() => todayStr(), []);

  return (
    <div className="flex items-center justify-between gap-3">
      <div>
        <div className="text-sm font-semibold text-slate-500">{title}</div>
        <div className="text-2xl font-extrabold text-slate-900">{user?.name || "Dashboard"}</div>
      </div>

      <div className="flex items-center gap-2 flex-wrap justify-end">
        <Chip tone="slate"><span className="inline-flex items-center gap-2"><IconCalendar className="h-4 w-4" />{date}</span></Chip>
        {user?.roles?.map((r) => (
          <Chip key={r} tone={r === "ADMIN" ? "blue" : r === "DOCTOR" ? "green" : "slate"}>{r}</Chip>
        ))}
        <Button variant="secondary" onClick={logout}>Log out</Button>
      </div>
    </div>
  );
}
