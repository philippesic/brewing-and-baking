#!/usr/bin/env bash
# Extracts NeoForge-decompiled Minecraft sources from Gradle's cache.
# NeoForm already runs Fernflower internally during the build; this just unpacks the result.
#
# Usage: ./decompile.sh [output-dir]
# Default output dir: ./minecraft-src

set -euo pipefail

CACHE_DIR="$HOME/.gradle/caches/neoformruntime/intermediate_results"
OUT_DIR="${1:-./source}"

# Find all sources zips, newest first (mapfile not available on macOS bash 3.2)
# 26.1+ (no obfuscation): applyNeoforgePatches_*_output.zip
# pre-26.1 (obfuscated):  sourcesWithNeoForge_*_output.zip
ZIPS=()
while IFS= read -r f; do
  ZIPS+=("$f")
done < <(find "$CACHE_DIR" \( -name "applyNeoforgePatches_*_output.zip" -o -name "sourcesWithNeoForge_*_output.zip" \) -print0 \
  | xargs -0 ls -t 2>/dev/null)

if [[ ${#ZIPS[@]} -eq 0 ]]; then
  echo "No decompiled source zips found in $CACHE_DIR"
  echo "Run './gradlew build' or './gradlew genSources' first to populate the cache."
  exit 1
fi

if [[ ${#ZIPS[@]} -eq 1 ]]; then
  CHOSEN="${ZIPS[0]}"
else
  echo "Multiple source zips found:"
  for i in "${!ZIPS[@]}"; do
    printf "  [%d] %s\n" "$((i+1))" "$(basename "${ZIPS[$i]}")"
  done
  read -rp "Choose [1-${#ZIPS[@]}]: " PICK
  CHOSEN="${ZIPS[$((PICK-1))]}"
fi

echo "Source zip: $CHOSEN"
echo "Output dir: $OUT_DIR"

mkdir -p "$OUT_DIR"
unzip -q -o "$CHOSEN" -d "$OUT_DIR"

echo "Done — $(find "$OUT_DIR" -name '*.java' | wc -l | tr -d ' ') Java files extracted to $OUT_DIR"
