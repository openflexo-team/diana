# diana — Known defects

Bugs of the diagram framework (diana-core, diana-swing) that are known and not fixed yet. Each entry says what is verified, how to reproduce it, and how to work around it. Features and evolutions go to `BACKLOG.md`, not here.

Identifiers are stable and never reused: `DIANA-D-<n>`. Reference them from commits and from other entries.

Status legend: `TODO` · `IN PROGRESS` · `BLOCKED` · `DONE` · `DEFERRED`.

---

### DIANA-D-1 — The user interface of the shape selector cannot instantiate its controller  ·  `TODO`

**Symptom.** At the start of an application that loads the diagram editors: `NoSuchMethodException: Could not instanciate class org.openflexo.diana.swing.widget.JFIBShapeSelector$ShapeDetailsPanel$CustomFIBController` (`FIBController.instanciateController`), twice per start (formod-app, 2026-10-09).

**Read, not reproduced in isolation.** `CustomFIBController` is declared `public class CustomFIBController extends FIBController` inside the details panel of the selector, i.e. NOT static: its constructor takes the enclosing panel as a hidden first parameter, which the reflection of `instanciateController` — looking for `(FIBComponent, GinaViewFactory)` — cannot find. `JFIBTextStyleSelector` declares the same inner class the same way. Whether the selector still works (the panel creates its own controller with `new CustomFIBController(...)`) was not checked.
