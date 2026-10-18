import os
import subprocess
import torch
from transformers import pipeline

AUDIO_DIR = r"C:\Users\saras\OneDrive\Documents\sehatSetu\audio"
test_ogg = os.path.join(AUDIO_DIR, "1.ogg")
test_wav = os.path.join(AUDIO_DIR, "1_test.wav")

# Convert to wav 16kHz mono using ffmpeg
subprocess.run(["ffmpeg", "-y", "-i", test_ogg, "-ar", "16000", "-ac", "1", test_wav], shell=True, check=True)

device = 0 if torch.cuda.is_available() else -1
print(f"Loading whisper-base on device {device}...")
asr = pipeline("automatic-speech-recognition", model="openai/whisper-base", device=device)

print(f"Transcribing {test_wav}...")
result = asr(test_wav, generate_kwargs={"language": "english", "task": "transcribe"})
print("Transcription:", result["text"])

# Clean up test wav
if os.path.exists(test_wav):
    os.remove(test_wav)
