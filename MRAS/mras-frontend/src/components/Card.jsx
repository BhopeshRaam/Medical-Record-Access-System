export default function Card({ children, className="" }) {
  return (
    <div className={"rounded-[2rem] bg-white border border-slate-100 shadow-sm " + className}>
      {children}
    </div>
  );
}
