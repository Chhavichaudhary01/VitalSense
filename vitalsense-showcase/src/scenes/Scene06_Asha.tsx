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

export const Scene06_Asha: React.FC<SceneProps> = ({
  showCaptions = true,
  showOverlays = true,
  audioFile = "audio/scene06.mp3",
  narrationText = "Frontline ASHA workers act as digital proxies for non-literate citizens—conducting home visits, updating ABHA digital health cards, and tracking a unified longitudinal care journey across clinic visits and lab investigations.",
}) => {
  const sceneCallouts = (calloutsData as CalloutDef[]).filter((c) => c.scene === "scene06");

  return (
    <div style={{ position: "relative", width: 1920, height: 1080, overflow: "hidden" }}>
      <Background />
      {showOverlays && (
        <StepBadge stepNumber="06 / 13" category="FRONTLINE CARE" title="ASHA Proxy Intake & Longitudinal Care Journey" />
      )}

      {/* Phone chassis showing ABHA Health Card and Care Journey Timeline */}
      <PhoneFrame
        videoSrc="footage/Screen_Recording_20260919_121836_VitalSense.mp4"
        startFromFrame={1080} // ~36s in
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
