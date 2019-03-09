package org.openflexo.tikz.model;

import org.openflexo.tikz.model.TikzConstruction.ParameterType;
import org.openflexo.tikz.parser.node.AAbsolutePosition;
import org.openflexo.tikz.parser.node.ADecimalNumber;
import org.openflexo.tikz.parser.node.ADrawCommand;
import org.openflexo.tikz.parser.node.AIdValue;
import org.openflexo.tikz.parser.node.ANodeCommand;
import org.openflexo.tikz.parser.node.ANodePosition;
import org.openflexo.tikz.parser.node.ANumValue;
import org.openflexo.tikz.parser.node.AParamParam;
import org.openflexo.tikz.parser.node.APreciseNumber;
import org.openflexo.tikz.parser.node.PCommand;
import org.openflexo.tikz.parser.node.PNumber;
import org.openflexo.tikz.parser.node.PParam;
import org.openflexo.tikz.parser.node.PPosition;
import org.openflexo.tikz.parser.node.PValue;
import org.openflexo.tikz.parser.node.TIdentifier;
import org.openflexo.tikz.model.ParameterValue;

public class TikzFactory {
	

	///////////////////////////////////////////////////////////////////////////////////////////////
	// Generic ////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////
	
	public static TikzConstructionNode makeTikzConstructionNode() {
		return new TikzConstructionNode();
	}
	
	public static TikzConstructionDraw makeTikzConstructionDraw() {
		return new TikzConstructionDraw();
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	// From AST ///////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////
	
	public static TikzConstruction makeTikzConstruction(PCommand ast_command) {
		if(ast_command instanceof ANodeCommand) {
			return makeTikzConstructionNode((ANodeCommand) ast_command);
		}
		else if(ast_command instanceof ADrawCommand) {
			return makeTikzConstructionDraw((ADrawCommand) ast_command);
		}
		return null;
	}
	
	public static TikzConstructionNode makeTikzConstructionNode(ANodeCommand ast_node) {
		TikzConstructionNode tcn = new TikzConstructionNode();
		
		setConstructionParameterWithTIdentifier(tcn, ParameterType.NAME, ast_node.getName());
		setConstructionPosition(tcn, ParameterType.AT_ABSOLUTE, ParameterType.AT_NODE, ast_node.getPos());
		
		for(PParam param : ast_node.getParams()) {
			AParamParam p = (AParamParam) param;
			ParameterType pt = ParameterType.fromString(makeStringFromTIdentifier(p.getName()));
			if(pt != null && tcn.parameterExists(pt)) {
				setConstructionParameter(tcn, pt, p.getValue());
			}
		}
		
		return tcn;
	}
	
	public static TikzConstructionDraw makeTikzConstructionDraw(ADrawCommand ast_draw) {
		TikzConstructionDraw tcd = new TikzConstructionDraw();
		
		setConstructionPosition(tcd, ParameterType.FROM_ABSOLUTE, ParameterType.FROM_NODE, ast_draw.getFirst());
		setConstructionPosition(tcd, ParameterType.TO_ABSOLUTE, ParameterType.TO_NODE, ast_draw.getSecond());
		
		return tcd;
	}
	
	// Parameter assignments //////////////////////////////////////////////////////////////////////
	
	public static void setConstructionParameter(TikzConstruction tc, ParameterType pt, PValue pv) {
		if(pv instanceof AIdValue) {
			AIdValue idPV = (AIdValue) pv;
			setConstructionParameterWithTIdentifier(tc, pt, idPV.getContent());
		}
		else if(pv instanceof ANumValue) {
			ANumValue numPV = (ANumValue) pv;
			setConstructionParameterWithANumValue(tc, pt, numPV);
		}
	}
	
	public static void setConstructionPosition(TikzConstruction tc, ParameterType typeIfAbsolute, ParameterType typeIfNode, PPosition p) {
		if(p instanceof AAbsolutePosition) {
			AAbsolutePosition absP = (AAbsolutePosition) p;
			setConstructionParameterWithAAbsolutePosition(tc, typeIfAbsolute, absP);
		}
		else if(p instanceof ANodePosition) {
			ANodePosition nodeP = (ANodePosition) p;
			setConstructionParameterWithTIdentifier(tc, typeIfNode, nodeP.getName());
		}
	}
	
	
	public static void setConstructionParameterWithANumValue(TikzConstruction tc, ParameterType pt, ANumValue numv) {
		ParameterValue<?> pv = tc.getParameterValue(pt);
		pv.setValueAsObject(makeParameterValueValueWithUnitFromANumValue(numv));
	}
	
	public static void setConstructionParameterWithTIdentifier(TikzConstruction tc, ParameterType pt, TIdentifier id) {
		ParameterValue<?> pv = tc.getParameterValue(pt);
		pv.setValueAsObject(makeStringFromTIdentifier(id));
	}
	
	public static void setConstructionParameterWithPNumber(TikzConstruction tc, ParameterType pt, PNumber num) {
		ParameterValue<?> pv = tc.getParameterValue(pt);
		pv.setValueAsObject(makeDoubleFromPNumber(num));
	}

	public static void setConstructionParameterWithAAbsolutePosition(TikzConstruction tc, ParameterType pt, AAbsolutePosition p) {
		ParameterValue<?> pv = tc.getParameterValue(pt);
		pv.setValueAsObject(makeParameterValuePositionFromAAbsolutePosition(p));
	}
	
	// Type conversions ///////////////////////////////////////////////////////////////////////////
	
	public static String makeStringFromTIdentifier(TIdentifier id) {
		return id.getText();
	}
	
	public static double makeDoubleFromPNumber(PNumber n) {
		if(n instanceof ADecimalNumber) {
			return makeDoubleFromADecimalNumber((ADecimalNumber) n);
		}
		else {
			return makeDoubleFromAPreciseNumber((APreciseNumber) n);
		}
	}
	
	public static double makeDoubleFromADecimalNumber(ADecimalNumber n) {
		return Double.parseDouble(n.getDecimalNumber().getText());
	}
	
	public static double makeDoubleFromAPreciseNumber(APreciseNumber n) {
		return Double.parseDouble(n.getPreciseNumber().getText());
	}

	public static ParameterValuePosition makeParameterValuePositionFromAAbsolutePosition(AAbsolutePosition pos) {
		return new ParameterValuePosition(makeDoubleFromPNumber(pos.getX()), makeDoubleFromPNumber(pos.getY()));
	}
	
	public static ParameterValueValueWithUnit makeParameterValueValueWithUnitFromANumValue(ANumValue numv) {
		return new ParameterValueValueWithUnit(makeDoubleFromPNumber(numv.getContent()), makeStringFromTIdentifier(numv.getUnit()));
	}
	
}
