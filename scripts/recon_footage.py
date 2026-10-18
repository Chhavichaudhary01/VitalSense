import os
import json
import subprocess
import glob

FOOTAGE_DIR = r"C:\Users\saras\OneDrive\Documents\sehatSetu\video"
REFERENCE_DIR = r"C:\Users\saras\OneDrive\Documents\sehatSetu\reference"
CONTACT_DIR = os.path.join(REFERENCE_DIR, "contact_sheets")
os.makedirs(CONTACT_DIR, exist_ok=True)

# Find mp4 files in FOOTAGE_DIR
mp4_files = sorted(glob.glob(os.path.join(FOOTAGE_DIR, "*.mp4")))

probe_results = []

for filepath in mp4_files:
    filename = os.path.basename(filepath)
    clip_id = os.path.splitext(filename)[0]
    clip_contact_dir = os.path.join(CONTACT_DIR, clip_id)
    os.makedirs(clip_contact_dir, exist_ok=True)
    
    # Run ffprobe
    cmd = [
        "ffprobe",
        "-v", "quiet",
        "-print_format", "json",
        "-show_format",
        "-show_streams",
        filepath
    ]
    res = subprocess.run(cmd, capture_output=True, text=True)
    if res.returncode != 0:
        print(f"Error probing {filename}: {res.stderr}")
        continue
    
    data = json.loads(res.stdout)
    video_stream = next((s for s in data.get("streams", []) if s.get("codec_type") == "video"), None)
    audio_stream = next((s for s in data.get("streams", []) if s.get("codec_type") == "audio"), None)
    
    width = int(video_stream.get("width", 0)) if video_stream else 0
    height = int(video_stream.get("height", 0)) if video_stream else 0
    orientation = "portrait" if height > width else "landscape"
    duration = float(data.get("format", {}).get("duration", 0))
    codec = video_stream.get("codec_name", "unknown") if video_stream else "none"
    r_fps = video_stream.get("r_frame_rate", "")
    avg_fps = video_stream.get("avg_frame_rate", "")
    
    # Check variable frame rate: if r_frame_rate != avg_frame_rate
    is_vfr = r_fps != avg_fps
    
    probe_results.append({
        "filename": filename,
        "filepath": filepath,
        "width": width,
        "height": height,
        "orientation": orientation,
        "duration_sec": round(duration, 2),
        "codec": codec,
        "r_frame_rate": r_fps,
        "avg_frame_rate": avg_fps,
        "is_vfr": is_vfr,
        "has_audio": audio_stream is not None,
        "audio_codec": audio_stream.get("codec_name") if audio_stream else None
    })
    
    # Extract frames: 1 frame every 2 seconds
    # For shorter clips, extract 1 frame per second
    fps_rate = "0.5" if duration > 30 else "1"
    frame_pattern = os.path.join(clip_contact_dir, "frame_%04d.jpg")
    
    # Check if frames already exist
    existing_frames = glob.glob(os.path.join(clip_contact_dir, "*.jpg"))
    if not existing_frames:
        extract_cmd = [
            "ffmpeg",
            "-y",
            "-i", filepath,
            "-vf", f"fps={fps_rate},scale=720:-1",
            "-q:v", "4",
            frame_pattern
        ]
        subprocess.run(extract_cmd, capture_output=True)

with open(os.path.join(REFERENCE_DIR, "ffprobe_summary.json"), "w") as f:
    json.dump(probe_results, f, indent=2)

print("FFprobe summary and frame extraction complete.")
