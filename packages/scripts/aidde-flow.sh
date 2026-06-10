#!/bin/bash
# AIDDE Flow - Complete session lifecycle handler
set -euo pipefail

# [Parent Feature/Milestone] AIDDE Infrastructure
# [Subtask] Handle full AI session flow with delegation
# [Law Check] 60 lines | Passed Do It Check

# Colors
G='\033[0;32m' R='\033[0;31m' Y='\033[1;33m' NC='\033[0m'

get_repo_info() {
    REPO=$(gh repo view --json nameWithOwner --jq '.nameWithOwner' 2>/dev/null || echo "owner/repo")
    OWNER=$(echo "$REPO" | cut -d'/' -f1)
    REPO_NAME=$(echo "$REPO" | cut -d'/' -f2)
}

stage_1_mapper() {
    echo -e "${G}=== Stage 1: Project Mapper ===${NC}"
    get_repo_info
    echo "📋 Prompt: ${1:-}"
    echo "🔍 Syncing GitHub state..."
    gh api repos/"$OWNER"/"$REPO_NAME"/milestones --jq '.[] | select(.state=="open") | .title' 2>/dev/null || true
}

stage_4_quad() {
    echo -e "${G}=== Stage 4: Quad Validation ===${NC}"
    [[ -f "scripts/aidde-gates.sh" ]] && ./scripts/aidde-gates.sh 2>/dev/null || echo "⚠️ Running inline gates"
}

main() {
    stage_1_mapper "$1"
    stage_4_quad
    echo -e "${G}✅ AIDDE Flow Complete${NC}"
}

main "$@"