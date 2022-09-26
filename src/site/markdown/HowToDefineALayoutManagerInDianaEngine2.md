# How to define a new LayoutManager in DIANA layout engine ?

Here are the steps to follow to define a new LayoutManager in DIANA layout engine.

Suppose that we want to define a very simple layout manager that for a given shape, places all its children (those layouted by layout manager), in the border (the outline) of the shape.
  
[/images/components/diana/OutlineLayoutManager] OutlineLayoutManager
  
We have first to think at the API ou our layout manager. What are the configuring parameters ? Should we define them for the layout manager specification, or for any instances of this layout manager ?

1. Defining LayoutManager specification

We define the LayoutManager specification (which also play as a factory for instances of LayoutManager).
Here we have chosen to call it OutlineLayoutManagerSpecification.
There is only one configuration parameter which is the type of desired location (center on outline, object outside the container shape,   object inside the container shape)

This java class will be put in org.openflexo.fge.layout package (in DIANA-api project).

```
/**
 * Represents the specification of a OutlineLayoutManager in DIANA<br>
 * (places layouted nodes around outline of container shape)
 * 
 * @author sylvain
 * 
 */
@ModelEntity
@XMLElement
@Imports({ @Import(OutlineLayoutManager.class) })
public interface OutlineLayoutManagerSpecification extends FGELayoutManagerSpecification<OutlineLayoutManager> {

	@PropertyIdentifier(type = OutlineLocationType.class)
	public static final String OUTLINE_LOCATION_TYPE_KEY = "outlineLocationType";

	@Getter(value = OUTLINE_LOCATION_TYPE_KEY, defaultValue = "ON")
	@XMLAttribute
	public OutlineLocationType getOutlineLocationType();

	@Setter(value = OUTLINE_LOCATION_TYPE_KEY)
	public void setOutlineLocationType(OutlineLocationType outlineLocationType);

	/**
	 * Represents type of location constraints relative to outline (should the layouted node be inside the shape, outside the shape, or just
	 * on outline)
	 * 
	 * @author sylvain
	 *
	 */
	public static enum OutlineLocationType {
		INNER, ON, OUTER
	}

}
```

2. Defining LayoutManager instances

Then we define the LayoutManager instance (which is instantiated for each instance of container shape beeing layouted).
Here we have chosen to call it OutlineLayoutManager.
This java class will be put in org.openflexo.fge.layout package (in DIANA-api project).

We have here nothing to encode (because the layout manager is very simple here).
We simply reflect the configuration parameter exposed by its specification.

```
/**
 * Represents a layout manager automatically placing the layouted nodes around the outline of container shape
 * 
 * @author sylvain
 * 
 */
@ModelEntity
@XMLElement
public interface OutlineLayoutManager<O> extends FGELayoutManager<OutlineLayoutManagerSpecification, O> {

	public OutlineLocationType getOutlineLocationType();

}
```

3. Proving default implementations

We will define an implementation class for OutlineLayoutManagerSpecification.

This java class will be put in org.openflexo.fge.layout.impl package (in DIANA-core project).

```
/**
 * Default implementation for the specification of a {@link OutlineLayoutManager} in DIANA<br>
 * 
 * @author sylvain
 * 
 */
public abstract class OutlineLayoutManagerSpecificationImpl extends FGELayoutManagerSpecificationImpl<OutlineLayoutManager>
		implements OutlineLayoutManagerSpecification {

	@Override
	public LayoutManagerSpecificationType getLayoutManagerSpecificationType() {
		return LayoutManagerSpecificationType.OUTLINE;
	}

	@Override
	public Class<OutlineLayoutManager> getLayoutManagerClass() {
		return OutlineLayoutManager.class;
	}

	/**
	 * Return true indicating that this layout manager supports autolayout
	 * 
	 * @return
	 */
	@Override
	public boolean supportAutolayout() {
		return true;
	}

	/**
	 * Return false indicating that this layout manager does not support decoration painting<br>
	 * 
	 * @return
	 */
	@Override
	public boolean supportDecoration() {
		return false;
	}

	@Override
	public DraggingMode getDefaultDraggingMode() {
		return DraggingMode.ContinuousLayout;
	}
}
```

Then we define an implementation class for OutlineLayoutManager.
This java class will be put in org.openflexo.fge.layout.impl package (in DIANA-core project).

Note the performLayout(ShapeNode) method that effectively perform the layout for one node.

