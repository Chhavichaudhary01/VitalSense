import React from "react";
import { spring, useCurrentFrame, useVideoConfig } from "remotion";

interface ArrowProps {
  startX: number;
  startY: number;
  targetX: number;
  targetY: number;
  progress: number; // 0 to 1
  color?: string;
}

export const Arrow: React.FC<ArrowProps> = ({
  startX,
  startY,
  targetX,
  targetY,
  progress,
  color = "#10B981", // Medical Mint
}) => {
  const { fps } = useVideoConfig();
  const frame = useCurrentFrame();

  if (progress <= 0) return null;

  // Curvature for natural hand-drawn arc
  const dx = targetX - startX;
  const dy = targetY - startY;
  const arcOffset = -40; // Graceful upward arc
  const cx = startX + dx * 0.5;
  const cy = startY + dy * 0.5 + arcOffset;

  const pathString = `M ${startX} ${startY} Q ${cx} ${cy} ${targetX} ${targetY}`;

  // Quadratic bezier path length estimation
  const chord = Math.hypot(dx, dy);
  const distance = chord + 30;
  const strokeDashoffset = Math.max(0, distance * (1 - Math.min(1, progress * 1.15)));

  // Arrowhead pop with spring
  const headSpring = spring({
    frame: Math.max(0, frame - 6),
    fps,
    config: { damping: 12, stiffness: 120 },
  });

  // Tangent angle of quadratic bezier arrival at target: P'(1) = 2*(target - c)
  const angle = Math.atan2(targetY - cy, targetX - cx);
  const headSize = 16 * Math.min(1, headSpring);

  return (
    <svg
      style={{
        position: "absolute",
        top: 0,
        left: 0,
        width: 1920,
        height: 1080,
        pointerEvents: "none",
        zIndex: 40,
      }}
    >
      <defs>
        <filter id="arrow-glow" filterUnits="userSpaceOnUse" x="0" y="0" width="1920" height="1080">
          <feDropShadow dx="0" dy="2" stdDeviation="4" floodColor="rgba(0,0,0,0.8)" />
          <feDropShadow dx="0" dy="0" stdDeviation="8" floodColor={color} floodOpacity="0.6" />
        </filter>
      </defs>

      {/* Target subtle pulsing ping */}
      {progress > 0.8 && (
        <circle
          cx={targetX}
          cy={targetY}
          r={12 + Math.sin(frame / 4) * 4}
          fill="none"
          stroke={color}
          strokeWidth="3"
          opacity={0.85}
        />
      )}

      {/* Main Curved Arrow Line */}
      <path
        d={pathString}
        fill="none"
        stroke={color}
        strokeWidth="5"
        strokeLinecap="round"
        strokeDasharray={distance}
        strokeDashoffset={strokeDashoffset}
        filter="url(#arrow-glow)"
      />

      {/* Arrowhead */}
      {progress > 0.35 && headSize > 0 && (
        <polygon
          points={`
            ${targetX},${targetY} 
            ${targetX - headSize * Math.cos(angle - Math.PI / 6)},${targetY - headSize * Math.sin(angle - Math.PI / 6)} 
            ${targetX - headSize * 0.6 * Math.cos(angle)},${targetY - headSize * 0.6 * Math.sin(angle)} 
            ${targetX - headSize * Math.cos(angle + Math.PI / 6)},${targetY - headSize * Math.sin(angle + Math.PI / 6)}
          `}
          fill={color}
          filter="url(#arrow-glow)"
        />
      )}
    </svg>
  );
};
