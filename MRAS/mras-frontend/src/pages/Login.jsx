import { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { motion } from "framer-motion";
import Navbar from "../components/Navbar";
import Footer from "../components/Footer";
import Input from "../components/Input";
import Button from "../components/Button";
import Card from "../components/Card";
import { useAuth } from "../auth/AuthContext";
import { IconAudit, IconLogo, IconShield } from "../components/Icons";

export default function Login() {
  const { login } = useAuth();
  const nav = useNavigate();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [err, setErr] = useState("");

  async function onSubmit(e) {
    e.preventDefault();
    setErr("");
    try {
      await login(email, password);
      nav("/dashboard/home");
    } catch (e2) {
      setErr(e2?.response?.data?.message || "Login failed");
    }
  }

  return (
    <div className="min-h-screen bg-slate-100">
      <Navbar />
      <div>
        <div className="mx-auto max-w-6xl px-4 py-16 grid md:grid-cols-2 gap-10 items-center">
          <motion.div
            initial={{ opacity: 0, y: 12 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.35 }}
          >
            <div className="flex items-center gap-3">
              <IconLogo className="h-11 w-11" />
              <div>
                <div className="text-2xl font-extrabold text-slate-900">
                  MRAS
                </div>
                <div className="text-sm font-semibold text-slate-500">
                  Medical Record Access System
                </div>
              </div>
            </div>
            <div className="mt-5 text-slate-600">
              Secure access to patient records with strict RBAC, audited
              downloads, and a secure attachment vault.
            </div>
            <div className="mt-6 grid gap-3">
              <div className="flex items-start gap-3">
                <div className="h-10 w-10 rounded-2xl border border-blue-100 bg-blue-50 grid place-items-center text-blue-700">
                  <IconShield className="h-5 w-5" />
                </div>
                <div>
                  <div className="font-semibold text-slate-900">
                    Role-based access
                  </div>
                  <div className="text-sm text-slate-600">
                    Separate views for staff and patients.
                  </div>
                </div>
              </div>
              <div className="flex items-start gap-3">
                <div className="h-10 w-10 rounded-2xl border border-blue-100 bg-blue-50 grid place-items-center text-blue-700">
                  <IconAudit className="h-5 w-5" />
                </div>
                <div>
                  <div className="font-semibold text-slate-900">
                    Audit-ready
                  </div>
                  <div className="text-sm text-slate-600">
                    Access events are tracked for accountability.
                  </div>
                </div>
              </div>
            </div>
          </motion.div>

          <motion.div
            initial={{ opacity: 0, y: 12 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.35, delay: 0.05 }}
          >
            <Card className="p-6">
              <div className="mb-4">
                <div className="text-xl font-extrabold text-slate-900">
                  Sign in
                </div>
                <div className="text-sm text-slate-500">
                  Use your hospital-provided credentials.
                </div>
              </div>
              <form onSubmit={onSubmit} className="space-y-3">
                <Input
                  placeholder="Email address"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                />
                <Input
                  placeholder="Password"
                  type="password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                />
                {err && <div className="text-sm text-red-600">{err}</div>}
                <Button className="w-full">Log in</Button>
                <div className="flex items-center gap-3">
                  <div className="h-px flex-1 bg-slate-200" />
                  <div className="text-xs font-semibold text-slate-500">or</div>
                  <div className="h-px flex-1 bg-slate-200" />
                </div>
                <Link to="/signup">
                  <Button variant="secondary" className="w-full">
                    Request / Create account
                  </Button>
                </Link>
              </form>
              <div className="mt-4 text-xs text-slate-500">
                Accounts are typically created by hospital admins. If you don’t
                have an account, use the request flow.
              </div>
            </Card>
          </motion.div>
        </div>
      </div>
      <Footer compact />
    </div>
  );
}
