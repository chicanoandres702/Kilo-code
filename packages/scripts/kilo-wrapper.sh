#!/usr/bin/env bash
# [Parent Feature/Milestone] Kilo Android App
# [Child Task/Issue] #23
# [Subtask] Create robust wrapper script for manual binary execution
# [Upstream] Manual install -> [Downstream] Kilo CLI execution
# [Law Check] 25 lines | Passed Do It Check
set -euo pipefail

# 1. Search for binary in common locations if KILO_BIN is unset
if [[ -z "${KILO_BIN:-}" ]]; then
    # List of candidate locations
    candidates=(
        "$HOME/bin/kilo"
        "$HOME/.local/bin/kilo"
        "/data/data/com.termux/files/home/bin/kilo"
    )
    for candidate in "${candidates[@]}"; do
        if [[ -f "$candidate" ]]; then
            KILO_BIN="$candidate"
            break
        fi
    done
fi

# 2. Fallback to default if still not found
KILO_BIN="${KILO_BIN:-$HOME/bin/kilo}"

# 3. Robust Error Reporting
if [[ ! -f "$KILO_BIN" ]]; then
    echo "❌ Kilo binary not found." >&2
    echo "Searched: $KILO_BIN" >&2
    echo "Ensure it is installed or set KILO_BIN to the correct path." >&2
    echo "Tip: Check if the installation script has been run." >&2
    exit 1
fi

# Ensure executable permissions
chmod +x "$KILO_BIN" 2>/dev/null || true

# Execute with arguments
exec "$KILO_BIN" "$@"
