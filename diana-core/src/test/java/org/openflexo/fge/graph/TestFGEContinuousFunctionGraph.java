/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Diana-core, a component of the software infrastructure 
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

package org.openflexo.fge.graph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.awt.Color;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openflexo.connie.DataBinding;
import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.fge.ColorGradientBackgroundStyle.ColorGradientDirection;
import org.openflexo.fge.FGEModelFactory;
import org.openflexo.fge.FGEModelFactoryImpl;
import org.openflexo.fge.graph.FGEFunction.FGEGraphType;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;

@RunWith(OrderedRunner.class)
public class TestFGEContinuousFunctionGraph {

	private static FGEModelFactory FACTORY;

	private static FGEContinuousSimpleFunctionGraph<Double> graph;
	private static FGENumericFunction<Double> yFunction;

	@BeforeClass
	public static void beforeClass() throws ModelDefinitionException {
		FACTORY = new FGEModelFactoryImpl();
	}

	@AfterClass
	public static void afterClass() {
		// FACTORY.delete();
	}

	@Before
	public void before() {
	}

	@After
	public void after() {
	}

	@Test
	@TestOrder(1)
	public void test1InitGraph() {
		graph = new FGEContinuousSimpleFunctionGraph<Double>(Double.class);
		graph.setParameter("x", Double.class);
		graph.setParameterRange(-10.0, 10.0);

		yFunction = graph.addNumericFunction("y", Double.class, new DataBinding<Double>("x*x-2*x+1"), FGEGraphType.CURVE);
		yFunction.setRange(0.0, 100.0);
		yFunction.setForegroundStyle(FACTORY.makeForegroundStyle(Color.RED, 1.0f));
		yFunction.setBackgroundStyle(FACTORY.makeColorGradientBackground(Color.BLUE, Color.WHITE, ColorGradientDirection.NORTH_SOUTH));

		assertTrue(yFunction.getFunctionExpression().isValid());
	}

	@Test
	@TestOrder(2)
	public void testRedefineParameter() {
		try {
			graph.setParameter("x2", Double.class);
			fail();
		} catch (IllegalArgumentException e) {
			// Normal
		}
	}

	@Test
	@TestOrder(3)
	public void testEvaluation() throws TypeMismatchException, NullReferenceException, InvocationTargetException {
		assertEquals((Double) 0.0, graph.evaluateFunction(yFunction, 1.0));
		assertEquals((Double) 1.0, graph.evaluateFunction(yFunction, 2.0));
		assertEquals((Double) 4.0, graph.evaluateFunction(yFunction, 3.0));
		assertEquals(4.0, graph.getEvaluator().get("y"));
	}

	@Test
	@TestOrder(4)
	public void testIterate() throws TypeMismatchException, NullReferenceException, InvocationTargetException {
		int i = 0;
		Iterator<Double> it = graph.iterateParameter();
		while (it.hasNext()) {
			Double d = it.next();
			System.out.println("For " + d + " value is " + graph.evaluateFunction(yFunction, d));
			i++;
		}
		assertEquals(21, i);

	}
}
