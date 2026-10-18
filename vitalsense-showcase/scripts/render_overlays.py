import subprocess
import os
import sys

OVERLAYS = [
    ("OVERLAY-SCENE02", "02_problem_overlay.png", 90),
    ("OVERLAY-SCENE03", "03_solution_overlay.png", 90),
    ("OVERLAY-SCENE04", "04_role_select_overlay.png", 90),
    ("OVERLAY-SCENE05", "05_patient_overlay.png", 90),
    ("OVERLAY-SCENE06", "06_asha_overlay.png", 90),
    ("OVERLAY-SCENE07", "07_prescription_overlay.png", 90),
    ("OVERLAY-SCENE08", "08_doctor_overlay.png", 90),
    ("OVERLAY-SCENE09", "09_admin_overlay.png", 90),
    ("OVERLAY-SCENE10", "10_emergency_overlay.png", 90),
    ("OVERLAY-SCENE11", "11_mental_wellness_overlay.png", 90),
    ("OVERLAY-SCENE12", "12_architecture_overlay.png", 90),
]

OUT_DIR = r"C:\Users\saras\OneDrive\Documents\sehatSetu\video\output\overlays"
CWD = r"C:\Users\saras\OneDrive\Documents\sehatSetu\vitalsense-showcase"

os.makedirs(OUT_DIR, exist_ok=True)

for comp, filename, frame in OVERLAYS:
    target_path = os.path.join(OUT_DIR, filename)
    print(f"Rendering {comp} -> {filename} at frame {frame}...")
    cmd = [
        "npx.cmd",
        "remotion",
        "still",
        "src/index.ts",
        comp,
        target_path,
        f"--frame={frame}",
        "--image-format=png",
        "--overwrite"
    ]
    res = subprocess.run(cmd, cwd=CWD, shell=True)
    if res.returncode != 0:
        print(f"Failed to render {comp}")
    else:
        print(f"Successfully rendered {filename}")

print("All overlays completed!")
