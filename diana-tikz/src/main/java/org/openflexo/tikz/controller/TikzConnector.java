/**
 * 
 */
package org.openflexo.tikz.controller;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.StringReader;
import java.util.List;
import java.util.Vector;

import org.openflexo.tikz.model.TikzConstruction;
import org.openflexo.tikz.model.TikzFactory;
import org.openflexo.tikz.parser.lexer.Lexer;
import org.openflexo.tikz.parser.lexer.LexerException;
import org.openflexo.tikz.parser.node.ATikzTikz;
import org.openflexo.tikz.parser.node.PCommand;
import org.openflexo.tikz.parser.node.Start;
import org.openflexo.tikz.parser.parser.Parser;
import org.openflexo.tikz.parser.parser.ParserException;

/**
 * @author Quentin
 *
 */
public class TikzConnector {
	protected List<TikzConstruction> tikzConstructions;
	protected Start ast;
	protected TikzFactory tikzFactory;
	
	public TikzConnector() {
		tikzConstructions = new Vector<TikzConstruction>();
		ast = null;
		tikzFactory = new TikzFactory();
	}

	public String toString() {
		String returned = "TikzConnector\n";
		
		returned += "tikzConstructions : {\n";
		for(TikzConstruction tc : tikzConstructions) {
			returned += "\t" + tc.toString() + "\n";
		}
		returned += "}\n";
		
		return returned;
	}
	
	
	public Boolean printCodeFromTikz() {
		System.out.println(TikzWriter.writeTikzCode(tikzConstructions, ast));
		return true;
	}
	
	public String makeCodeFromTikz() {
		return TikzWriter.writeTikzCode(tikzConstructions, ast);
	}

	
	public Boolean makeAstFromString(String s) {
		Parser p = new Parser(new Lexer(new PushbackReader(new StringReader(s))));
		try{
			ast = p.parse();
		} catch (ParserException e) {
			e.printStackTrace();
			ast = null;
			return false;
		} catch (LexerException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			ast = null;
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			ast = null;
			return false;
		}
		return true;
	}
	
	public Boolean makeTikzConstructionsFromAst() {
		if(ast == null) {
			return false;
		}
		tikzConstructions.clear();
		for(PCommand pc : ((ATikzTikz) ast.getPTikz()).getCommands()) {
			TikzConstruction tc = TikzFactory.makeTikzConstruction(pc);
			if(tc != null) {
				tikzConstructions.add(tc);
			}
		}
		return true;
	}
	
}
