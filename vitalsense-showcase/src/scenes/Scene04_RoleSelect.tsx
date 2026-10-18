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

export const Scene04_RoleSelect: React.FC<SceneProps> = ({
  showCaptions = true,
  showOverlays = true,
  audioFile = "audio/scene04.mp3",
  narrationText = "At launch, users select their role with a single tap. The interface instantly adapts to English, Hindi, Tamil, or Marathi, making navigation natural for vernacular communities.",
}) => {
  const sceneCallouts = (calloutsData as CalloutDef[]).filter((c) => c.scene === "scene04");

  return (
    <div style={{ position: "relative", width: 1920, height: 1080, overflow: "hidden" }}>
      <Background />
      {showOverlays && (
        <StepBadge stepNumber="04 / 13" category="SYSTEM ONBOARDING" title="Role-Gated Portals & Instant Localization" />
      )}

      {/* Phone chassis with normalized Clip 5 (multilingual switching) */}
      <PhoneFrame
        videoSrc="footage/Screen_Recording_20260919_122813_VitalSense.mp4"
        startFromFrame={90} // ~3s into recording
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
