#!/bin/bash
# AIDDE Plan - Auto create GitHub planning (Milestone + Issue + Branch)
set -euo pipefail

prompt="$1"
SLUG=$(echo "$prompt" | tr '[:upper:]' '[:lower:]' | tr ' ' '-' | cut -c1-30)

# Get repo info
REPO=$(gh repo view --json nameWithOwner --jq '.nameWithOwner' 2>/dev/null || echo "owner/repo")
OWNER=$(echo "$REPO" | cut -d'/' -f1)
NAME=$(echo "$REPO" | cut -d'/' -f2)

# Create Milestone
MILESTONE_NUM=$(gh api repos/"$OWNER"/"$NAME"/milestones --jq ".[] | select(.title==\"$prompt\") | .number" 2>/dev/null || echo "")
if [[ -z "$MILESTONE_NUM" ]] || [[ "$MILESTONE_NUM" == "null" ]]; then
    echo "Creating Milestone: $prompt"
    MILESTONE_NUM=$(gh api repos/"$OWNER"/"$NAME"/milestones -f title="$prompt" -f description="Auto-created" | jq -r '.number')
fi

# Create branch
BRANCH="feature/$SLUG"
git checkout -b "$BRANCH" 2>/dev/null || git checkout "$BRANCH" 2>/dev/null || true

echo "Created: Milestone #$MILESTONE_NUM, Branch: $BRANCH"