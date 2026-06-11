#!/usr/bin/env bash
# AIDDE Flow - Prompt planning, execution handoff, and validation.
set -euo pipefail

prompt="${1:-}"
SCRIPT_DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)

if [[ -z "$prompt" ]]; then
    echo "AIDDE planning mode requires a prompt." >&2
    exit 2
fi

echo "=== Stage 1: Project Mapper ==="
bash "$SCRIPT_DIR/aidde-plan.sh" "$prompt" "Implement ${prompt}"

echo "=== Stage 2: Contract Import ==="
echo "Load centralized models/types before writing logic. HALT if no model exists."

echo "=== Stage 3: Code Generation ==="
echo "Implement only on the task branch. Keep files <=100 active lines and add trace headers."
echo "Optional: AIDDE_USE_WORKTREE=1 opens the task branch in a parallel git worktree."
echo "Optional: AIDDE_CREATE_GIST=0 disables gist-backed planning artifacts."

echo "=== Stage 4: AIDDE Quad ==="
if [[ -f "$SCRIPT_DIR/aidde-gates.sh" ]]; then
    bash "$SCRIPT_DIR/aidde-gates.sh"
else
    echo "⚠️ aidde-gates.sh not found; run validation manually."
fi

echo "✅ AIDDE Flow Complete"
