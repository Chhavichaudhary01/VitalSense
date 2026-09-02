import subprocess
import os

adb = os.path.expandvars(r'%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe')
out_path = r'C:\Users\saras\.gemini\antigravity\brain\1b9a7fe9-0339-424c-8eaa-ab4b486adfe7\screen.png'

data = subprocess.check_output([adb, '-s', 'ZA222JX6C9', 'exec-out', 'screencap', '-p'])
with open(out_path, 'wb') as f:
    f.write(data)
print('Screenshot saved!')
