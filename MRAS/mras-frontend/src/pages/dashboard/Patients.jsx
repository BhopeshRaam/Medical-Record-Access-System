import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import Card from "../../components/Card";
import Input from "../../components/Input";
import Button from "../../components/Button";
import Chip from "../../components/Chip";
import { useAuth } from "../../auth/AuthContext";
import { listPatients, createPatient, myPatient } from "../../api/patients";
import Modal from "../../components/Modal";
import Select from "../../components/Select";
import { IconPatients, IconSearch } from "../../components/Icons";

export default function Patients() {
  const { user } = useAuth();
  const nav = useNavigate();
  const isPatient = user?.roles?.includes("PATIENT");
  const canCreate = user?.roles?.some((r) => r === "ADMIN" || r === "RECEPTIONIST");

  const [q, setQ] = useState("");
  const [page, setPage] = useState(0);
  const [data, setData] = useState({ items: [] });
  const [err, setErr] = useState("");

  const [mrn, setMrn] = useState("");
  const [name, setName] = useState("");
  const [dob, setDob] = useState("");
  const [gender, setGender] = useState("F");
  const [phone, setPhone] = useState("");

  const [selfModal, setSelfModal] = useState(false);
  const [selfPatient, setSelfPatient] = useState(null);

  async function load() {
    setErr("");
    try {
      if (isPatient) {
        const res = await myPatient();
        setSelfPatient(res.data.data);
        setData({ items: [res.data.data], hasNext: false, hasPrevious: false, page: 0, totalPages: 1 });
        return;
      }
      const res = await listPatients({ q: q || undefined, page, size: 10, sort: "createdAt,desc" });
      setData(res.data.data);
    } catch (e) {
      setErr(e?.response?.data?.message || "Failed to load patients");
    }
  }

  useEffect(() => { load(); }, [page]);

  async function onSearch() { setPage(0); await load(); }

  async function onCreate() {
    setErr("");
    try {
      await createPatient({ mrn, name, dob, gender, phone });
      setMrn(""); setName(""); setDob(""); setPhone("");
      await load();
    } catch (e) {
      setErr(e?.response?.data?.message || "Create failed");
    }
  }

  return (
    <div className="space-y-4">
      <Card className="p-5">
        <div className="flex flex-col md:flex-row md:items-center gap-3 justify-between">
          <div>
            <div className="font-extrabold text-slate-900 flex items-center gap-2">
              <span className="h-9 w-9 rounded-2xl border border-blue-100 bg-blue-50 grid place-items-center text-blue-700"><IconPatients className="h-5 w-5" /></span>
              {isPatient ? "My Profile" : "Patients"}
            </div>
            <div className="text-xs text-slate-500">Doctors: view info. Admin/Reception: manage.</div>
          </div>

          {!isPatient && (
            <div className="flex gap-2 w-full md:w-auto">
              <Input leftIcon={IconSearch} placeholder="Search name / MRN / phone" value={q} onChange={(e)=>setQ(e.target.value)} />
              <Button onClick={onSearch}>Search</Button>
            </div>
          )}
        </div>
        {err && <div className="mt-3 text-sm text-red-600">{err}</div>}
      </Card>

      {canCreate && (
        <Card className="p-5">
          <div className="font-extrabold text-slate-900">Create Patient</div>
          <div className="mt-3 grid grid-cols-1 md:grid-cols-2 gap-2">
            <Input placeholder="MRN" value={mrn} onChange={(e)=>setMrn(e.target.value)} />
            <Input placeholder="Name" value={name} onChange={(e)=>setName(e.target.value)} />
            <Input type="date" placeholder="DOB" value={dob} onChange={(e)=>setDob(e.target.value)} />
            <Select value={gender} onChange={(e)=>setGender(e.target.value)}>
              <option value="F">Female (F)</option>
              <option value="M">Male (M)</option>
              <option value="O">Other (O)</option>
              <option value="U">Undisclosed (U)</option>
            </Select>
            <Input placeholder="Phone" value={phone} onChange={(e)=>setPhone(e.target.value)} />
          </div>
          <div className="mt-3"><Button onClick={onCreate}>Create</Button></div>
        </Card>
      )}

      <Card className="p-5">
        <div className="font-extrabold text-slate-900 mb-3">{isPatient ? "Details" : "List"}</div>
        <div className="overflow-x-auto">
          <table className="min-w-full text-sm">
            <thead className="text-xs text-slate-500">
              <tr className="border-b border-slate-100">
                <th className="text-left py-2 pr-4">Name</th>
                <th className="text-left py-2 pr-4">MRN</th>
                <th className="text-left py-2 pr-4">Phone</th>
                <th className="text-left py-2 pr-4">DOB</th>
                <th className="text-left py-2 pr-4">Actions</th>
              </tr>
            </thead>
            <tbody>
              {data?.items?.map((p) => (
                <tr key={p.id} className="border-b border-slate-100 hover:bg-slate-50/60">
                  <td className="py-3 pr-4 font-semibold text-slate-900">{p.name}</td>
                  <td className="py-3 pr-4"><Chip tone="slate">{p.mrn}</Chip></td>
                  <td className="py-3 pr-4">{p.phone || "-"}</td>
                  <td className="py-3 pr-4">{p.dob || "-"}</td>
                  <td className="py-3 pr-4">
                    <div className="flex gap-2">
                      <Button variant="secondary" onClick={() => (isPatient ? setSelfModal(true) : nav(`/dashboard/patients/${p.id}`))}>
                        View
                      </Button>
                      {!isPatient && (
                        <Button variant="ghost" onClick={() => nav(`/dashboard/records?patientId=${p.id}`)}>
                          Records
                        </Button>
                      )}
                    </div>
                  </td>
                </tr>
              ))}
              {!data?.items?.length && (
                <tr><td className="py-6 text-slate-500" colSpan={5}>No patients found.</td></tr>
              )}
            </tbody>
          </table>
        </div>

        {!isPatient && (
          <div className="mt-4 flex items-center gap-2">
            <Button variant="secondary" disabled={!data?.hasPrevious} onClick={() => setPage((p) => Math.max(0, p - 1))}>Prev</Button>
            <Button variant="secondary" disabled={!data?.hasNext} onClick={() => setPage((p) => p + 1)}>Next</Button>
          </div>
        )}
      </Card>

      <Modal
        open={selfModal}
        title="My Patient Profile"
        onClose={() => setSelfModal(false)}
        footer={<Button onClick={() => setSelfModal(false)}>OK</Button>}
      >
        <div className="space-y-2 text-sm">
          <div><span className="text-slate-500">Name:</span> <span className="font-semibold">{selfPatient?.name}</span></div>
          <div><span className="text-slate-500">MRN:</span> <span className="font-semibold">{selfPatient?.mrn}</span></div>
          <div><span className="text-slate-500">Phone:</span> <span className="font-semibold">{selfPatient?.phone || "-"}</span></div>
          <div><span className="text-slate-500">DOB:</span> <span className="font-semibold">{selfPatient?.dob || "-"}</span></div>
        </div>
      </Modal>
    </div>
  );
}
