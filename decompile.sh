#!/usr/bin/env bash
# Extracts NeoForge-decompiled Minecraft sources from Gradle's cache.
# NeoForm already runs Fernflower internally during the build; this just unpacks the result.
#
# Usage: ./decompile.sh [output-dir]
# Default output dir: ./source

set -euo pipefail

CACHE_DIR="$HOME/.gradle/caches/neoformruntime/intermediate_results"
OUT_DIR="${1:-./source}"

# Read neo_version from gradle.properties in the current directory
NEO_VERSION=""
if [[ -f "./gradle.properties" ]]; then
  NEO_VERSION=$(grep -E '^neo_version\s*=' ./gradle.properties | sed 's/.*=\s*//' | tr -d '[:space:]')
fi

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

# Extract a human-readable version label from the .txt sidecar next to a zip
version_label() {
  local zip="$1"
  local txt="${zip%_output.zip}.txt"
  [[ -f "$txt" ]] || { echo "unknown"; return; }
  local ver
  ver=$(grep -o 'neoforge-[0-9][^"/]*' "$txt" 2>/dev/null | head -1 \
    | sed 's/-userdev\.jar.*//; s/-sources\.jar.*//')
  if [[ "$(basename "$zip")" == sourcesWithNeoForge_* ]]; then
    echo "${ver} (full sources)"
  else
    echo "$ver"
  fi
}

CHOSEN=""

# Auto-select: find the zip whose sidecar mentions this project's neo_version
if [[ -n "$NEO_VERSION" ]]; then
  MATCHES=()
  for zip in "${ZIPS[@]}"; do
    txt="${zip%_output.zip}.txt"
    if [[ -f "$txt" ]] && grep -qF "$NEO_VERSION" "$txt" 2>/dev/null; then
      MATCHES+=("$zip")
    fi
  done

  if [[ ${#MATCHES[@]} -eq 1 ]]; then
    CHOSEN="${MATCHES[0]}"
    echo "Auto-selected: $(version_label "$CHOSEN")"
  elif [[ ${#MATCHES[@]} -gt 1 ]]; then
    echo "Multiple zips match neo_version=${NEO_VERSION}:"
    for i in "${!MATCHES[@]}"; do
      printf "  [%d] %s\n" "$((i+1))" "$(version_label "${MATCHES[$i]}")"
    done
    read -rp "Choose [1-${#MATCHES[@]}]: " PICK
    CHOSEN="${MATCHES[$((PICK-1))]}"
  else
    echo "Warning: no zip found for neo_version=${NEO_VERSION} — showing all available:"
  fi
fi

# Fallback: interactive selection with version labels
if [[ -z "$CHOSEN" ]]; then
  if [[ ${#ZIPS[@]} -eq 1 ]]; then
    CHOSEN="${ZIPS[0]}"
  else
    echo "Available source zips:"
    for i in "${!ZIPS[@]}"; do
      printf "  [%d] %s\n" "$((i+1))" "$(version_label "${ZIPS[$i]}")"
    done
    read -rp "Choose [1-${#ZIPS[@]}]: " PICK
    CHOSEN="${ZIPS[$((PICK-1))]}"
  fi
fi

echo "Source zip: $CHOSEN"
echo "Output dir: $OUT_DIR"

mkdir -p "$OUT_DIR"
unzip -q -o "$CHOSEN" -d "$OUT_DIR"

echo "Done — $(find "$OUT_DIR" -name '*.java' | wc -l | tr -d ' ') Java files extracted to $OUT_DIR"
