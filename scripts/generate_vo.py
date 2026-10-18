import os
import json
import asyncio
import subprocess
import edge_tts

ROOT_DIR = r"C:\Users\saras\OneDrive\Documents\sehatSetu"
SHOWCASE_DIR = os.path.join(ROOT_DIR, "vitalsense-showcase")
NARRATION_FILE = os.path.join(SHOWCASE_DIR, "src", "data", "narration.json")
TIMING_FILE = os.path.join(SHOWCASE_DIR, "src", "data", "timing.json")
AUDIO_DIR = os.path.join(SHOWCASE_DIR, "public", "audio")
SUBTITLE_DIR = os.path.join(SHOWCASE_DIR, "public", "subtitles")
TEMP_DIR = os.path.join(SHOWCASE_DIR, "public", "temp_audio")

os.makedirs(AUDIO_DIR, exist_ok=True)
os.makedirs(SUBTITLE_DIR, exist_ok=True)
os.makedirs(TEMP_DIR, exist_ok=True)

VOICE = "en-IN-PrabhatNeural"
RATE = "-2%"

def format_timestamp(seconds: float) -> str:
    millis = int(round((seconds - int(seconds)) * 1000))
    s = int(seconds) % 60
    m = (int(seconds) // 60) % 60
    h = int(seconds) // 3600
    return f"{h:02d}:{m:02d}:{s:02d},{millis:03d}"

async def generate_scene_audio(scene):
    scene_id = scene["id"]
    tts_text = scene.get("tts_text", scene["text"])
    display_text = scene["text"]
    
    raw_mp3 = os.path.join(TEMP_DIR, f"{scene_id}_raw.mp3")
    final_mp3 = os.path.join(AUDIO_DIR, f"{scene_id}.mp3")
    final_wav = os.path.join(AUDIO_DIR, f"{scene_id}.wav")
    srt_path = os.path.join(SUBTITLE_DIR, f"{scene_id}.srt")
    
    print(f"Synthesizing {scene_id} ({VOICE})...")
    communicate = edge_tts.Communicate(tts_text, VOICE, rate=RATE)
    sub_maker = edge_tts.SubMaker()
    
    with open(raw_mp3, "wb") as file:
        async for chunk in communicate.stream():
            if chunk["type"] == "audio":
                file.write(chunk["data"])
            elif chunk["type"] == "WordBoundary":
                sub_maker.feed(chunk)
                
    # Generate scene SRT
    # Offset by 0.4s because of the initial 0.4s silence added in ffmpeg
    with open(srt_path, "w", encoding="utf-8") as srt_file:
        # We write clean display text with 0.4s start offset
        srt_content = sub_maker.get_srt()
        if not srt_content.strip():
            # Fallback if single chunk
            duration_raw = get_audio_duration(raw_mp3)
            start_str = format_timestamp(0.4)
            end_str = format_timestamp(0.4 + duration_raw)
            srt_file.write(f"1\n{start_str} --> {end_str}\n{display_text}\n\n")
        else:
            # We can write the generated srt or a clean subtitle
            srt_file.write(srt_content)
            
    # Process audio with ffmpeg:
    # 1. 0.4s delay at start (adelay=400|400)
    # 2. 0.6s pad at end (apad=pad_dur=0.6)
    # 3. loudnorm to -16 LUFS
    # 4. 48kHz stereo 16-bit
    filter_complex = "adelay=400|400,apad=pad_dur=0.6,loudnorm=I=-16:LRA=11:TP=-1.5,aformat=sample_fmts=s16:sample_rates=48000:channel_layouts=stereo"
    cmd_mp3 = [
        "ffmpeg", "-y", "-i", raw_mp3,
        "-af", filter_complex,
        "-b:a", "192k",
        final_mp3
    ]
    subprocess.run(cmd_mp3, capture_output=True)
    
    cmd_wav = [
        "ffmpeg", "-y", "-i", final_mp3,
        final_wav
    ]
    subprocess.run(cmd_wav, capture_output=True)
    
    duration = get_audio_duration(final_mp3)
    frames = int(round(duration * 30))
    print(f"Finished {scene_id}: {duration:.2f}s ({frames} frames)")
    return {
        "id": scene_id,
        "title": scene["title"],
        "audio_file": f"audio/{scene_id}.mp3",
        "wav_file": f"audio/{scene_id}.wav",
        "duration_sec": round(duration, 2),
        "frames": frames,
        "text": display_text
    }

def get_audio_duration(filepath):
    cmd = [
        "ffprobe", "-v", "quiet",
        "-print_format", "json",
        "-show_format", filepath
    ]
    res = subprocess.run(cmd, capture_output=True, text=True)
    data = json.loads(res.stdout)
    return float(data.get("format", {}).get("duration", 0))

async def main():
    with open(NARRATION_FILE, "r", encoding="utf-8") as f:
        scenes = json.load(f)
        
    timing_results = []
    for scene in scenes:
        res = await generate_scene_audio(scene)
        timing_results.append(res)
        
    total_frames = sum(s["frames"] for s in timing_results)
    total_sec = sum(s["duration_sec"] for s in timing_results)
    
    timing_data = {
        "fps": 30,
        "total_duration_sec": round(total_sec, 2),
        "total_frames": total_frames,
        "scenes": timing_results
    }
    
    with open(TIMING_FILE, "w", encoding="utf-8") as f:
        json.dump(timing_data, f, indent=2)
        
    print(f"\n==========================================")
    print(f"Total Voiceover Duration: {total_sec:.2f} seconds ({total_sec/60:.2f} mins)")
    print(f"Total Frames: {total_frames} @ 30 fps")
    print(f"Timing JSON saved to {TIMING_FILE}")
    print(f"==========================================")
    
    # Generate full combined master WAV
    concat_list = os.path.join(TEMP_DIR, "concat.txt")
    with open(concat_list, "w") as cl:
        for t in timing_results:
            wav_path = os.path.join(AUDIO_DIR, f"{t['id']}.wav").replace('\\', '/')
            cl.write(f"file '{wav_path}'\n")
            
    master_wav = os.path.join(AUDIO_DIR, "full_voiceover.wav")
    concat_cmd = [
        "ffmpeg", "-y", "-f", "concat", "-safe", "0",
        "-i", concat_list,
        "-c", "copy",
        master_wav
    ]
    subprocess.run(concat_cmd, capture_output=True)
    print(f"Master voiceover rendered: {master_wav}")
    
    # Generate master combined SRT
    master_srt_path = os.path.join(SUBTITLE_DIR, "vitalsense_subtitles.srt")
    current_offset = 0.0
    with open(master_srt_path, "w", encoding="utf-8") as msrt:
        for i, t in enumerate(timing_results):
            start_s = current_offset + 0.4
            end_s = current_offset + t["duration_sec"] - 0.4
            msrt.write(f"{i+1}\n")
            msrt.write(f"{format_timestamp(start_s)} --> {format_timestamp(end_s)}\n")
            msrt.write(f"{t['text']}\n\n")
            current_offset += t["duration_sec"]
    print(f"Master SRT rendered: {master_srt_path}")

if __name__ == "__main__":
    asyncio.run(main())
