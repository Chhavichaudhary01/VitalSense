import React from "react";

interface StepBadgeProps {
  stepNumber: string;
  category: string;
  title: string;
}

export const StepBadge: React.FC<StepBadgeProps> = ({
  stepNumber,
  category,
  title,
}) => {
  return (
    <div
      style={{
        position: "absolute",
        top: 48,
        left: 80,
        display: "flex",
        alignItems: "center",
        gap: 16,
        zIndex: 50,
      }}
    >
      <div
        style={{
          display: "inline-flex",
          alignItems: "center",
          gap: 10,
          padding: "8px 18px",
          borderRadius: 999,
          backgroundColor: "rgba(0, 106, 96, 0.25)",
          border: "1.5px solid rgba(16, 185, 129, 0.6)",
          backdropFilter: "blur(10px)",
        }}
      >
        <span
          style={{
            fontFamily: "Inter, sans-serif",
            fontSize: 15,
            fontWeight: 800,
            color: "#10B981",
            letterSpacing: "0.08em",
          }}
        >
          {stepNumber}
        </span>
        <span
          style={{
            width: 4,
            height: 4,
            borderRadius: "50%",
            backgroundColor: "#94A3B8",
          }}
        />
        <span
          style={{
            fontFamily: "Inter, sans-serif",
            fontSize: 14,
            fontWeight: 700,
            color: "#E2E8F0",
            letterSpacing: "0.05em",
            textTransform: "uppercase",
          }}
        >
          {category}
        </span>
      </div>

      <div
        style={{
          fontFamily: "Inter, sans-serif",
          fontSize: 22,
          fontWeight: 700,
          color: "#F8FAFC",
          letterSpacing: "-0.01em",
        }}
      >
        {title}
      </div>
    </div>
  );
};
