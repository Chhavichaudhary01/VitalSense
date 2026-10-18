import React from "react";
import { Audio, Img, interpolate, spring, staticFile, useCurrentFrame, useVideoConfig } from "remotion";
import { Background } from "../components/Background";
import { Captions } from "../components/Captions";

interface SceneProps {
  showCaptions?: boolean;
  audioFile?: string;
  narrationText?: string;
}

export const Scene13_Outro: React.FC<SceneProps> = ({
  showCaptions = true,
  audioFile = "audio/scene13.mp3",
  narrationText = "VitalSense: healthcare that keeps working, even when the network doesn't. Scan the QR code to test the live prototype on GitHub.",
}) => {
  const frame = useCurrentFrame();
  const { fps } = useVideoConfig();

  const animSpring = spring({ frame, fps, config: { damping: 14, stiffness: 90 } });

  return (
    <div style={{ position: "relative", width: 1920, height: 1080, overflow: "hidden" }}>
      <Background />

      <div
        style={{
          position: "absolute",
          top: "48%",
          left: "50%",
          transform: `translate(-50%, -50%) scale(${interpolate(animSpring, [0, 1], [0.94, 1])})`,
          display: "flex",
          alignItems: "center",
          gap: 90,
          opacity: animSpring,
        }}
      >
        {/* Left: Project & Team Credits */}
        <div style={{ maxWidth: 740, display: "flex", flexDirection: "column" }}>
          <div
            style={{
              display: "inline-flex",
              alignItems: "center",
              gap: 12,
              padding: "8px 20px",
              borderRadius: 999,
              backgroundColor: "rgba(0, 106, 96, 0.25)",
              border: "1.5px solid #10B981",
              marginBottom: 20,
              width: "fit-content",
            }}
          >
            <span style={{ fontFamily: "Inter, sans-serif", fontSize: 15, fontWeight: 800, color: "#10B981", letterSpacing: "0.08em" }}>
              TEAM AAROGYAX
            </span>
            <span style={{ width: 4, height: 4, borderRadius: "50%", backgroundColor: "#94A3B8" }} />
            <span style={{ fontFamily: "Inter, sans-serif", fontSize: 14, fontWeight: 700, color: "#CBD5E1" }}>
              SIH 26133
            </span>
          </div>

          <h1
            style={{
              margin: 0,
              fontFamily: "Inter, system-ui, sans-serif",
              fontSize: 68,
              fontWeight: 900,
              color: "#FFFFFF",
              letterSpacing: "-0.03em",
              lineHeight: 1.15,
            }}
          >
            VitalSense <span style={{ color: "#10B981", fontSize: 48 }}>(SehatSetu)</span>
          </h1>

          <p
            style={{
              margin: "18px 0 28px 0",
              fontFamily: "Inter, sans-serif",
              fontSize: 24,
              color: "#94A3B8",
              lineHeight: 1.45,
            }}
          >
            Healthcare that keeps working, even when the network doesn't.
          </p>

          <div
            style={{
              padding: "16px 24px",
              borderRadius: 16,
              backgroundColor: "rgba(15, 23, 42, 0.8)",
              border: "1px solid rgba(255, 255, 255, 0.15)",
              display: "flex",
              alignItems: "center",
              gap: 14,
              width: "fit-content",
            }}
          >
            <span style={{ fontSize: 24 }}>🔗</span>
            <span style={{ fontFamily: "Inter, monospace", fontSize: 20, fontWeight: 600, color: "#E2E8F0" }}>
              github.com/alexansh/VitalSense
            </span>
          </div>
        </div>

        {/* Right: QR Code Download Card */}
        <div
          style={{
            padding: "36px 36px 28px 36px",
            borderRadius: 28,
            backgroundColor: "rgba(15, 23, 42, 0.95)",
            border: "2px solid #10B981",
            boxShadow: "0 25px 60px rgba(0, 0, 0, 0.7), 0 0 35px rgba(16, 185, 129, 0.3)",
            display: "flex",
            flexDirection: "column",
            alignItems: "center",
            textAlign: "center",
          }}
        >
          <div
            style={{
              width: 250,
              height: 250,
              backgroundColor: "#FFFFFF",
              borderRadius: 18,
              padding: 12,
              boxShadow: "0 10px 25px rgba(0,0,0,0.3)",
              display: "flex",
              alignItems: "center",
              justifyContent: "center",
              marginBottom: 20,
            }}
          >
            <Img
              src={staticFile("assets/vitalsense_download_qr.png")}
              style={{
                width: "100%",
                height: "100%",
                objectFit: "contain",
              }}
            />
          </div>

          <span style={{ fontFamily: "Inter, sans-serif", fontSize: 20, fontWeight: 800, color: "#FFFFFF", marginBottom: 4 }}>
            Scan to Download
          </span>
          <span style={{ fontFamily: "Inter, sans-serif", fontSize: 15, fontWeight: 500, color: "#10B981" }}>
            Live Android Release APK
          </span>
        </div>
      </div>

      <Captions text={narrationText} showCaptions={showCaptions} />
      {audioFile && <Audio src={staticFile(audioFile)} />}
    </div>
  );
};
