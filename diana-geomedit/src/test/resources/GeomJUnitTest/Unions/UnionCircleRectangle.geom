<?xml version="1.0" encoding="UTF-8"?>
<GeometricDiagram id="0" name="UnionCircleRectangle.geom">
  <RectangleWithTwoPointsConstruction id="1" name="Rectangle0">
    <P1_ExplicitPointConstruction id="2" point="248.0,213.0" name="Point2" />
    <P2_ExplicitPointConstruction id="3" point="525.0,419.0" name="Point3" />
    <ColorBackgroundStyle id="4" color="254,247,217" transparencyLevel="0.5" />
    <ForegroundStyle id="5" joinStyle="JOIN_MITER" color="0,0,0" dashStyle="PLAIN_STROKE" capStyle="CAP_SQUARE" />
    <RectangleGraphicalRepresentation id="6" identifier="7bac0b4a">
      <TextStyle id="7" backgroundColor="255,255,255" color="0,0,0" font="Lucida Sans,0,11" />
    </RectangleGraphicalRepresentation>
  </RectangleWithTwoPointsConstruction>
  <CircleWithCenterAndPointConstruction id="8" name="Circle1">
    <Center_ExplicitPointConstruction id="9" point="375.0,309.0" name="Point4" />
    <Point_ExplicitPointConstruction id="10" point="465.0,367.0" name="Point5" />
    <ColorBackgroundStyle id="11" color="255,51,255" transparencyLevel="0.5" />
    <ForegroundStyle id="12" joinStyle="JOIN_MITER" color="0,0,0" dashStyle="PLAIN_STROKE" capStyle="CAP_SQUARE" />
    <EllipsGraphicalRepresentation id="13" identifier="edbf2a5">
      <TextStyle id="14" backgroundColor="255,255,255" color="0,0,0" font="Lucida Sans,0,11" />
    </EllipsGraphicalRepresentation>
  </CircleWithCenterAndPointConstruction>
  <UnionConstruction id="15" mergeContents="false" name="Union6">
    <ObjectReference id="16" name="Construction7">
      <RectangleWithTwoPointsConstruction idref="1" />
    </ObjectReference>
    <ObjectReference id="17" name="Construction8">
      <CircleWithCenterAndPointConstruction idref="8" />
    </ObjectReference>
    <ColorBackgroundStyle id="18" color="254,247,217" transparencyLevel="0.5" />
    <ForegroundStyle id="19" joinStyle="JOIN_MITER" color="0,0,0" dashStyle="PLAIN_STROKE" capStyle="CAP_SQUARE" />
    <ComputedAreaGraphicalRepresentation id="20" identifier="53e14f21">
      <TextStyle id="21" backgroundColor="255,255,255" color="0,0,0" font="Lucida Sans,0,11" />
    </ComputedAreaGraphicalRepresentation>
  </UnionConstruction>
  <DrawingGraphicalRepresentation id="22" backgroundColor="255,255,255" drawWorkingArea="false" focusColor="255,0,0" selectionColor="0,0,255" rectangleSelectingSelectionColor="0,0,255" dimensionConstraints="FREELY_RESIZABLE" identifier="74ead6c9">
    <TextStyle id="23" backgroundColor="255,255,255" color="0,0,0" font="Lucida Sans,0,11" />
  </DrawingGraphicalRepresentation>
</GeometricDiagram>
