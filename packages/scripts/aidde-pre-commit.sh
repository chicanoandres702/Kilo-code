#!/usr/bin/env bash
# [Parent Feature/Milestone] AI Planning Enforcement
# [Child Task/Issue] #15
# [Subtask] Add mandatory pre-commit branch guard
# [Upstream] Git commit -> [Downstream] AIDDE branch policy
# [Law Check] 31 lines | Passed Do It Check
set -euo pipefail

branch=$(git rev-parse --abbrev-ref HEAD 2>/dev/null || echo "")
default_branch=$(gh repo view --json defaultBranchRef --jq '.defaultBranchRef.name' 2>/dev/null || echo "")

if [[ -z "$branch" || "$branch" == "$default_branch" || "$branch" == "master" ]]; then
    echo "AIDDE policy blocks commits on $branch. Use feature/<milestone>/<task>." >&2
    exit 1
fi

if [[ "$branch" != feature/* ]]; then
    echo "AIDDE policy requires a feature/* branch. Current: $branch" >&2
    exit 1
fi

git diff --cached --check
