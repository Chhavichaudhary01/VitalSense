import React from "react";
import { interpolate, useCurrentFrame, useVideoConfig } from "remotion";
import { PHONE_HEIGHT, PHONE_LEFT, PHONE_TOP, PHONE_WIDTH } from "./PhoneFrame";

interface SpotlightProps {
  targetX: number; // 0 to 1
  targetY: number; // 0 to 1
  startSec: number;
  endSec: number;
  radius?: number;
}

export const Spotlight: React.FC<SpotlightProps> = ({
  targetX,
  targetY,
  startSec,
  endSec,
  radius = 90,
}) => {
  const frame = useCurrentFrame();
  const { fps } = useVideoConfig();

  const startFrame = Math.round(startSec * fps);
  const endFrame = Math.round(endSec * fps);

  if (frame < startFrame || frame > endFrame) return null;

  const progress = interpolate(
    frame,
    [startFrame, startFrame + 10, endFrame - 8, endFrame],
    [0, 1, 1, 0],
    { extrapolateLeft: "clamp", extrapolateRight: "clamp" }
  );

  const cx = PHONE_LEFT + targetX * PHONE_WIDTH;
  const cy = PHONE_TOP + targetY * PHONE_HEIGHT;

  return (
    <div
      style={{
        position: "absolute",
        top: 0,
        left: 0,
        width: 1920,
        height: 1080,
        pointerEvents: "none",
        zIndex: 25,
        opacity: progress * 0.45,
        background: `radial-gradient(circle at ${cx}px ${cy}px, transparent ${radius}px, rgba(0, 0, 0, 0.75) ${radius + 60}px)`,
      }}
    />
  );
};
