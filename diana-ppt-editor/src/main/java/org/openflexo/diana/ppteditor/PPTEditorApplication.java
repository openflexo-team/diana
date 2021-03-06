/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
 * 
 * This file is part of Diana-ppt-editor, a component of the software infrastructure 
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

package org.openflexo.diana.ppteditor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.openflexo.diana.DianaCoreUtils;
import org.openflexo.diana.control.DianaInteractiveViewer;
import org.openflexo.diana.swing.control.SwingToolFactory;
import org.openflexo.diana.swing.control.tools.JDianaDialogInspectors;
import org.openflexo.diana.swing.control.tools.JDianaLayoutWidget;
import org.openflexo.diana.swing.control.tools.JDianaPalette;
import org.openflexo.diana.swing.control.tools.JDianaScaleSelector;
import org.openflexo.diana.swing.control.tools.JDianaStyles;
import org.openflexo.diana.swing.control.tools.JDianaToolSelector;
import org.openflexo.exceptions.CopyException;
import org.openflexo.exceptions.CutException;
import org.openflexo.exceptions.PasteException;
import org.openflexo.gina.ApplicationFIBLibrary.ApplicationFIBLibraryImpl;
import org.openflexo.gina.swing.utils.localization.LocalizedEditor;
import org.openflexo.gina.swing.utils.logging.FlexoLoggingViewer;
import org.openflexo.icon.ImageIconResource;
import org.openflexo.localization.LocalizedDelegate;
import org.openflexo.localization.LocalizedDelegateImpl;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.logging.FlexoLoggingManager;
import org.openflexo.rm.FileSystemResourceLocatorImpl;
import org.openflexo.rm.ResourceLocator;
import org.openflexo.swing.FlexoFileChooser;
import org.openflexo.toolbox.HasPropertyChangeSupport;
import org.openflexo.toolbox.PropertyChangeListenerRegistrationManager;
import org.openflexo.toolbox.ToolBox;

/**
 * Represents the SlideShowEditor application
 * 
 * @author sylvain
 * 
 */
public class PPTEditorApplication {

	private static final Logger logger = FlexoLogger.getLogger(PPTEditorApplication.class.getPackage().getName());

	public static LocalizedDelegate PPT_EDITOR_LOCALIZATION = new LocalizedDelegateImpl(
			ResourceLocator.locateResource("FlexoLocalization/PPTEditor"), DianaCoreUtils.DIANA_LOCALIZATION, true, true);

	private static final int META_MASK = ToolBox.isMacOS() ? InputEvent.META_MASK : InputEvent.CTRL_MASK;

	private final JFrame frame;
	private JDialog paletteDialog;
	private final FlexoFileChooser fileChooser;
	private final SwingToolFactory toolFactory;

	// private JFIBInspectorController inspector;

	// private Vector<SlideShowEditor> pptEditors = new Vector<SlideShowEditor>();
	private final JPanel mainPanel;
	private JTabbedPane tabbedPane;

	private SlideEditor currentSlideEditor;
	private SlideShowEditor currentSlideShowEditor;
	private final List<SlideShowEditor> pptEditors = new ArrayList<>();

	private final JDianaToolSelector toolSelector;
	private final JDianaScaleSelector scaleSelector;
	private final JDianaLayoutWidget layoutWidget;
	private final JDianaStyles stylesWidget;
	private JDianaPalette commonPalette;
	// private DiagramEditorPalette commonPaletteModel;
	private final JDianaDialogInspectors inspectors;

	protected PropertyChangeListenerRegistrationManager manager;

	private final SynchronizedMenuItem copyItem;
	private final SynchronizedMenuItem cutItem;
	private final SynchronizedMenuItem pasteItem;
	private SynchronizedMenuItem undoItem;
	private SynchronizedMenuItem redoItem;

	private LocalizedEditor localizedEditor;

