import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import Card from "../../components/Card";
import Button from "../../components/Button";
import Chip from "../../components/Chip";
import { getPatient } from "../../api/patients";
import { listRecordsByPatient } from "../../api/records";

export default function PatientDetail() {
  const { id } = useParams();
  const nav = useNavigate();
  const [patient, setPatient] = useState(null);
  const [records, setRecords] = useState({ items: [] });
  const [err, setErr] = useState("");
  const [page, setPage] = useState(0);

  async function load() {
    setErr("");
    try {
      const p = await getPatient(id);
      setPatient(p.data.data);
      const r = await listRecordsByPatient({ patientId: id, page, size: 10 });
      setRecords(r.data.data);
    } catch (e) {
      setErr(e?.response?.data?.message || "Failed to load patient");
    }
  }

  useEffect(() => { load(); }, [page]);

  return (
    <div className="space-y-4">
      {err && <Card className="p-5"><div className="text-sm text-red-600">{err}</div></Card>}

      <Card className="p-6">
        <div className="flex items-start justify-between gap-3">
          <div>
            <div className="text-2xl font-extrabold text-slate-900">{patient?.name}</div>
            <div className="mt-2 flex flex-wrap gap-2">
              <Chip tone="slate">MRN: {patient?.mrn}</Chip>
              <Chip tone="slate">Phone: {patient?.phone || "-"}</Chip>
              <Chip tone="slate">DOB: {patient?.dob || "-"}</Chip>
            </div>
          </div>
          <Button variant="secondary" onClick={() => nav(-1)}>Back</Button>
        </div>
      </Card>

      <Card className="p-6">
        <div className="font-extrabold text-slate-900">Records</div>
        <div className="mt-3 space-y-2">
          {records?.items?.map((r) => (
            <div key={r.id} className="rounded-3xl border border-slate-100 p-4 flex items-center justify-between gap-3 hover:bg-slate-50/60">
              <div>
                <div className="font-semibold text-slate-900">{r.chiefComplaint || "Visit"}</div>
                <div className="text-xs text-slate-500">{r.encounterDate ? new Date(r.encounterDate).toLocaleString() : "-"}</div>
              </div>
              <Button variant="secondary" onClick={() => nav(`/dashboard/records/${r.id}`)}>View</Button>
            </div>
          ))}
          {!records?.items?.length && <div className="text-sm text-slate-500 mt-3">No records yet.</div>}
        </div>

        <div className="mt-4 flex items-center gap-2">
          <Button variant="secondary" disabled={!records?.hasPrevious} onClick={() => setPage((p) => Math.max(0, p - 1))}>Prev</Button>
          <Button variant="secondary" disabled={!records?.hasNext} onClick={() => setPage((p) => p + 1)}>Next</Button>
        </div>
      </Card>
    </div>
  );
}
