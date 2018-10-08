/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
 * 
 * This file is part of Diana-geomedit, a component of the software infrastructure 
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

package org.openflexo.diana.geomedit;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.openflexo.diana.DianaCoreUtils;
import org.openflexo.diana.geomedit.model.GeometricConstructionFactory;
import org.openflexo.diana.geomedit.model.GeometricDiagram;
import org.openflexo.diana.geomedit.view.GeometricDiagramView;
import org.openflexo.diana.swing.control.SwingToolFactory;
import org.openflexo.diana.swing.control.tools.JDianaDialogInspectors;
import org.openflexo.diana.swing.control.tools.JDianaLayoutWidget;
import org.openflexo.diana.swing.control.tools.JDianaPalette;
import org.openflexo.diana.swing.control.tools.JDianaScaleSelector;
import org.openflexo.diana.swing.control.tools.JDianaStyles;
import org.openflexo.diana.swing.control.tools.JDianaToolSelector;
import org.openflexo.gina.ApplicationFIBLibrary.ApplicationFIBLibraryImpl;
import org.openflexo.gina.swing.utils.localization.LocalizedEditor;
import org.openflexo.localization.LocalizedDelegate;
import org.openflexo.localization.LocalizedDelegateImpl;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.rm.FileSystemResourceLocatorImpl;
import org.openflexo.rm.ResourceLocator;
import org.openflexo.swing.ComponentBoundSaver;
import org.openflexo.swing.FlexoFileChooser;
import org.openflexo.toolbox.HasPropertyChangeSupport;
import org.openflexo.toolbox.PropertyChangeListenerRegistrationManager;
import org.openflexo.toolbox.ToolBox;

/**
 * Represents the GeomEdit application
 * 
 * One instance of this application is generally launched, allowing to edit one or more GeomEditEditor
 * 
 * @author sylvain
 * 
 */
public class GeomEditApplication {

	private static final Logger logger = FlexoLogger.getLogger(GeomEditApplication.class.getPackage().getName());

	public static LocalizedDelegate GEOMEDIT_LOCALIZATION = new LocalizedDelegateImpl(
			ResourceLocator.locateResource("FlexoLocalization/GeomEdit"), DianaCoreUtils.DIANA_LOCALIZATION, true, true);

	public static final int META_MASK = ToolBox.isMacOS() ? InputEvent.META_MASK : InputEvent.CTRL_MASK;

	public static final int DELETE_KEY_CODE = ToolBox.isMacOS() ? KeyEvent.VK_BACK_SPACE : KeyEvent.VK_DELETE;
	public static final int BACKSPACE_DELETE_KEY_CODE = ToolBox.isMacOS() ? KeyEvent.VK_DELETE : KeyEvent.VK_BACK_SPACE;

	private final JFrame frame;
	private final FlexoFileChooser fileChooser;
	private final SwingToolFactory toolFactory;

	final FileSystemResourceLocatorImpl resourceLocator;

	private GeometricConstructionFactory factory;

	private final Vector<GeomEditEditor> diagramEditors = new Vector<GeomEditEditor>();
	private final JPanel mainPanel;
	private JTabbedPane tabbedPane;

	private GeomEditEditor currentDiagramEditor;

	private final JDianaToolSelector toolSelector;
	private final JDianaScaleSelector scaleSelector;
	private final JDianaLayoutWidget layoutWidget;
	private final JDianaStyles stylesWidget;
	private final JDianaPalette commonPalette;
	private final DiagramEditorPalette commonPaletteModel;
	final JDianaDialogInspectors inspectors;

	protected PropertyChangeListenerRegistrationManager manager;

	private final DiagramEditingContext editingContext;

	LocalizedEditor localizedEditor;

	private GeomEditMenuBar geomEditMenuBar;

	private GeomEditInspectorController constructionInspector;

