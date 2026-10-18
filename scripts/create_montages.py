import os
import glob
import subprocess

REFERENCE_DIR = r"C:\Users\saras\OneDrive\Documents\sehatSetu\reference"
CONTACT_DIR = os.path.join(REFERENCE_DIR, "contact_sheets")
MONTAGES_DIR = os.path.join(REFERENCE_DIR, "montages")
os.makedirs(MONTAGES_DIR, exist_ok=True)

clip_dirs = sorted(glob.glob(os.path.join(CONTACT_DIR, "*")))

for cdir in clip_dirs:
    if not os.path.isdir(cdir):
        continue
    cname = os.path.basename(cdir)
    frames = sorted(glob.glob(os.path.join(cdir, "*.jpg")))
    total_frames = len(frames)
    print(f"Clip {cname}: {total_frames} frames")
    
    # We will tile in groups of up to 16 or 20 frames (e.g. 4x4 or 5x4)
    # Using ffmpeg image sequence or python PIL to make clean labeled contact sheets
    from PIL import Image, ImageDraw, ImageFont
    
    # Each thumbnail: width 180, height 300
    thumb_w, thumb_h = 180, 300
    cols = 5
    
    chunk_size = 20
    for chunk_idx in range(0, total_frames, chunk_size):
        chunk_frames = frames[chunk_idx:chunk_idx+chunk_size]
        rows = (len(chunk_frames) + cols - 1) // cols
        
        sheet_w = cols * thumb_w
        sheet_h = rows * thumb_h
        sheet = Image.new("RGB", (sheet_w, sheet_h), (20, 20, 20))
        draw = ImageDraw.Draw(sheet)
        
        for i, fpath in enumerate(chunk_frames):
            c = i % cols
            r = i // cols
            x = c * thumb_w
            y = r * thumb_h
            
            try:
                im = Image.open(fpath)
                im_thumb = im.resize((thumb_w, thumb_h - 24))
                sheet.paste(im_thumb, (x, y))
                
                # frame index and approximate time
                frame_num = int(os.path.splitext(os.path.basename(fpath))[0].split('_')[1])
                # calculate timestamp based on clip
                step = 1.0 if ("120510" in cname or "122813" in cname) else 2.0
                time_sec = (frame_num - 1) * step
                m, s = divmod(int(time_sec), 60)
                time_str = f"{m:02d}:{s:02d}"
                
                draw.rectangle([x, y + thumb_h - 24, x + thumb_w, y + thumb_h], fill=(0, 0, 0))
                draw.text((x + 6, y + thumb_h - 20), f"#{frame_num:03d} ({time_str})", fill=(255, 255, 255))
            except Exception as e:
                print(f"Error on {fpath}: {e}")
                
        sheet_name = f"{cname}_part{chunk_idx//chunk_size + 1}.jpg"
        sheet_path = os.path.join(MONTAGES_DIR, sheet_name)
        sheet.save(sheet_path, quality=85)
        print(f"Saved {sheet_path}")

print("Montages created successfully.")
