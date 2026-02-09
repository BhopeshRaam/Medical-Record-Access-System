import { Outlet, useLocation } from "react-router-dom";
import { motion } from "framer-motion";
import Sidebar from "../../components/Sidebar";
import Navbar from "../../components/Navbar";
import Footer from "../../components/Footer";
import Card from "../../components/Card";
import Topbar from "../../components/Topbar";
import { useAuth } from "../../auth/AuthContext";

function titleFromPath(path) {
  if (path.includes("/home")) return "Home";
  if (path.includes("/patients")) return "Patients";
  if (path.includes("/records")) return "Records";
  if (path.includes("/users")) return "Users";
  if (path.includes("/audit")) return "Audit Logs";
  if (path.includes("/settings")) return "Settings";
  return "Dashboard";
}

export default function DashboardLayout() {
  const { user } = useAuth();
  const loc = useLocation();
  const title = titleFromPath(loc.pathname);

  return (
    <div className="min-h-screen bg-slate-50">
      <Navbar variant="dashboard" />
      <div className="mx-auto max-w-6xl px-4 py-4 flex gap-4">
        <Sidebar user={user} />
        <main className="flex-1 space-y-4">
          <Card className="p-6"><Topbar title={title} /></Card>
          <motion.div key={loc.pathname} initial={{ opacity: 0, y: 10 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.22 }}>
            <Outlet />
          </motion.div>
        </main>
      </div>
      <Footer />
    </div>
  );
}
