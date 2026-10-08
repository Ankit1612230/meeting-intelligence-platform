import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import api from "../api.js";

export default function NewMeeting() {
    const [title, setTitle] = useState("");
    const [meetingDate, setMeetingDate] = useState("");
    const [transcript, setTranscript] = useState("");
    const [participants, setParticipants] = useState([{ name: "", email: "" }]);
    const [error, setError] = useState("");
    const [saving, setSaving] = useState(false);
    const navigate = useNavigate();

    function updateParticipant(index, field, value) {
        setParticipants(participants.map((p, i) => (i === index ? { ...p, [field]: value } : p)));
    }

    function addParticipant() {
        setParticipants([...participants, { name: "", email: "" }]);
    }

    function removeParticipant(index) {
        setParticipants(participants.filter((_, i) => i !== index));
    }

    async function submit(e) {
        e.preventDefault();
        setError("");
        setSaving(true);
        try {
            const filled = participants.filter((p) => p.name.trim() || p.email.trim());
            await api.post("/meeting-service/api/meetings", {
                title,
                meetingDate: meetingDate + ":00",
                transcript,
                participants: filled,
            });
            navigate("/meetings");
        } catch (err) {
            setError(err.response?.data?.detail || "Could not create meeting");
        } finally {
            setSaving(false);
        }
    }

    return (
        <form className="card" style={{ maxWidth: 600, margin: "40px auto" }} onSubmit={submit}>
            <h2>New meeting</h2>
            <input placeholder="Title" value={title} onChange={(e) => setTitle(e.target.value)} required />
            <input type="datetime-local" value={meetingDate} onChange={(e) => setMeetingDate(e.target.value)} required />
            <textarea
                placeholder="Paste the meeting transcript here"
                value={transcript}
                onChange={(e) => setTranscript(e.target.value)}
                rows={8}
                style={{ width: "100%", marginTop: 10, padding: 10, boxSizing: "border-box", fontSize: 15 }}
            />

            <h3>Participants</h3>
            {participants.map((p, i) => (
                <div key={i} style={{ display: "flex", gap: 8 }}>
                    <input placeholder="Name" value={p.name} onChange={(e) => updateParticipant(i, "name", e.target.value)} />
                    <input placeholder="Email" value={p.email} onChange={(e) => updateParticipant(i, "email", e.target.value)} />
                    <button type="button" onClick={() => removeParticipant(i)} style={{ width: 40, background: "#b91c1c" }}>x</button>
                </div>
            ))}
            <button type="button" onClick={addParticipant} style={{ background: "#475569" }}>+ Add participant</button>

            <button type="submit" disabled={saving}>{saving ? "Saving..." : "Create meeting"}</button>
            {error && <div className="error">{error}</div>}
            <p><Link to="/meetings">Back to meetings</Link></p>
        </form>
    );
}