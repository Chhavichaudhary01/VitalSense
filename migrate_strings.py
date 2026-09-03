import os
import re

localization_file = r"app\src\main\java\com\vitalsense\app\core\ui\theme\Localization.kt"

def extract_strings(file_path):
    with open(file_path, "r", encoding="utf-8") as f:
        content = f.read()

    classes = ["EnglishAppStrings", "HindiAppStrings", "TamilAppStrings", "MarathiAppStrings"]
    lang_dirs = ["values", "values-hi", "values-ta", "values-mr"]
    
    for cls, lang_dir in zip(classes, lang_dirs):
        match = re.search(f"class {cls} : AppStrings {{(.*?)}}", content, re.DOTALL)
        if not match:
            print(f"Could not find {cls}")
            continue
        
        class_content = match.group(1)
        strings = {}
        
        for line in class_content.split('\n'):
            line = line.strip()
            if line.startswith("override val "):
                # override val appName: String = "VitalSense"
                prop_match = re.match(r'override val (\w+):\s*String\s*=\s*"(.*?)"', line)
                if prop_match:
                    name = prop_match.group(1)
                    val = prop_match.group(2)
                    strings[name] = val
        
        out_dir = os.path.join(r"app\src\main\res", lang_dir)
        os.makedirs(out_dir, exist_ok=True)
        out_file = os.path.join(out_dir, "strings.xml")
        
        with open(out_file, "w", encoding="utf-8") as f_out:
            f_out.write('<?xml version="1.0" encoding="utf-8"?>\n')
            f_out.write('<resources>\n')
            f_out.write('    <string name="app_name">VitalSense</string>\n')
            for name, val in strings.items():
                if name == "appName": continue
                # Escape single quotes and ampersands
                val_escaped = val.replace("'", "\\'").replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                f_out.write(f'    <string name="{name}">{val_escaped}</string>\n')
            f_out.write('</resources>\n')
            
        print(f"Generated {out_file}")

if __name__ == "__main__":
    extract_strings(localization_file)
