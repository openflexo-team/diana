/**
 * 
 */
package org.openflexo.tikz.controller;

import java.util.LinkedList;
import java.util.List;

import org.openflexo.tikz.model.TikzConstruction;
import org.openflexo.tikz.model.TikzConstructionDraw;
import org.openflexo.tikz.model.TikzConstructionNode;
import org.openflexo.tikz.model.TikzFactory;
import org.openflexo.tikz.model.TikzConstruction.ParameterType;
import org.openflexo.tikz.parser.node.ADrawCommand;
import org.openflexo.tikz.parser.node.ANodeCommand;
import org.openflexo.tikz.parser.node.AParamParam;
import org.openflexo.tikz.parser.node.ATikzTikz;
import org.openflexo.tikz.parser.node.PCommand;
import org.openflexo.tikz.parser.node.PParam;
import org.openflexo.tikz.parser.node.Start;
import org.openflexo.tikz.parser.node.TIdentifier;

/**
 * @author Quentin
 *
 */
public class TikzWriter {
	
	public static String writeTikzCode(List<TikzConstruction> tikzConstructions, Start ast) {
		String returned = "\\begin{tikzpicture}\n";
		
		List<PCommand> commands = ((ATikzTikz) ast.getPTikz()).getCommands();
		int i = 0, j = 0;
		ADrawCommand adcEmpty = new ADrawCommand();
		ANodeCommand ancEmpty = new ANodeCommand(new TIdentifier(""), null, new LinkedList<PParam>());
		
		for(i = 0 ; i < commands.size() ; i ++) {
			// corresponding command and construction should be at the same position because added constructions come after.
			PCommand command = commands.get(i);
			TikzConstruction tikzConstruction = tikzConstructions.get(i);
			if(tikzConstruction.isDeleted()) {
				continue;
			}
			if(command instanceof ADrawCommand && tikzConstruction instanceof TikzConstructionDraw) {
				returned += writeDrawCommand((ADrawCommand) command, (TikzConstructionDraw) tikzConstruction);
			}
			else if(command instanceof ANodeCommand && tikzConstruction instanceof TikzConstructionNode) {
				returned += writeNodeCommand((ANodeCommand) command, (TikzConstructionNode) tikzConstruction);
			}
		}
		
		for(j = i ; j < tikzConstructions.size() ; j ++) {
			TikzConstruction tikzConstruction = tikzConstructions.get(j);
			if(tikzConstruction.isDeleted()) {
				continue;
			}
			if(tikzConstruction instanceof TikzConstructionDraw) {
				returned += writeDrawCommand(adcEmpty, (TikzConstructionDraw) tikzConstruction);
			}
			else if(tikzConstruction instanceof TikzConstructionNode) {
				returned += writeNodeCommand(ancEmpty, (TikzConstructionNode) tikzConstruction);
			}
		}
		
		returned += "\\end{tikzpicture}";
		
		return returned;
	}
	
	public static String writeDrawCommand(ADrawCommand adc, TikzConstructionDraw tcd) {
		String returned = "    \\draw ";
		
		// write (position)
		if(tcd.has_from_node()) {
			returned += "( " + tcd.get_from_node() + " ) ";
		}
		else if(tcd.has_from_absolute()) {
			returned += tcd.get_from_absolute().toString(5) + " ";
		}
		// write --
		returned += "-- ";
		// write (position)
		if(tcd.has_to_node()) {
			returned += "( " + tcd.get_to_node() + " ) ";
		}
		else if(tcd.has_to_absolute()) {
			returned += tcd.get_to_absolute().toString(5) + " ";
		}
		// write ;
		returned += ";\n";
		
		return returned;
	}
	
	public static String writeNodeCommand(ANodeCommand anc, TikzConstructionNode tcn) {
		String returned = "    \\node ";
		
		// write parameters
		returned += writeParameters(anc.getParams(), tcn);
		// write (name)
		if(tcn.has_name()) {
			returned += "(" + tcn.get_name() + ") ";
		}
		// write at (position)
		if(tcn.has_at_node()) {
			returned += "at (" + tcn.get_at_node() + ") ";
		}
		else if(tcn.has_at_absolute()) {
			returned += "at " + tcn.get_at_absolute().toString(5) + " ";
		}
		// write ;
		returned += "{} ;\n";
		
		return returned;
	}
	
	public static String writeParameters(List<PParam> ast_parameters, TikzConstruction tc) {
		String returned = "[";
		Boolean firstParameter = true;
		
		List<ParameterType> parameters = ParameterType.allParameterTypes();
		// parameters given in the Tikz code previously parsed
		for(PParam ast_pparam : ast_parameters) {
			AParamParam ast_param = (AParamParam) ast_pparam;
			String parameterName = TikzFactory.makeStringFromTIdentifier(ast_param.getName());
			ParameterType parameterType = ParameterType.fromString(parameterName);
			if(parameterType != null 
					&& parameterType.isInParametersList() 
					&& tc.parameterExists(parameterType) 
					&& tc.getParameterValue(parameterType).hasValue()) {
				returned += writeParameter(firstParameter, parameterType, tc);
				parameters.remove(parameterType);
				firstParameter = false;
			}
		}
		// other parameters
		for(ParameterType parameterType : parameters) {
			if(parameterType.isInParametersList() 
					&& tc.parameterExists(parameterType) 
					&& tc.getParameterValue(parameterType).hasValue()
					&& !tc.getParameterValue(parameterType).hasDefaultValue()) {
				returned += writeParameter(firstParameter, parameterType, tc);
				firstParameter = false;
			}
		}
		
		returned += "] ";
		
		return returned;
	}
	
	public static String writeParameter(Boolean firstParameter, ParameterType parameterType, TikzConstruction tc) {
		String returned = "";
		
		String parameterValue = tc.getParameterValue(parameterType).getValueAsString();
		if(!firstParameter) {
			returned += ", ";
		}
		returned += parameterType.toString() + "=" + parameterValue;
		
		return returned;
	}
	
}
