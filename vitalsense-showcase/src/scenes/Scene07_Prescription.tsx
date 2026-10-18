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

export const Scene07_Prescription: React.FC<SceneProps> = ({
  showCaptions = true,
  showOverlays = true,
  audioFile = "audio/scene07.mp3",
  narrationText = "A photo of a paper prescription is processed on-device with ML Kit OCR, auto-cropped, and digitized into structured dosages. Patients can then locate prescribed medicines at nearby Jan Aushadhi stores with real-time stock indicators.",
}) => {
  const sceneCallouts = (calloutsData as CalloutDef[]).filter((c) => c.scene === "scene07");

  return (
    <div style={{ position: "relative", width: 1920, height: 1080, overflow: "hidden" }}>
      <Background />
      {showOverlays && (
        <StepBadge stepNumber="07 / 13" category="INTELLIGENT PRESCRIPTION" title="On-Device ML Kit OCR & Jan Aushadhi Locator" />
      )}

      {/* Phone chassis showing Camera Scanner & Medicine Stock Locator */}
      <PhoneFrame
        videoSrc="footage/Screen_Recording_20260919_121836_VitalSense.mp4"
        startFromFrame={5400} // ~3:00 in
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
