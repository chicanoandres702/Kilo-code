#!/bin/bash
# AIDDE Loop - Continuous AIDDE session management
set -euo pipefail

# On every prompt: plan → generate → validate
while IFS= read -r prompt || [[ -n "$prompt" ]]; do
    [[ -z "$prompt" ]] && continue
    
    echo "🔁 Processing: $prompt"
    
    # Quick gate check
    ./scripts/aidde-gates.sh 2>/dev/null | head -3
    
done