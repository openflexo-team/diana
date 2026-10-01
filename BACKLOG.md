# DIANA backlog

Features and evolutions to develop. Known bugs go in `KNOWN_DEFECTS.md`.

## DIANA-F-1 — Richer shape palettes · IN PROGRESS

**Problem.** DIANA ships a single palette (`diana-core`, `Palettes/Basic`, 17 elements), whose path
is hard-coded in `CommonPalette` (diagram-ta-ui) and `DiagramEditorPalette` (diana-drawing-editor).
It leaves out shapes DIANA already supports (rounded rectangle, square, circle, arc, star and
polygon variants) and offers no complex shapes.

**Done.**
- New module `diana-palettes`, holding palettes as resources: `Rectangles` (8 elements),
  `BasicShapes` (31), `Arrows` (30), `Flowchart` (29), `StarsAndBanners` (21), `Equations` (6) and
  `Shapes3D` (4). DrawingML presets matching a native shape of `Basic` are left out, and so are line
  and speech callouts, connectors and lines, action buttons, chart placeholders and tab corners.
- They are generated from the DrawingML preset geometries (ECMA-376) shipped with Apache POI, by
  `DrawingMLPaletteGenerator` (test source set, so POI is never a runtime dependency):
  `gradle :diana-palettes:generatePalettes`. The generated `.pel` files are committed;
  `TestDianaPalettes` fails when they are not up to date with the generator.
- `gradle :diana-palettes:renderPalettes` renders each palette to a PNG through DIANA Swing
  rendering, for visual review (needs a display).
- A DrawingML path is translated into a filled shape (sub-paths joined by zero-width back and forth
  segments, so that holes survive the non-zero winding rule), an optional translucent overlay
  (`darken`/`lighten` fills) and stroked-only shapes, honouring `fill="none"` and `stroke="false"`.
  POI draws an arc of null radius as NaN coordinates: such segments are skipped.
- A palette element keeps its aspect ratio, fitted and centered in its cell
  (`PaletteModel.makePaletteElement`), and a drop gets that size: narrow shapes (brackets, vertical
  arrows) are no longer stretched. The Basic `Image` element now declares its size (60x50).
- Generated files are stable: the representation identifier is the preset name, and attributes are
  sorted, since PAMELA writes them in an order changing from one run to the other.
- A shape of a `ShapeUnion` overrides default styles for itself only (`DianaShapeUnion.paint`,
  `AbstractDianaGraphics.saveDefaultStyles()`/`restoreDefaultStyles()`): its foreground or
  background no longer leaks to the following shapes.
- The DIANA drawing editor (`LaunchDiagramEditor`) shows all palettes in tabs of its palette
  dialog; the selected tab is the palette attached to the current editor.
- `GeneralShape.pathElements` now allows repeated elements: DIANA objects compare by value, so
  PAMELA silently dropped a segment ending on an already-used point (flowchart *Collate* lost its
  second pass through the center).
- `Basic` has a rounded rectangle (`RoundedRectangle.pel`, `arcSize` 12 pixels), right after the
  rectangle.
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
- Complete `Basic` with the existing shape parameters (arc, more stars/polygons).
- Not translated from DrawingML: adjust handles (shapes are frozen at default values) and text
  rectangles.

**Acceptance.** Each palette loads in the DIANA drawing editor and in the FML diagram editor, its
elements can be dropped and resized, and `TestDianaPalettes` passes.
