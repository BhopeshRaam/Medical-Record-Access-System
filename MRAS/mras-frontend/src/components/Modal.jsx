import { motion, AnimatePresence } from "framer-motion";

export default function Modal({ open, title, onClose, children, footer }) {
  return (
    <AnimatePresence>
      {open && (
        <motion.div
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          exit={{ opacity: 0 }}
          className="fixed inset-0 z-50 grid place-items-center bg-black/30 p-4"
          onMouseDown={onClose}
        >
          <motion.div
            initial={{ opacity: 0, y: 20, scale: 0.98 }}
            animate={{ opacity: 1, y: 0, scale: 1 }}
            exit={{ opacity: 0, y: 20, scale: 0.98 }}
            transition={{ duration: 0.22 }}
            className="w-full max-w-lg rounded-[2rem] bg-white shadow-xl border border-slate-100 overflow-hidden"
            onMouseDown={(e) => e.stopPropagation()}
          >
            <div className="p-5 border-b border-slate-100 flex items-center justify-between">
              <div className="font-semibold text-slate-900">{title}</div>
              <button className="h-9 w-9 rounded-2xl hover:bg-slate-100 grid place-items-center" onClick={onClose}>
                ✕
              </button>
            </div>
            <div className="p-5">{children}</div>
            {footer && <div className="p-5 border-t border-slate-100">{footer}</div>}
          </motion.div>
        </motion.div>
      )}
    </AnimatePresence>
  );
}
