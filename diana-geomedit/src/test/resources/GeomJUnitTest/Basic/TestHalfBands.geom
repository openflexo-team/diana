<?xml version="1.0" encoding="UTF-8"?>
<GeometricDiagram id="0" name="TestHalfBands.geom">
  <ExplicitPointConstruction id="1" point="321.0,258.0" name="Point0">
    <PointGraphicalRepresentation id="2" identifier="6e097dcd">
      <TexturedBackgroundStyle id="3" color1="255,0,0" color2="255,255,255" textureType="TEXTURE1" transparencyLevel="0.5" />
      <ForegroundStyle id="4" joinStyle="JOIN_MITER" color="0,0,0" dashStyle="PLAIN_STROKE" capStyle="CAP_SQUARE" />
      <TextStyle id="5" backgroundColor="255,255,255" color="0,0,0" font="Lucida Sans,0,11" />
    </PointGraphicalRepresentation>
  </ExplicitPointConstruction>
  <ExplicitPointConstruction id="6" point="506.0,232.0" name="Point1">
    <PointGraphicalRepresentation id="7" identifier="6030eb47">
      <TexturedBackgroundStyle id="8" color1="255,0,0" color2="255,255,255" textureType="TEXTURE1" transparencyLevel="0.5" />
      <ForegroundStyle id="9" joinStyle="JOIN_MITER" color="0,0,0" dashStyle="PLAIN_STROKE" capStyle="CAP_SQUARE" />
      <TextStyle id="10" backgroundColor="255,255,255" color="0,0,0" font="Lucida Sans,0,11" />
    </PointGraphicalRepresentation>
  </ExplicitPointConstruction>
  <LineWithTwoPointsConstruction id="11" name="Line2">
    <P1_PointReference id="12" name="Point6">
      <Ref_ExplicitPointConstruction idref="1" />
    </P1_PointReference>
    <P2_PointReference id="13" name="Point7">
      <Ref_ExplicitPointConstruction idref="6" />
    </P2_PointReference>
    <LineGraphicalRepresentation id="14" identifier="544b3821">
      <ColorBackgroundStyle id="15" color="254,247,217" transparencyLevel="0.5" />
      <ForegroundStyle id="16" joinStyle="JOIN_MITER" color="0,0,0" dashStyle="PLAIN_STROKE" capStyle="CAP_SQUARE" />
      <TextStyle id="17" backgroundColor="255,255,255" color="0,0,0" font="Lucida Sans,0,11" />
    </LineGraphicalRepresentation>
  </LineWithTwoPointsConstruction>
  <ParallelLineWithPointConstruction id="18" name="Line3">
    <ExplicitPointConstruction id="19" point="306.3138005843396,153.50204261933243" name="Point9" />
    <LineReference id="20" name="Line8">
      <LineWithTwoPointsConstruction idref="11" />
    </LineReference>
    <LineGraphicalRepresentation id="21" identifier="59c8bf22">
      <ColorBackgroundStyle id="22" color="254,247,217" transparencyLevel="0.5" />
      <ForegroundStyle id="23" joinStyle="JOIN_MITER" color="0,0,0" dashStyle="PLAIN_STROKE" capStyle="CAP_SQUARE" />
      <TextStyle id="24" backgroundColor="255,255,255" color="0,0,0" font="Lucida Sans,0,11" />
    </LineGraphicalRepresentation>
  </ParallelLineWithPointConstruction>
  <ExplicitPointConstruction id="25" point="273.0,433.0" name="Point4">
    <PointGraphicalRepresentation id="26" identifier="4ab73691">
      <TexturedBackgroundStyle id="27" color1="255,0,0" color2="255,255,255" textureType="TEXTURE1" transparencyLevel="0.5" />
      <ForegroundStyle id="28" joinStyle="JOIN_MITER" color="0,0,0" dashStyle="PLAIN_STROKE" capStyle="CAP_SQUARE" />
      <TextStyle id="29" backgroundColor="255,255,255" color="0,0,0" font="Lucida Sans,0,11" />
    </PointGraphicalRepresentation>
  </ExplicitPointConstruction>
  <LineWithTwoPointsConstruction id="30" name="Line5">
    <P1_PointReference id="31" name="Point10">
      <Ref_ExplicitPointConstruction idref="1" />
    </P1_PointReference>
    <P2_PointReference id="32" name="Point11">
      <Ref_ExplicitPointConstruction idref="25" />
    </P2_PointReference>
    <LineGraphicalRepresentation id="33" identifier="23f20f48">
      <ColorBackgroundStyle id="34" color="254,247,217" transparencyLevel="0.5" />
      <ForegroundStyle id="35" joinStyle="JOIN_MITER" color="0,0,0" dashStyle="PLAIN_STROKE" capStyle="CAP_SQUARE" />
      <TextStyle id="36" backgroundColor="255,255,255" color="0,0,0" font="Lucida Sans,0,11" />
    </LineGraphicalRepresentation>
  </LineWithTwoPointsConstruction>
  <HalfBandWithLinesConstruction id="37" name="Construction12">
    <Limit_LineReference id="38" name="Line13">
      <LineWithTwoPointsConstruction idref="30" />
    </Limit_LineReference>
    <L2_LineReference id="39" name="Line14">
      <ParallelLineWithPointConstruction idref="18" />
    </L2_LineReference>
    <L1_LineReference id="40" name="Line15">
      <LineWithTwoPointsConstruction idref="11" />
    </L1_LineReference>
    <ExplicitPointConstruction id="41" point="458.0,422.0" name="Point16" />
    <HalfBandGraphicalRepresentation id="42" identifier="1ef9483f">
      <ColorBackgroundStyle id="43" color="254,247,217" transparencyLevel="0.5" />
      <ForegroundStyle id="44" joinStyle="JOIN_MITER" color="0,0,0" dashStyle="PLAIN_STROKE" capStyle="CAP_SQUARE" />
      <TextStyle id="45" backgroundColor="255,255,255" color="0,0,0" font="Lucida Sans,0,11" />
    </HalfBandGraphicalRepresentation>
  </HalfBandWithLinesConstruction>
  <DrawingGraphicalRepresentation id="46" drawWorkingArea="false" backgroundColor="255,255,255" focusColor="255,0,0" selectionColor="0,0,255" rectangleSelectingSelectionColor="0,0,255" dimensionConstraints="FREELY_RESIZABLE" identifier="4c198547">
    <TextStyle id="47" backgroundColor="255,255,255" color="0,0,0" font="Lucida Sans,0,11" />
  </DrawingGraphicalRepresentation>
</GeometricDiagram>
