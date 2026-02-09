import { useEffect, useMemo, useState } from "react";
import Card from "../../components/Card";
import StatCard from "../../components/StatCard";
import { useAuth } from "../../auth/AuthContext";
import { listPatients, myPatient } from "../../api/patients";
import { motion } from "framer-motion";
import { IconAudit, IconCalendar, IconDownload, IconPatients, IconRecords, IconShield } from "../../components/Icons";

export default function Home() {
  const { user } = useAuth();
  const isPatient = user?.roles?.includes("PATIENT");
  const isDoctor = user?.roles?.includes("DOCTOR");
  const isAdmin = user?.roles?.includes("ADMIN");

  const [patientCount, setPatientCount] = useState("—");
  const [welcomeHint, setWelcomeHint] = useState("");
  const today = useMemo(() => new Date().toISOString().slice(0, 10), []);

  useEffect(() => {
    (async () => {
      try {
        if (isPatient) {
          await myPatient();
          setWelcomeHint("You can view your records and downloads securely.");
          setPatientCount("1");
        } else {
          const res = await listPatients({ page: 0, size: 1 });
          setPatientCount(String(res.data.data.totalElements ?? "—"));
          setWelcomeHint(
            isDoctor
              ? "Find patients you can access, review encounters, and download attachments when needed."
              : "Manage patients and monitor access with searchable audit logs."
          );
        }
      } catch {
        setWelcomeHint("Unable to load summary data. Check your login role or backend connection.");
      }
    })();
  }, [isPatient, isDoctor, isAdmin]);

  return (
    <>
      <motion.div
        initial={{ opacity: 0, y: 10 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.22 }}
        className="relative overflow-hidden rounded-[2rem] border border-slate-100 bg-gradient-to-r from-blue-600 to-blue-400 text-white p-6 shadow-sm"
      >
        <div className="absolute -right-10 -top-10 h-48 w-48 rounded-full bg-white/15 blur-3xl" aria-hidden="true" />
        <div className="absolute -left-10 -bottom-10 h-48 w-48 rounded-full bg-white/10 blur-3xl" aria-hidden="true" />

        <div className="flex items-start justify-between gap-3">
          <div>
            <div className="text-sm font-semibold opacity-90 flex items-center gap-2">
              <IconShield className="h-5 w-5 opacity-90" />
              MRAS Dashboard
            </div>
            <div className="mt-2 text-3xl font-extrabold">Welcome, {user?.name || "User"}.</div>
            <div className="mt-2 text-sm opacity-90 max-w-3xl">{welcomeHint}</div>
          </div>
          <div className="hidden sm:flex items-center gap-2 rounded-2xl bg-white/10 border border-white/20 px-3 py-2 text-xs font-semibold">
            <IconCalendar className="h-4 w-4" />
            {today}
          </div>
        </div>
      </motion.div>

      <div className="grid md:grid-cols-4 gap-3">
        <StatCard title={isPatient ? "Your Profile" : "Patients"} value={patientCount} icon={IconPatients} hint="Visible based on your role" />
        <StatCard title="Records" value="—" icon={IconRecords} hint="Available per patient" />
        <StatCard title="Attachments" value="—" icon={IconDownload} hint="PDFs & scans (GridFS)" />
        <StatCard title="Audit" value={isAdmin ? "Enabled" : "—"} icon={IconAudit} hint={isAdmin ? "Admin-only" : "Restricted"} />
      </div>

      <div className="grid md:grid-cols-2 gap-4">
        <Card className="p-6">
          <div className="font-extrabold text-slate-900">Quick actions</div>
          <div className="mt-2 text-sm text-slate-600">
            {isDoctor && "Open My Patients, pick a patient, and review encounter records & attachments."}
            {isAdmin && "Manage staff accounts, create patients, and monitor access via Audit Logs."}
            {isPatient && "View your profile, records, and download attached documents securely."}
            {!isDoctor && !isAdmin && !isPatient && "Use Patients and Records as per your role permissions."}
          </div>
        </Card>

        <Card className="p-6">
          <div className="font-extrabold text-slate-900">Security status</div>
          <div className="mt-2 text-sm text-slate-600">
            Access is governed by roles and ownership checks. Every sensitive operation can be audited by admins.
          </div>
        </Card>
      </div>
    </>
  );
}
