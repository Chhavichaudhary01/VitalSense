Add-Type -AssemblyName System.Drawing
[Windows.Globalization.Language,Windows.Foundation.Numerics,ContentType=WindowsRuntime] | Out-Null
[Windows.Media.Ocr.OcrEngine,Windows.Foundation.UniversalApiContract,ContentType=WindowsRuntime] | Out-Null
[Windows.Graphics.Imaging.BitmapDecoder,Windows.Foundation.UniversalApiContract,ContentType=WindowsRuntime] | Out-Null

$engine = [Windows.Media.Ocr.OcrEngine]::TryCreateFromLanguage([Windows.Globalization.Language]::new("en-US"))
$contactDir = "C:\Users\saras\OneDrive\Documents\sehatSetu\reference\contact_sheets"
$clipDirs = Get-ChildItem -Path $contactDir -Directory

$allResults = @{}

foreach ($clip in $clipDirs) {
    $clipName = $clip.Name
    Write-Host "Analyzing clip: $clipName..."
    $frames = Get-ChildItem -Path $clip.FullName -Filter *.jpg | Sort-Object Name
    $clipResults = @()

    # Determine frame time step
    # Shorter clips were 1 fps, longer were 0.5 fps (1 frame per 2 sec)
    $step = 2.0
    if ($clipName -match "120510" -or $clipName -match "122813") {
        $step = 1.0
    }

    $idx = 0
    foreach ($frame in $frames) {
        $timestamp = $idx * $step
        $idx++

        # Only sample every frame or every other frame if needed
        # Let's load image with Windows Imaging BitmapDecoder
        $fileStream = [System.IO.File]::OpenRead($frame.FullName)
        $winRtStream = [System.IO.WindowsRuntimeStreamExtensions]::AsRandomAccessStream($fileStream)
        $decoderAsync = [Windows.Graphics.Imaging.BitmapDecoder]::CreateAsync($winRtStream)
        $decoder = $decoderAsync.GetAwaiter().GetResult()
        $softwareBitmapAsync = $decoder.GetSoftwareBitmapAsync()
        $softwareBitmap = $softwareBitmapAsync.GetAwaiter().GetResult()
        $fileStream.Dispose()

        $ocrResultAsync = $engine.RecognizeAsync($softwareBitmap)
        $ocrResult = $ocrResultAsync.GetAwaiter().GetResult()
        
        $lines = @()
        foreach ($line in $ocrResult.Lines) {
            $lines += $line.Text
        }

        $allText = $lines -join " | "

        $clipResults += [PSCustomObject]@{
            frame = $frame.Name
            timestamp = $timestamp
            text = $allText
        }
    }
    $allResults[$clipName] = $clipResults
}

$outputPath = "C:\Users\saras\OneDrive\Documents\sehatSetu\reference\ocr_timeline.json"
$allResults | ConvertTo-Json -Depth 5 | Set-Content -Path $outputPath -Encoding UTF8
Write-Host "OCR Timeline written to $outputPath"
