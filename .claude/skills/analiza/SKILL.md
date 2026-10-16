---
name: analiza
description: Read-only analysis of a task before planning. Inspects code and Git history, then reports ambiguities, questions for the interviewer, relevant files, risks, edge cases, and the smallest reasonable solution. Never implements.
argument-hint: "[task description]"
allowed-tools: Read Grep Glob Bash(git log *) Bash(git status *) Bash(git diff *) Bash(git show *) Bash(git blame *)
---

Analyze this task: $ARGUMENTS

If no task was given, use the task from the conversation, or ask for it.

## Rules
- Read-only. Do not create, edit or delete files, run builds or tests, or implement
  anything. (`allowed-tools` only skips permission prompts for the listed lookups;
  read-only is enforced by these instructions, not by the tool settings.)
- Do not write a step-by-step implementation plan. That is the job of native Plan
  Mode, which follows this analysis.
- Read only what is relevant. Do not scan the whole repo unnecessarily.
- If the repo has little or no code or Git history, say so instead of guessing.

## Steps
1. Restate the task and what "done" means.
2. Inspect project structure and the code relevant to the task. Use Git history
   only where it explains patterns or earlier decisions.
3. Note existing patterns: naming, package layout, test style, libraries in use.

## Output (concise)
- **Task understanding**: 1-3 sentences.
- **Questions for the interviewer**: what must be clarified before we agree on a
  final specification (requirements, inputs/outputs, constraints, scope,
  non-goals, acceptance criteria). For each, a proposed default assumption.
- **Other ambiguities**: ones we can resolve ourselves from the code.
- **Relevant files**: `path:line` and why (or "no existing code").
- **Existing patterns** to follow.
- **Risks and edge cases**: for example null or empty input, overflow, negatives,
  precision, concurrency, where relevant.
- **Smallest reasonable solution**: the approach in a few sentences, and what we
  deliberately will not build.
- **Verification ideas**: which tests or commands would cover it.

End by suggesting the next step: enter Plan Mode with my answers to the questions above.
