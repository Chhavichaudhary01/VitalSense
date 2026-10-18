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

export const Scene08_Doctor: React.FC<SceneProps> = ({
  showCaptions = true,
  showOverlays = true,
  audioFile = "audio/scene08.mp3",
  narrationText = "Doctors receive an organized OPD queue sorted by clinical severity. From their consultation desk, doctors review longitudinal history, issue digital prescriptions, manage IPD bed admissions, and directly refer high-risk cases to tertiary specialists.",
}) => {
  const sceneCallouts = (calloutsData as CalloutDef[]).filter((c) => c.scene === "scene08");

  return (
    <div style={{ position: "relative", width: 1920, height: 1080, overflow: "hidden" }}>
      <Background />
      {showOverlays && (
        <StepBadge stepNumber="08 / 13" category="DOCTOR PORTAL" title="Severity Triage & Closed-Loop Specialist Referrals" />
      )}

      {/* Phone chassis with Doctor Portal recording */}
      <PhoneFrame
        videoSrc="footage/Screen_Recording_20260919_122252_VitalSense.mp4"
        startFromFrame={150} // ~5s in
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
