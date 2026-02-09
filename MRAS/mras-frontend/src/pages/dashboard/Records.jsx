import { useEffect, useMemo, useState } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import Card from "../../components/Card";
import Button from "../../components/Button";
import Input from "../../components/Input";
import Chip from "../../components/Chip";
import { listRecordsByPatient } from "../../api/records";
import { IconRecords, IconSearch } from "../../components/Icons";

export default function Records() {
  const nav = useNavigate();
  const [sp] = useSearchParams();
  const patientId = sp.get("patientId");

  const [page, setPage] = useState(0);
  const [records, setRecords] = useState({ items: [] });
  const [err, setErr] = useState("");
  const [pid, setPid] = useState(patientId || "");

  const effectivePatientId = useMemo(() => patientId || pid || "", [patientId, pid]);

  async function load() {
    setErr("");
    if (!effectivePatientId) return;
    try {
      const r = await listRecordsByPatient({ patientId: effectivePatientId, page, size: 10 });
      setRecords(r.data.data);
    } catch (e) {
      setErr(e?.response?.data?.message || "Failed to load records");
    }
  }

  useEffect(() => { load(); }, [page, effectivePatientId]);

  return (
    <div className="space-y-4">
      <Card className="p-5">
        <div className="flex flex-col md:flex-row md:items-center gap-3 justify-between">
          <div>
            <div className="font-extrabold text-slate-900 flex items-center gap-2">
              <span className="h-9 w-9 rounded-2xl border border-blue-100 bg-blue-50 grid place-items-center text-blue-700"><IconRecords className="h-5 w-5" /></span>
              Records
            </div>
            <div className="text-xs text-slate-500">Select a patient from Patients, or enter a Patient ID to load encounter history.</div>
          </div>
          <div className="flex gap-2 w-full md:w-auto">
            <Input leftIcon={IconSearch} placeholder="Patient ID" value={pid} onChange={(e)=>setPid(e.target.value)} />
            <Button onClick={() => { setPage(0); load(); }}>Load</Button>
          </div>
        </div>
        {effectivePatientId && <div className="mt-3"><Chip tone="slate">patientId: {effectivePatientId}</Chip></div>}
        {err && <div className="mt-3 text-sm text-red-600">{err}</div>}
      </Card>

      <Card className="p-5">
        <div className="font-extrabold text-slate-900 mb-3">List</div>
        <div className="space-y-2">
          {records?.items?.map((r) => (
            <div key={r.id} className="rounded-3xl border border-slate-100 p-4 flex items-center justify-between gap-3 hover:bg-slate-50/60">
              <div>
                <div className="font-semibold text-slate-900">{r.chiefComplaint || "Visit"}</div>
                <div className="text-xs text-slate-500">{r.encounterDate ? new Date(r.encounterDate).toLocaleString() : "-"}</div>
              </div>
              <Button variant="secondary" onClick={() => nav(`/dashboard/records/${r.id}`)}>View</Button>
            </div>
          ))}
          {!records?.items?.length && <div className="text-sm text-slate-500">No records loaded.</div>}
        </div>

        <div className="mt-4 flex items-center gap-2">
          <Button variant="secondary" disabled={!records?.hasPrevious} onClick={() => setPage((p) => Math.max(0, p - 1))}>Prev</Button>
          <Button variant="secondary" disabled={!records?.hasNext} onClick={() => setPage((p) => p + 1)}>Next</Button>
        </div>
      </Card>
    </div>
  );
}
