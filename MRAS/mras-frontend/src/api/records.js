import { api } from "./axios";

export const listRecordsByPatient = ({ patientId, page = 0, size = 10, sort = "encounterDate,desc" }) =>
  api.get(`/api/patients/${patientId}/records`, { params: { page, size, sort } });

export const getRecord = (recordId) => api.get(`/api/records/${recordId}`);
export const updateRecord = (recordId, payload) => api.patch(`/api/records/${recordId}`, payload);

export const uploadRecordFile = (recordId, file) => {
  const form = new FormData();
  form.append("file", file);
  return api.post(`/api/records/${recordId}/files`, form, {
    headers: { "Content-Type": "multipart/form-data" },
  });
};

export const deleteRecordFile = (recordId, fileId) =>
  api.delete(`/api/records/${recordId}/files/${fileId}`);

export const fileDownloadUrl = (fileId) =>
  `${import.meta.env.VITE_API_BASE_URL || "http://localhost:8080"}/api/files/${fileId}`;
