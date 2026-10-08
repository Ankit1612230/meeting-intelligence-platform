import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import api, { getRole } from "../api.js";

export default function Meetings() {
    const [meetings, setMeetings] = useState([]);
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(true);
    const navigate = useNavigate();

    useEffect(() => {
        api.get("/meeting-service/api/meetings")
            .then(({ data }) => setMeetings(data))
            .catch((err) => {
                if (err.response?.status === 401) {
                    localStorage.removeItem("token");
                    navigate("/login");
                } else {
                    setError("Could not load meetings");
                }
            })
            .finally(() => setLoading(false));
    }, [navigate]);

    function logout() {
        localStorage.removeItem("token");
        navigate("/login");
    }

    return (
        <div style={{ maxWidth: 800, margin: "40px auto", padding: "0 16px" }}>
            <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
                <h2>My Meetings</h2>
                <div style={{ display: "flex", gap: 12, alignItems: "center" }}>
                    <Link to="/meetings/new">+ New meeting {getRole() === "ADMIN" && <Link to="/admin">Admin</Link>}</Link>
                    <button onClick={logout} style={{ width: "auto", marginTop: 0 }}>Logout</button>
                </div>
            </div>

            {loading && <p>Loading...</p>}
            {error && <div className="error">{error}</div>}
            {!loading && !error && meetings.length === 0 && <p>No meetings yet.</p>}

            {meetings.map((m) => (
                <div key={m.id} className="card" style={{ margin: "12px 0", maxWidth: "none" }}>
                    <Link to={`/meetings/${m.id}`}><b>{m.title}</b></Link>
                    <div>{new Date(m.meetingDate).toLocaleString()}</div>
                    <div>Status: {m.status} | Participants: {m.participants.length}</div>
                </div>
            ))}
        </div>
    );
}