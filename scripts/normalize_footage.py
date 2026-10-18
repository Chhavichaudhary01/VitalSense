import os
import glob
import subprocess

FOOTAGE_DIR = r"C:\Users\saras\OneDrive\Documents\sehatSetu\video"
SHOWCASE_DIR = r"C:\Users\saras\OneDrive\Documents\sehatSetu\vitalsense-showcase"
PUBLIC_FOOTAGE_DIR = os.path.join(SHOWCASE_DIR, "public", "footage")
os.makedirs(PUBLIC_FOOTAGE_DIR, exist_ok=True)

files = sorted(glob.glob(os.path.join(FOOTAGE_DIR, "*.mp4")))

for fpath in files:
    fname = os.path.basename(fpath)
    out_path = os.path.join(PUBLIC_FOOTAGE_DIR, fname)
    if os.path.exists(out_path):
        print(f"Already exists: {out_path}")
        continue
    
    print(f"Normalizing {fname} to 30 fps constant frame rate...")
    cmd = [
        "ffmpeg",
        "-y",
        "-i", fpath,
        "-vf", "fps=30",
        "-c:v", "libx264",
        "-crf", "14",
        "-pix_fmt", "yuv420p",
        "-an",
        out_path
    ]
    res = subprocess.run(cmd)
    if res.returncode == 0:
        print(f"Successfully normalized {fname}")
    else:
        print(f"Error normalizing {fname}")

print("All footage normalized.")
