import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import api from "../api.js";

export default function Login() {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");
    const navigate = useNavigate();

    async function submit(e) {
        e.preventDefault();
        setError("");
        try {
            const { data } = await api.post("/auth-service/api/auth/login", { email, password });
            localStorage.setItem("token", data.token);
            navigate("/meetings");
        } catch (err) {
            setError(err.response?.data?.detail || "Something went wrong");
        }
    }

    return (
        <form className="card" onSubmit={submit}>
            <h2>Login</h2>
            <input placeholder="Email" value={email} onChange={(e) => setEmail(e.target.value)} />
            <input type="password" placeholder="Password" value={password} onChange={(e) => setPassword(e.target.value)} />
            <button type="submit">Login</button>
            {error && <div className="error">{error}</div>}
            <p>No account? <Link to="/register">Register</Link></p>
        </form>
    );
}