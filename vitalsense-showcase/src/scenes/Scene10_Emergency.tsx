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

export const Scene10_Emergency: React.FC<SceneProps> = ({
  showCaptions = true,
  showOverlays = true,
  audioFile = "audio/scene10.mp3",
  narrationText = "In a critical emergency, one tap triggers the SOS system. It transmits GPS coordinates to nearby doctors and ASHA workers, and automatically falls back to cellular SMS when mobile data is unavailable.",
}) => {
  const sceneCallouts = (calloutsData as CalloutDef[]).filter((c) => c.scene === "scene10");

  return (
    <div style={{ position: "relative", width: 1920, height: 1080, overflow: "hidden" }}>
      <Background />
      {showOverlays && (
        <StepBadge stepNumber="10 / 13" category="EMERGENCY DISPATCH" title="GPS Distress Alert & Cellular SMS Fallback" />
      )}

      {/* Phone chassis with Emergency SOS recording */}
      <PhoneFrame
        videoSrc="footage/Screen_Recording_20260919_121836_VitalSense.mp4"
        startFromFrame={6510} // ~3:37 in
      />

      {/* Synchronized Callouts */}
      {showOverlays &&
        sceneCallouts.map((c, i) => (
          <Callout key={i} def={c} />
        ))}

      <Captions text={narrationText} showCaptions={showCaptions} />
      {audioFile && <Audio src={staticFile(audioFile)} />}
    </div>
  );
};
