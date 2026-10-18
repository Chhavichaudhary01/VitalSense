import os
import subprocess
import json
import torch
from transformers import pipeline

AUDIO_DIR = r"C:\Users\saras\OneDrive\Documents\sehatSetu\audio"
OUT_DIR = os.path.join(AUDIO_DIR, "transcripts")
os.makedirs(OUT_DIR, exist_ok=True)

files = [f"{i}.ogg" for i in range(1, 11)]

device = 0 if torch.cuda.is_available() else -1
print(f"Loading Whisper pipeline on device {device}...")
asr = pipeline(
    "automatic-speech-recognition",
    model="openai/whisper-base",
    device=device
)

results = []

for idx, fname in enumerate(files, start=1):
    ogg_path = os.path.join(AUDIO_DIR, fname)
    if not os.path.exists(ogg_path):
        print(f"File not found: {ogg_path}")
        continue
    
    # Probe duration
    probe_cmd = [
        "ffprobe", "-v", "error",
        "-show_entries", "format=duration",
        "-of", "json", ogg_path
    ]
    p = subprocess.run(probe_cmd, capture_output=True, text=True, shell=True)
    duration = 0.0
    try:
        dur_json = json.loads(p.stdout)
        duration = float(dur_json["format"]["duration"])
    except Exception:
        pass

    txt_filename = f"{idx:02d}_transcript.txt"
    txt_path = os.path.join(OUT_DIR, txt_filename)

    # If already transcribed, load it
    if os.path.exists(txt_path) and os.path.getsize(txt_path) > 0 and idx != 10:
        with open(txt_path, "r", encoding="utf-8") as f:
            text = f.read().strip()
        print(f"Loaded existing {txt_filename} ({duration:.2f}s)")
    else:
        # Convert to temp wav
        temp_wav = os.path.join(OUT_DIR, f"temp_{idx}.wav")
        subprocess.run(["ffmpeg", "-y", "-i", ogg_path, "-ar", "16000", "-ac", "1", temp_wav], capture_output=True, shell=True)

        # Transcribe with return_timestamps=True for files > 30s
        print(f"Transcribing {fname} ({duration:.2f}s)...")
        res = asr(temp_wav, return_timestamps=True, generate_kwargs={"language": "english", "task": "transcribe"})
        text = res["text"].strip()
        
        # Remove temp wav
        if os.path.exists(temp_wav):
            os.remove(temp_wav)

        with open(txt_path, "w", encoding="utf-8") as f:
            f.write(text + "\n")

    results.append({
        "index": idx,
        "filename": fname,
        "duration": duration,
        "text": text
    })
    print(f"[{idx:02d}] {text}\n")

# Write master markdown file
master_path = os.path.join(AUDIO_DIR, "TRANSCRIPT_MASTER.md")
with open(master_path, "w", encoding="utf-8") as f:
    f.write("# VitalSense — Audio Recordings Master Transcripts\n\n")
    f.write(f"**Total Files:** {len(results)}\n\n")
    f.write("| # | Audio File | Duration | Transcribed Script |\n")
    f.write("|---|---|---|---|\n")
    for r in results:
        f.write(f"| **{r['index']:02d}** | `{r['filename']}` | {r['duration']:.2f}s | {r['text']} |\n")
    
    f.write("\n---\n\n## Detailed Scene-by-Scene Scripts\n\n")
    for r in results:
        f.write(f"### Audio File {r['index']:02d} (`{r['filename']}` — {r['duration']:.2f}s)\n\n")
        f.write(f"> {r['text']}\n\n")

print(f"Saved master transcript to {master_path}")
print("ALL_DONE_SUCCESS")
