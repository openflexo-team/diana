# diana — Known defects

Bugs of the diagram framework (diana-core, diana-swing) that are known and not fixed yet. Each entry says what is verified, how to reproduce it, and how to work around it. Features and evolutions go to `BACKLOG.md`, not here.

Identifiers are stable and never reused: `DIANA-D-<n>`. Reference them from commits and from other entries.

Status legend: `TODO` · `IN PROGRESS` · `BLOCKED` · `DONE` · `DEFERRED`.

Fixed defects are moved to [`FIXED_DEFECTS.md`](./FIXED_DEFECTS.md) once their status is `DONE`.

---

### DIANA-D-2 — Warnings repeated in the log while editing a diagram  ·  `TODO`

Seen in the formod application (2026-10-12), not investigated: `notifyObservers() called by a deleted DrawingTreeNode` (hundreds per diagram close), `finalizeDeletion called for ShapeNodeImpl … while object still being observed`, `Calling updateBounds() for element is not connected to the drawing`, `More than one applicable CustomMouseDragControlImpl … [Rectangle selection, Rectangle selection]`, `Not implemented: DrawRectangle`. None stopped a scenario.
