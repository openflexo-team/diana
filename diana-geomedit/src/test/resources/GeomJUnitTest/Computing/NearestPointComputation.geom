<?xml version="1.0" encoding="UTF-8"?>
<GeometricDiagram id="0" name="NearestPointComputation.geom">
  <LineWithTwoPointsConstruction id="1" name="Line0">
    <P1_ExplicitPointConstruction id="2" point="159.0,189.0" name="Point2" />
    <P2_ExplicitPointConstruction id="3" point="484.0,325.0" name="Point3" />
    <ColorBackgroundStyle id="4" color="254,247,217" transparencyLevel="0.5" />
    <ForegroundStyle id="5" joinStyle="JOIN_MITER" color="0,0,0" dashStyle="PLAIN_STROKE" capStyle="CAP_SQUARE" lineWidth="1.9000000014901162" />
    <LineGraphicalRepresentation id="6" identifier="11b614b2">
      <TextStyle id="7" backgroundColor="255,255,255" color="0,0,0" font="Lucida Sans,0,11" />
    </LineGraphicalRepresentation>
  </LineWithTwoPointsConstruction>
  <ExplicitPointConstruction id="8" point="134.0,324.0" name="Point1">
    <ColorBackgroundStyle id="9" color="254,247,217" transparencyLevel="0.5" />
    <ForegroundStyle id="10" joinStyle="JOIN_MITER" color="0,0,0" dashStyle="PLAIN_STROKE" capStyle="CAP_SQUARE" />
    <PointGraphicalRepresentation id="11" identifier="1e931b15">
      <TexturedBackgroundStyle id="12" color1="255,0,0" color2="255,255,255" textureType="TEXTURE1" transparencyLevel="0.5" />
      <TextStyle id="13" backgroundColor="255,255,255" color="0,0,0" font="Lucida Sans,0,11" />
    </PointGraphicalRepresentation>
  </ExplicitPointConstruction>
  <NearestPointFromObjectConstruction id="14" name="Point4">
    <Ref_ObjectReference id="15" name="Construction6">
      <LineWithTwoPointsConstruction idref="1" />
    </Ref_ObjectReference>
    <P_PointReference id="16" name="Point7">
      <Ref_ExplicitPointConstruction idref="8" />
    </P_PointReference>
    <ColorBackgroundStyle id="17" color="254,247,217" transparencyLevel="0.5" />
    <ForegroundStyle id="18" joinStyle="JOIN_MITER" color="0,0,0" dashStyle="PLAIN_STROKE" capStyle="CAP_SQUARE" />
    <PointGraphicalRepresentation id="19" identifier="a3e338e">
      <TexturedBackgroundStyle id="20" color1="255,0,0" color2="255,255,255" textureType="TEXTURE1" transparencyLevel="0.5" />
      <TextStyle id="21" backgroundColor="255,255,255" color="0,0,0" font="Lucida Sans,0,11" />
    </PointGraphicalRepresentation>
  </NearestPointFromObjectConstruction>
  <SegmentWithTwoPointsConstruction id="22" name="Segment5">
    <P1_PointReference id="23" name="Point8">
      <Ref_ExplicitPointConstruction idref="8" />
    </P1_PointReference>
    <P2_ControlPointReference id="24" controlPointName="point" name="Point9">
      <NearestPointFromObjectConstruction idref="14" />
    </P2_ControlPointReference>
    <ColorBackgroundStyle id="25" color="254,247,217" transparencyLevel="0.5" />
    <ForegroundStyle id="26" joinStyle="JOIN_MITER" color="102,102,102" dashStyle="BIG_DASHES" capStyle="CAP_SQUARE" lineWidth="0.3999999985098839" />
    <SegmentGraphicalRepresentation id="27" identifier="43cbee2e">
      <TextStyle id="28" backgroundColor="255,255,255" color="0,0,0" font="Lucida Sans,0,11" />
    </SegmentGraphicalRepresentation>
  </SegmentWithTwoPointsConstruction>
  <RectangleWithTwoPointsConstruction id="29" name="Rectangle10">
    <P1_ExplicitPointConstruction id="30" point="493.0,115.0" name="Point13" />
    <P2_ExplicitPointConstruction id="31" point="715.0,270.0" name="Point14" />
    <ColorBackgroundStyle id="32" color="254,247,217" transparencyLevel="0.5" />
    <ForegroundStyle id="33" joinStyle="JOIN_MITER" color="0,0,0" dashStyle="PLAIN_STROKE" capStyle="CAP_SQUARE" />
    <RectangleGraphicalRepresentation id="34" identifier="7018d9b9">
      <TextStyle id="35" backgroundColor="255,255,255" color="0,0,0" font="Lucida Sans,0,11" />
    </RectangleGraphicalRepresentation>
  </RectangleWithTwoPointsConstruction>
  <NearestPointFromObjectConstruction id="36" name="Point11">
    <Ref_ObjectReference id="37" name="Construction15">
      <RectangleWithTwoPointsConstruction idref="29" />
    </Ref_ObjectReference>
    <P_PointReference id="38" name="Point16">
      <Ref_ExplicitPointConstruction idref="8" />
    </P_PointReference>
    <ColorBackgroundStyle id="39" color="254,247,217" transparencyLevel="0.5" />
    <ForegroundStyle id="40" joinStyle="JOIN_MITER" color="0,0,0" dashStyle="PLAIN_STROKE" capStyle="CAP_SQUARE" />
    <PointGraphicalRepresentation id="41" identifier="1b3f7285">
      <TexturedBackgroundStyle id="42" color1="255,0,0" color2="255,255,255" textureType="TEXTURE1" transparencyLevel="0.5" />
      <TextStyle id="43" backgroundColor="255,255,255" color="0,0,0" font="Lucida Sans,0,11" />
    </PointGraphicalRepresentation>
  </NearestPointFromObjectConstruction>
  <SegmentWithTwoPointsConstruction id="44" name="Segment12">
    <P1_PointReference id="45" name="Point17">
      <Ref_ExplicitPointConstruction idref="8" />
    </P1_PointReference>
    <P2_ControlPointReference id="46" controlPointName="point" name="Point18">
      <NearestPointFromObjectConstruction idref="36" />
    </P2_ControlPointReference>
    <ColorBackgroundStyle id="47" color="254,247,217" transparencyLevel="0.5" />
    <ForegroundStyle id="48" joinStyle="JOIN_MITER" color="102,102,102" dashStyle="BIG_DASHES" capStyle="CAP_SQUARE" lineWidth="0.3999999985098839" />
    <SegmentGraphicalRepresentation id="49" identifier="69cbf5bf">
      <TextStyle id="50" backgroundColor="255,255,255" color="0,0,0" font="Lucida Sans,0,11" />
    </SegmentGraphicalRepresentation>
  </SegmentWithTwoPointsConstruction>
  <QuadCurveWithThreePointsConstruction id="51" name="Curve19">
    <Control_ExplicitPointConstruction id="52" point="425.0,244.0" name="Point20" />
    <End_ExplicitPointConstruction id="53" point="463.0,556.0" name="Point21" />
    <Start_ExplicitPointConstruction id="54" point="158.0,561.0" name="Point22" />
    <ColorBackgroundStyle id="55" color="254,247,217" transparencyLevel="0.5" />
    <ForegroundStyle id="56" joinStyle="JOIN_MITER" color="0,0,0" dashStyle="PLAIN_STROKE" capStyle="CAP_SQUARE" />
    <QuadCurveGraphicalRepresentation id="57" identifier="5b2fe6bc">
      <TextStyle id="58" backgroundColor="255,255,255" color="0,0,0" font="Lucida Sans,0,11" />
    </QuadCurveGraphicalRepresentation>
  </QuadCurveWithThreePointsConstruction>
  <NearestPointFromObjectConstruction id="59" name="Point23">
    <Ref_ObjectReference id="60" name="Construction25">
      <QuadCurveWithThreePointsConstruction idref="51" />
    </Ref_ObjectReference>
    <P_PointReference id="61" name="Point26">
      <Ref_ExplicitPointConstruction idref="8" />
    </P_PointReference>
    <ColorBackgroundStyle id="62" color="254,247,217" transparencyLevel="0.5" />
    <ForegroundStyle id="63" joinStyle="JOIN_MITER" color="0,0,0" dashStyle="PLAIN_STROKE" capStyle="CAP_SQUARE" />
    <PointGraphicalRepresentation id="64" identifier="79bd101a">
      <TexturedBackgroundStyle id="65" color1="255,0,0" color2="255,255,255" textureType="TEXTURE1" transparencyLevel="0.5" />
      <TextStyle id="66" backgroundColor="255,255,255" color="0,0,0" font="Lucida Sans,0,11" />
    </PointGraphicalRepresentation>
  </NearestPointFromObjectConstruction>
  <SegmentWithTwoPointsConstruction id="67" name="Segment24">
    <P1_PointReference id="68" name="Point27">
      <Ref_ExplicitPointConstruction idref="8" />
    </P1_PointReference>
    <P2_ControlPointReference id="69" controlPointName="point" name="Point28">
      <NearestPointFromObjectConstruction idref="59" />
    </P2_ControlPointReference>
    <ColorBackgroundStyle id="70" color="254,247,217" transparencyLevel="0.5" />
    <ForegroundStyle id="71" joinStyle="JOIN_MITER" color="102,102,102" dashStyle="BIG_DASHES" capStyle="CAP_SQUARE" lineWidth="0.3999999985098839" />
    <SegmentGraphicalRepresentation id="72" identifier="532c55e">
      <TextStyle id="73" backgroundColor="255,255,255" color="0,0,0" font="Lucida Sans,0,11" />
    </SegmentGraphicalRepresentation>
  </SegmentWithTwoPointsConstruction>
  <DrawingGraphicalRepresentation id="74" backgroundColor="255,255,255" drawWorkingArea="false" focusColor="255,0,0" selectionColor="0,0,255" rectangleSelectingSelectionColor="0,0,255" dimensionConstraints="FREELY_RESIZABLE" identifier="5cbbcddb">
    <TextStyle id="75" backgroundColor="255,255,255" color="0,0,0" font="Lucida Sans,0,11" />
  </DrawingGraphicalRepresentation>
</GeometricDiagram>
