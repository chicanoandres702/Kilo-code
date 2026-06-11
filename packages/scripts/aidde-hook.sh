#!/usr/bin/env bash
# [Parent Feature/Milestone] AI Planning Enforcement
# [Child Task/Issue] #15
# [Subtask] Make AIDDE prompt hook mandatory
# [Upstream] User prompt -> [Downstream] AIDDE planning flow
# [Law Check] 34 lines | Passed Do It Check
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
prompt="${1:-}"

if [[ -z "$prompt" ]]; then
    echo "Mandatory AIDDE hook requires a prompt." >&2
    exit 2
fi

if [[ ! -x "$SCRIPT_DIR/aidde-flow.sh" ]]; then
    echo "Mandatory AIDDE flow script is missing or not executable: $SCRIPT_DIR/aidde-flow.sh" >&2
    exit 127
fi

log_prompt() {
    local branch ts
    branch=$(git rev-parse --abbrev-ref HEAD 2>/dev/null || echo "init")
    ts=$(date -Iseconds 2>/dev/null || date)
    echo "$ts | PROMPT | $branch | $prompt" >> "$SCRIPT_DIR/../session-log.txt"
}

log_prompt
echo "=== Mandatory AIDDE Prompt Hook ==="
bash "$SCRIPT_DIR/aidde-flow.sh" "$prompt"
echo "✅ Mandatory AIDDE prompt hook completed"
