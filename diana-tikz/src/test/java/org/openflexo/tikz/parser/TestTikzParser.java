/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2012-2012, AgileBirds
 * 
 * This file is part of Connie-core, a component of the software infrastructure 
 * developed at Openflexo.
 * 
 * 
 * Openflexo is dual-licensed under the European Union Public License (EUPL, either 
 * version 1.1 of the License, or any later version ), which is available at 
 * https://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 * and the GNU General Public License (GPL, either version 3 of the License, or any 
 * later version), which is available at http://www.gnu.org/licenses/gpl.html .
 * 
 * You can redistribute it and/or modify under the terms of either of these licenses
 * 
 * If you choose to redistribute it and/or modify under the terms of the GNU GPL, you
 * must include the following additional permission.
 *
 *          Additional permission under GNU GPL version 3 section 7
 *
 *          If you modify this Program, or any covered work, by linking or 
 *          combining it with software containing parts covered by the terms 
 *          of EPL 1.0, the licensors of this Program grant you additional permission
 *          to convey the resulting work. * 
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE. 
 *
 * See http://www.openflexo.org/license.html for details.
 * 
 * 
 * Please contact Openflexo (openflexo-contacts@openflexo.org)
 * or visit www.openflexo.org if you need additional information.
 * 
 */

package org.openflexo.tikz.parser;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.StringReader;

import org.openflexo.tikz.parser.lexer.Lexer;
import org.openflexo.tikz.parser.lexer.LexerException;
import org.openflexo.tikz.parser.node.Start;
import org.openflexo.tikz.parser.parser.Parser;
import org.openflexo.tikz.parser.parser.ParserException;

import junit.framework.TestCase;

public class TestTikzParser extends TestCase {
	private static void tryToParse(String snippet) {
		try {
			Parser p = new Parser(new Lexer(new PushbackReader(new StringReader(snippet))));
			System.out.println("Parsing... " + snippet);
			Start parsed = p.parse();
			String result = parsed.toString().trim();
			System.out.println("Result is '" + result + "'");
			assertEquals(snippet.replaceAll("\\s", ""), result.replaceAll("\\s", ""));
		} catch (ParserException e) {
			e.printStackTrace();
			fail();
		} catch (LexerException e) {
			e.printStackTrace();
			fail();
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}
	}

	private static String addBeginEnd(String snippet) {
		return "\\begin{tikzpicture}\n" + snippet + "\n\\end{tikzpicture}";
	}

	public void testDraw() {
		tryToParse(addBeginEnd("\\draw (a) -- (b);"));
	}

	public void testNode() {
		tryToParse(addBeginEnd("\\node[color=red,shape=circle,width=10mm] (a) at (b) {} ;"));
	}

	public void testSnippet() {
		tryToParse("\\begin{tikzpicture}\n" + "  \\node[draw=red,shape=circle,radius=10mm] (a) at (0,0) {} ;\n"
				+ "  \\node[draw=red,shape=circle,radius=10mm] (b) at (2,2) {} ;\n" + "  \\draw (a) -- (b);\n" + "\\end{tikzpicture}");
	}
}
