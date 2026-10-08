import { useCallback, useEffect, useRef, useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import api from "../api.js";

export default function MeetingDetail() {
    const { id } = useParams();
    const navigate = useNavigate();
    const [meeting, setMeeting] = useState(null);
    const [error, setError] = useState("");
    const [notice, setNotice] = useState("");
    const [analyzing, setAnalyzing] = useState(false);
    const timer = useRef(null);
    const base = `/meeting-service/api/meetings/${id}`;

    const fail = useCallback((err, fallback) => {
        if (err.response?.status === 401) {
            localStorage.removeItem("token");
            navigate("/login");
        } else {
            setError(err.response?.status === 404 ? "Meeting not found" : fallback);
        }
    }, [navigate]);

    useEffect(() => {
        api.get(base)
            .then(({ data }) => setMeeting(data))
            .catch((err) => fail(err, "Could not load meeting"));
        return () => clearInterval(timer.current);
    }, [base, fail]);

    async function analyze() {
        setError("");
        setNotice("");
        setAnalyzing(true);
        const before = meeting.summary;
        try {
            await api.post(`${base}/analyze`);
        } catch (err) {
            setAnalyzing(false);
            setError("Could not start analysis. Does the meeting have a transcript?");
            return;
        }
        let tries = 0;
        timer.current = setInterval(async () => {
            tries++;
            try {
                const { data } = await api.get(base);
                setMeeting(data);
                const done = data.status === "FAILED" ||
                    (data.status === "ANALYZED" && data.summary !== before);
                if (done || tries >= 15) {
                    clearInterval(timer.current);
                    setAnalyzing(false);
                    if (data.status === "FAILED") setError("AI analysis failed. Please try again.");
                    else if (!done) setNotice("Still processing. Refresh the page in a moment.");
                }
            } catch {
                clearInterval(timer.current);
                setAnalyzing(false);
            }
        }, 2000);
    }

    async function complete(itemId) {
        try {
            const { data } = await api.patch(`${base}/action-items/${itemId}/complete`);
            setMeeting((m) => ({
                ...m,
                actionItems: m.actionItems.map((a) => (a.id === itemId ? data : a)),
            }));
        } catch (err) {
            fail(err, "Could not update task");
        }
    }

    if (!meeting) {
        return (
            <div style={{ maxWidth: 800, margin: "40px auto", padding: "0 16px" }}>
                {error ? <div className="error">{error}</div> : <p>Loading...</p>}
                <Link to="/meetings">Back to meetings</Link>
            </div>
        );
    }

    return (
        <div style={{ maxWidth: 800, margin: "40px auto", padding: "0 16px" }}>
            <Link to="/meetings">Back to meetings</Link>
            <h2>{meeting.title}</h2>
            <div>{new Date(meeting.meetingDate).toLocaleString()} | Status: <b>{meeting.status}</b></div>
            <div>
                Participants: {meeting.participants.map((p) => `${p.name} (${p.email})`).join(", ") || "none"}
            </div>

            <h3>Transcript</h3>
            <p style={{ whiteSpace: "pre-wrap" }}>{meeting.transcript || "No transcript"}</p>

            <button onClick={analyze} disabled={analyzing} style={{ width: "auto", padding: "10px 20px" }}>
                {analyzing ? "Analyzing..." : meeting.status === "ANALYZED" ? "Re-analyze" : "Analyze with AI"}
            </button>
            {notice && <div className="success">{notice}</div>}
            {error && <div className="error">{error}</div>}

            {meeting.summary && (
                <>
                    <h3>Summary</h3>
                    <p>{meeting.summary}</p>

                    <h3>Decisions</h3>
                    <ul>{meeting.decisions.map((d, i) => <li key={i}>{d}</li>)}</ul>

                    <h3>Action items</h3>
                    {meeting.actionItems.length === 0 && <p>No action items.</p>}
                    {meeting.actionItems.map((a) => (
                        <div key={a.id} className="card" style={{ margin: "10px 0", maxWidth: "none", padding: 16 }}>
                            <b>{a.task}</b>
                            <div>
                                {a.ownerEmail || "Unassigned"}
                                {a.dueDate && ` | due ${a.dueDate}`}
                                {" | "}{a.status}
                            </div>
                            {a.status !== "DONE" && (
                                <button onClick={() => complete(a.id)} style={{ width: "auto", padding: "6px 14px" }}>
                                    Mark done
                                </button>
                            )}
                        </div>
                    ))}
                </>
            )}
        </div>
    );
}