	public GeomEditApplication() {
		super();

		editingContext = new DiagramEditingContext();

		try {
			factory = new GeometricConstructionFactory(editingContext);
			// System.out.println("factory: " + factory.debug());
			// DianaPamelaInjectionModule injectionModule = new DianaPamelaInjectionModule(factory);
			// injector = Guice.createInjector(injectionModule);

			// factory = new DiagramFactory();
		} catch (ModelDefinitionException e1) {
			e1.printStackTrace();
		}

		frame = new JFrame();
		frame.setBounds(GeomEditPreferences.getFrameBounds());
		new ComponentBoundSaver(frame) {

			@Override
			public void saveBounds(Rectangle bounds) {
				GeomEditPreferences.setFrameBounds(bounds);
			}
		};
		fileChooser = new FlexoFileChooser(frame);
		fileChooser.setFileFilterAsString("*.geom");
		fileChooser.setCurrentDirectory(ResourceLocator.retrieveResourceAsFile(ResourceLocator.locateResource("GeomEditExamples")));

		resourceLocator = new FileSystemResourceLocatorImpl();
		if (GeomEditPreferences.getLastDirectory() != null) {
			resourceLocator.appendToDirectories(GeomEditPreferences.getLastDirectory().getAbsolutePath());
		}
		resourceLocator.appendToDirectories(System.getProperty("user.home"));
		ResourceLocator.appendDelegate(resourceLocator);

		toolFactory = new SwingToolFactory(frame);

		// inspector = new JFIBInspectorController(frame);

		frame.setTitle("GeomEdit");

		mainPanel = new JPanel(new BorderLayout());

		toolSelector = toolFactory.makeDianaToolSelector(null);
		stylesWidget = toolFactory.makeDianaStyles();
		scaleSelector = toolFactory.makeDianaScaleSelector(null);
		layoutWidget = toolFactory.makeDianaLayoutWidget();
		inspectors = toolFactory.makeDianaDialogInspectors();

		inspectors.getForegroundStyleInspector().setLocation(1000, 100);
		inspectors.getTextPropertiesInspector().setLocation(1000, 300);
		inspectors.getShadowStyleInspector().setLocation(1000, 400);
		inspectors.getBackgroundStyleInspector().setLocation(1000, 500);
		inspectors.getShapeInspector().setLocation(1000, 600);
		inspectors.getConnectorInspector().setLocation(1000, 700);
		inspectors.getLocationSizeInspector().setLocation(1000, 50);
		inspectors.getLayoutManagersInspector().setLocation(1000, 300);

		constructionInspector = new GeomEditInspectorController(frame, ResourceLocator.locateResource("Inspectors/Basic"),
				ApplicationFIBLibraryImpl.instance(), GEOMEDIT_LOCALIZATION);
		constructionInspector.setVisible(true);

		/*constructionInspector = new JDialogInspector<GeometricElement>(
				AbstractDianaEditor.EDITOR_FIB_LIBRARY.retrieveFIBComponent(JDianaInspectorsResources.SHAPE_SPECIFICATION_PANEL_FIB_FILE,
						true),
				(getInspectedShapeSpecification() != null ? getInspectedShapeSpecification().getStyleFactory() : null), frame,
				JDianaInspectorsResources.SHAPE_NAME);*/

		JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		topPanel.add(toolSelector.getComponent());
		topPanel.add(stylesWidget.getComponent());
		topPanel.add(layoutWidget.getComponent());
		topPanel.add(scaleSelector.getComponent());

		mainPanel.add(topPanel, BorderLayout.NORTH);

		commonPaletteModel = new DiagramEditorPalette();
		commonPalette = toolFactory.makeDianaPalette(commonPaletteModel);

		/*paletteDialog = new JDialog(frame, "Palette", false);
		paletteDialog.getContentPane().add(commonPalette.getComponent());
		paletteDialog.setLocation(1010, 0);
		paletteDialog.pack();
		paletteDialog.setVisible(true);
		paletteDialog.setFocusableWindowState(false);*/

		manager = new PropertyChangeListenerRegistrationManager();

		geomEditMenuBar = new GeomEditMenuBar(this);

		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				quit();
			}
		});
		frame.setJMenuBar(geomEditMenuBar);

		frame.getContentPane().add(mainPanel);

	}

	public GeomEditInspectorController getConstructionInspector() {
		return constructionInspector;
	}

	public DiagramEditingContext getEditingContext() {
		return editingContext;
	}

	/*private class MyDrawingViewScrollPane extends JScrollPane {
		private final GeomEditDrawingView drawingView;
	
		private MyDrawingViewScrollPane(GeomEditDrawingView v) {
			super(v);
			drawingView = v;
		}
	}*/

	public GeometricConstructionFactory getFactory() {
		return factory;
	}

	public SwingToolFactory getToolFactory() {
		return toolFactory;
	}

	/*public Injector getInjector() {
		return injector;
	}*/

	public void addDiagramEditor(GeomEditEditor diagramEditor) {
		if (tabbedPane == null) {
			tabbedPane = new JTabbedPane();
			tabbedPane.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					GeometricDiagramView c = (GeometricDiagramView) tabbedPane.getSelectedComponent();
					if (c != null) {
						drawingSwitched(c.getDrawing().getModel());
					}
				}
			});
			mainPanel.add(tabbedPane, BorderLayout.CENTER);
			// mainPanel.add(drawing.getEditedDrawing().getPalette().getPaletteView(),BorderLayout.EAST);
		}
		diagramEditors.add(diagramEditor);

		// DianaEditor controller = new DianaEditor(diagramEditor.getEditedDrawing(), diagramEditor.getFactory());
		// AbstractDianaEditor<GeometricDiagramDrawing> controller = new AbstractDianaEditor<GeometricDiagramDrawing>(aDrawing, factory)

		tabbedPane.add(diagramEditor.getTitle(), new GeometricDiagramView(diagramEditor.getController()));
		// diagramEditor.getController().getToolbox().getForegroundInspector().setVisible(true);
		switchToDiagramEditor(diagramEditor);

		/*frame.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent event) 
			{
				if (event.getKeyCode() == KeyEvent.VK_UP) {
					drawing.getEditedDrawing().getController().upKeyPressed();
					event.consume();
					return;
				}
				else if (event.getKeyCode() == KeyEvent.VK_DOWN) {
					drawing.getEditedDrawing().getController().downKeyPressed();
					event.consume();
					return;
				}
				else if (event.getKeyCode() == KeyEvent.VK_RIGHT) {
					drawing.getEditedDrawing().getController().rightKeyPressed();
					event.consume();
					return;
				}
				else if (event.getKeyCode() == KeyEvent.VK_LEFT) {
					drawing.getEditedDrawing().getController().leftKeyPressed();
					event.consume();
					return;
				}
				super.keyPressed(event);
			}
		});*/
	}

	private void removeDiagramEditor(GeomEditEditor diagramEditor) {

	}

	public void switchToDiagramEditor(GeomEditEditor diagramEditor) {
		tabbedPane.setSelectedIndex(diagramEditors.indexOf(diagramEditor));
	}

	private void drawingSwitched(GeometricDiagram diagram) {
		for (GeomEditEditor editor : diagramEditors) {
			if (editor.getDiagram() == diagram) {
				drawingSwitched(editor);
				return;
			}
		}
	}

	private void drawingSwitched(GeomEditEditor diagramEditor) {

		logger.info("Switch to editor " + diagramEditor);

		if (currentDiagramEditor != null) {
			// mainPanel.remove(currentDiagramEditor.getController().getScalePanel());
			// currentDiagramEditor.getController().deleteObserver(inspector);
			currentDiagramEditor.getController().getPropertyChangeSupport().removePropertyChangeListener(constructionInspector);
		}
		currentDiagramEditor = diagramEditor;
		toolSelector.attachToEditor(diagramEditor.getController());
		stylesWidget.attachToEditor(diagramEditor.getController());
		scaleSelector.attachToEditor(diagramEditor.getController());
		layoutWidget.attachToEditor(diagramEditor.getController());
		commonPaletteModel.setEditor(diagramEditor.getController());
		commonPalette.attachToEditor(diagramEditor.getController());
		inspectors.attachToEditor(diagramEditor.getController());

		diagramEditor.getController().getPropertyChangeSupport().addPropertyChangeListener(constructionInspector);

		/*JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		topPanel.add(currentDiagramEditor.getController().getToolbox().getStyleToolBar());
		topPanel.add(currentDiagramEditor.getController().getScalePanel());
		
		mainPanel.add(topPanel, BorderLayout.NORTH);*/

		geomEditMenuBar.copyItem.synchronizeWith(diagramEditor.getController());
		geomEditMenuBar.cutItem.synchronizeWith(diagramEditor.getController());
		geomEditMenuBar.pasteItem.synchronizeWith(diagramEditor.getController());

		geomEditMenuBar.undoItem.synchronizeWith(diagramEditor.getController().getFactory().getUndoManager());
		geomEditMenuBar.redoItem.synchronizeWith(diagramEditor.getController().getFactory().getUndoManager());

		// currentDiagramEditor.getController().addObserver(inspector);
		updateFrameTitle();
		mainPanel.revalidate();
		mainPanel.repaint();
	}

	private void updateFrameTitle() {
		frame.setTitle("Basic drawing editor - " + currentDiagramEditor.getTitle());
	}

	private void updateTabTitle() {
		tabbedPane.setTitleAt(diagramEditors.indexOf(currentDiagramEditor), currentDiagramEditor.getTitle());
	}

	public void showMainPanel() {

		frame.validate();
		frame.pack();
		frame.setVisible(true);

	}

	public JPanel getMainPanel() {
		return mainPanel;
	}

	public void quit() {
		frame.dispose();
		System.exit(0);
	}

	public void closeDrawing() {
		if (currentDiagramEditor == null) {
			return;
		}
		if (currentDiagramEditor.getDiagram().hasChanged()) {
			int result = JOptionPane.showOptionDialog(frame, "Would you like to save drawing changes?", "Save changes",
					JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, JOptionPane.YES_OPTION);
			switch (result) {
				case JOptionPane.YES_OPTION:
					if (!currentDiagramEditor.save()) {
						return;
					}
					break;
				case JOptionPane.NO_OPTION:
					break;
				default:
					return;
			}
		}
		diagramEditors.remove(currentDiagramEditor);
		tabbedPane.remove(tabbedPane.getSelectedIndex());
		if (diagramEditors.size() == 0) {
			newDiagramEditor();
		}
	}

	public void newDiagramEditor() {
		GeomEditEditor newDiagramEditor = GeomEditEditor.newDiagramEditor(factory, this);
		addDiagramEditor(newDiagramEditor);
	}

	public void loadDiagramEditor() {
		if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			loadDiagramEditor(file);
		}
	}

	public void loadDiagramEditor(File file) {
		GeomEditEditor loadedDiagramEditor = GeomEditEditor.loadDiagramEditor(file, factory, this);
		if (loadedDiagramEditor != null) {
			addDiagramEditor(loadedDiagramEditor);
		}
		GeomEditPreferences.setLastFile(file);
	}

	public boolean saveDiagramEditor() {
		if (currentDiagramEditor == null) {
			return false;
		}
		if (currentDiagramEditor.getFile() == null) {
			return saveDrawingAs();
		}
		else {
			return currentDiagramEditor.save();
		}
	}

	public boolean saveDrawingAs() {
		if (currentDiagramEditor == null) {
			return false;
		}
		if (fileChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			if (!file.getName().endsWith(".geom")) {
				file = new File(file.getParentFile(), file.getName() + ".geom");
			}
			currentDiagramEditor.setFile(file);
			updateFrameTitle();
			updateTabTitle();
			return currentDiagramEditor.save();
		}
		else {
			return false;
		}
	}

	private JMenuItem makeJMenuItem(String actionName, Icon icon, KeyStroke accelerator, AbstractAction action) {

		JMenuItem returned = new JMenuItem(GEOMEDIT_LOCALIZATION.localizedForKey(actionName));
		returned.addActionListener(action);
		returned.setIcon(icon);
		returned.setAccelerator(accelerator);
		frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(accelerator, actionName);
		frame.getRootPane().getActionMap().put(actionName, action);
		return returned;
	}

	SynchronizedMenuItem makeSynchronizedMenuItem(String actionName, Icon icon, KeyStroke accelerator, AbstractAction action,
			Synchronizer synchronizer) {

		String localizedName = GEOMEDIT_LOCALIZATION.localizedForKey(actionName);
		SynchronizedMenuItem returned = new SynchronizedMenuItem(localizedName, synchronizer);
		action.putValue(Action.NAME, localizedName);
		returned.setAction(action);
		returned.setIcon(icon);
		returned.setAccelerator(accelerator);
		frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(accelerator, actionName);
		frame.getRootPane().getActionMap().put(actionName, action);
		returned.setEnabled(false);
		return returned;
	}

	public interface Synchronizer {
		public void synchronize(HasPropertyChangeSupport observable, SynchronizedMenuItem menuItem);
	}

	public class SynchronizedMenuItem extends JMenuItem implements PropertyChangeListener {

		private HasPropertyChangeSupport observable;
		private final Synchronizer synchronizer;

		public SynchronizedMenuItem(String menuName, Synchronizer synchronizer) {
			super(menuName);
			this.synchronizer = synchronizer;
		}

		public void synchronizeWith(HasPropertyChangeSupport anObservable) {
			if (this.observable != null) {
				manager.removeListener(this, this.observable);
			}
			manager.addListener(this, anObservable);
			observable = anObservable;
			synchronizer.synchronize(observable, this);
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			synchronizer.synchronize(observable, this);
		}

		@Override
		public void setEnabled(boolean b) {
			super.setEnabled(b);
			getAction().setEnabled(b);
		}

	}

	public class WindowMenuItem extends JCheckBoxMenuItem implements WindowListener {

		private final Window window;

		public WindowMenuItem(String menuName, Window aWindow) {
			super(menuName);
			this.window = aWindow;
			addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					window.setVisible(!window.isVisible());
				}
			});
			aWindow.addWindowListener(this);
		}

		@Override
		public void windowOpened(WindowEvent e) {
			setState(window.isVisible());
		}

		@Override
		public void windowIconified(WindowEvent e) {
		}

		@Override
		public void windowDeiconified(WindowEvent e) {
		}

		@Override
		public void windowDeactivated(WindowEvent e) {
			setState(window.isVisible());
		}

		@Override
		public void windowClosing(WindowEvent e) {
			setState(window.isVisible());
		}

		@Override
		public void windowClosed(WindowEvent e) {
			setState(window.isVisible());
		}

		@Override
		public void windowActivated(WindowEvent e) {
			setState(window.isVisible());
		}

	}

	public void setExitOnClose() {
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public JFrame getFrame() {
		return frame;
	}

	public GeomEditEditor getCurrentDiagramEditor() {
		return currentDiagramEditor;
	}

}
