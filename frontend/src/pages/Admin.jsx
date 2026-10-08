import { useCallback, useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import api from "../api.js";

export default function Admin() {
    const [users, setUsers] = useState([]);
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(true);
    const navigate = useNavigate();

    const load = useCallback(() => {
        api.get("/auth-service/api/admin/pending")
            .then(({ data }) => setUsers(data))
            .catch((err) => {
                if (err.response?.status === 401) {
                    localStorage.removeItem("token");
                    navigate("/login");
                } else if (err.response?.status === 403) {
                    setError("Admins only");
                } else {
                    setError("Could not load users");
                }
            })
            .finally(() => setLoading(false));
    }, [navigate]);

    useEffect(() => { load(); }, [load]);

    async function act(request) {
        setError("");
        try {
            await request();
            load();
        } catch {
            setError("Action failed");
        }
    }

    return (
        <div style={{ maxWidth: 800, margin: "40px auto", padding: "0 16px" }}>
            <Link to="/meetings">Back to meetings</Link>
            <h2>Pending users</h2>

            {loading && <p>Loading...</p>}
            {error && <div className="error">{error}</div>}
            {!loading && !error && users.length === 0 && <p>No pending users.</p>}

            {users.map((u) => (
                <div key={u.id} className="card" style={{ margin: "10px 0", maxWidth: "none", padding: 16 }}>
                    <b>{u.fullName}</b>
                    <div>{u.email}</div>
                    <div style={{ display: "flex", gap: 8 }}>
                        <button style={{ width: "auto", padding: "6px 14px" }}
                                onClick={() => act(() => api.put(`/auth-service/api/admin/approve/${u.id}`))}>
                            Approve
                        </button>
                        <button style={{ width: "auto", padding: "6px 14px", background: "#b91c1c" }}
                                onClick={() => act(() => api.delete(`/auth-service/api/admin/reject/${u.id}`))}>
                            Reject
                        </button>
                    </div>
                </div>
            ))}
        </div>
    );
}