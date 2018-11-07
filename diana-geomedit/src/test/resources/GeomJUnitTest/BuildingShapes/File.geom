<?xml version="1.0" encoding="UTF-8"?>
<GeometricDiagram id="0" name="File.geom">
  <RectangleWithTwoPointsConstruction id="1" isVisible="false" name="Rectangle0">
    <P1_ExplicitPointConstruction id="2" point="213.0,203.0" name="Point5" />
    <P2_ExplicitPointConstruction id="3" point="463.0,500.0" name="Point6" />
    <ColorBackgroundStyle id="4" color="254,247,217" transparencyLevel="0.5" />
    <ForegroundStyle id="5" joinStyle="JOIN_MITER" color="0,0,0" dashStyle="PLAIN_STROKE" capStyle="CAP_SQUARE" />
    <RectangleGraphicalRepresentation id="6" identifier="43b62674">
      <TextStyle id="7" backgroundColor="255,255,255" color="0,0,0" font="Lucida Sans,0,11" />
    </RectangleGraphicalRepresentation>
  </RectangleWithTwoPointsConstruction>
  <PolygonWithNPointsConstruction id="8" isVisible="false" name="Polygon1">
    <ExplicitPointConstruction id="9" point="318.0,153.0" name="Point7" />
    <ExplicitPointConstruction id="10" point="528.0,337.0" name="Point8" />
    <ExplicitPointConstruction id="11" point="562.0,119.0" name="Point9" />
    <ExplicitPointConstruction id="12" point="475.0,124.0" name="Point10" />
    <ColorBackgroundStyle id="13" color="254,247,217" transparencyLevel="0.5" />
    <ForegroundStyle id="14" joinStyle="JOIN_MITER" color="0,0,0" dashStyle="PLAIN_STROKE" capStyle="CAP_SQUARE" />
    <PolygonGraphicalRepresentation id="15" identifier="1ee93a25">
      <TextStyle id="16" backgroundColor="255,255,255" color="0,0,0" font="Lucida Sans,0,11" />
    </PolygonGraphicalRepresentation>
  </PolygonWithNPointsConstruction>
  <SubstractionConstruction id="17" isVisible="false" name="Substraction2">
    <Container_ObjectReference id="18" name="Construction11">
      <RectangleWithTwoPointsConstruction idref="1" />
      <ColorBackgroundStyle idref="4" />
      <ForegroundStyle idref="5" />
    </Container_ObjectReference>
    <Substracted_ObjectReference id="19" name="Construction12">
      <PolygonWithNPointsConstruction idref="8" />
      <ColorBackgroundStyle idref="13" />
      <ForegroundStyle idref="14" />
    </Substracted_ObjectReference>
    <ColorBackgroundStyle id="20" color="254,247,217" transparencyLevel="0.5" />
    <ForegroundStyle id="21" joinStyle="JOIN_MITER" color="0,0,0" dashStyle="PLAIN_STROKE" capStyle="CAP_SQUARE" />
    <ComputedAreaGraphicalRepresentation id="22" identifier="506dbdf4">
      <TextStyle id="23" backgroundColor="255,255,255" color="0,0,0" font="Lucida Sans,0,11" />
    </ComputedAreaGraphicalRepresentation>
  </SubstractionConstruction>
  <PolygonWithNPointsConstruction id="24" name="Polygon3">
    <ExplicitPointConstruction id="25" point="377.0,206.0" name="Point13" />
    <ExplicitPointConstruction id="26" point="462.0,280.0" name="Point14" />
    <ExplicitPointConstruction id="27" point="368.0,287.0" name="Point15" />
    <ColorBackgroundStyle id="28" color="254,247,217" transparencyLevel="0.5" />
    <ForegroundStyle id="29" joinStyle="JOIN_MITER" color="0,0,0" dashStyle="PLAIN_STROKE" capStyle="CAP_SQUARE" />
    <PolygonGraphicalRepresentation id="30" identifier="36a362c0">
      <TextStyle id="31" backgroundColor="255,255,255" color="0,0,0" font="Lucida Sans,0,11" />
    </PolygonGraphicalRepresentation>
  </PolygonWithNPointsConstruction>
  <UnionConstruction id="32" mergeContents="false" name="Union4">
    <ObjectReference id="33" name="Construction16">
      <SubstractionConstruction idref="17" />
      <ColorBackgroundStyle idref="20" />
      <ForegroundStyle idref="21" />
    </ObjectReference>
    <ObjectReference id="34" name="Construction17">
      <PolygonWithNPointsConstruction idref="24" />
      <ColorBackgroundStyle idref="28" />
      <ForegroundStyle idref="29" />
    </ObjectReference>
    <ColorBackgroundStyle id="35" color="254,247,217" transparencyLevel="0.5" />
    <ForegroundStyle id="36" joinStyle="JOIN_MITER" color="0,0,0" dashStyle="PLAIN_STROKE" capStyle="CAP_SQUARE" />
    <ComputedAreaGraphicalRepresentation id="37" identifier="62b0df1a">
      <TextStyle id="38" backgroundColor="255,255,255" color="0,0,0" font="Lucida Sans,0,11" />
    </ComputedAreaGraphicalRepresentation>
  </UnionConstruction>
  <DrawingGraphicalRepresentation id="39" backgroundColor="255,255,255" drawWorkingArea="false" focusColor="255,0,0" selectionColor="0,0,255" rectangleSelectingSelectionColor="0,0,255" dimensionConstraints="FREELY_RESIZABLE" identifier="24b2b552">
    <TextStyle id="40" backgroundColor="255,255,255" color="0,0,0" font="Lucida Sans,0,11" />
  </DrawingGraphicalRepresentation>
</GeometricDiagram>
