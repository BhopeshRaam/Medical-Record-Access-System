import { useEffect, useState } from "react";
import Card from "../../components/Card";
import Input from "../../components/Input";
import Button from "../../components/Button";
import Chip from "../../components/Chip";
import { useAuth } from "../../auth/AuthContext";
import { searchAudit } from "../../api/audit";

export default function Audit() {
  const { user } = useAuth();
  const isAdmin = user?.roles?.includes("ADMIN");

  const [action, setAction] = useState("");
  const [status, setStatus] = useState("");
  const [page, setPage] = useState(0);
  const [data, setData] = useState({ items: [] });
  const [err, setErr] = useState("");

  async function load() {
    setErr("");
    try {
      const res = await searchAudit({ action: action || undefined, status: status || undefined, page, size: 20, sort: "timestamp,desc" });
      setData(res.data.data);
    } catch (e) {
      setErr(e?.response?.data?.message || "Failed to load audit logs");
    }
  }

  useEffect(() => { if (isAdmin) load(); }, [page]);

  if (!isAdmin) {
    return <Card className="p-6"><div className="font-extrabold text-slate-900">Audit Logs</div><div className="mt-2 text-sm text-slate-600">Admin only.</div></Card>;
  }

  return (
    <div className="space-y-4">
      <Card className="p-5">
        <div className="flex flex-col md:flex-row md:items-center justify-between gap-3">
          <div>
            <div className="font-extrabold text-slate-900">Audit Logs</div>
            <div className="text-xs text-slate-500">Filter by action or status.</div>
          </div>
          <div className="flex gap-2 w-full md:w-auto">
            <Input placeholder="Action (VIEW_RECORD)" value={action} onChange={(e)=>setAction(e.target.value)} />
            <Input placeholder="Status (SUCCESS)" value={status} onChange={(e)=>setStatus(e.target.value)} />
            <Button onClick={() => { setPage(0); load(); }}>Apply</Button>
          </div>
        </div>
        {err && <div className="mt-3 text-sm text-red-600">{err}</div>}
      </Card>

      <Card className="p-5">
        <div className="space-y-2">
          {data?.items?.map((a) => (
            <div key={a.id} className="rounded-3xl border border-slate-100 p-4 hover:bg-slate-50/60">
              <div className="flex items-center justify-between gap-3">
                <div className="font-semibold text-slate-900">{a.action} <span className="text-slate-400">•</span> {a.status}</div>
                <Chip tone={a.status === "SUCCESS" ? "green" : a.status === "DENIED" ? "red" : "slate"}>{a.status}</Chip>
              </div>
              <div className="mt-1 text-xs text-slate-500">actorId: {a.actorId || "-"} • {a.resourceType}:{a.resourceId || "-"}</div>
              <div className="mt-1 text-xs text-slate-500">{a.timestamp ? new Date(a.timestamp).toLocaleString() : "-"}</div>
            </div>
          ))}
          {!data?.items?.length && <div className="text-sm text-slate-500">No logs.</div>}
        </div>

        <div className="mt-4 flex items-center gap-2">
          <Button variant="secondary" disabled={!data?.hasPrevious} onClick={() => setPage((p) => Math.max(0, p - 1))}>Prev</Button>
          <Button variant="secondary" disabled={!data?.hasNext} onClick={() => setPage((p) => p + 1)}>Next</Button>
        </div>
      </Card>
    </div>
  );
}
