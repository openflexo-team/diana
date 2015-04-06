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
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.fge.ColorGradientBackgroundStyle.ColorGradientDirection;
import org.openflexo.fge.FGEModelFactory;
import org.openflexo.fge.FGEModelFactoryImpl;
import org.openflexo.fge.graph.FGEGraph.GraphType;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;

@RunWith(OrderedRunner.class)
public class TestFGEContinuousFunctionGraph {

	private static FGEModelFactory FACTORY;

	private static FGEContinuousFunctionGraph<Double> graph;
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
		graph = new FGEContinuousFunctionGraph<Double>(Double.class);
		graph.setParameter("x", Double.class);
		graph.setParameterRange(-10.0, 10.0);

		yFunction = graph.addNumericFunction("y", Double.class, new DataBinding<Double>("x*x-2*x+1"), GraphType.CURVE);
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
