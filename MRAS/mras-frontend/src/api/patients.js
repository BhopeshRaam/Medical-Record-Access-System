import { api } from "./axios";

export const listPatients = ({ q, page = 0, size = 10, sort = "createdAt,desc" }) =>
  api.get("/api/patients", { params: { q, page, size, sort } });

export const getPatient = (id) => api.get(`/api/patients/${id}`);
export const createPatient = (payload) => api.post("/api/patients", payload);
export const myPatient = () => api.get("/api/patients/me");
