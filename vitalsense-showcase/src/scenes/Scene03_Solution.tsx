import React from "react";
import { Audio, interpolate, spring, staticFile, useCurrentFrame, useVideoConfig } from "remotion";
import { Background } from "../components/Background";
import { Captions } from "../components/Captions";
import { StepBadge } from "../components/StepBadge";

interface SceneProps {
  showCaptions?: boolean;
  audioFile?: string;
  narrationText?: string;
}

export const Scene03_Solution: React.FC<SceneProps> = ({
  showCaptions = true,
  audioFile = "audio/scene03.mp3",
  narrationText = "One app, four roles. Patients, ASHA workers, doctors, and health administrators each get their own permission-aware workflow, inside a single native Android codebase.",
}) => {
  const frame = useCurrentFrame();
  const { fps } = useVideoConfig();

  const roles = [
    {
      role: "1. PATIENT",
      sub: "Rural Citizen",
      icon: "🧑‍🌾",
      color: "#0284C7",
      features: ["Vernacular Voice / Multilingual UI", "Offline ABHA QR Health Card", "Specialist Referral Tracking", "1-Tap SMS Fallback SOS", "Mental Wellness & Breathing"],
    },
    {
      role: "2. ASHA WORKER",
      sub: "Frontline Community Care",
      icon: "👩‍⚕️",
      color: "#10B981",
      features: ["Household Caseload Registry", "Proxy Patient Survey & Intake", "Automated Clinical Risk Triage", "Sunlight High-Contrast Mode", "Zero-Block Offline Outbox Sync"],
    },
    {
      role: "3. DOCTOR",
      sub: "PHC Medical Officer",
      icon: "👨‍⚕️",
      color: "#006A60",
      features: ["Live OPD Consultation Queue", "Closed-Loop Specialist Referrals", "CameraX Video Teleconsultation", "Persistent Tele-Vitals HUD", "Digital Structured Rx Engine"],
    },
    {
      role: "4. DISTRICT ADMIN",
      sub: "Chief Medical Officer (CMO)",
      icon: "🏛️",
      color: "#8B5CF6",
      features: ["Village Outbreak Heat Maps", "IPD Bed Occupancy Roster", "Operation Theatre Scheduler", "Bio-Medical Equipment Tracker", "District-Wide Directive Push"],
    },
  ];

  return (
    <div style={{ position: "relative", width: 1920, height: 1080, overflow: "hidden" }}>
      <Background />
      <StepBadge stepNumber="03 / 13" category="THE SOLUTION" title="4-in-1 Unified Role Architecture" />

      {/* 4 Role Grid */}
      <div
        style={{
          position: "absolute",
          top: 220,
          left: 80,
          right: 80,
          display: "grid",
          gridTemplateColumns: "repeat(4, 1fr)",
          gap: 28,
        }}
      >
        {roles.map((r, i) => {
          const s = spring({ frame: Math.max(0, frame - i * 8), fps, config: { damping: 14, stiffness: 95 } });
          return (
            <div
              key={i}
              style={{
                backgroundColor: "rgba(15, 23, 42, 0.9)",
                border: `2px solid ${r.color}66`,
                borderRadius: 22,
                padding: "32px 26px",
                display: "flex",
                flexDirection: "column",
                boxShadow: `0 18px 36px rgba(0,0,0,0.5), 0 0 25px ${r.color}22`,
                backdropFilter: "blur(12px)",
                opacity: s,
                transform: `translateY(${interpolate(s, [0, 1], [35, 0])}px)`,
              }}
            >
              <div
                style={{
                  width: 60,
                  height: 60,
                  borderRadius: 16,
                  backgroundColor: `${r.color}22`,
                  border: `1.5px solid ${r.color}`,
                  display: "flex",
                  alignItems: "center",
                  justifyContent: "center",
                  fontSize: 32,
                  marginBottom: 18,
                }}
              >
                {r.icon}
              </div>

              <span style={{ fontFamily: "Inter, sans-serif", fontSize: 13, fontWeight: 800, color: r.color, letterSpacing: "0.08em" }}>
                {r.role}
              </span>
              <h4 style={{ margin: "4px 0 16px 0", fontFamily: "Inter, sans-serif", fontSize: 21, fontWeight: 800, color: "#FFFFFF" }}>
                {r.sub}
              </h4>

              <div style={{ width: "100%", height: 1, backgroundColor: "rgba(255,255,255,0.1)", marginBottom: 16 }} />

              <div style={{ display: "flex", flexDirection: "column", gap: 10 }}>
                {r.features.map((feat, fi) => (
                  <div key={fi} style={{ display: "flex", alignItems: "flex-start", gap: 8 }}>
                    <span style={{ color: r.color, fontSize: 14, marginTop: 1 }}>•</span>
                    <span style={{ fontFamily: "Inter, sans-serif", fontSize: 14, color: "#CBD5E1", lineHeight: 1.35, fontWeight: 500 }}>
                      {feat}
                    </span>
                  </div>
                ))}
              </div>
            </div>
          );
        })}
      </div>

      <Captions text={narrationText} showCaptions={showCaptions} />
      {audioFile && <Audio src={staticFile(audioFile)} />}
    </div>
  );
};
