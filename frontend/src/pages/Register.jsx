import { useState } from "react";
import { Link } from "react-router-dom";
import api from "../api.js";

export default function Register() {
    const [fullName, setFullName] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");
    const [message, setMessage] = useState("");

    async function submit(e) {
        e.preventDefault();
        setError("");
        setMessage("");
        try {
            const { data } = await api.post("/auth-service/api/auth/register", { fullName, email, password });
            setMessage(data.message);
        } catch (err) {
            setError(err.response?.data?.detail || "Something went wrong");
        }
    }

    return (
        <form className="card" onSubmit={submit}>
            <h2>Register</h2>
            <input placeholder="Full name" value={fullName} onChange={(e) => setFullName(e.target.value)} />
            <input placeholder="Email" value={email} onChange={(e) => setEmail(e.target.value)} />
            <input type="password" placeholder="Password (min 8 characters)" value={password} onChange={(e) => setPassword(e.target.value)} />
            <button type="submit">Register</button>
            {message && <div className="success">{message}</div>}
            {error && <div className="error">{error}</div>}
            <p>Already registered? <Link to="/login">Login</Link></p>
        </form>
    );
}