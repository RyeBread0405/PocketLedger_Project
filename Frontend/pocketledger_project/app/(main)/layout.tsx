import "../globals.css";
import Navbar from "../components/Navbar";
import RequireLogin from "../components/RequireLogin";

export default function MainLayout({ children }: LayoutProps<"/">) {
  return (
    //Every page in this folder needs the user to be logged in
    <RequireLogin>
      <div className="min-h-full flex flex-col">
        <Navbar />
        {children}
      </div>
    </RequireLogin>
  );
}