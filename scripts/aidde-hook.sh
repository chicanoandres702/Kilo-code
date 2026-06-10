#!/bin/bash
# AIDDE Prompt Hook - Auto-runs on every AI interaction
set -euo pipefail

# Source the flow script for delegation
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Log prompt for audit trail
log_prompt() {
    local prompt="$1"
    local branch
    branch=$(git rev-parse --abbrev-ref HEAD 2>/dev/null || echo "init")
    local ts
    ts=$(date -Iseconds 2>/dev/null || date)
    echo "$ts | PROMPT | $branch | $prompt" >> "$SCRIPT_DIR/../session-log.txt" 2>/dev/null || true
}

# Run Stage 1: Parse + Todo Injection
run_flow() {
    if [[ -f "$SCRIPT_DIR/aidde-flow.sh" ]]; then
        bash "$SCRIPT_DIR/aidde-flow.sh" "$1" 2>&1 | head -10 || true
    fi
}

# Auto-execution
log_prompt "${1:-}"
run_flow "${1:-}"

# Output signal
echo "🔁 AIDDE prompt hook executed" >&2