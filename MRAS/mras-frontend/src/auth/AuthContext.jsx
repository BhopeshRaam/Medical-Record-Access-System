import { createContext, useContext, useEffect, useMemo, useState } from "react";
import { loginApi, meApi } from "../api/auth";

const AuthCtx = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  async function loadMe() {
    const token = localStorage.getItem("mras_token");
    if (!token) {
      setUser(null);
      setLoading(false);
      return;
    }
    try {
      const res = await meApi();
      setUser(res.data.data);
    } catch {
      localStorage.removeItem("mras_token");
      setUser(null);
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => { loadMe(); }, []);

  async function login(email, password) {
    const res = await loginApi({ email, password });
    localStorage.setItem("mras_token", res.data.data.token);
    await loadMe();
  }

  function logout() {
    localStorage.removeItem("mras_token");
    setUser(null);
  }

  const value = useMemo(() => ({ user, loading, login, logout, reload: loadMe }), [user, loading]);
  return <AuthCtx.Provider value={value}>{children}</AuthCtx.Provider>;
}

export const useAuth = () => useContext(AuthCtx);
