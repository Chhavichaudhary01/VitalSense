import React from "react";
import { interpolate, useCurrentFrame, useVideoConfig } from "remotion";

interface CaptionsProps {
  text: string;
  showCaptions?: boolean;
}

export const Captions: React.FC<CaptionsProps> = ({
  text,
  showCaptions = true,
}) => {
  const frame = useCurrentFrame();
  const { durationInFrames } = useVideoConfig();

  if (!showCaptions || !text) return null;

  // Gentle fade in over 10 frames and fade out at scene end
  const opacity = interpolate(
    frame,
    [0, 10, durationInFrames - 10, durationInFrames],
    [0, 1, 1, 0],
    { extrapolateLeft: "clamp", extrapolateRight: "clamp" }
  );

  return (
    <div
      style={{
        position: "absolute",
        bottom: 36,
        left: 0,
        width: 1920,
        display: "flex",
        justifyContent: "center",
        zIndex: 60,
        opacity,
        pointerEvents: "none",
      }}
    >
      <div
        style={{
          maxWidth: 1300,
          padding: "12px 28px",
          borderRadius: 16,
          backgroundColor: "rgba(10, 15, 24, 0.88)",
          border: "1px solid rgba(255, 255, 255, 0.12)",
          backdropFilter: "blur(12px)",
          boxShadow: "0 10px 25px rgba(0, 0, 0, 0.5)",
          textAlign: "center",
        }}
      >
        <p
          style={{
            margin: 0,
            fontFamily: "Inter, system-ui, sans-serif",
            fontSize: 24,
            fontWeight: 600,
            color: "#F8FAFC",
            lineHeight: 1.35,
            letterSpacing: "-0.01em",
          }}
        >
          {text}
        </p>
      </div>
    </div>
  );
};
