#!/usr/bin/env bash
# [Parent Feature/Milestone] AI Planning Enforcement
# [Child Task/Issue] #15
# [Subtask] Install mandatory local git hooks
# [Upstream] Repo setup -> [Downstream] pre-commit and commit-msg guards
# [Law Check] 43 lines | Passed Do It Check
set -euo pipefail

ROOT=$(git rev-parse --show-toplevel)
HOOK_DIR=$(git rev-parse --git-path hooks)

install_hook() {
    local hook=$1
    local script=$2
    local wrapper="$HOOK_DIR/$hook"
    cat > "$wrapper" <<EOF
#!/usr/bin/env bash
set -euo pipefail
ROOT=\$(git rev-parse --show-toplevel)
exec "\$ROOT/$script" "\$@"
EOF
    chmod +x "$wrapper"
    echo "Installed mandatory AIDDE $hook hook: $wrapper"
}

install_hook pre-commit packages/scripts/aidde-pre-commit.sh
install_hook commit-msg packages/scripts/aidde-commit-msg.sh

cat <<EOF
✅ Mandatory AIDDE git hooks installed.
Run ./aidde "<prompt>" or configure your editor/Kilo prompt hook to call packages/scripts/aidde-hook.sh.
EOF
