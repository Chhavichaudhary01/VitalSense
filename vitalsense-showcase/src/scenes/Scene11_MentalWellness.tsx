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

export const Scene11_MentalWellness: React.FC<SceneProps> = ({
  showCaptions = true,
  showOverlays = true,
  audioFile = "audio/scene11.mp3",
  narrationText = "To address mental health in rural areas without stigma, VitalSense offers simple, icon-based emotional check-ins and guided box breathing exercises that guide users with visual cues.",
}) => {
  const sceneCallouts = (calloutsData as CalloutDef[]).filter((c) => c.scene === "scene11");

  return (
    <div style={{ position: "relative", width: 1920, height: 1080, overflow: "hidden" }}>
      <Background />
      {showOverlays && (
        <StepBadge stepNumber="11 / 13" category="PREVENTIVE HEALTH" title="Non-Stigmatized Check-Ins & Guided Breathing" />
      )}

      {/* Phone chassis with Mental Wellness recording */}
      <PhoneFrame
        videoSrc="footage/Screen_Recording_20260919_121836_VitalSense.mp4"
        startFromFrame={2910} // ~1:37 in
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
