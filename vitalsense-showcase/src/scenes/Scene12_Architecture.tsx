import React from "react";
import { Audio, Img, interpolate, spring, staticFile, useCurrentFrame, useVideoConfig } from "remotion";
import { Background } from "../components/Background";
import { Captions } from "../components/Captions";
import { PhoneFrame } from "../components/PhoneFrame";
import { StepBadge } from "../components/StepBadge";

interface SceneProps {
  showCaptions?: boolean;
  audioFile?: string;
  narrationText?: string;
}

export const Scene12_Architecture: React.FC<SceneProps> = ({
  showCaptions = true,
  audioFile = "audio/scene12.mp3",
  narrationText = "Under the hood, an atomic Room database outbox and WorkManager background synchronization keep every action functional in zero-connectivity dead-zones, syncing seamlessly once network connectivity resumes.",
}) => {
  const frame = useCurrentFrame();
  const { fps } = useVideoConfig();

  const cardSpring = spring({ frame: Math.max(0, frame - 10), fps, config: { damping: 14, stiffness: 90 } });

  return (
    <div style={{ position: "relative", width: 1920, height: 1080, overflow: "hidden" }}>
      <Background />
      <StepBadge stepNumber="12 / 13" category="SYSTEM ARCHITECTURE" title="Offline-First Room Outbox & WorkManager Sync Engine" />

      {/* Phone chassis shifted left showing Airplane Mode toggle */}
      <PhoneFrame
        videoSrc="footage/Screen_Recording_20260919_122813_VitalSense.mp4"
        startFromFrame={450} // ~15s into Clip 5 (Airplane Mode pull-down)
        style={{
          left: 120,
        }}
      />

      {/* Right Column: Architectural Highlights */}
      <div
        style={{
          position: "absolute",
          top: 150,
          left: 740,
          width: 1080,
          display: "flex",
          flexDirection: "column",
          gap: 20,
          opacity: cardSpring,
          transform: `translateX(${interpolate(cardSpring, [0, 1], [30, 0])}px)`,
        }}
      >
        {/* Architecture Diagram Preview */}
        <div
          style={{
            width: "100%",
            height: 380,
            borderRadius: 20,
            backgroundColor: "rgba(15, 23, 42, 0.9)",
            border: "1.5px solid rgba(16, 185, 129, 0.4)",
            boxShadow: "0 20px 40px rgba(0,0,0,0.6)",
            padding: 16,
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
            overflow: "hidden",
          }}
        >
          <Img
            src={staticFile("assets/vitalsense_tech_stack.svg")}
            style={{
              maxWidth: "100%",
              maxHeight: "100%",
              objectFit: "contain",
            }}
          />
        </div>

        {/* 2 Architecture Callout Cards */}
        <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 20 }}>
          <div
            style={{
              padding: "24px 28px",
              borderRadius: 18,
              backgroundColor: "rgba(11, 19, 32, 0.95)",
              border: "2px solid #006A60",
              boxShadow: "0 10px 25px rgba(0,0,0,0.4)",
            }}
          >
            <div style={{ fontFamily: "Inter, sans-serif", fontSize: 20, fontWeight: 800, color: "#10B981", marginBottom: 8 }}>
              💾 SQLite Room Outbox
            </div>
            <p style={{ margin: 0, fontFamily: "Inter, sans-serif", fontSize: 15, color: "#CBD5E1", lineHeight: 1.45 }}>
              Zero-block local persistence. Every patient record, prescription, and triage action is committed to SQLite with 0ms server latency.
            </p>
          </div>

          <div
            style={{
              padding: "24px 28px",
              borderRadius: 18,
              backgroundColor: "rgba(11, 19, 32, 0.95)",
              border: "2px solid #10B981",
              boxShadow: "0 10px 25px rgba(0,0,0,0.4)",
            }}
          >
            <div style={{ fontFamily: "Inter, sans-serif", fontSize: 20, fontWeight: 800, color: "#10B981", marginBottom: 8 }}>
              🔄 WorkManager Sync Engine
            </div>
            <p style={{ margin: 0, fontFamily: "Inter, sans-serif", fontSize: 15, color: "#CBD5E1", lineHeight: 1.45 }}>
              Background battery-efficient synchronization with exponential backoff. Flushes local outbox records automatically upon network reconnection.
            </p>
          </div>
        </div>
      </div>

      <Captions text={narrationText} showCaptions={showCaptions} />
      {audioFile && <Audio src={staticFile(audioFile)} />}
    </div>
  );
};
