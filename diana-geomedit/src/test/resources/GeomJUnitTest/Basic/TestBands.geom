<?xml version="1.0" encoding="UTF-8"?>
<GeometricDiagram id="0" name="TestBands.geom">
  <ExplicitPointConstruction id="1" point="349.0,192.0" name="Point0">
    <PointGraphicalRepresentation id="2" identifier="3b5b2862">
      <TexturedBackgroundStyle id="3" color1="255,0,0" color2="255,255,255" textureType="TEXTURE1" transparencyLevel="0.5" />
      <ForegroundStyle id="4" joinStyle="JOIN_MITER" color="0,0,0" dashStyle="PLAIN_STROKE" capStyle="CAP_SQUARE" />
      <TextStyle id="5" backgroundColor="255,255,255" color="0,0,0" font="Lucida Sans,0,11" />
    </PointGraphicalRepresentation>
  </ExplicitPointConstruction>
  <ExplicitPointConstruction id="6" point="549.0,233.0" name="Point1">
    <PointGraphicalRepresentation id="7" identifier="733bfc7c">
      <TexturedBackgroundStyle id="8" color1="255,0,0" color2="255,255,255" textureType="TEXTURE1" transparencyLevel="0.5" />
      <ForegroundStyle id="9" joinStyle="JOIN_MITER" color="0,0,0" dashStyle="PLAIN_STROKE" capStyle="CAP_SQUARE" />
      <TextStyle id="10" backgroundColor="255,255,255" color="0,0,0" font="Lucida Sans,0,11" />
    </PointGraphicalRepresentation>
  </ExplicitPointConstruction>
  <LineWithTwoPointsConstruction id="11" name="Line2">
    <P1_PointReference id="12" name="Point4">
      <Ref_ExplicitPointConstruction idref="1" />
    </P1_PointReference>
    <P2_PointReference id="13" name="Point5">
      <Ref_ExplicitPointConstruction idref="6" />
    </P2_PointReference>
    <LineGraphicalRepresentation id="14" identifier="4c942c45">
      <ColorBackgroundStyle id="15" color="254,247,217" transparencyLevel="0.5" />
      <ForegroundStyle id="16" joinStyle="JOIN_MITER" color="0,0,0" dashStyle="PLAIN_STROKE" capStyle="CAP_SQUARE" />
      <TextStyle id="17" backgroundColor="255,255,255" color="0,0,0" font="Lucida Sans,0,11" />
    </LineGraphicalRepresentation>
  </LineWithTwoPointsConstruction>
  <ParallelLineWithPointConstruction id="18" name="Line3">
    <ExplicitPointConstruction id="19" point="322.2370321719853,322.5510625757047" name="Point6" />
    <LineReference id="20" name="Line7">
      <LineWithTwoPointsConstruction idref="11" />
    </LineReference>
    <LineGraphicalRepresentation id="21" identifier="37a7b77c">
      <ColorBackgroundStyle id="22" color="254,247,217" transparencyLevel="0.5" />
      <ForegroundStyle id="23" joinStyle="JOIN_MITER" color="0,0,0" dashStyle="PLAIN_STROKE" capStyle="CAP_SQUARE" />
      <TextStyle id="24" backgroundColor="255,255,255" color="0,0,0" font="Lucida Sans,0,11" />
    </LineGraphicalRepresentation>
  </ParallelLineWithPointConstruction>
  <BandWithTwoLinesConstruction id="25" name="Construction8">
    <L2_LineReference id="26" name="Line9">
      <ParallelLineWithPointConstruction idref="18" />
    </L2_LineReference>
    <L1_LineReference id="27" name="Line10">
      <LineWithTwoPointsConstruction idref="11" />
    </L1_LineReference>
    <BandGraphicalRepresentation id="28" identifier="2c5e7b6e">
      <ColorBackgroundStyle id="29" color="254,247,217" transparencyLevel="0.5" />
      <ForegroundStyle id="30" joinStyle="JOIN_MITER" color="0,0,0" dashStyle="PLAIN_STROKE" capStyle="CAP_SQUARE" />
      <TextStyle id="31" backgroundColor="255,255,255" color="0,0,0" font="Lucida Sans,0,11" />
    </BandGraphicalRepresentation>
  </BandWithTwoLinesConstruction>
  <DrawingGraphicalRepresentation id="32" backgroundColor="255,255,255" drawWorkingArea="false" focusColor="255,0,0" selectionColor="0,0,255" rectangleSelectingSelectionColor="0,0,255" dimensionConstraints="FREELY_RESIZABLE" identifier="55bef373">
    <TextStyle id="33" backgroundColor="255,255,255" color="0,0,0" font="Lucida Sans,0,11" />
  </DrawingGraphicalRepresentation>
</GeometricDiagram>
