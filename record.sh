#!/usr/bin/env bash
# Record device screen and transcode to a compact video (<10MB, ~640p, 8fps).
# Usage: ./record.sh [output.mp4]
# Press Ctrl+C to stop recording.

set -euo pipefail

OUTPUT="${1:-recording_$(date +%Y%m%d_%H%M%S).mp4}"
DEVICE_PATH="/sdcard/_screenrecord_tmp.mp4"

cleanup() {
  echo ""
  echo "Stopping recording..."
  adb shell rm -f "$DEVICE_PATH" 2>/dev/null || true
}

echo "Recording... Press Ctrl+C to stop."
# Record at a lower bitrate to keep the source manageable
adb shell screenrecord --bit-rate 8000000 "$DEVICE_PATH" &
ADB_PID=$!

trap 'kill $ADB_PID 2>/dev/null; wait $ADB_PID 2>/dev/null; transcode' INT

transcode() {
  echo "Pulling video from device..."
  RAW="$(mktemp /tmp/screenrecord_raw_XXXX.mp4)"
  adb pull "$DEVICE_PATH" "$RAW"
  adb shell rm -f "$DEVICE_PATH"

  echo "Transcoding to $OUTPUT (640p, 8fps)..."
  ffmpeg -i "$RAW" \
    -vf "scale='min(640,iw)':-2" \
    -r 8 \
    -c:v libx264 \
    -crf 32 \
    -preset fast \
    -an \
    -movflags +faststart \
    "$OUTPUT"

  rm -f "$RAW"

  SIZE=$(du -sh "$OUTPUT" | cut -f1)
  echo "Done: $OUTPUT ($SIZE)"
}

wait $ADB_PID 2>/dev/null || true
transcode
