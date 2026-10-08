import { Navigate, Route, Routes } from "react-router-dom";
import ProtectedRoute from "./ProtectedRoute.jsx";
import Login from "./pages/Login.jsx";
import Register from "./pages/Register.jsx";
import Meetings from "./pages/Meetings.jsx";
import NewMeeting from "./pages/NewMeeting.jsx";
import MeetingDetail from "./pages/MeetingDetail.jsx";
import Admin from "./pages/Admin.jsx";

export default function App() {
    return (
        <Routes>
            <Route path="/" element={<Navigate to="/meetings" replace />} />
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />
            <Route path="/meetings" element={<ProtectedRoute><Meetings /></ProtectedRoute>} />
            <Route path="/meetings/new" element={<ProtectedRoute><NewMeeting /></ProtectedRoute>} />
            <Route path="/meetings/:id" element={<ProtectedRoute><MeetingDetail /></ProtectedRoute>} />
            <Route path="/admin" element={<ProtectedRoute><Admin /></ProtectedRoute>} />
            <Route path="*" element={<Navigate to="/meetings" replace />} />
        </Routes>
    );
}