import { useState } from "react";
import { Link } from "react-router-dom";
import { motion } from "framer-motion";
import Navbar from "../components/Navbar";
import Footer from "../components/Footer";
import Input from "../components/Input";
import Button from "../components/Button";
import Card from "../components/Card";
import { registerApi } from "../api/auth";
import { IconLogo, IconShield } from "../components/Icons";

export default function Signup() {
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [msg, setMsg] = useState("");
  const [err, setErr] = useState("");

  async function onSubmit(e) {
    e.preventDefault();
    setErr("");
    setMsg("");
    try {
      await registerApi({ name, email, password, roles: ["PATIENT"] });
      setMsg(
        "Request submitted. If your account is already active, you can sign in now.",
      );
    } catch (e2) {
      setErr(
        e2?.response?.data?.message ||
          "Request failed. If registration is admin-only, ask an admin to create your account.",
      );
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
            <div className="mt-5 text-xl font-extrabold text-slate-900">
              Request access
            </div>
            <div className="mt-2 text-slate-600">
              Submit your details and a hospital admin can approve your access.
            </div>
            <div className="mt-6 flex items-start gap-3">
              <div className="h-10 w-10 rounded-2xl border border-blue-100 bg-blue-50 grid place-items-center text-blue-700">
                <IconShield className="h-5 w-5" />
              </div>
              <div>
                <div className="font-semibold text-slate-900">
                  Privacy by design
                </div>
                <div className="text-sm text-slate-600">
                  Records and file downloads are protected by role checks and
                  audit logs.
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
              <form onSubmit={onSubmit} className="space-y-3">
                <Input
                  placeholder="Full name"
                  value={name}
                  onChange={(e) => setName(e.target.value)}
                />
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
                {msg && <div className="text-sm text-blue-700">{msg}</div>}
                <Button className="w-full">Sign up</Button>
                <Link
                  to="/login"
                  className="block text-center text-sm text-slate-600 hover:text-slate-900"
                >
                  Already have an account? Log in
                </Link>
              </form>
            </Card>
          </motion.div>
        </div>
      </div>
      <Footer compact />
    </div>
  );
}
