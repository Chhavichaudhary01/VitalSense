import React from "react";
import { PHONE_HEIGHT, PHONE_LEFT, PHONE_TOP, PHONE_WIDTH } from "./PhoneFrame";

interface BlurProps {
  x: number; // 0 to 1 normalized in phone
  y: number; // 0 to 1 normalized in phone
  width: number;
  height: number;
  label?: string;
}

export const Blur: React.FC<BlurProps> = ({
  x,
  y,
  width,
  height,
  label = "REDACTED",
}) => {
  const canvasX = PHONE_LEFT + x * PHONE_WIDTH;
  const canvasY = PHONE_TOP + y * PHONE_HEIGHT;
  const pixelWidth = width * PHONE_WIDTH;
  const pixelHeight = height * PHONE_HEIGHT;

  return (
    <div
      style={{
        position: "absolute",
        left: canvasX,
        top: canvasY,
        width: pixelWidth,
        height: pixelHeight,
        backdropFilter: "blur(14px)",
        backgroundColor: "rgba(15, 23, 42, 0.7)",
        borderRadius: 8,
        border: "1px solid rgba(255, 255, 255, 0.2)",
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
        zIndex: 20,
        pointerEvents: "none",
      }}
    >
      <span
        style={{
          fontFamily: "Inter, sans-serif",
          fontSize: 10,
          letterSpacing: "0.1em",
          color: "rgba(255, 255, 255, 0.6)",
          fontWeight: 700,
        }}
      >
        {label}
      </span>
    </div>
  );
};
