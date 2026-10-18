import React from "react";
import { interpolate, useCurrentFrame } from "remotion";

export const Background: React.FC<{ blur?: boolean }> = ({ blur = false }) => {
  const frame = useCurrentFrame();
  const pulse = interpolate(Math.sin(frame / 30), [-1, 1], [0.15, 0.25]);

  return (
    <div
      style={{
        position: "absolute",
        top: 0,
        left: 0,
        width: 1920,
        height: 1080,
        backgroundColor: "#070D14",
        backgroundImage: `
          radial-gradient(circle at 20% 30%, rgba(0, 106, 96, ${pulse}) 0%, transparent 50%),
          radial-gradient(circle at 80% 70%, rgba(16, 185, 129, ${pulse * 0.7}) 0%, transparent 45%),
          radial-gradient(circle at 50% 90%, rgba(2, 44, 34, 0.4) 0%, transparent 60%)
        `,
        overflow: "hidden",
        filter: blur ? "blur(20px)" : "none",
      }}
    >
      {/* Subtle clean technological grid */}
      <div
        style={{
          position: "absolute",
          top: 0,
          left: 0,
          width: "100%",
          height: "100%",
          backgroundImage: `
            linear-gradient(rgba(255, 255, 255, 0.02) 1px, transparent 1px),
            linear-gradient(90deg, rgba(255, 255, 255, 0.02) 1px, transparent 1px)
          `,
          backgroundSize: "60px 60px",
        }}
      />
    </div>
  );
};
