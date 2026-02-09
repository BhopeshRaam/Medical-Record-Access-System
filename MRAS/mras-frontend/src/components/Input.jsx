export default function Input({
  className = "",
  leftIcon: LeftIcon,
  rightSlot,
  ...props
}) {
  const base =
    "w-full rounded-2xl border border-slate-200 bg-white px-4 py-2 text-sm outline-none " +
    "placeholder:text-slate-400 focus:ring-2 focus:ring-blue-200 focus:border-blue-300 transition " +
    "disabled:bg-slate-50 disabled:text-slate-500 disabled:cursor-not-allowed";

  if (!LeftIcon && !rightSlot) {
    return <input className={`${base} ${className}`} {...props} />;
  }

  return (
    <div className={"relative"}>
      {LeftIcon && (
        <div className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400">
          <LeftIcon className="h-4 w-4" />
        </div>
      )}
      <input
        className={`${base} ${LeftIcon ? "pl-10" : ""} ${rightSlot ? "pr-12" : ""} ${className}`}
        {...props}
      />
      {rightSlot && <div className="absolute right-2 top-1/2 -translate-y-1/2">{rightSlot}</div>}
    </div>
  );
}
