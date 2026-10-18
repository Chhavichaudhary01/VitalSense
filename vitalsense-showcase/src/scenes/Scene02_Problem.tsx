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

export const Scene02_Problem: React.FC<SceneProps> = ({
  showCaptions = true,
  audioFile = "audio/scene02.mp3",
  narrationText = "In many rural areas, weak connectivity and low digital literacy make timely healthcare hard to reach. VitalSense is built to work exactly where the internet doesn't.",
}) => {
  const frame = useCurrentFrame();
  const { fps } = useVideoConfig();

  const card1Spring = spring({ frame: Math.max(0, frame - 5), fps, config: { damping: 14, stiffness: 90 } });
  const card2Spring = spring({ frame: Math.max(0, frame - 20), fps, config: { damping: 14, stiffness: 90 } });
  const card3Spring = spring({ frame: Math.max(0, frame - 35), fps, config: { damping: 14, stiffness: 90 } });

  const cards = [
    {
      icon: "📶",
      tag: "INFRASTRUCTURE GAP",
      title: "Intermittent & Zero Connectivity",
      desc: "Remote Sub-Centers and tribal belts operate under 2G or complete cellular dead-zones where standard telemedicine web apps crash and fail completely.",
      spring: card1Spring,
    },
    {
      icon: "🗣️",
      tag: "ACCESSIBILITY BARRIER",
      title: "Low Digital Literacy & Vernacular Gap",
      desc: "Elderly and non-literate rural citizens struggle with text-heavy portals. Healthcare delivery requires proxy intake and native vernacular interfaces.",
      spring: card2Spring,
    },
    {
      icon: "📄",
      tag: "CONTINUITY OF CARE",
      title: "Paper Records & Specialist Isolation",
      desc: "Primary Health Centers lack on-site specialists. Paper prescription slips get damaged or lost, resulting in over 70% of informal referrals failing to reach care.",
      spring: card3Spring,
    },
  ];

  return (
    <div style={{ position: "relative", width: 1920, height: 1080, overflow: "hidden" }}>
      <Background />
      <StepBadge stepNumber="02 / 13" category="THE CHALLENGE" title="Healthcare In Low-Connectivity Rural Communities" />

      {/* 3 Problem Cards */}
      <div
        style={{
          position: "absolute",
          top: 260,
          left: 100,
          right: 100,
          display: "grid",
          gridTemplateColumns: "repeat(3, 1fr)",
          gap: 36,
        }}
      >
        {cards.map((c, i) => (
          <div
            key={i}
            style={{
              backgroundColor: "rgba(15, 23, 42, 0.85)",
              border: "1.5px solid rgba(255, 255, 255, 0.12)",
              borderRadius: 24,
              padding: "44px 36px",
              display: "flex",
              flexDirection: "column",
              boxShadow: "0 20px 40px rgba(0, 0, 0, 0.5)",
              backdropFilter: "blur(12px)",
              opacity: c.spring,
              transform: `translateY(${interpolate(c.spring, [0, 1], [30, 0])}px)`,
            }}
          >
            <div
              style={{
                width: 64,
                height: 64,
                borderRadius: 18,
                backgroundColor: "rgba(186, 26, 26, 0.2)",
                border: "1.5px solid rgba(239, 68, 68, 0.5)",
                display: "flex",
                alignItems: "center",
                justifyContent: "center",
                fontSize: 32,
                marginBottom: 24,
              }}
            >
              {c.icon}
            </div>

            <span
              style={{
                fontFamily: "Inter, sans-serif",
                fontSize: 13,
                fontWeight: 800,
                color: "#EF4444",
                letterSpacing: "0.1em",
                marginBottom: 8,
              }}
            >
              {c.tag}
            </span>

            <h3
              style={{
                margin: "0 0 16px 0",
                fontFamily: "Inter, sans-serif",
                fontSize: 26,
                fontWeight: 800,
                color: "#FFFFFF",
                lineHeight: 1.25,
                letterSpacing: "-0.02em",
              }}
            >
              {c.title}
            </h3>

            <p
              style={{
                margin: 0,
                fontFamily: "Inter, sans-serif",
                fontSize: 17,
                color: "#94A3B8",
                lineHeight: 1.55,
                fontWeight: 400,
              }}
            >
              {c.desc}
            </p>
          </div>
        ))}
      </div>

      <Captions text={narrationText} showCaptions={showCaptions} />
      {audioFile && <Audio src={staticFile(audioFile)} />}
    </div>
  );
};
