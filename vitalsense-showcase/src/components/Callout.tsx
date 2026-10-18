import React from "react";
import { interpolate, spring, useCurrentFrame, useVideoConfig } from "remotion";
import { Arrow } from "./Arrow";
import { PHONE_HEIGHT, PHONE_LEFT, PHONE_TOP, PHONE_WIDTH } from "./PhoneFrame";

export interface CalloutDef {
  scene: string;
  startSec: number;
  endSec: number;
  targetX: number; // 0 to 1 in phone coordinate space
  targetY: number; // 0 to 1 in phone coordinate space
  label: string;
  sublabel?: string;
  side: "left" | "right";
  color?: string;
}

export const Callout: React.FC<{ def: CalloutDef }> = ({ def }) => {
  const frame = useCurrentFrame();
  const { fps } = useVideoConfig();

  const startFrame = Math.round(def.startSec * fps);
  const endFrame = Math.round(def.endSec * fps);

  if (frame < startFrame || frame > endFrame) {
    return null;
  }

  const activeFrame = frame - startFrame;
  const remainingFrames = endFrame - frame;

  // Entrance spring
  const enterSpring = spring({
    frame: activeFrame,
    fps,
    config: { damping: 14, stiffness: 100 },
  });

  // Exit fade (over 8 frames)
  const exitOpacity = interpolate(remainingFrames, [0, 8], [0, 1], {
    extrapolateLeft: "clamp",
    extrapolateRight: "clamp",
  });

  const opacity = Math.min(enterSpring, exitOpacity);
  const slideX = interpolate(enterSpring, [0, 1], [def.side === "left" ? -14 : 14, 0]);

  // Map target to canvas coordinate space
  const targetCanvasX = PHONE_LEFT + def.targetX * PHONE_WIDTH;
  const targetCanvasY = PHONE_TOP + def.targetY * PHONE_HEIGHT;

  const cardWidth = 440;
  const cardHeight = def.sublabel ? 104 : 76;

  // Callout position
  let calloutX = 80;
  if (def.side === "right") {
    calloutX = PHONE_LEFT + PHONE_WIDTH + 80; // ~1323px
  }

  // Vertical position clamped to canvas safe margin (100px to 860px)
  const calloutY = Math.max(100, Math.min(860 - cardHeight, targetCanvasY - cardHeight / 2));

  // Arrow start coordinate (edge of the callout card facing the phone)
  const arrowStartX = def.side === "left" ? calloutX + cardWidth + 12 : calloutX - 12;
  const arrowStartY = calloutY + cardHeight / 2;

  const color = def.color || "#10B981"; // Medical Mint

  return (
    <>
      {/* Arrow pointing into target */}
      <Arrow
        startX={arrowStartX}
        startY={arrowStartY}
        targetX={targetCanvasX}
        targetY={targetCanvasY}
        progress={enterSpring}
        color={color}
      />

      {/* Callout Card */}
      <div
        style={{
          position: "absolute",
          left: calloutX,
          top: calloutY,
          width: cardWidth,
          minHeight: cardHeight,
          padding: "16px 22px",
          borderRadius: 20,
          backgroundColor: "rgba(11, 19, 32, 0.94)",
          border: `2px solid ${color}`,
          boxShadow: `0 15px 35px rgba(0, 0, 0, 0.6), 0 0 20px ${color}33`,
          backdropFilter: "blur(12px)",
          display: "flex",
          flexDirection: "column",
          justifyContent: "center",
          transform: `translateX(${slideX}px)`,
          opacity,
          zIndex: 35,
        }}
      >
        <div
          style={{
            fontFamily: "Inter, system-ui, sans-serif",
            fontSize: 27,
            fontWeight: 800,
            color: "#FFFFFF",
            letterSpacing: "-0.02em",
            lineHeight: 1.25,
            marginBottom: def.sublabel ? 4 : 0,
          }}
        >
          {def.label}
        </div>
        {def.sublabel && (
          <div
            style={{
              fontFamily: "Inter, system-ui, sans-serif",
              fontSize: 18,
              fontWeight: 500,
              color: "#94A3B8",
              lineHeight: 1.3,
            }}
          >
            {def.sublabel}
          </div>
        )}
      </div>
    </>
  );
};