	@SuppressWarnings("serial")
	public PPTEditorApplication() {
		super();

		final FileSystemResourceLocatorImpl fsrl = new FileSystemResourceLocatorImpl();
		fsrl.appendToDirectories(System.getProperty("user.dir"));
		ResourceLocator.appendDelegate(fsrl);

		frame = new JFrame();
		frame.setPreferredSize(new Dimension(1100, 800));
		fileChooser = new FlexoFileChooser(frame);
		fileChooser.setFileFilterAsString("*.ppt,*.pptx");
		fileChooser.setCurrentDirectory(ResourceLocator.retrieveResourceAsFile(ResourceLocator.locateResource("ppt")));

		toolFactory = new SwingToolFactory(frame);

		// inspector = new JFIBInspectorController(frame);

		frame.setTitle("Powerpoint editor");

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

		JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		topPanel.add(toolSelector.getComponent());
		topPanel.add(stylesWidget.getComponent());
		topPanel.add(layoutWidget.getComponent());
		topPanel.add(scaleSelector.getComponent());

		mainPanel.add(topPanel, BorderLayout.NORTH);

		// commonPaletteModel = new DiagramEditorPalette();
		// commonPalette = toolFactory.makeDianaPalette(commonPaletteModel);

		/*paletteDialog = new JDialog(frame, "Palette", false);
		paletteDialog.getContentPane().add(commonPalette.getComponent());
		paletteDialog.setLocation(1010, 0);
		paletteDialog.pack();
		paletteDialog.setVisible(true);
		paletteDialog.setFocusableWindowState(false);*/

		manager = new PropertyChangeListenerRegistrationManager();

		JMenuBar mb = new JMenuBar();
		JMenu fileMenu = new JMenu(PPT_EDITOR_LOCALIZATION.localizedForKey("file"));
		JMenu editMenu = new JMenu(PPT_EDITOR_LOCALIZATION.localizedForKey("edit"));
		JMenu viewMenu = new JMenu(PPT_EDITOR_LOCALIZATION.localizedForKey("view"));
		JMenu toolsMenu = new JMenu(PPT_EDITOR_LOCALIZATION.localizedForKey("tools"));
		JMenu helpMenu = new JMenu(PPT_EDITOR_LOCALIZATION.localizedForKey("help"));

		JMenuItem newItem = makeJMenuItem("new_slideshow", NEW_ICON, KeyStroke.getKeyStroke(KeyEvent.VK_N, META_MASK),
				new AbstractAction() {
					@Override
					public void actionPerformed(ActionEvent e) {
						newDiagramEditor();
					}
				});

		JMenuItem loadItem = makeJMenuItem("open_slideshow", OPEN_ICON, KeyStroke.getKeyStroke(KeyEvent.VK_O, META_MASK),
				new AbstractAction() {
					@Override
					public void actionPerformed(ActionEvent e) {
						loadDiagramEditor();
					}
				});

		JMenuItem saveItem = makeJMenuItem("save_slideshow", SAVE_ICON, KeyStroke.getKeyStroke(KeyEvent.VK_S, META_MASK),
				new AbstractAction() {
					@Override
					public void actionPerformed(ActionEvent e) {
						saveDiagramEditor();
					}
				});

		JMenuItem saveAsItem = makeJMenuItem("save_slideshow_as", SAVE_AS_ICON, null, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveDrawingAs();
			}
		});

		JMenuItem closeItem = makeJMenuItem("close", null, null, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				closeDrawing();
			}
		});

		JMenuItem quitItem = makeJMenuItem("quit", null, KeyStroke.getKeyStroke(KeyEvent.VK_Q, META_MASK), new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				quit();
			}
		});

		fileMenu.add(newItem);
		fileMenu.add(loadItem);
		fileMenu.add(saveItem);
		fileMenu.add(saveAsItem);
		fileMenu.add(closeItem);
		fileMenu.addSeparator();
		fileMenu.add(quitItem);

		copyItem = makeSynchronizedMenuItem("copy", COPY_ICON, KeyStroke.getKeyStroke(KeyEvent.VK_C, META_MASK), new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("copy");
				try {
					currentSlideEditor.copy();
				} catch (CopyException e1) {
					e1.printStackTrace();
				}
			}
		}, new Synchronizer() {
			@Override
			public void synchronize(HasPropertyChangeSupport observable, SynchronizedMenuItem menuItem) {
				if (observable instanceof SlideEditor) {
					menuItem.setEnabled(((SlideEditor) observable).isCopiable());
				}
			}
		});

		cutItem = makeSynchronizedMenuItem("cut", CUT_ICON, KeyStroke.getKeyStroke(KeyEvent.VK_X, META_MASK), new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("cut");
				try {
					currentSlideEditor.cut();
				} catch (CutException e1) {
					e1.printStackTrace();
				}
			}
		}, new Synchronizer() {
			@Override
			public void synchronize(HasPropertyChangeSupport observable, SynchronizedMenuItem menuItem) {
				if (observable instanceof SlideEditor) {
					menuItem.setEnabled(((SlideEditor) observable).isCutable());
				}
			}
		});

		pasteItem = makeSynchronizedMenuItem("paste", PASTE_ICON, KeyStroke.getKeyStroke(KeyEvent.VK_V, META_MASK), new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("paste");
				if (currentSlideEditor != null) {
					try {
						currentSlideEditor.paste();
					} catch (PasteException e1) {
						e1.printStackTrace();
					}
				}
			}
		}, new Synchronizer() {
			@Override
			public void synchronize(HasPropertyChangeSupport observable, SynchronizedMenuItem menuItem) {
				if (observable instanceof DianaInteractiveViewer) {
					menuItem.setEnabled(currentSlideEditor.isPastable());
				}
			}
		});

		/*undoItem = makeSynchronizedMenuItem("undo", UNDO_ICON, KeyStroke.getKeyStroke(KeyEvent.VK_Z, META_MASK), new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("undo");
				currentDiagramEditor.getController().undo();
			}
		}, new Synchronizer() {
			@Override
			public void synchronize(HasPropertyChangeSupport observable, SynchronizedMenuItem menuItem) {
				if (observable instanceof UndoManager) {
					menuItem.setEnabled(currentDiagramEditor.getController().canUndo());
					if (currentDiagramEditor.getController().canUndo()) {
						menuItem.setText(currentDiagramEditor.getController().getFactory().getUndoManager().getUndoPresentationName());
					} else {
						menuItem.setText(FlexoLocalization.localizedForKey(LOCALIZATION, "undo"));
					}
				}
			}
		});
		
		redoItem = makeSynchronizedMenuItem("redo", REDO_ICON, KeyStroke.getKeyStroke(KeyEvent.VK_R, META_MASK), new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("redo");
				currentDiagramEditor.getController().redo();
			}
		}, new Synchronizer() {
			@Override
			public void synchronize(HasPropertyChangeSupport observable, SynchronizedMenuItem menuItem) {
				if (observable instanceof UndoManager) {
					menuItem.setEnabled(currentDiagramEditor.getController().canRedo());
					if (currentDiagramEditor.getController().canRedo()) {
						menuItem.setText(currentDiagramEditor.getController().getFactory().getUndoManager().getRedoPresentationName());
					} else {
						menuItem.setText(FlexoLocalization.localizedForKey(LOCALIZATION, "redo"));
					}
				}
			}
		});*/

		editMenu.add(copyItem);
		editMenu.add(cutItem);
		editMenu.add(pasteItem);
		// editMenu.addSeparator();
		// editMenu.add(undoItem);
		// editMenu.add(redoItem);

		WindowMenuItem foregroundInspectorItem = new WindowMenuItem(PPT_EDITOR_LOCALIZATION.localizedForKey("foreground_inspector"),
				inspectors.getForegroundStyleInspector());
		WindowMenuItem backgroundInspectorItem = new WindowMenuItem(PPT_EDITOR_LOCALIZATION.localizedForKey("background_inspector"),
				inspectors.getBackgroundStyleInspector());
		WindowMenuItem textInspectorItem = new WindowMenuItem(PPT_EDITOR_LOCALIZATION.localizedForKey("text_inspector"),
				inspectors.getTextPropertiesInspector());
		WindowMenuItem shapeInspectorItem = new WindowMenuItem(PPT_EDITOR_LOCALIZATION.localizedForKey("shape_inspector"),
				inspectors.getShapeInspector());
		WindowMenuItem connectorInspectorItem = new WindowMenuItem(PPT_EDITOR_LOCALIZATION.localizedForKey("connector_inspector"),
				inspectors.getConnectorInspector());
		WindowMenuItem shadowInspectorItem = new WindowMenuItem(PPT_EDITOR_LOCALIZATION.localizedForKey("shadow_inspector"),
				inspectors.getShadowStyleInspector());
		WindowMenuItem locationSizeInspectorItem = new WindowMenuItem(PPT_EDITOR_LOCALIZATION.localizedForKey("location_size_inspector"),
				inspectors.getLocationSizeInspector());

		// WindowMenuItem paletteItem = new WindowMenuItem(FlexoLocalization.localizedForKey(LOCALIZATION, "palette"), paletteDialog);

		viewMenu.add(foregroundInspectorItem);
		viewMenu.add(backgroundInspectorItem);
		viewMenu.add(textInspectorItem);
		viewMenu.add(shapeInspectorItem);
		viewMenu.add(connectorInspectorItem);
		viewMenu.add(shadowInspectorItem);
		viewMenu.add(locationSizeInspectorItem);
		viewMenu.addSeparator();
		// viewMenu.add(paletteItem);

		JMenuItem logsItem = new JMenuItem(PPT_EDITOR_LOCALIZATION.localizedForKey("logs"));
		logsItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				FlexoLoggingViewer.showLoggingViewer(FlexoLoggingManager.instance(), ApplicationFIBLibraryImpl.instance(), frame);
			}
		});

		JMenuItem localizedItem = new JMenuItem(PPT_EDITOR_LOCALIZATION.localizedForKey("localized_editor"));
		localizedItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (localizedEditor == null) {
					localizedEditor = new LocalizedEditor(frame, "localized_editor", PPT_EDITOR_LOCALIZATION, null, true, false);
				}
				localizedEditor.setVisible(true);
			}
		});

		toolsMenu.add(logsItem);
		toolsMenu.add(localizedItem);

		mb.add(fileMenu);
		mb.add(editMenu);
		mb.add(viewMenu);
		mb.add(toolsMenu);
		mb.add(helpMenu);

		frame.setJMenuBar(mb);

		frame.getContentPane().add(mainPanel);
		frame.validate();
		frame.pack();

	}

	public SwingToolFactory getToolFactory() {
		return toolFactory;
	}

	private void addSlideShowEditor(SlideShowEditor slideShowEditor) {
		if (tabbedPane == null) {
			tabbedPane = new JTabbedPane();
			tabbedPane.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					/*SlideShowEditor c = (SlideShowEditor) tabbedPane.getSelectedComponent();
					if (c != null) {
						switchToSlideShowEditor(c);
						logger.info("tabbedPane switched to " + c);
					}*/
				}
			});
			mainPanel.add(tabbedPane, BorderLayout.CENTER);
			// mainPanel.add(drawing.getEditedDrawing().getPalette().getPaletteView(),BorderLayout.EAST);
		}

		// DianaEditor controller = new DianaEditor(diagramEditor.getEditedDrawing(), diagramEditor.getFactory());
		// AbstractDianaEditor<SlideDrawing> controller = new AbstractDianaEditor<SlideDrawing>(aDrawing, factory)

		pptEditors.add(slideShowEditor);
		tabbedPane.add(slideShowEditor.getTitle(), slideShowEditor);
		// diagramEditor.getController().getToolbox().getForegroundInspector().setVisible(true);
		// switchToDiagramEditor(slideShowEditor);

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

		switchToSlideShowEditor(slideShowEditor);

	}

	// FD : unused
	// private void removeDiagramEditor(SlideShowEditor diagramEditor) {

	// }

	public void switchToSlideShowEditor(SlideShowEditor slideShowEditor) {
		currentSlideShowEditor = slideShowEditor;
		if (slideShowEditor != null) {
			System.out.println("Switch to " + slideShowEditor.getFile());
			tabbedPane.setSelectedIndex(pptEditors.indexOf(slideShowEditor));
			slideSwitched(slideShowEditor.getEditor(slideShowEditor.getCurrentSlide()));
		}
	}

	protected void slideSwitched(SlideEditor slideEditor) {

		logger.info("Switch to editor " + slideEditor);

		/*if (currentDiagramEditor != null) {
			// mainPanel.remove(currentDiagramEditor.getController().getScalePanel());
			currentDiagramEditor.getController().deleteObserver(inspector);
		}*/
		currentSlideEditor = slideEditor;

		if (slideEditor != null) {
			toolSelector.attachToEditor(slideEditor);
			stylesWidget.attachToEditor(slideEditor);
			scaleSelector.attachToEditor(slideEditor);
			layoutWidget.attachToEditor(slideEditor);
			// commonPaletteModel.setEditor(diagramEditor.getController());
			// commonPalette.attachToEditor(slideEditor);
			inspectors.attachToEditor(slideEditor);
		}

		if (slideEditor != null) {
			copyItem.synchronizeWith(slideEditor);
			cutItem.synchronizeWith(slideEditor);
			pasteItem.synchronizeWith(slideEditor);
		}

		// undoItem.synchronizeWith(diagramEditor.getController().getFactory().getUndoManager());
		// redoItem.synchronizeWith(diagramEditor.getController().getFactory().getUndoManager());

		// currentDiagramEditor.getController().addObserver(inspector);
		updateFrameTitle();
		mainPanel.revalidate();
		mainPanel.repaint();
	}

	private void updateFrameTitle() {
		// frame.setTitle("Basic drawing editor - " + currentSlideEditor.getTitle());
	}

	private void updateTabTitle() {
		// tabbedPane.setTitleAt(pptEditors.indexOf(currentSlideEditor), currentSlideEditor.getTitle());
	}

	public void showMainPanel() {

		frame.setVisible(true);

	}

	public void quit() {
		frame.dispose();
		System.exit(0);
	}

	public void closeDrawing() {
		if (currentSlideEditor == null) {
			return;
		}
		if (currentSlideEditor.getSlide() != null) {
			int result = JOptionPane.showOptionDialog(frame, "Would you like to save ppt changes?", "Save changes",
					JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, JOptionPane.YES_OPTION);
			switch (result) {
				case JOptionPane.YES_OPTION:
					// currentSlideEditor.getSlide().getSlideShow().write();
					break;
				case JOptionPane.NO_OPTION:
					break;
				default:
					return;
			}
		}
		pptEditors.remove(currentSlideEditor);
		tabbedPane.remove(tabbedPane.getSelectedIndex());
		if (pptEditors.size() == 0) {
			newDiagramEditor();
		}
	}

	public void newDiagramEditor() {
		// SlideShowEditor newDiagramEditor = SlideShowEditor.newDiagramEditor(factory, this);
		// addDiagramEditor(newDiagramEditor);
	}

	public void loadDiagramEditor() {
		if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			loadDiagramEditor(file);
		}
	}

	public void loadDiagramEditor(File file) {
		SlideShowEditor loadedDiagramEditor = SlideShowEditor.loadSlideShowEditor(file, this);
		if (loadedDiagramEditor != null) {
			addSlideShowEditor(loadedDiagramEditor);
		}
	}

	public boolean saveDiagramEditor() {
		if (currentSlideShowEditor == null) {
			return false;
		}
		if (currentSlideShowEditor.getFile() == null) {
			return saveDrawingAs();
		}
		else {
			return currentSlideShowEditor.save();
		}
	}

	public boolean saveDrawingAs() {
		if (currentSlideEditor == null) {
			return false;
		}
		if (fileChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			if (!file.getName().endsWith(".drw")) {
				file = new File(file.getParentFile(), file.getName() + ".drw");
			}
			currentSlideShowEditor.setFile(file);
			updateFrameTitle();
			updateTabTitle();
			return currentSlideShowEditor.save();
		}
		else {
			return false;
		}
	}

	private JMenuItem makeJMenuItem(String actionName, Icon icon, KeyStroke accelerator, AbstractAction action) {

		JMenuItem returned = new JMenuItem(PPT_EDITOR_LOCALIZATION.localizedForKey(actionName));
		returned.addActionListener(action);
		returned.setIcon(icon);
		returned.setAccelerator(accelerator);
		frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(accelerator, actionName);
		frame.getRootPane().getActionMap().put(actionName, action);
		return returned;
	}

	private SynchronizedMenuItem makeSynchronizedMenuItem(String actionName, Icon icon, KeyStroke accelerator, AbstractAction action,
			Synchronizer synchronizer) {

		String localizedName = PPT_EDITOR_LOCALIZATION.localizedForKey(actionName);
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

	@SuppressWarnings("serial")
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

	@SuppressWarnings("serial")
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

	// Actions icons
	public static final ImageIcon UNDO_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/Undo.png"));
	public static final ImageIcon REDO_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/Redo.png"));
	public static final ImageIcon COPY_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/Copy.png"));
	public static final ImageIcon PASTE_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/Paste.png"));
	public static final ImageIcon CUT_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/Cut.png"));
	public static final ImageIcon DELETE_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/Delete.png"));
	public static final ImageIcon HELP_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/Help.png"));
	public static final ImageIcon IMPORT_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/Import.png"));
	public static final ImageIcon EXPORT_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/Export.png"));
	public static final ImageIcon OPEN_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/Open.png"));
	public static final ImageIcon NEW_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/New.png"));
	public static final ImageIcon PRINT_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/Print.png"));
	public static final ImageIcon SAVE_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/Save.png"));
	public static final ImageIcon SAVE_DISABLED_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/Save-disabled.png"));
	public static final ImageIcon SAVE_AS_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/Save-as.png"));
	public static final ImageIcon SAVE_ALL_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/Save-all.png"));
	public static final ImageIcon NETWORK_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/Network.png"));
	public static final ImageIcon INFO_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/Info.png"));
	public static final ImageIcon INSPECT_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/Inspect.png"));
	public static final ImageIcon REFRESH_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/Refresh.png"));
	public static final ImageIcon REFRESH_DISABLED_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/Refresh-disabled.png"));

}
