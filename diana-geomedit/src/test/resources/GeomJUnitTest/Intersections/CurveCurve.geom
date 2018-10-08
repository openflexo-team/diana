<?xml version="1.0" encoding="UTF-8"?>
<GeometricDiagram id="0" name="CurveCurve.geom">
  <ComplexCurveWithNPointsConstruction id="1" closure="CLOSED_FILLED" name="Curve0">
    <ExplicitPointConstruction id="2" point="229.0,267.0" name="Point3" />
    <ExplicitPointConstruction id="3" point="364.0,87.0" name="Point4" />
    <ExplicitPointConstruction id="4" point="425.0,288.0" name="Point5" />
    <ExplicitPointConstruction id="5" point="302.0,369.0" name="Point6" />
    <ExplicitPointConstruction id="6" point="368.0,257.0" name="Point7" />
    <ColorBackgroundStyle id="7" color="254,247,217" transparencyLevel="0.5" />
    <ForegroundStyle id="8" joinStyle="JOIN_MITER" color="0,0,0" dashStyle="PLAIN_STROKE" capStyle="CAP_SQUARE" />
    <ComplexCurveGraphicalRepresentation id="9" identifier="6496e12f">
      <ColorBackgroundStyle id="10" color="254,247,217" transparencyLevel="0.5" />
      <ForegroundStyle id="11" joinStyle="JOIN_MITER" color="0,0,0" dashStyle="PLAIN_STROKE" capStyle="CAP_SQUARE" />
      <TextStyle id="12" backgroundColor="255,255,255" color="0,0,0" font="Lucida Sans,0,11" />
    </ComplexCurveGraphicalRepresentation>
  </ComplexCurveWithNPointsConstruction>
  <ComplexCurveWithNPointsConstruction id="13" closure="CLOSED_FILLED" name="Curve1">
    <ExplicitPointConstruction id="14" point="198.0,445.0" name="Point8" />
    <ExplicitPointConstruction id="15" point="230.0,195.0" name="Point9" />
    <ExplicitPointConstruction id="16" point="429.0,441.0" name="Point10" />
    <ExplicitPointConstruction id="17" point="362.0,567.0" name="Point11" />
    <ExplicitPointConstruction id="18" point="208.0,495.0" name="Point12" />
    <ColorBackgroundStyle id="19" color="254,247,217" transparencyLevel="0.5" />
    <ForegroundStyle id="20" joinStyle="JOIN_MITER" color="0,0,0" dashStyle="PLAIN_STROKE" capStyle="CAP_SQUARE" />
    <ComplexCurveGraphicalRepresentation id="21" identifier="722825c0">
      <ColorBackgroundStyle id="22" color="254,247,217" transparencyLevel="0.5" />
      <ForegroundStyle id="23" joinStyle="JOIN_MITER" color="0,0,0" dashStyle="PLAIN_STROKE" capStyle="CAP_SQUARE" />
      <TextStyle id="24" backgroundColor="255,255,255" color="0,0,0" font="Lucida Sans,0,11" />
    </ComplexCurveGraphicalRepresentation>
  </ComplexCurveWithNPointsConstruction>
  <IntersectionConstruction id="25" name="Intersection2">
    <ObjectReference id="26" name="Construction13">
      <ComplexCurveWithNPointsConstruction idref="1" />
    </ObjectReference>
    <ObjectReference id="27" name="Construction14">
      <ComplexCurveWithNPointsConstruction idref="13" />
    </ObjectReference>
    <TexturedBackgroundStyle id="28" color1="255,51,51" color2="255,255,255" textureType="TEXTURE1" transparencyLevel="0.5" />
    <ForegroundStyle id="29" joinStyle="JOIN_MITER" color="0,0,0" dashStyle="PLAIN_STROKE" capStyle="CAP_SQUARE" />
    <ComputedAreaGraphicalRepresentation id="30" identifier="4a3eb5b1">
      <ColorBackgroundStyle id="31" color="254,247,217" transparencyLevel="0.5" />
      <ForegroundStyle id="32" joinStyle="JOIN_MITER" color="0,0,0" dashStyle="PLAIN_STROKE" capStyle="CAP_SQUARE" />
      <TextStyle id="33" backgroundColor="255,255,255" color="0,0,0" font="Lucida Sans,0,11" />
    </ComputedAreaGraphicalRepresentation>
  </IntersectionConstruction>
  <ExplicitPointConstruction id="34" point="107.0,218.0" name="Point15">
    <ColorBackgroundStyle id="35" color="254,247,217" transparencyLevel="0.5" />
    <ForegroundStyle id="36" joinStyle="JOIN_MITER" color="0,0,0" dashStyle="PLAIN_STROKE" capStyle="CAP_SQUARE" />
    <PointGraphicalRepresentation id="37" identifier="ffc44fc">
      <TexturedBackgroundStyle id="38" color1="255,0,0" color2="255,255,255" textureType="TEXTURE1" transparencyLevel="0.5" />
      <TextStyle id="39" backgroundColor="255,255,255" color="0,0,0" font="Lucida Sans,0,11" />
    </PointGraphicalRepresentation>
  </ExplicitPointConstruction>
  <NearestPointFromObjectConstruction id="40" name="Point16">
    <Ref_ObjectReference id="41" name="Construction17">
      <IntersectionConstruction idref="25" />
    </Ref_ObjectReference>
    <P_PointReference id="42" name="Point18">
      <Ref_ExplicitPointConstruction idref="34" />
    </P_PointReference>
    <ColorBackgroundStyle id="43" color="254,247,217" transparencyLevel="0.5" />
    <ForegroundStyle id="44" joinStyle="JOIN_MITER" color="0,0,0" dashStyle="PLAIN_STROKE" capStyle="CAP_SQUARE" />
    <PointGraphicalRepresentation id="45" identifier="7b28b54b">
      <TexturedBackgroundStyle id="46" color1="255,0,0" color2="255,255,255" textureType="TEXTURE1" transparencyLevel="0.5" />
      <TextStyle id="47" backgroundColor="255,255,255" color="0,0,0" font="Lucida Sans,0,11" />
    </PointGraphicalRepresentation>
  </NearestPointFromObjectConstruction>
  <LineWithTwoPointsConstruction id="48" name="Line19">
    <P1_PointReference id="49" name="Point20">
      <Ref_ExplicitPointConstruction idref="34" />
    </P1_PointReference>
    <P2_ControlPointReference id="50" controlPointName="point" name="Point21">
      <NearestPointFromObjectConstruction idref="40" />
    </P2_ControlPointReference>
    <ColorBackgroundStyle id="51" color="254,247,217" transparencyLevel="0.5" />
    <ForegroundStyle id="52" joinStyle="JOIN_MITER" color="153,153,153" dashStyle="BIG_DASHES" capStyle="CAP_SQUARE" lineWidth="0.3999999985098839" />
    <LineGraphicalRepresentation id="53" identifier="63610c53">
      <TextStyle id="54" backgroundColor="255,255,255" color="0,0,0" font="Lucida Sans,0,11" />
    </LineGraphicalRepresentation>
  </LineWithTwoPointsConstruction>
  <DrawingGraphicalRepresentation id="55" backgroundColor="255,255,255" drawWorkingArea="false" focusColor="255,0,0" selectionColor="0,0,255" rectangleSelectingSelectionColor="0,0,255" dimensionConstraints="FREELY_RESIZABLE" identifier="29433a6d">
    <TextStyle id="56" backgroundColor="255,255,255" color="0,0,0" font="Lucida Sans,0,11" />
  </DrawingGraphicalRepresentation>
</GeometricDiagram>
