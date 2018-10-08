<?xml version="1.0" encoding="UTF-8"?>
<GeometricDiagram id="0" name="TestSinglePoint.geom">
  <ExplicitPointConstruction id="1" point="231.0,112.0" name="Construction0">
    <PointGraphicalRepresentation id="2" identifier="286a7b7d">
      <TexturedBackgroundStyle id="3" color1="255,0,0" color2="255,255,255" textureType="TEXTURE1" transparencyLevel="0.5" />
      <ForegroundStyle id="4" joinStyle="JOIN_MITER" color="0,0,0" dashStyle="PLAIN_STROKE" capStyle="CAP_SQUARE" />
      <TextStyle id="5" backgroundColor="255,255,255" color="0,0,0" font="Lucida Sans,0,11" />
    </PointGraphicalRepresentation>
  </ExplicitPointConstruction>
  <ExplicitPointConstruction id="6" point="564.0,297.0" name="Construction1">
    <PointGraphicalRepresentation id="7" identifier="3261493d">
      <TexturedBackgroundStyle id="8" color1="255,0,0" color2="255,255,255" textureType="TEXTURE1" transparencyLevel="0.5" />
      <ForegroundStyle id="9" joinStyle="JOIN_MITER" color="0,0,0" dashStyle="PLAIN_STROKE" capStyle="CAP_SQUARE" />
      <TextStyle id="10" backgroundColor="255,255,255" color="0,0,0" font="Lucida Sans,0,11" />
    </PointGraphicalRepresentation>
  </ExplicitPointConstruction>
  <PointMiddleOfTwoPointsConstruction id="11" name="Construction2">
    <P1_PointReference id="12" name="Construction3">
      <Ref_ExplicitPointConstruction idref="1" />
    </P1_PointReference>
    <P2_PointReference id="13" name="Construction4">
      <Ref_ExplicitPointConstruction idref="6" />
    </P2_PointReference>
    <PointGraphicalRepresentation id="14" identifier="4a52f1f4">
      <TexturedBackgroundStyle id="15" color1="255,0,0" color2="255,255,255" textureType="TEXTURE1" transparencyLevel="0.5" />
      <ForegroundStyle id="16" joinStyle="JOIN_MITER" color="0,0,0" dashStyle="PLAIN_STROKE" capStyle="CAP_SQUARE" />
      <TextStyle id="17" backgroundColor="255,255,255" color="0,0,0" font="Lucida Sans,0,11" />
    </PointGraphicalRepresentation>
  </PointMiddleOfTwoPointsConstruction>
  <ExplicitPointConstruction id="18" point="278.0,283.0" name="Construction5">
    <PointGraphicalRepresentation id="19" identifier="4b72c5b5">
      <TexturedBackgroundStyle id="20" color1="255,0,0" color2="255,255,255" textureType="TEXTURE1" transparencyLevel="0.5" />
      <ForegroundStyle id="21" joinStyle="JOIN_MITER" color="0,0,0" dashStyle="PLAIN_STROKE" capStyle="CAP_SQUARE" />
      <TextStyle id="22" backgroundColor="255,255,255" color="0,0,0" font="Lucida Sans,0,11" />
    </PointGraphicalRepresentation>
  </ExplicitPointConstruction>
  <SymetricPointConstruction id="23" name="Construction6">
    <P2_ControlPointReference id="24" controlPointName="point" name="Construction7">
      <PointMiddleOfTwoPointsConstruction idref="11" />
    </P2_ControlPointReference>
    <P1_PointReference id="25" name="Construction8">
      <Ref_ExplicitPointConstruction idref="18" />
    </P1_PointReference>
    <PointGraphicalRepresentation id="26" identifier="3b78835">
      <TexturedBackgroundStyle id="27" color1="255,0,0" color2="255,255,255" textureType="TEXTURE1" transparencyLevel="0.5" />
      <ForegroundStyle id="28" joinStyle="JOIN_MITER" color="0,0,0" dashStyle="PLAIN_STROKE" capStyle="CAP_SQUARE" />
      <TextStyle id="29" backgroundColor="255,255,255" color="0,0,0" font="Lucida Sans,0,11" />
    </PointGraphicalRepresentation>
  </SymetricPointConstruction>
  <DrawingGraphicalRepresentation id="30" backgroundColor="255,255,255" drawWorkingArea="false" focusColor="255,0,0" selectionColor="0,0,255" rectangleSelectingSelectionColor="0,0,255" dimensionConstraints="FREELY_RESIZABLE" identifier="11ed09b4">
    <TextStyle id="31" backgroundColor="255,255,255" color="0,0,0" font="Lucida Sans,0,11" />
  </DrawingGraphicalRepresentation>
</GeometricDiagram>
