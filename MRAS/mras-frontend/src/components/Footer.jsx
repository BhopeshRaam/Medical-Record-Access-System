export default function Footer({ compact = false }) {
  return (
    <footer className={"border-t border-slate-100 " + (compact ? "bg-white" : "bg-slate-50")}>
      <div className="mx-auto max-w-6xl px-4 py-8 flex flex-col md:flex-row items-start md:items-center justify-between gap-3">
        <div>
          <div className="font-extrabold text-slate-900">MRAS</div>
          <div className="text-xs text-slate-500 mt-1">
            Medical Record Access System • React + Tailwind • Spring Boot + MongoDB
          </div>
        </div>
        <div className="text-xs text-slate-500">
          © {new Date().getFullYear()} MRAS • Built for learning & demo
        </div>
      </div>
    </footer>
  );
}
