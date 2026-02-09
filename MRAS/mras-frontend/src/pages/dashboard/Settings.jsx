import Card from "../../components/Card";
import Button from "../../components/Button";
import { useAuth } from "../../auth/AuthContext";
import { IconSettings, IconShield } from "../../components/Icons";

export default function Settings() {
  const { user, reload } = useAuth();
  return (
    <div className="space-y-4">
      <Card className="p-6">
        <div className="font-extrabold text-slate-900 flex items-center gap-2">
          <span className="h-9 w-9 rounded-2xl border border-blue-100 bg-blue-50 grid place-items-center text-blue-700"><IconSettings className="h-5 w-5" /></span>
          Profile & session
        </div>
        <div className="mt-2 text-sm text-slate-600">View your signed-in account details and refresh session data.</div>
        <div className="mt-4 text-sm">
          <div><span className="text-slate-500">Email:</span> <span className="font-semibold">{user?.email}</span></div>
          <div><span className="text-slate-500">Roles:</span> <span className="font-semibold">{user?.roles?.join(", ")}</span></div>
        </div>
        <div className="mt-6 flex items-start gap-3 rounded-3xl border border-slate-100 bg-slate-50/60 p-4">
          <div className="h-10 w-10 rounded-2xl border border-blue-100 bg-blue-50 grid place-items-center text-blue-700"><IconShield className="h-5 w-5" /></div>
          <div>
            <div className="font-semibold text-slate-900">Security</div>
            <div className="text-sm text-slate-600">MRAS uses role-based access controls and audited actions to protect sensitive data.</div>
          </div>
        </div>
        <div className="mt-4"><Button variant="secondary" onClick={reload}>Refresh session</Button></div>
      </Card>
    </div>
  );
}
