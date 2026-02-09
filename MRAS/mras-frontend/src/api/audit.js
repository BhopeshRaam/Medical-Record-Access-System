import { api } from "./axios";
export const searchAudit = (params) => api.get("/api/admin/audit", { params });
