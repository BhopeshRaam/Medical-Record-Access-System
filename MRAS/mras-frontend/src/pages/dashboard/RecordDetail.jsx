import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import Card from "../../components/Card";
import Button from "../../components/Button";
import Chip from "../../components/Chip";
import Modal from "../../components/Modal";
import { getRecord, uploadRecordFile, deleteRecordFile, fileDownloadUrl } from "../../api/records";
import { useAuth } from "../../auth/AuthContext";
import { IconDownload, IconTrash, IconUpload } from "../../components/Icons";

export default function RecordDetail() {
  const { id } = useParams();
  const nav = useNavigate();
  const { user } = useAuth();
  const canManageFiles = user?.roles?.some((r) => r === "ADMIN" || r === "DOCTOR");

  const [record, setRecord] = useState(null);
  const [err, setErr] = useState("");
  const [file, setFile] = useState(null);
  const [confirm, setConfirm] = useState({ open: false, fileId: null, filename: "" });

  async function load() {
    setErr("");
    try {
      const res = await getRecord(id);
      setRecord(res.data.data);
    } catch (e) {
      setErr(e?.response?.data?.message || "Failed to load record");
    }
  }

  useEffect(() => { load(); }, [id]);

  async function onUpload() {
    if (!file) return;
    setErr("");
    try {
      await uploadRecordFile(id, file);
      setFile(null);
      await load();
    } catch (e) {
      setErr(e?.response?.data?.message || "Upload failed (doctor must be author or admin).");
    }
  }

  async function onDeleteConfirmed() {
    setErr("");
    try {
      await deleteRecordFile(id, confirm.fileId);
      setConfirm({ open: false, fileId: null, filename: "" });
      await load();
    } catch (e) {
      setErr(e?.response?.data?.message || "Delete failed");
    }
  }

  return (
    <div className="space-y-4">
      {err && <Card className="p-5"><div className="text-sm text-red-600">{err}</div></Card>}

      <Card className="p-6">
        <div className="flex items-start justify-between gap-3">
          <div>
            <div className="text-2xl font-extrabold text-slate-900">{record?.chiefComplaint || "Record"}</div>
            <div className="mt-2 flex flex-wrap gap-2">
              <Chip tone="slate">recordId: {record?.id}</Chip>
              <Chip tone="slate">patientId: {record?.patientId}</Chip>
              <Chip tone="slate">authorId: {record?.authorId}</Chip>
            </div>
          </div>
          <Button variant="secondary" onClick={() => nav(-1)}>Back</Button>
        </div>
        <div className="mt-4 grid md:grid-cols-2 gap-4">
          <div className="rounded-3xl border border-slate-100 p-4 bg-slate-50/40">
            <div className="text-xs font-extrabold text-slate-500">Diagnosis</div>
            <div className="mt-2 text-sm text-slate-800">{(record?.diagnosis || []).join(", ") || "-"}</div>
          </div>
          <div className="rounded-3xl border border-slate-100 p-4 bg-slate-50/40">
            <div className="text-xs font-extrabold text-slate-500">Notes</div>
            <div className="mt-2 text-sm text-slate-800 whitespace-pre-wrap">{record?.notes || "-"}</div>
          </div>
        </div>
      </Card>

      <Card className="p-6">
        <div className="flex items-center justify-between gap-3 flex-wrap">
          <div>
            <div className="font-extrabold text-slate-900">Attachments</div>
            <div className="text-xs text-slate-500">Upload/download PDFs (GridFS).</div>
          </div>
          {canManageFiles && (
            <div className="flex items-center gap-2">
              <label className="inline-flex items-center gap-2 rounded-2xl border border-slate-200 bg-white px-3 py-2 text-sm font-semibold text-slate-700 hover:bg-slate-50 cursor-pointer">
                Choose file
                <input className="sr-only" type="file" onChange={(e) => setFile(e.target.files?.[0] || null)} />
              </label>
              <Button leftIcon={IconUpload} onClick={onUpload} disabled={!file}>Upload</Button>
            </div>
          )}
        </div>

        <div className="mt-4 space-y-2">
          {(record?.attachments || []).map((a) => (
            <div key={a.fileId} className="rounded-3xl border border-slate-100 p-4 flex items-center justify-between gap-3 hover:bg-slate-50/60">
              <div className="min-w-0">
                <div className="font-semibold text-slate-900 truncate">{a.filename}</div>
                <div className="text-xs text-slate-500">{a.mimeType} • {a.sizeBytes} bytes</div>
              </div>
              <div className="flex items-center gap-2">
                <a
                  className="inline-flex items-center gap-2 text-sm font-semibold text-blue-700 hover:underline"
                  href={fileDownloadUrl(a.fileId)}
                  target="_blank"
                  rel="noreferrer"
                >
                  <IconDownload className="h-4 w-4" />
                  Download
                </a>
                {canManageFiles && (
                  <Button leftIcon={IconTrash} variant="danger" onClick={() => setConfirm({ open: true, fileId: a.fileId, filename: a.filename })}>Delete</Button>
                )}
              </div>
            </div>
          ))}
          {(!record?.attachments || record.attachments.length === 0) && (
            <div className="text-sm text-slate-500">No files yet.</div>
          )}
        </div>
      </Card>

      <Modal
        open={confirm.open}
        title="Delete attachment?"
        onClose={() => setConfirm({ open: false, fileId: null, filename: "" })}
        footer={
          <div className="flex justify-end gap-2">
            <Button variant="secondary" onClick={() => setConfirm({ open: false, fileId: null, filename: "" })}>Cancel</Button>
            <Button variant="danger" onClick={onDeleteConfirmed}>Delete</Button>
          </div>
        }
      >
        <div className="text-sm text-slate-700">
          This will remove <span className="font-semibold">{confirm.filename}</span> from the record and delete it from GridFS.
        </div>
      </Modal>
    </div>
  );
}
