#!/usr/bin/env bash
# [Parent Feature/Milestone] AI Planning Enforcement
# [Child Task/Issue] #15
# [Subtask] Add mandatory commit message trace guard
# [Upstream] Git commit -> [Downstream] GitHub issue traceability
# [Law Check] 30 lines | Passed Do It Check
set -euo pipefail

message_file="${1:-}"
if [[ -z "$message_file" || ! -f "$message_file" ]]; then
    echo "AIDDE commit-msg hook requires a message file." >&2
    exit 2
fi

message=$(<"$message_file")
if ! grep -Eq 'Resolves: #[0-9]+' <<<"$message"; then
    echo "AIDDE policy requires commit message to include: Resolves: #<issue>" >&2
    exit 1
fi

if ! grep -Fq 'Parent Feature (Milestone):' <<<"$message"; then
    echo "AIDDE policy requires commit message to include Parent Feature (Milestone)." >&2
    exit 1
fi
