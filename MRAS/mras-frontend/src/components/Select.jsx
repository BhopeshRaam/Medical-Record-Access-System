export default function Select({ className = "", children, ...props }) {
  return (
    <select
      className={
        "w-full rounded-2xl border border-slate-200 bg-white px-4 py-2 text-sm outline-none " +
        "focus:ring-2 focus:ring-blue-200 focus:border-blue-300 transition " +
        "disabled:bg-slate-50 disabled:text-slate-500 disabled:cursor-not-allowed " +
        className
      }
      {...props}
    >
      {children}
    </select>
  );
}
