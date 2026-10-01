# DIANA backlog

Features and evolutions to develop. Known bugs go in `KNOWN_DEFECTS.md`.

## DIANA-F-1 — Richer shape palettes · IN PROGRESS

**Problem.** DIANA ships a single palette (`diana-core`, `Palettes/Basic`, 17 elements), whose path
is hard-coded in `CommonPalette` (diagram-ta-ui) and `DiagramEditorPalette` (diana-drawing-editor).
It leaves out shapes DIANA already supports (rounded rectangle, square, circle, arc, star and
polygon variants) and offers no complex shapes.

**Done.**
- New module `diana-palettes`, holding palettes as resources: `Palettes/Flowchart` (29 elements)
  and `Palettes/Shapes3D` (cube, cylinder, bevel, folded corner).
- They are generated from the DrawingML preset geometries (ECMA-376) shipped with Apache POI, by
  `DrawingMLPaletteGenerator` (test source set, so POI is never a runtime dependency):
  `gradle :diana-palettes:generatePalettes`. The generated `.pel` files are committed;
  `TestDianaPalettes` fails when they are not up to date with the generator.
- `gradle :diana-palettes:renderPalettes` renders each palette to a PNG through DIANA Swing
  rendering, for visual review (needs a display).
- The DIANA drawing editor (`LaunchDiagramEditor`) shows Basic, Flowchart and Shapes3D in tabs of
  its palette dialog; the selected tab is the palette attached to the current editor.
- `GeneralShape.pathElements` now allows repeated elements: DIANA objects compare by value, so
  PAMELA silently dropped a segment ending on an already-used point (flowchart *Collate* lost its
  second pass through the center).
- Shadow of a `ShapeUnion` (`JDianaShapeGraphics.paintShadow`): it was clipped by the union's
  bounding box (a `DianaShapeUnion` is a `Rectangle2D` as a `java.awt.Shape`), and darker, one
  translucent layer per shape of the union. It is now cast from the area the shapes really cover,
  painted once (`TestPaletteShadows`, UI test).

**Remaining.**
- Discover palettes (scan `Palettes/*` across jars) rather than listing them in each tool; the
  drawing editor uses a fixed list.
- FML diagram editor (`CommonPalette`, diagram-ta-ui) still shows `Basic` only.
- `JDrawingView` tracks a single active palette (used to place the dragged image): tools showing
  several palettes at once need to activate a palette when a drag starts from it.
- Complete `Basic` with the existing shape parameters (rounded rectangle, arc, more stars/polygons).
- Further DrawingML families (arrows, stars and banners, misc.); callouts and connectors excluded.
- Not translated from DrawingML: adjust handles (shapes are frozen at default values) and text
  rectangles.

**Acceptance.** Each palette loads in the DIANA drawing editor and in the FML diagram editor, its
elements can be dropped and resized, and `TestDianaPalettes` passes.
