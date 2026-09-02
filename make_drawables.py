import os

os.makedirs('app/src/main/res/drawable', exist_ok=True)

bg_input_field = '''<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
    <corners android:radius="14dp" />
    <solid android:color="@color/surface_card_light" />
    <stroke
        android:width="1dp"
        android:color="@color/border_subtle_light" />
    <padding
        android:left="14dp"
        android:top="14dp"
        android:right="14dp"
        android:bottom="14dp" />
</shape>
'''

bg_floating_island = '''<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
    <corners android:radius="28dp" />
    <solid android:color="@color/surface_card_light" />
    <stroke
        android:width="1dp"
        android:color="@color/border_subtle_light" />
</shape>
'''

bg_status_chip_urgent = '''<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
    <corners android:radius="50dp" />
    <solid android:color="@color/status_urgent_container" />
    <stroke
        android:width="1dp"
        android:color="@color/status_urgent_red" />
    <padding
        android:left="8dp"
        android:top="3dp"
        android:right="8dp"
        android:bottom="3dp" />
</shape>
'''

bg_status_chip_progress = '''<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
    <corners android:radius="50dp" />
    <solid android:color="@color/status_progress_container" />
    <stroke
        android:width="1dp"
        android:color="@color/status_progress_amber" />
    <padding
        android:left="8dp"
        android:top="3dp"
        android:right="8dp"
        android:bottom="3dp" />
</shape>
'''

bg_status_chip_normal = '''<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
    <corners android:radius="50dp" />
    <solid android:color="@color/status_normal_container" />
    <stroke
        android:width="1dp"
        android:color="@color/status_normal_emerald" />
    <padding
        android:left="8dp"
        android:top="3dp"
        android:right="8dp"
        android:bottom="3dp" />
</shape>
'''

with open('app/src/main/res/drawable/bg_input_field.xml', 'w', encoding='utf-8') as f:
    f.write(bg_input_field)

with open('app/src/main/res/drawable/bg_floating_island.xml', 'w', encoding='utf-8') as f:
    f.write(bg_floating_island)

with open('app/src/main/res/drawable/bg_status_chip_urgent.xml', 'w', encoding='utf-8') as f:
    f.write(bg_status_chip_urgent)

with open('app/src/main/res/drawable/bg_status_chip_progress.xml', 'w', encoding='utf-8') as f:
    f.write(bg_status_chip_progress)

with open('app/src/main/res/drawable/bg_status_chip_normal.xml', 'w', encoding='utf-8') as f:
    f.write(bg_status_chip_normal)

print('Wrote XML drawables')
