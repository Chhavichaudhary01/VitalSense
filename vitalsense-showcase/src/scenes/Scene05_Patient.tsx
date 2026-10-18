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

export const Scene05_Patient: React.FC<SceneProps> = ({
  showCaptions = true,
  showOverlays = true,
  audioFile = "audio/scene05.mp3",
  narrationText = "For patients like Ramesh, the dashboard presents a simple, icon-driven interface. Vitals like pulse rate and blood oxygen can be captured seamlessly from connected health sensors and saved directly to the offline health record.",
}) => {
  const sceneCallouts = (calloutsData as CalloutDef[]).filter((c) => c.scene === "scene05");

  return (
    <div style={{ position: "relative", width: 1920, height: 1080, overflow: "hidden" }}>
      <Background />
      {showOverlays && (
        <StepBadge stepNumber="05 / 13" category="PATIENT PORTAL" title="Low-Literacy UI & IoT Vitals Ingestion" />
      )}

      {/* Phone chassis with normalized Clip 2 (Patient Dashboard & Vitals) */}
      <PhoneFrame
        videoSrc="footage/Screen_Recording_20260919_121836_VitalSense.mp4"
        startFromFrame={120} // ~4s in
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
