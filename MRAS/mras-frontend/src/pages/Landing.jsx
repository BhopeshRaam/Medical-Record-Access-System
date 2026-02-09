import { Link } from "react-router-dom";
import { motion } from "framer-motion";
import Button from "../components/Button";
import Navbar from "../components/Navbar";
import Footer from "../components/Footer";

function HeroIllustration() {
  return (
    <svg viewBox="0 0 600 420" className="w-full h-auto">
      <defs>
        <linearGradient id="g" x1="0" x2="1">
          <stop offset="0" stopColor="#60a5fa" stopOpacity="0.25" />
          <stop offset="1" stopColor="#2563eb" stopOpacity="0.15" />
        </linearGradient>
      </defs>
      <rect x="30" y="40" width="540" height="320" rx="32" fill="url(#g)" stroke="#e2e8f0"/>
      <rect x="70" y="90" width="240" height="22" rx="11" fill="#93c5fd"/>
      <rect x="70" y="130" width="320" height="14" rx="7" fill="#cbd5e1"/>
      <rect x="70" y="156" width="280" height="14" rx="7" fill="#cbd5e1"/>
      <rect x="70" y="182" width="300" height="14" rx="7" fill="#cbd5e1"/>
      <rect x="70" y="230" width="200" height="96" rx="20" fill="#ffffff" stroke="#e2e8f0"/>
      <rect x="290" y="230" width="240" height="96" rx="20" fill="#ffffff" stroke="#e2e8f0"/>
      <circle cx="130" cy="270" r="18" fill="#60a5fa"/>
      <rect x="160" y="258" width="90" height="10" rx="5" fill="#cbd5e1"/>
      <rect x="160" y="276" width="60" height="10" rx="5" fill="#cbd5e1"/>
      <circle cx="350" cy="270" r="18" fill="#2563eb"/>
      <rect x="380" y="258" width="110" height="10" rx="5" fill="#cbd5e1"/>
      <rect x="380" y="276" width="70" height="10" rx="5" fill="#cbd5e1"/>
    </svg>
  );
}

export default function Landing() {
  return (
    <div className="min-h-screen bg-white">
      <Navbar variant="marketing" />

      <main className="mx-auto max-w-6xl px-4">
        <section className="py-14 grid lg:grid-cols-2 gap-10 items-center">
          <motion.div initial={{ opacity: 0, y: 14 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.45 }}>
            <div className="inline-flex items-center gap-2 rounded-full border border-blue-100 bg-blue-50 px-3 py-1 text-xs font-semibold text-blue-700">
              Secure • Audited • Role-based Access
            </div>
            <h1 className="mt-4 text-5xl font-extrabold tracking-tight text-slate-900 leading-tight">
              Calm UI for a serious job.
            </h1>
            <p className="mt-4 text-slate-600 text-lg max-w-xl">
              MRAS helps hospitals manage secure access to medical records and documents with strict RBAC, audit trails, and secure file handling.
            </p>
            <div className="mt-6 flex items-center gap-3">
              <Link to="/login"><Button>Launch App</Button></Link>
              <a href="#features"><Button variant="secondary">Explore features</Button></a>
            </div>

            <div className="mt-8 grid grid-cols-3 gap-3 text-xs font-semibold text-slate-600">
              <div className="rounded-2xl border border-slate-100 bg-slate-50 p-3">JWT Auth</div>
              <div className="rounded-2xl border border-slate-100 bg-slate-50 p-3">GridFS Files</div>
              <div className="rounded-2xl border border-slate-100 bg-slate-50 p-3">Audit Trail</div>
            </div>
          </motion.div>

          <motion.div initial={{ opacity: 0, y: 14 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.45, delay: 0.05 }}>
            <div className="relative">
              <div className="absolute -inset-6 rounded-full bg-blue-200/30 blur-3xl" />
              <div className="relative rounded-[2rem] border border-slate-100 shadow-sm p-4 bg-gradient-to-b from-blue-50 to-white">
                <HeroIllustration />
              </div>
            </div>
          </motion.div>
        </section>

        <section id="features" className="py-10 scroll-mt-28">
          <div className="text-center">
            <div className="text-xs font-extrabold tracking-widest text-blue-600">FEATURES</div>
            <div className="mt-2 text-3xl font-extrabold text-slate-900">Built for real workflows</div>
            <div className="mt-2 text-slate-600">Fast navigation, calm visuals, strict access controls.</div>
          </div>

          <div className="mt-8 grid md:grid-cols-3 gap-4">
            {[
              ["RBAC", "Admin/Doctor/Nurse/Receptionist/Patient roles with strict guards."],
              ["Audit logs", "Who accessed what and when — searchable by admin."],
              ["Secure files", "Upload/download PDFs linked to records with access checks."],
            ].map(([t, d]) => (
              <div key={t} className="rounded-[2rem] border border-slate-100 bg-white shadow-sm p-6">
                <div className="font-extrabold text-slate-900">{t}</div>
                <div className="mt-2 text-sm text-slate-600">{d}</div>
              </div>
            ))}
          </div>
        </section>

        <section id="workflow" className="py-10 scroll-mt-28">
          <div className="rounded-[2rem] border border-slate-100 bg-gradient-to-r from-blue-600 to-blue-400 text-white p-8 shadow-sm">
            <div className="text-sm font-semibold opacity-90">Dashboard workflow</div>
            <div className="mt-2 text-3xl font-extrabold">Sidebar + cards layout</div>
            <div className="mt-2 text-sm opacity-90 max-w-2xl">
              Doctors see patient info quickly, admins manage staff and audit logs, and patients can view only their own data.
            </div>
          </div>
        </section>

        <section id="security" className="py-10 scroll-mt-28">
          <div className="rounded-[2rem] border border-blue-100 bg-blue-50 p-8">
            <div className="text-sm font-extrabold text-slate-900">Security note</div>
            <div className="mt-2 text-sm text-slate-700">
              Records are structured data. PDFs can be attached and every access (view, download, export) is audited.
            </div>
          </div>
        </section>
      </main>

      <Footer />
    </div>
  );
}
