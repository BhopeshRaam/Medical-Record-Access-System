import { motion } from "framer-motion";
import Card from "./Card";

export default function StatCard({ title, value, icon: Icon, hint }) {
  return (
    <motion.div initial={{ opacity: 0, y: 10 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.28 }}>
      <Card className="p-5">
        <div className="flex items-start justify-between gap-3">
          <div>
            <div className="text-xs font-semibold text-slate-500">{title}</div>
            <div className="mt-1 text-2xl font-extrabold text-slate-900">{value}</div>
            {hint && <div className="mt-2 text-xs text-slate-500">{hint}</div>}
          </div>
          {Icon && (
            <div className="h-10 w-10 rounded-2xl bg-blue-50 border border-blue-100 grid place-items-center text-blue-700">
              <Icon className="h-5 w-5" />
            </div>
          )}
        </div>
      </Card>
    </motion.div>
  );
}
