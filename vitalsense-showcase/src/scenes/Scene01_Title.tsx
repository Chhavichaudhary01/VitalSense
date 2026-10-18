import React from "react";
import { Audio, interpolate, spring, staticFile, useCurrentFrame, useVideoConfig } from "remotion";
import { Background } from "../components/Background";
import { Captions } from "../components/Captions";

interface SceneProps {
  showCaptions?: boolean;
  audioFile?: string;
  narrationText?: string;
}

export const Scene01_Title: React.FC<SceneProps> = ({
  showCaptions = true,
  audioFile = "audio/scene01.mp3",
  narrationText = "This is VitalSense, an offline-first Android application that connects rural patients, ASHA workers, doctors, and health administrators in one ecosystem.",
}) => {
  const frame = useCurrentFrame();
  const { fps } = useVideoConfig();

  const titleSpring = spring({
    frame,
    fps,
    config: { damping: 14, stiffness: 90 },
  });

  const subtitleSpring = spring({
    frame: Math.max(0, frame - 10),
    fps,
    config: { damping: 14, stiffness: 90 },
  });

  const badgeSpring = spring({
    frame: Math.max(0, frame - 18),
    fps,
    config: { damping: 14, stiffness: 90 },
  });

  return (
    <div style={{ position: "relative", width: 1920, height: 1080, overflow: "hidden" }}>
      <Background />

      {/* Top Banner: SIH 26133 & Team AarogyaX */}
      <div
        style={{
          position: "absolute",
          top: 80,
          left: 0,
          width: "100%",
          display: "flex",
          flexDirection: "column",
          alignItems: "center",
          gap: 12,
          opacity: titleSpring,
          transform: `translateY(${interpolate(titleSpring, [0, 1], [-20, 0])}px)`,
        }}
      >
        <div
          style={{
            display: "inline-flex",
            alignItems: "center",
            gap: 12,
            padding: "8px 24px",
            borderRadius: 999,
            backgroundColor: "rgba(0, 106, 96, 0.25)",
            border: "1.5px solid #10B981",
            boxShadow: "0 0 20px rgba(16, 185, 129, 0.3)",
          }}
        >
          <span style={{ fontFamily: "Inter, sans-serif", fontSize: 16, fontWeight: 800, color: "#10B981", letterSpacing: "0.1em" }}>
            SMART INDIA HACKATHON 2024
          </span>
          <span style={{ width: 6, height: 6, borderRadius: "50%", backgroundColor: "#94A3B8" }} />
          <span style={{ fontFamily: "Inter, sans-serif", fontSize: 16, fontWeight: 700, color: "#E2E8F0", letterSpacing: "0.05em" }}>
            TEAM AAROGYAX
          </span>
        </div>

        <div style={{ fontFamily: "Inter, sans-serif", fontSize: 18, color: "#94A3B8", fontWeight: 500, letterSpacing: "0.02em" }}>
          Problem Statement SIH26133: Healthcare Accessibility in Rural & Underserved Areas
        </div>
      </div>

      {/* Center Hero: VitalSense Branding */}
      <div
        style={{
          position: "absolute",
          top: "46%",
          left: "50%",
          transform: `translate(-50%, -50%) scale(${interpolate(titleSpring, [0, 1], [0.92, 1])})`,
          textAlign: "center",
          display: "flex",
          flexDirection: "column",
          alignItems: "center",
          width: 1400,
        }}
      >
        {/* Medical Cross Emblem */}
        <div
          style={{
            width: 80,
            height: 80,
            borderRadius: 24,
            backgroundColor: "#006A60",
            border: "2px solid #10B981",
            boxShadow: "0 0 35px rgba(0, 106, 96, 0.6)",
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
            marginBottom: 24,
          }}
        >
          <span style={{ fontSize: 44, color: "#FFFFFF" }}>🏥</span>
        </div>

        <h1
          style={{
            margin: 0,
            fontFamily: "Inter, system-ui, sans-serif",
            fontSize: 92,
            fontWeight: 900,
            color: "#FFFFFF",
            letterSpacing: "-0.04em",
            lineHeight: 1.1,
            textShadow: "0 10px 40px rgba(0,0,0,0.6)",
          }}
        >
          VitalSense <span style={{ color: "#10B981", fontWeight: 700, fontSize: 64 }}>(SehatSetu)</span>
        </h1>

        <p
          style={{
            margin: "20px 0 0 0",
            fontFamily: "Inter, system-ui, sans-serif",
            fontSize: 28,
            color: "#CBD5E1",
            fontWeight: 500,
            maxWidth: 1000,
            lineHeight: 1.4,
            opacity: subtitleSpring,
          }}
        >
          Connecting Rural Health Infrastructure with Low-Connectivity Communities
        </p>

        {/* Feature Badges */}
        <div
          style={{
            display: "flex",
            gap: 16,
            marginTop: 40,
            opacity: badgeSpring,
            transform: `translateY(${interpolate(badgeSpring, [0, 1], [15, 0])}px)`,
          }}
        >
          {["100% Offline-First", "4-in-1 Unified Roles", "Clean Architecture + Room", "ML Kit On-Device OCR"].map((badge, idx) => (
            <div
              key={idx}
              style={{
                padding: "10px 20px",
                borderRadius: 12,
                backgroundColor: "rgba(15, 23, 42, 0.8)",
                border: "1px solid rgba(255, 255, 255, 0.15)",
                color: "#E2E8F0",
                fontFamily: "Inter, sans-serif",
                fontSize: 15,
                fontWeight: 600,
                letterSpacing: "0.02em",
              }}
            >
              {badge}
            </div>
          ))}
        </div>
      </div>

      <Captions text={narrationText} showCaptions={showCaptions} />
      {audioFile && <Audio src={staticFile(audioFile)} />}
    </div>
  );
};
