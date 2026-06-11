#!/usr/bin/env bash
# AIDDE Plan - Enforce milestone, issue, branch, gist, and worktree setup.
set -euo pipefail

prompt="${1:-General Planning}"
issue_title="${2:-Implement ${prompt}}"
PLAN_FILE=$(mktemp)
trap 'rm -f "$PLAN_FILE"' EXIT
slugify() { printf '%s' "$1" | tr '[:upper:]' '[:lower:]' | sed -E 's/[^a-z0-9]+/-/g; s/^-+|-+$//g' | cut -c1-48; }
repo_info() { REPO=$(gh repo view --json nameWithOwner --jq '.nameWithOwner' 2>/dev/null || echo "owner/repo"); OWNER=${REPO%%/*}; NAME=${REPO#*/}; }
require_clean_tree() { [[ -z "$(git status --porcelain)" ]] || { echo "Planning mode requires a clean working tree. Stash or commit existing changes first." >&2; exit 2; }; }
write_plan() {
    cat > "$PLAN_FILE" <<EOF
### 🎯 Purpose
Ensure every actionable prompt is planned before implementation so work is traceable, branch-safe, and aligned with AIDDE.

### 📝 Description
${issue_title}

### 🐛 The Issue (If Applicable)
Planning mode exists but was not consistently enforced before edits.

### 🔄 System Flow (Traceability)
* **Upstream:** User prompt -> AIDDE planning parser -> GitHub Milestone/Issue/branch provisioning
* **Downstream:** Todo list -> implementation branch -> traceable commits -> validation gates

### ☑️ Subtasks (Execution Checklist)
- [x] Create or reuse milestone and issue
- [x] Create feature integration and task execution branches
- [ ] Add the plan to the top todo list before implementation
- [ ] Commit with Resolves and Parent Milestone traceability

### 🚀 Future Aspirations & Tracing
Add automated checks that fail fast when implementation starts without an issue, milestone, or task branch.
EOF
}
milestone() {
    local n
    n=$(gh api repos/"$OWNER"/"$NAME"/milestones --jq ".[] | select(.title==\"$1\") | .number" 2>/dev/null | sed -n '1p')
    [[ -n "$n" ]] && { echo "$n"; return; }
    gh api repos/"$OWNER"/"$NAME"/milestones -f title="$1" -f description="AIDDE feature milestone auto-created for prompt-driven planning." --jq '.number'
}
issue() {
    local n
    n=$(gh issue list --state open --milestone "$2" --search "$1" --json number --jq '.[0].number // empty' 2>/dev/null)
    [[ -n "$n" ]] && { echo "$n"; return; }
    gh issue create --title "$1" --milestone "$2" --label enhancement --assignee "@me" --body-file "$PLAN_FILE" --jq '.number'
}
branch() {
    local b=$1 base=$2
    if git show-ref --verify --quiet "refs/heads/$b"; then git checkout "$b"; else git checkout -b "$b" "$base"; fi
    git rev-parse --abbrev-ref --symbolic-full-name '@{u}' >/dev/null 2>&1 || git push -u origin "$b"
}
worktree() {
    local b=$1 base=$2 dir=".worktrees/aidde/$(slugify "$b")-$(date +%s)"
    git worktree add "$dir" -b "$b" "$base"
    git -C "$dir" push -u origin "$b" >/dev/null 2>&1 || true
    echo "$dir"
}
gist() {
    [[ "${AIDDE_CREATE_GIST:-1}" == "0" ]] && return
    gh gist create "$PLAN_FILE" --description "AIDDE plan: ${issue_title}" --filename plan.md 2>/dev/null | sed -n '1p' || true
}
require_clean_tree
repo_info
write_plan
milestone_title="AI Planning Enforcement"
milestone_number=$(milestone "$milestone_title")
issue_number=$(issue "$issue_title" "$milestone_number")
milestone_slug=$(slugify "$milestone_title")
task_slug=$(slugify "$issue_title")
# Git cannot have both feature/foo and feature/foo/bar, so -main is the integration ref.
integration_branch="feature/${milestone_slug}-main"
task_branch="feature/${milestone_slug}/${task_slug}"
default_branch=$(gh repo view --json defaultBranchRef --jq '.defaultBranchRef.name')

git fetch origin "$default_branch"
branch "$integration_branch" "origin/$default_branch"
if [[ "${AIDDE_USE_WORKTREE:-0}" == "1" ]]; then
    task_worktree=$(worktree "$task_branch" "$integration_branch")
else
    branch "$task_branch" "$integration_branch"
    task_worktree="current-worktree"
fi
gist_url=$(gist)
cat <<EOF
✅ AIDDE PLANNING MODE READY
Milestone: ${milestone_title} (#${milestone_number})
Issue: ${issue_title} (#${issue_number})
Integration branch: ${integration_branch}
Task branch: ${task_branch}
Planning worktree: ${task_worktree}
Planning gist: ${gist_url:-not-created}
TODO:
- [x] Milestone/issue and branch hierarchy provisioned
- [x] Optional gist/worktree support provisioned
- [ ] Add top todo list before implementation
- [ ] Implement on ${task_branch}
- [ ] Commit with Resolves: #${issue_number} and Parent Milestone: ${milestone_title}
EOF
