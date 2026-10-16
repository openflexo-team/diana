# diana — Known defects

Bugs of the diagram framework (diana-core, diana-swing) that are known and not fixed yet. Each entry says what is verified, how to reproduce it, and how to work around it. Features and evolutions go to `BACKLOG.md`, not here.

Identifiers are stable and never reused: `DIANA-D-<n>`. Reference them from commits and from other entries.

Status legend: `TODO` · `IN PROGRESS` · `BLOCKED` · `DONE` · `DEFERRED`.

---

### DIANA-D-1 — The user interface of the shape selector cannot instantiate its controller  ·  `DONE`

**Symptom.** At the start of an application that loads the diagram editors: `NoSuchMethodException: Could not instanciate class org.openflexo.diana.swing.widget.JFIBShapeSelector$ShapeDetailsPanel$CustomFIBController` (`FIBController.instanciateController`), twice per start (formod-app, 2026-10-09).

**Measured (2026-10-14, stack of the warning).** It is not the shape selector. Both warnings come from `Fib/ConnectorSelectorPanel.fib` (component `ConnectorPanel`): `JDianaInspectors.getConnectorInspector` -> `JInspector` -> `FIBJPanel.makeFIBController` (first one), and a `JFIBDialog.initDialog` (second one), both reaching `FIBController.instanciateController`. That file declared `controllerClassName="…JFIBShapeSelector$ShapeDetailsPanel$CustomFIBController"`, a copy-paste of the shape selector (it has been there for years, see "Fixed FIBs" in its history). `CustomFIBController` is not static, so its constructor takes the enclosing panel as a hidden first parameter and the reflection of `instanciateController`, looking for `(FIBComponent, GinaViewFactory)` then `(FIBComponent)`, finds neither: the warning, then the fallback on a plain `FIBController`. The shape selector is not concerned: `JFIBShapeSelector.ShapeDetailsPanel` builds its controller itself, with `new CustomFIBController(...)`, and does not go through the reflection.

**Fixed (2026-10-14).** `ConnectorSelectorPanel.fib` declares `org.openflexo.gina.controller.FIBController`, as its sibling `*Panel.fib` do (`BackgroundStylePanel`, `ForegroundStylePanel`, `TextPropertiesPanel`, …). No change of behaviour: the fallback already built that very class. No warning at the start of formod-app; `diana-swing` 21/21 (`TestDianaFibs` validates the FIB).

**Not checked.** The other `*Selector.fib` (`ShapeSelector`, `BackgroundStyleSelector`, `ForegroundStyleSelector`, `ShadowStyleSelector`, `TextStyleSelector`) declare their inner `CustomFIBController` the same way; they are built directly by their selector, so they do not reach the reflection, but nothing guards that a future caller does not.

### DIANA-D-2 — Warnings repeated in the log while editing a diagram  ·  `TODO`

Seen in the formod application (2026-10-12), not investigated: `notifyObservers() called by a deleted DrawingTreeNode` (hundreds per diagram close), `finalizeDeletion called for ShapeNodeImpl … while object still being observed`, `Calling updateBounds() for element is not connected to the drawing`, `More than one applicable CustomMouseDragControlImpl … [Rectangle selection, Rectangle selection]`, `Not implemented: DrawRectangle`. None stopped a scenario.
