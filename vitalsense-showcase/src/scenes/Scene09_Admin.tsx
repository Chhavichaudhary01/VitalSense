import React from "react";
import { Audio, staticFile } from "remotion";
import { Background } from "../components/Background";
import { Callout, CalloutDef } from "../components/Callout";
import { Captions } from "../components/Captions";
import { PhoneFrame } from "../components/PhoneFrame";
import { StepBadge } from "../components/StepBadge";
import calloutsData from "../data/callouts.json";

interface SceneProps {
  showCaptions?: boolean;
  showOverlays?: boolean;
  audioFile?: string;
  narrationText?: string;
}

export const Scene09_Admin: React.FC<SceneProps> = ({
  showCaptions = true,
  showOverlays = true,
  audioFile = "audio/scene09.mp3",
  narrationText = "District health officers monitor real-time disease outbreak heat maps and longitudinal symptom curves. When an outbreak like malaria is detected, administrators broadcast instant health directives to frontline workers across affected villages.",
}) => {
  const sceneCallouts = (calloutsData as CalloutDef[]).filter((c) => c.scene === "scene09");

  return (
    <div style={{ position: "relative", width: 1920, height: 1080, overflow: "hidden" }}>
      <Background />
      {showOverlays && (
        <StepBadge stepNumber="09 / 13" category="DISTRICT SURVEILLANCE" title="Outbreak Heatmaps & Broadcast Health Directives" />
      )}

      {/* Phone chassis with Admin Portal Outbreak Heatmap & Directives */}
      <PhoneFrame
        videoSrc="footage/Screen_Recording_20260919_122712_VitalSense.mp4"
        startFromFrame={690} // ~23s in
      />

      {/* Callouts */}
      {showOverlays &&
        sceneCallouts.map((c, i) => (
          <Callout key={i} def={c} />
        ))}

      <Captions text={narrationText} showCaptions={showCaptions} />
      {audioFile && <Audio src={staticFile(audioFile)} />}
    </div>
  );
};
