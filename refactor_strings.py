import os
import re

src_dir = r"app\src\main\java"

def refactor_file(filepath):
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()

    original_content = content

    # Remove the `val strings = LocalAppStrings.current` declaration
    content = re.sub(r'[ \t]*val\s+strings\s*=\s*LocalAppStrings\.current\n?', '', content)
    
    # Remove imports
    content = re.sub(r'import\s+com\.vitalsense\.app\.core\.ui\.theme\.LocalAppStrings\n?', '', content)
    content = re.sub(r'import\s+com\.vitalsense\.app\.core\.ui\.theme\.AppStrings\n?', '', content)

    # Replace `strings.propertyName` with `stringResource(R.string.propertyName)`
    # Be careful not to replace `strings` if it's something else, but here it's overwhelmingly LocalAppStrings
    content = re.sub(r'strings\.([a-zA-Z0-9_]+)', r'stringResource(R.string.\1)', content)
    
    # Replace `LocalAppStrings.current.propertyName`
    content = re.sub(r'LocalAppStrings\.current\.([a-zA-Z0-9_]+)', r'stringResource(R.string.\1)', content)

    if content != original_content:
        # Check if we need to add imports
        imports_to_add = []
        if 'stringResource(R.string.' in content:
            if 'import androidx.compose.ui.res.stringResource' not in content:
                imports_to_add.append('import androidx.compose.ui.res.stringResource')
            if 'import com.vitalsense.app.R' not in content:
                imports_to_add.append('import com.vitalsense.app.R')

        if imports_to_add:
            # Find the last import statement or the package declaration
            import_block_end = 0
            lines = content.split('\n')
            for i, line in enumerate(lines):
                if line.startswith('import ') or line.startswith('package '):
                    import_block_end = i
            
            # Insert new imports after the last import
            for imp in imports_to_add:
                lines.insert(import_block_end + 1, imp)
            
            content = '\n'.join(lines)

        with open(filepath, 'w', encoding='utf-8') as f:
            f.write(content)
        print(f"Refactored {filepath}")

if __name__ == "__main__":
    for root, dirs, files in os.walk(src_dir):
        for file in files:
            if file.endswith('.kt'):
                refactor_file(os.path.join(root, file))
