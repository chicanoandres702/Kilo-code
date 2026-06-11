# AIDDE Delegation Matrix

## Planning Mode Gate (Mandatory Before Implementation)

Before any code change, create the plan in the top todo list and run:

```bash
./packages/scripts/aidde-flow.sh "<user prompt>"
```

For mandatory runtime enforcement, use the prompt hook or CLI entrypoint:

```bash
./packages/scripts/aidde-hook.sh "<user prompt>"
./aidde "<user prompt>"
```

Install local git guards with:

```bash
./packages/scripts/aidde-install-hooks.sh
```

The planning script must create or reuse:

1. GitHub Milestone for the feature.
2. GitHub Issue for the task.
3. Feature integration branch.
4. Task execution branch.
5. Optional gist-backed planning artifact.
6. Optional additional `git worktree` so multiple branches can stay open.

Set `AIDDE_USE_WORKTREE=1` to open the task branch in a parallel worktree.
Set `AIDDE_CREATE_GIST=0` to disable gist-backed planning artifacts.

Do not start implementation until the issue number, milestone number, integration branch, and task branch are visible in the todo list.
The hook must not suppress failures, truncate output, or use `|| true`.

## Delegation Triggers

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
| Code changes | Auto-delegate | `packages/scripts/aidde-flow.sh` |
| Prompt received | Mandatory hook | `packages/scripts/aidde-hook.sh` |
| Local git commit | Mandatory guard | `packages/scripts/aidde-install-hooks.sh` |
| PR creation | Auto-validate | `.github/workflows/do-it-check.yml` |
| Session start | Boot sync | `packages/scripts/aidde-flow.sh` |