```
/**
 * Default implementation for {@link OutlineLayoutManager}
 * 
 * @author sylvain
 * 
 */
public abstract class OutlineLayoutManagerImpl<O> extends FGELayoutManagerImpl<OutlineLayoutManagerSpecification, O>
		implements OutlineLayoutManager<O> {

	@Override
	public OutlineLocationType getOutlineLocationType() {
		return getLayoutManagerSpecification().getOutlineLocationType();
	}

	/**
	 * Return flag indicating if the move or resize of one node might invalidate the whole container
	 * 
	 * @return
	 */
	@Override
	public boolean isFullyLayouted() {
		return false;
	}

	/**
	 * Perform layout for supplied {@link ShapeNode}
	 * 
	 * @param node
	 *            node to layout
	 */
	@Override
	protected void performLayout(ShapeNode<?> node) {

		// Applicable only to SHAPE as container
		if (getContainerNode() instanceof ShapeNode) {

			// System.out.println("Relayouting " + node);

			// Forces the shape node beeing layouted to leave it's original bounds
			// (this outline layout put the layouted shapes at the border of represented shape)
			node.setAllowsToLeaveBounds(true);

			// Then perform the layout, according to the center of layouted shape
			FGEPoint currentPoint = new FGEPoint(node.getLocation());
			ShapeNode<?> container = (ShapeNode<?>) getContainerNode();
			FGEPoint center = new FGEPoint(currentPoint.x + node.getWidth() / 2, currentPoint.y + node.getHeight() / 2);
			FGEPoint normalizedCenterPoint = container.convertViewCoordinatesToNormalizedPoint((int) center.getX(), (int) center.getY(),
					1.0);
			FGEPoint normalizedPointInOutline = container.getShape().nearestOutlinePoint(normalizedCenterPoint);
			Point pointInOutline = container.convertNormalizedPointToViewCoordinates(normalizedPointInOutline, 1.0);
			node.setLocation(new FGEPoint(pointInOutline.x - node.getWidth() / 2, pointInOutline.y - node.getHeight() / 2));
		}

	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {

		// System.out.println("propertyChange in OutlineLayoutManagerImpl with " + evt.getPropertyName() + " evt=" + evt);

		super.propertyChange(evt);
		if (evt.getPropertyName().equals(OutlineLayoutManagerSpecification.OUTLINE_LOCATION_TYPE_KEY)) {
			invalidate();
			doLayout(true);
			// getContainerNode().notifyNodeLayoutDecorationChanged(this);
		}
	}
}
```

4. Reference this new layout manager in DIANA layout engine

Declare the specification in the imports of FGELayoutManagerSpecification interface:

```
@ModelEntity(isAbstract = true)
@Imports({ @Import(OutlineLayoutManagerSpecification.class), @Import(...),... })
public interface FGELayoutManagerSpecification<LM extends FGELayoutManager<?, ?>> extends FGEObject, Bindable, KeyValueCoding {
...
}
```

Add an entry to the LayoutManagerSpecificationType enumerate (in FGELayoutManagerSpecification.java)

```
OUTLINE {
	@Override
	public Class<OutlineLayoutManagerSpecification> getLayoutManagerSpecificationClass() {
		return OutlineLayoutManagerSpecification.class;
	}

	@Override
	public String getDefaultLayoutManagerName() {
		return "outline";
	}
},
```

  In FGEModelFactoryImpl, add this code:

```
public static void installImplementingClasses(ModelFactory modelFactory) throws ModelDefinitionException {
	...
	modelFactory.setImplementingClassForInterface(OutlineLayoutManagerImpl.class, OutlineLayoutManager.class);
	modelFactory.setImplementingClassForInterface(OutlineLayoutManagerSpecificationImpl.class, OutlineLayoutManagerSpecification.class);
	...
}
```

5. Define graphical user interface

Finally, you might provide any GUI to the user of your new LayoutManager.

To that you just have to provide this simple FIB file OutlineLayoutManager.inspector which is to be put in LayoutInspectors directory, located in diana-core project (src/main/resources)

```
<?xml version="1.0" encoding="UTF-8"?>
<Inspector
	layout="border" titleFont="SansSerif,0,12" darkLevel="0"
	dataClassName="org.openflexo.fge.layout.OutlineLayoutManager" font="SansSerif,0,12"
	name="Inspector">
	<TabPanel name="Tab" index="0" constraints="border(index=0;location=center)">

		<Tab title="Basic" layout="twocols" titleFont="SansSerif,0,11"
			darkLevel="0" index="1" font="SansSerif,0,11" useScrollBar="true"
			horizontalScrollbarPolicy="HORIZONTAL_SCROLLBAR_AS_NEEDED"
			verticalScrollbarPolicy="VERTICAL_SCROLLBAR_AS_NEEDED" name="TextTab">
			<Label label="location" index="16"
				constraints="twocols(expandHorizontally=false;expandVertically=false;location=left)"
				name="VerticalAlignmentLabel" localize="true" />
			<DropDown index="17" data="data.layoutManagerSpecification.outlineLocationType"
				constraints="twocols(expandHorizontally=false;expandVertically=false;location=right)"
				width="150" name="OutlineLocationType" format="object.name"
				localize="true"
				iteratorClassName="org.openflexo.fge.layout.OutlineLayoutManagerSpecification$OutlineLocationType"
				autoSelectFirstRow="true" />
		</Tab>

	</TabPanel>
	<LocalizedDictionary>
	</LocalizedDictionary>
	<Parameter name="title" value="OutlineLayoutManager" />
</Inspector>
```

That's all folks !

Run any Openflexo’s Diana-powered application (such as DiagramEditor, see LaunchDiagramEditor.java). It should works !

