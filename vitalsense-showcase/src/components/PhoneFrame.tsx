import React from "react";
import { OffthreadVideo, staticFile } from "remotion";

export const PHONE_WIDTH = 567;
export const PHONE_HEIGHT = 950;
export const PHONE_LEFT = (1920 - PHONE_WIDTH) / 2; // 676.5px
export const PHONE_TOP = (1080 - PHONE_HEIGHT) / 2; // 65px

interface PhoneFrameProps {
  videoSrc?: string;
  startFromFrame?: number;
  endAtFrame?: number;
  playbackRate?: number;
  style?: React.CSSProperties;
  children?: React.ReactNode;
}

export const PhoneFrame: React.FC<PhoneFrameProps> = ({
  videoSrc,
  startFromFrame = 0,
  endAtFrame,
  playbackRate = 1,
  style,
  children,
}) => {
  return (
    <div
      style={{
        position: "absolute",
        left: PHONE_LEFT,
        top: PHONE_TOP,
        width: PHONE_WIDTH,
        height: PHONE_HEIGHT,
        borderRadius: 42,
        backgroundColor: "#0F172A",
        border: "10px solid #1E293B",
        outline: "2px solid rgba(255, 255, 255, 0.1)",
        boxShadow: "0 30px 70px -15px rgba(0, 0, 0, 0.8), 0 0 35px rgba(0, 106, 96, 0.25)",
        overflow: "hidden",
        display: "flex",
        flexDirection: "column",
        zIndex: 10,
        ...style,
      }}
    >
      {/* Front camera punch hole */}
      <div
        style={{
          position: "absolute",
          top: 10,
          left: "50%",
          transform: "translateX(-50%)",
          width: 14,
          height: 14,
          borderRadius: "50%",
          backgroundColor: "#000000",
          border: "1.5px solid rgba(255, 255, 255, 0.15)",
          zIndex: 50,
          boxShadow: "inset 0 1px 3px rgba(0, 0, 0, 0.9)",
        }}
      />

      {/* Screen container */}
      <div
        style={{
          position: "relative",
          width: "100%",
          height: "100%",
          backgroundColor: "#F8FAFC",
          overflow: "hidden",
        }}
      >
        {videoSrc && (
          <OffthreadVideo
            src={staticFile(videoSrc)}
            startFrom={startFromFrame}
            endAt={endAtFrame}
            playbackRate={playbackRate}
            style={{
              width: "100%",
              height: "100%",
              objectFit: "cover",
            }}
          />
        )}
        {children}
      </div>
    </div>
  );
};
