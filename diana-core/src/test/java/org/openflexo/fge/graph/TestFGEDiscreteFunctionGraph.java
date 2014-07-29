package org.openflexo.fge.graph;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.fge.ColorGradientBackgroundStyle.ColorGradientDirection;
import org.openflexo.fge.FGEModelFactory;
import org.openflexo.fge.FGEModelFactoryImpl;
import org.openflexo.fge.graph.FGEFunctionGraph.Orientation;
import org.openflexo.fge.graph.FGEGraph.GraphType;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;

@RunWith(OrderedRunner.class)
public class TestFGEDiscreteFunctionGraph {

	private static FGEModelFactory FACTORY;

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

	public static class Person {
		public String name;
		public int age;
		public double weight;

		public Person(String name, int age, double weight) {
			super();
			this.name = name;
			this.age = age;
			this.weight = weight;
		}
	}

	@Test
	@TestOrder(1)
	public void test1InitGraph() {
		List<Person> persons = new ArrayList<Person>();
		persons.add(new Person("Martin", 39, 73.7));
		persons.add(new Person("Mary", 41, 57.0));
		persons.add(new Person("John", 9, 26.3));
		persons.add(new Person("Martin Jr", 5, 19.2));

		FGEModelFactory factory = null;
		FGEDiscreteFunctionGraph<Person> graph = new FGEDiscreteFunctionGraph<Person>();

		graph.setParameter("person", Person.class);
		graph.setDiscreteValues(persons);
		graph.setDiscreteValuesLabel(new DataBinding<String>("person.name"));
		graph.setParameterOrientation(Orientation.HORIZONTAL);
		graph.setDiscreteValuesSpacing(80.0);

		FGENumericFunction<Integer> ageFunction = graph.addNumericFunction("age", Integer.class, new DataBinding<Integer>("person.age"),
				GraphType.BAR_GRAPH);
		ageFunction.setRange(0, 100);
		ageFunction.setForegroundStyle(FACTORY.makeForegroundStyle(Color.RED, 1.0f));
		ageFunction.setBackgroundStyle(FACTORY.makeColorGradientBackground(Color.BLUE, Color.WHITE, ColorGradientDirection.NORTH_SOUTH));

		FGENumericFunction<Double> weightFunction = graph.addNumericFunction("weight", Double.class, new DataBinding<Double>(
				"person.weight"), GraphType.POLYLIN);
		weightFunction.setForegroundStyle(FACTORY.makeForegroundStyle(Color.GREEN, 1.0f));
		weightFunction.setBackgroundStyle(FACTORY.makeColoredBackground(Color.YELLOW));

	}

}
