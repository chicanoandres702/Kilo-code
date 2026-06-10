# AIDDE Delegation Matrix

When AIDDE detects these patterns in code, delegate automatically:

| Pattern | Agent | Reason |
|---------|-------|--------|
| `any` in TypeScript | `@type-enforcer` | Enforce strict typing |
| File > 100 lines | `@complexity-guard` | 100-Line Law violation |
| Hardcoded secret | `@security-auditor` | Credential hygiene |
| Missing test file | `@test-validator` | Gate 6 compliance |
| Model changes | `@type-enforcer` | Contract change protocol |
| New dependency | `@security-auditor` | OWASP + audit scan |
| PR opened | `@pr-handler` | Gate validation |
| Every 10 messages | `@context-manager` | Anti-drift sync |

## Delegation Commands

| When | Command | Script |
|------|---------|--------|
| Code changes | Auto-delegate | `scripts/aidde-flow.sh` |
| PR creation | Auto-validate | `.github/workflows/do-it-check.yml` |
| Session start | Boot sync | `scripts/aidde-init.sh` |