import json
import os
import subprocess

TIMING_PATH = r"C:\Users\saras\OneDrive\Documents\sehatSetu\vitalsense-showcase\src\data\timing.json"
MASTER_MP4 = r"C:\Users\saras\OneDrive\Documents\sehatSetu\video\output\VitalSense_SIH_final.mp4"
SCENES_DIR = r"C:\Users\saras\OneDrive\Documents\sehatSetu\video\output\scenes"

SCENE_NAMES = {
    "scene01": "01_title.mp4",
    "scene02": "02_problem.mp4",
    "scene03": "03_solution.mp4",
    "scene04": "04_role_select.mp4",
    "scene05": "05_patient.mp4",
    "scene06": "06_asha.mp4",
    "scene07": "07_prescription.mp4",
    "scene08": "08_doctor.mp4",
    "scene09": "09_admin.mp4",
    "scene10": "10_emergency.mp4",
    "scene11": "11_mental_wellness.mp4",
    "scene12": "12_architecture.mp4",
    "scene13": "13_outro.mp4",
}

with open(TIMING_PATH, "r", encoding="utf-8") as f:
    timing = json.load(f)

current_offset = 0.0

for scene in timing["scenes"]:
    sid = scene["id"]
    dur = scene["duration_sec"]
    out_name = SCENE_NAMES.get(sid, f"{sid}.mp4")
    out_path = os.path.join(SCENES_DIR, out_name)
    
    # We already have 01_title.mp4 from Remotion render, keep it or overwrite
    print(f"Slicing {sid} -> {out_name} (start: {current_offset:.2f}s, dur: {dur:.2f}s)...")
    cmd = [
        "ffmpeg",
        "-y",
        "-ss", f"{current_offset:.3f}",
        "-t", f"{dur:.3f}",
        "-i", MASTER_MP4,
        "-c:v", "libx264",
        "-crf", "16",
        "-c:a", "aac",
        "-b:a", "192k",
        out_path
    ]
    res = subprocess.run(cmd, shell=True)
    if res.returncode != 0:
        print(f"Failed slicing {sid}")
    else:
        print(f"Done {out_name}")
    
    current_offset += dur

print("All scenes sliced successfully!")
