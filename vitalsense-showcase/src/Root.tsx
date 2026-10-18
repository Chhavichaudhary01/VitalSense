import React from "react";
import { Composition, Sequence } from "remotion";
import timingData from "./data/timing.json";
import { Scene01_Title } from "./scenes/Scene01_Title";
import { Scene02_Problem } from "./scenes/Scene02_Problem";
import { Scene03_Solution } from "./scenes/Scene03_Solution";
import { Scene04_RoleSelect } from "./scenes/Scene04_RoleSelect";
import { Scene05_Patient } from "./scenes/Scene05_Patient";
import { Scene06_Asha } from "./scenes/Scene06_Asha";
import { Scene07_Prescription } from "./scenes/Scene07_Prescription";
import { Scene08_Doctor } from "./scenes/Scene08_Doctor";
import { Scene09_Admin } from "./scenes/Scene09_Admin";
import { Scene10_Emergency } from "./scenes/Scene10_Emergency";
import { Scene11_MentalWellness } from "./scenes/Scene11_MentalWellness";
import { Scene12_Architecture } from "./scenes/Scene12_Architecture";
import { Scene13_Outro } from "./scenes/Scene13_Outro";
import { OverlayView } from "./scenes/OverlayView";

const sceneComponents: { [key: string]: React.FC<any> } = {
  scene01: Scene01_Title,
  scene02: Scene02_Problem,
  scene03: Scene03_Solution,
  scene04: Scene04_RoleSelect,
  scene05: Scene05_Patient,
  scene06: Scene06_Asha,
  scene07: Scene07_Prescription,
  scene08: Scene08_Doctor,
  scene09: Scene09_Admin,
  scene10: Scene10_Emergency,
  scene11: Scene11_MentalWellness,
  scene12: Scene12_Architecture,
  scene13: Scene13_Outro,
};

interface VideoProps {
  showCaptions?: boolean;
  showOverlays?: boolean;
  showAudio?: boolean;
}

export const MainVideo: React.FC<VideoProps> = ({
  showCaptions = true,
  showOverlays = true,
  showAudio = true,
}) => {
  let currentFrameOffset = 0;

  return (
    <div style={{ position: "relative", width: 1920, height: 1080, backgroundColor: "#070D14" }}>
      {timingData.scenes.map((scene) => {
        const SceneComp = sceneComponents[scene.id];
        const fromFrame = currentFrameOffset;
        const duration = scene.frames;
        currentFrameOffset += duration;

        if (!SceneComp) return null;

        return (
          <Sequence key={scene.id} from={fromFrame} durationInFrames={duration}>
            <SceneComp
              showCaptions={showCaptions}
              showOverlays={showOverlays}
              audioFile={showAudio ? scene.audio_file : undefined}
              narrationText={scene.text}
            />
          </Sequence>
        );
      })}
    </div>
  );
};

export const RemotionRoot: React.FC = () => {
  const totalFrames = timingData.total_frames; // 6546 frames @ 30 fps (~218.2s / 3m 38s)

  return (
    <>
      {/* Master Showcase Video */}
      <Composition
        id="Main"
        component={MainVideo}
        durationInFrames={totalFrames}
        fps={30}
        width={1920}
        height={1080}
        defaultProps={{
          showCaptions: true,
          showOverlays: true,
          showAudio: true,
        }}
      />

      {/* Clean Footage & Layout Composition (No Overlays, No Audio, No Captions) */}
      <Composition
        id="Clean"
        component={MainVideo}
        durationInFrames={totalFrames}
        fps={30}
        width={1920}
        height={1080}
        defaultProps={{
          showCaptions: false,
          showOverlays: false,
          showAudio: false,
        }}
      />

      {/* Individual Scene Compositions for Clipchamp Modular Editing */}
      {timingData.scenes.map((scene) => {
        const SceneComp = sceneComponents[scene.id];
        if (!SceneComp) return null;

        return (
          <Composition
            key={scene.id}
            id={scene.id.toUpperCase()}
            component={SceneComp}
            durationInFrames={scene.frames}
            fps={30}
            width={1920}
            height={1080}
            defaultProps={{
              showCaptions: true,
              audioFile: scene.audio_file,
              narrationText: scene.text,
            }}
          />
        );
      })}

      {/* Transparent Overlay Compositions for Modular Clipchamp Editing */}
      {[
        { id: "scene02", num: "02 / 13", cat: "PROBLEM LANDSCAPE", tit: "Rural Healthcare Barriers in Underserved Areas" },
        { id: "scene03", num: "03 / 13", cat: "THE SOLUTION", tit: "VitalSense (SehatSetu) Integrated Architecture" },
        { id: "scene04", num: "04 / 13", cat: "SYSTEM ONBOARDING", tit: "Role-Gated Portals & Instant Localization" },
        { id: "scene05", num: "05 / 13", cat: "PATIENT PORTAL", tit: "Low-Literacy UI & IoT Vitals Ingestion" },
        { id: "scene06", num: "06 / 13", cat: "ASHA WORKER PORTAL", tit: "Offline Field Vitals & Care Tracking" },
        { id: "scene07", num: "07 / 13", cat: "PRESCRIPTION OCR", tit: "On-Device ML Kit Scanner & Medicine Stock" },
        { id: "scene08", num: "08 / 13", cat: "DOCTOR PORTAL", tit: "Severity Triage & Closed-Loop Specialist Referrals" },
        { id: "scene09", num: "09 / 13", cat: "HEALTH ADMIN", tit: "Real-Time Disease Surveillance & Bed Management" },
        { id: "scene10", num: "10 / 13", cat: "EMERGENCY SOS", tit: "Instant 3-Second Dispatch & Offline SMS Fallback" },
        { id: "scene11", num: "11 / 13", cat: "COMMUNITY WELLNESS", tit: "Icon-Driven Mental Health & Calming Exercises" },
        { id: "scene12", num: "12 / 13", cat: "SYSTEM ARCHITECTURE", tit: "Offline-First Room Outbox & WorkManager Sync Engine" },
      ].map((ov) => (
        <Composition
          key={`overlay_${ov.id}`}
          id={`OVERLAY-${ov.id.toUpperCase()}`}
          component={OverlayView}
          durationInFrames={600}
          fps={30}
          width={1920}
          height={1080}
          defaultProps={{
            sceneId: ov.id,
            stepNumber: ov.num,
            category: ov.cat,
            title: ov.tit,
          }}
        />
      ))}
    </>
  );
};
