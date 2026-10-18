import React from "react";
import { Callout, CalloutDef } from "../components/Callout";
import { StepBadge } from "../components/StepBadge";
import calloutsData from "../data/callouts.json";

interface OverlayProps {
  sceneId: string;
  stepNumber: string;
  category: string;
  title: string;
}

export const OverlayView: React.FC<OverlayProps> = ({
  sceneId,
  stepNumber,
  category,
  title,
}) => {
  const sceneCallouts = (calloutsData as CalloutDef[]).filter((c) => c.scene === sceneId);

  return (
    <div
      style={{
        position: "relative",
        width: 1920,
        height: 1080,
        backgroundColor: "transparent",
        overflow: "hidden",
      }}
    >
      <StepBadge stepNumber={stepNumber} category={category} title={title} />
      {sceneCallouts.map((c, i) => (
        <Callout key={i} def={c} />
      ))}
    </div>
  );
};
