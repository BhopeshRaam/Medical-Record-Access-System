import { useMemo, useState } from "react";
import Card from "../../components/Card";
import Button from "../../components/Button";
import Input from "../../components/Input";
import Select from "../../components/Select";
import Chip from "../../components/Chip";
import { useAuth } from "../../auth/AuthContext";
import { registerApi } from "../../api/auth";
import { IconUsers, IconShield } from "../../components/Icons";

const ROLE_OPTIONS = ["ADMIN", "DOCTOR", "NURSE", "RECEPTIONIST", "PATIENT"];

export default function AdminUsers() {
  const { user } = useAuth();
  const isAdmin = user?.roles?.includes("ADMIN");

  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [role, setRole] = useState("DOCTOR");
  const [err, setErr] = useState("");
  const [msg, setMsg] = useState("");

  const roleHint = useMemo(() => {
    if (role === "ADMIN") return "Full access including audit logs and account provisioning.";
    if (role === "DOCTOR") return "Can create/update own records and manage attachments they authored.";
    if (role === "NURSE") return "Can view patients and records, but cannot edit records.";
    if (role === "RECEPTIONIST") return "Can create/update patient profiles.";
    return "Can view only their own profile and records.";
  }, [role]);

  async function onCreate(e) {
    e.preventDefault();
    setErr("");
    setMsg("");

    try {
      await registerApi({ name, email, password, roles: [role] });
      setMsg("Account created successfully.");
      setName("");
      setEmail("");
      setPassword("");
      setRole("DOCTOR");
    } catch (e2) {
      setErr(e2?.response?.data?.message || "Failed to create account");
    }
  }

  if (!isAdmin) {
    return (
      <Card className="p-6">
        <div className="font-extrabold text-slate-900 flex items-center gap-2">
          <span className="h-9 w-9 rounded-2xl border border-blue-100 bg-blue-50 grid place-items-center text-blue-700">
            <IconUsers className="h-5 w-5" />
          </span>
          User provisioning
        </div>
        <div className="mt-2 text-sm text-slate-600">Admin only.</div>
      </Card>
    );
  }

  return (
    <div className="space-y-4">
      <Card className="p-5">
        <div className="font-extrabold text-slate-900 flex items-center gap-2">
          <span className="h-9 w-9 rounded-2xl border border-blue-100 bg-blue-50 grid place-items-center text-blue-700">
            <IconUsers className="h-5 w-5" />
          </span>
          Create user account
        </div>
        <div className="mt-2 text-sm text-slate-600">
          Create staff/patient logins using the <span className="font-semibold">/api/auth/register</span> endpoint.
        </div>
      </Card>

      <Card className="p-6">
        <form onSubmit={onCreate} className="space-y-3">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-2">
            <Input placeholder="Full name" value={name} onChange={(e) => setName(e.target.value)} required />
            <Input placeholder="Email" value={email} onChange={(e) => setEmail(e.target.value)} required />
            <Input placeholder="Temporary password" type="password" value={password} onChange={(e) => setPassword(e.target.value)} required />
            <Select value={role} onChange={(e) => setRole(e.target.value)}>
              {ROLE_OPTIONS.map((r) => (
                <option key={r} value={r}>{r}</option>
              ))}
            </Select>
          </div>

          <div className="flex items-center gap-2 flex-wrap">
            <span className="text-xs text-slate-500">Role:</span>
            <Chip tone={role === "ADMIN" ? "blue" : role === "DOCTOR" ? "green" : "slate"}>{role}</Chip>
            <span className="text-xs text-slate-500">•</span>
            <span className="text-xs text-slate-600">{roleHint}</span>
          </div>

          {err && <div className="text-sm text-red-600">{err}</div>}
          {msg && <div className="text-sm text-blue-700">{msg}</div>}

          <div className="flex items-center gap-2">
            <Button type="submit">Create account</Button>
            <Button
              type="button"
              variant="secondary"
              onClick={() => {
                setName(""); setEmail(""); setPassword(""); setRole("DOCTOR"); setErr(""); setMsg("");
              }}
            >
              Clear
            </Button>
          </div>

          <div className="mt-3 flex items-start gap-3 rounded-3xl border border-slate-100 bg-slate-50/60 p-4">
            <div className="h-10 w-10 rounded-2xl border border-blue-100 bg-blue-50 grid place-items-center text-blue-700">
              <IconShield className="h-5 w-5" />
            </div>
            <div className="text-sm text-slate-700">
              Use strong passwords and rotate them after first login. All authentication events are captured in audit logs.
            </div>
          </div>
        </form>
      </Card>
    </div>
  );
}
