export default function Button({
  variant = "primary",
  className = "",
  leftIcon: LeftIcon,
  rightIcon: RightIcon,
  children,
  ...props
}) {
  const base =
    "inline-flex items-center justify-center gap-2 rounded-2xl px-4 py-2 text-sm font-semibold transition active:scale-[0.99] focus:outline-none focus:ring-2 focus:ring-blue-200 disabled:opacity-60 disabled:pointer-events-none";

  const styles =
    variant === "primary"
      ? "bg-blue-600 text-white hover:bg-blue-700 shadow-sm"
      : variant === "ghost"
      ? "bg-transparent hover:bg-slate-100 text-slate-700"
      : variant === "danger"
      ? "bg-red-600 text-white hover:bg-red-700 shadow-sm"
      : "bg-white border border-slate-200 hover:bg-slate-50 text-slate-800 shadow-sm";

  return (
    <button className={`${base} ${styles} ${className}`} {...props}>
      {LeftIcon && <LeftIcon className="h-4 w-4" />}
      {children}
      {RightIcon && <RightIcon className="h-4 w-4" />}
    </button>
  );
}
