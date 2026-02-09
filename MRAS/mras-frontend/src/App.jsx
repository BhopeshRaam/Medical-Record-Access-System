import { Routes, Route, Navigate } from "react-router-dom";
import Landing from "./pages/Landing";
import Login from "./pages/Login";
import Signup from "./pages/Signup";
import DashboardLayout from "./pages/dashboard/DashboardLayout";
import Home from "./pages/dashboard/Home";
import Patients from "./pages/dashboard/Patients";
import PatientDetail from "./pages/dashboard/PatientDetail";
import Records from "./pages/dashboard/Records";
import RecordDetail from "./pages/dashboard/RecordDetail";
import AdminUsers from "./pages/dashboard/AdminUsers";
import Audit from "./pages/dashboard/Audit";
import Settings from "./pages/dashboard/Settings";
import ProtectedRoute from "./auth/ProtectedRoute";

export default function App() {
  return (
    <Routes>
      <Route path="/" element={<Landing />} />
      <Route path="/login" element={<Login />} />
      <Route path="/signup" element={<Signup />} />

      <Route
        path="/dashboard/*"
        element={
          <ProtectedRoute>
            <DashboardLayout />
          </ProtectedRoute>
        }
      >
        <Route index element={<Navigate to="home" replace />} />
        <Route path="home" element={<Home />} />
        <Route path="patients" element={<Patients />} />
        <Route path="patients/:id" element={<PatientDetail />} />
        <Route path="records" element={<Records />} />
        <Route path="records/:id" element={<RecordDetail />} />
        <Route path="users" element={<AdminUsers />} />
        <Route path="audit" element={<Audit />} />
        <Route path="settings" element={<Settings />} />
      </Route>

      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}
