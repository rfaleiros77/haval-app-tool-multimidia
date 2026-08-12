# Session handover — 2026-08-11

Valid while the open items below are unanswered. If you are reading this more
than two weeks on, check the open list against `git log` before trusting it.

Written to close the radar's "sem handover", against the project's earlier
conversation (12 messages, recovered on the Mac mini).

## Where the work stands

**Parked on purpose, not abandoned.** The conversation ends with *"vamos
continuar depois"* and *"depois voltamos para esse assunto"*.

This is a **study fork** of `bobaoapae/haval-tool`, cloned to Rogério's GitHub
for a stated reason: *"clone esse repositório para um local meu em caso de ser
fechado."* Most of the code is somebody else's; two commits are his:

- `37c0310` — steering-wheel action to open the charts screen on the cluster
- `6b76685` — eSIM manager plus a diagnostic script

Both sit on branch **`feature/acao-volante-abrir-graficos`**, which has never
been published.

## Open

- **The branch cannot be pushed.** The origin repo belongs to `tontonhaval`;
  push returns 403 (confirmed 2026-08-11). Either get access, publish under his
  own fork, or accept that the work stays local. **This is his call.**
- **A `.claude/launch.json` was committed locally on 2026-08-11** (Vite, port
  1420) and is stuck behind the same 403.
- **The upstream documentation is in a locked Google Doc.** Reading it
  automatically failed; the fallback discussed was transcribing it from the
  screen by hand. Never done.

## Traps

- **This is a car.** Anything that changes the cluster or the multimedia unit
  carries physical and warranty risk. Installing is always his decision, never
  the session's.
- **The disclaimer in `README.md` is load-bearing** — unofficial educational
  work, no link to Haval/GWM, may violate manufacturer terms. It travels with
  any fork or redistribution.
- **Do not rewrite `DOCS/`.** It came from upstream and is good; a local
  rewrite would collide with the next merge.

## Where to look

- `CLAUDE.md` — what is his versus what is third-party, and the rules that
  follow from that
- `DOCS/README.md` — upstream architecture: how to add a feature, how the
  internals work
- `DOCS/esim-diagnostico.sh` — his own diagnostic script
