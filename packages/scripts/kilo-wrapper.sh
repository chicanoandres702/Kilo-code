#!/usr/bin/env bash
# [Parent Feature/Milestone] Kilo Android App
# [Child Task/Issue] #22
# [Subtask] Create wrapper script for manual binary execution
# [Upstream] Manual install -> [Downstream] Kilo CLI execution
# [Law Check] 20 lines | Passed Do It Check
set -euo pipefail

# Directory where Kilo binary resides (default to common Termux path if not set)
KILO_BIN="${KILO_BIN:-$HOME/bin/kilo}"

if [[ ! -f "$KILO_BIN" ]]; then
    echo "❌ Kilo binary not found at $KILO_BIN" >&2
    echo "Please ensure the binary is installed and the KILO_BIN variable is correctly set." >&2
    exit 1
fi

# Ensure it's executable (if on an executable partition)
chmod +x "$KILO_BIN" 2>/dev/null || true

# Execute with arguments
exec "$KILO_BIN" "$@"
