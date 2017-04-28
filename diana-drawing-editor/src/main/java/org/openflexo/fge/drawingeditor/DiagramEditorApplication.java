/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
 * 
 * This file is part of Diana-drawing-editor, a component of the software infrastructure 
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

package org.openflexo.fge.drawingeditor;

import java.awt.*;
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
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import org.openflexo.exceptions.CopyException;
import org.openflexo.exceptions.CutException;
import org.openflexo.exceptions.PasteException;
import org.openflexo.fge.FGECoreUtils;
import org.openflexo.fge.control.DianaInteractiveViewer;
import org.openflexo.fge.drawingeditor.model.Diagram;
import org.openflexo.fge.drawingeditor.model.DiagramFactory;
import org.openflexo.fge.swing.control.SwingToolFactory;
import org.openflexo.fge.swing.control.tools.JDianaDialogInspectors;
import org.openflexo.fge.swing.control.tools.JDianaLayoutWidget;
import org.openflexo.fge.swing.control.tools.JDianaPalette;
import org.openflexo.fge.swing.control.tools.JDianaScaleSelector;
import org.openflexo.fge.swing.control.tools.JDianaStyles;
import org.openflexo.fge.swing.control.tools.JDianaToolSelector;
import org.openflexo.gina.ApplicationFIBLibrary.ApplicationFIBLibraryImpl;
import org.openflexo.gina.swing.utils.localization.LocalizedEditor;
import org.openflexo.gina.swing.utils.logging.FlexoLoggingViewer;
import org.openflexo.icon.ImageIconResource;
import org.openflexo.localization.LocalizedDelegate;
import org.openflexo.localization.LocalizedDelegateImpl;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.logging.FlexoLoggingManager;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.undo.UndoManager;
import org.openflexo.rm.FileSystemResourceLocatorImpl;
import org.openflexo.rm.ResourceLocator;
import org.openflexo.swing.ComponentBoundSaver;
import org.openflexo.swing.FlexoFileChooser;
import org.openflexo.toolbox.HasPropertyChangeSupport;
import org.openflexo.toolbox.PropertyChangeListenerRegistrationManager;
import org.openflexo.toolbox.ToolBox;

/**
 * Represents the DiagramEditor application
 * 
 * @author sylvain
 * 
 */
public class DiagramEditorApplication {

	private static final Logger logger = FlexoLogger.getLogger(DiagramEditorApplication.class.getPackage().getName());

	public static LocalizedDelegate DIAGRAM_EDITOR_LOCALIZATION = new LocalizedDelegateImpl(
			ResourceLocator.locateResource("FlexoLocalization/DrawingEditor"), FGECoreUtils.DIANA_LOCALIZATION, true, true);

	private static final int META_MASK = ToolBox.getPLATFORM() == ToolBox.MACOS ? InputEvent.META_MASK : InputEvent.CTRL_MASK;

	private final JFrame frame;
	private final JDialog paletteDialog;
	private final FlexoFileChooser fileChooser;
	private final SwingToolFactory toolFactory;

	// private JFIBInspectorController inspector;

	private DiagramFactory factory;

	private final Vector<DiagramEditor> diagramEditors = new Vector<DiagramEditor>();
	private final JPanel mainPanel;
	private JTabbedPane tabbedPane;

	private DiagramEditor currentDiagramEditor;

	private final JDianaToolSelector toolSelector;
	private final JDianaScaleSelector scaleSelector;
	private final JDianaLayoutWidget layoutWidget;
	private final JDianaStyles stylesWidget;
	private final JDianaPalette commonPalette;
	private final DiagramEditorPalette commonPaletteModel;
	private final JDianaDialogInspectors inspectors;

	protected PropertyChangeListenerRegistrationManager manager;

	private final DiagramEditingContext editingContext;

	private LocalizedEditor localizedEditor;

	final FileSystemResourceLocatorImpl resourceLocator;

	public class MenuBar extends JMenuBar implements PreferenceChangeListener {

		private final JMenu fileMenu;
		private final JMenu editMenu;
		private final JMenu viewMenu;
		private final JMenu toolsMenu;
		private final JMenu helpMenu;

		private final JMenuItem newItem;
		private final JMenuItem loadItem;
		private final JMenuItem saveItem;
		private final JMenuItem saveAsItem;
		private final JMenuItem closeItem;
		private final JMenuItem quitItem;

		private final SynchronizedMenuItem copyItem;
		private final SynchronizedMenuItem cutItem;
		private final SynchronizedMenuItem pasteItem;
		private final SynchronizedMenuItem undoItem;
		private final SynchronizedMenuItem redoItem;

		private final JMenuItem logsItem;
		private final JMenuItem localizedItem;

		private final JMenu openRecent;

		private final JMenuItem showPaletteItem;

		public MenuBar() {
			fileMenu = new JMenu(DIAGRAM_EDITOR_LOCALIZATION.localizedForKey("file"));
			editMenu = new JMenu(DIAGRAM_EDITOR_LOCALIZATION.localizedForKey("edit"));
			viewMenu = new JMenu(DIAGRAM_EDITOR_LOCALIZATION.localizedForKey("view"));
			toolsMenu = new JMenu(DIAGRAM_EDITOR_LOCALIZATION.localizedForKey("tools"));
			helpMenu = new JMenu(DIAGRAM_EDITOR_LOCALIZATION.localizedForKey("help"));

			newItem = new JMenuItem(DIAGRAM_EDITOR_LOCALIZATION.localizedForKey("new_diagram"));
			newItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					newDiagramEditor();
				}
			});

			loadItem = new JMenuItem(DIAGRAM_EDITOR_LOCALIZATION.localizedForKey("open_diagram"));
			loadItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					loadDiagramEditor();
				}
			});

			openRecent = new JMenu(DIAGRAM_EDITOR_LOCALIZATION.localizedForKey("open_recent"));
			DiagramEditorPreferences.addPreferenceChangeListener(this);
			updateOpenRecent();
			saveItem = new JMenuItem(DIAGRAM_EDITOR_LOCALIZATION.localizedForKey("save_diagram"));
			saveItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					saveDiagramEditor();
				}
			});

			saveAsItem = new JMenuItem(DIAGRAM_EDITOR_LOCALIZATION.localizedForKey("save_diagram_as"));
			saveAsItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					saveDrawingAs();
				}
			});

			closeItem = new JMenuItem(DIAGRAM_EDITOR_LOCALIZATION.localizedForKey("close"));
			closeItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					closeDrawing();
				}
			});

			quitItem = new JMenuItem(DIAGRAM_EDITOR_LOCALIZATION.localizedForKey("quit"));
			quitItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					quit();
				}
			});

			fileMenu.add(newItem);
			fileMenu.add(loadItem);
			fileMenu.add(openRecent);
			fileMenu.add(saveItem);
			fileMenu.add(saveAsItem);
			fileMenu.add(closeItem);
			fileMenu.addSeparator();

			fileMenu.add(quitItem);

			showPaletteItem = new JMenuItem(DIAGRAM_EDITOR_LOCALIZATION.localizedForKey("show_palette"));
			showPaletteItem.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					paletteDialog.setVisible(true);
				}
			});
			logsItem = new JMenuItem(DIAGRAM_EDITOR_LOCALIZATION.localizedForKey("logs"));
			logsItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					FlexoLoggingViewer.showLoggingViewer(FlexoLoggingManager.instance(), ApplicationFIBLibraryImpl.instance(), frame);
				}
			});

			localizedItem = new JMenuItem(DIAGRAM_EDITOR_LOCALIZATION.localizedForKey("localized_editor"));
			localizedItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (localizedEditor == null) {
						localizedEditor = new LocalizedEditor(getFrame(), "localized_editor", DIAGRAM_EDITOR_LOCALIZATION,
								DIAGRAM_EDITOR_LOCALIZATION, true, false);
					}
					localizedEditor.setVisible(true);
				}
			});

			copyItem = makeSynchronizedMenuItem("copy", COPY_ICON, KeyStroke.getKeyStroke(KeyEvent.VK_C, META_MASK), new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					System.out.println("copy");
					try {
						currentDiagramEditor.getController().copy();
					} catch (CopyException e1) {
						e1.printStackTrace();
					}
				}
			}, new Synchronizer() {
				@Override
				public void synchronize(HasPropertyChangeSupport observable, SynchronizedMenuItem menuItem) {
					if (observable instanceof DianaDrawingEditor) {
						menuItem.setEnabled(((DianaDrawingEditor) observable).isCopiable());
					}
				}
			});

			cutItem = makeSynchronizedMenuItem("cut", CUT_ICON, KeyStroke.getKeyStroke(KeyEvent.VK_X, META_MASK), new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					System.out.println("cut");
					try {
						currentDiagramEditor.getController().cut();
					} catch (CutException e1) {
						e1.printStackTrace();
					}
				}
			}, new Synchronizer() {
				@Override
				public void synchronize(HasPropertyChangeSupport observable, SynchronizedMenuItem menuItem) {
					if (observable instanceof DianaDrawingEditor) {
						menuItem.setEnabled(((DianaDrawingEditor) observable).isCutable());
					}
				}
			});

			pasteItem = makeSynchronizedMenuItem("paste", PASTE_ICON, KeyStroke.getKeyStroke(KeyEvent.VK_V, META_MASK),
					new AbstractAction() {
						@Override
						public void actionPerformed(ActionEvent e) {
							System.out.println("paste");
							if (currentDiagramEditor != null) {
								try {
									currentDiagramEditor.getController().paste();
								} catch (PasteException e1) {
									e1.printStackTrace();
								}
							}
						}
					}, new Synchronizer() {
						@Override
						public void synchronize(HasPropertyChangeSupport observable, SynchronizedMenuItem menuItem) {
							if (observable instanceof DianaInteractiveViewer) {
								menuItem.setEnabled(currentDiagramEditor.getController().isPastable());
							}
						}
					});

			undoItem = makeSynchronizedMenuItem("undo", UNDO_ICON, KeyStroke.getKeyStroke(KeyEvent.VK_Z, META_MASK), new AbstractAction() {
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
						}
						else {
							menuItem.setText(DIAGRAM_EDITOR_LOCALIZATION.localizedForKey("undo"));
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
						}
						else {
							menuItem.setText(DIAGRAM_EDITOR_LOCALIZATION.localizedForKey("redo"));
						}
					}
				}
			});

			editMenu.add(copyItem);
			editMenu.add(cutItem);
			editMenu.add(pasteItem);
			editMenu.addSeparator();
			editMenu.add(undoItem);
			editMenu.add(redoItem);

			WindowMenuItem foregroundInspectorItem = new WindowMenuItem(DIAGRAM_EDITOR_LOCALIZATION.localizedForKey("foreground_inspector"),
					inspectors.getForegroundStyleInspector());
			WindowMenuItem backgroundInspectorItem = new WindowMenuItem(DIAGRAM_EDITOR_LOCALIZATION.localizedForKey("background_inspector"),
					inspectors.getBackgroundStyleInspector());
			WindowMenuItem textInspectorItem = new WindowMenuItem(DIAGRAM_EDITOR_LOCALIZATION.localizedForKey("text_inspector"),
					inspectors.getTextPropertiesInspector());
			WindowMenuItem shapeInspectorItem = new WindowMenuItem(DIAGRAM_EDITOR_LOCALIZATION.localizedForKey("shape_inspector"),
					inspectors.getShapeInspector());
			WindowMenuItem connectorInspectorItem = new WindowMenuItem(DIAGRAM_EDITOR_LOCALIZATION.localizedForKey("connector_inspector"),
					inspectors.getConnectorInspector());
			WindowMenuItem shadowInspectorItem = new WindowMenuItem(DIAGRAM_EDITOR_LOCALIZATION.localizedForKey("shadow_inspector"),
					inspectors.getShadowStyleInspector());
			WindowMenuItem locationSizeInspectorItem = new WindowMenuItem(
					DIAGRAM_EDITOR_LOCALIZATION.localizedForKey("location_size_inspector"), inspectors.getLocationSizeInspector());
			WindowMenuItem layoutManagerInspectorItem = new WindowMenuItem(
					DIAGRAM_EDITOR_LOCALIZATION.localizedForKey("layout_manager_inspector"), inspectors.getLayoutManagersInspector());

			WindowMenuItem paletteItem = new WindowMenuItem(DIAGRAM_EDITOR_LOCALIZATION.localizedForKey("palette"), paletteDialog);

			viewMenu.add(foregroundInspectorItem);
			viewMenu.add(backgroundInspectorItem);
			viewMenu.add(textInspectorItem);
			viewMenu.add(shapeInspectorItem);
			viewMenu.add(connectorInspectorItem);
			viewMenu.add(shadowInspectorItem);
			viewMenu.add(locationSizeInspectorItem);
			viewMenu.add(layoutManagerInspectorItem);
			viewMenu.addSeparator();
			viewMenu.add(paletteItem);

			toolsMenu.add(showPaletteItem);
			toolsMenu.add(logsItem);
			toolsMenu.add(localizedItem);

			add(fileMenu);
			add(editMenu);
			add(viewMenu);
			add(toolsMenu);
			add(helpMenu);
		}

		private boolean willUpdate = false;

		@Override
		public void preferenceChange(PreferenceChangeEvent evt) {
			if (evt.getKey().startsWith(DiagramEditorPreferences.LAST_FILE)) {
				if (willUpdate) {
					return;
				}
				else {
					willUpdate = true;
				}
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						willUpdate = false;
						updateOpenRecent();
					}
				});
			}
		}

		private void updateOpenRecent() {
			openRecent.removeAll();
			List<File> files = DiagramEditorPreferences.getLastFiles();
			openRecent.setEnabled(files.size() != 0);
			for (final File file : files) {
				JMenuItem item = new JMenuItem(file.getName());
				item.setToolTipText(file.getAbsolutePath());
				item.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						loadDiagramEditor(file);
					}
				});
				openRecent.add(item);
			}
		}

	}

	private MenuBar menuBar;

	public DiagramEditorApplication() {
		super();

		editingContext = new DiagramEditingContext();

		try {
			factory = new DiagramFactory(editingContext);
			// System.out.println("factory: " + factory.debug());
			// FGEPamelaInjectionModule injectionModule = new FGEPamelaInjectionModule(factory);
			// injector = Guice.createInjector(injectionModule);

			// factory = new DiagramFactory();
		} catch (ModelDefinitionException e1) {
			e1.printStackTrace();
		}

		frame = new JFrame();

		frame.setBounds(DiagramEditorPreferences.getFrameBounds());
		new ComponentBoundSaver(frame) {

			@Override
			public void saveBounds(Rectangle bounds) {
				DiagramEditorPreferences.setFrameBounds(bounds);
			}
		};
		fileChooser = new FlexoFileChooser(frame);
		fileChooser.setFileFilter(new FileFilter() {

			@Override
			public String getDescription() {
				return "*.fib *.inspector";
			}

			@Override
			public boolean accept(File f) {
				return f.isDirectory() || f.getName().endsWith(".drw");
			}
		});
		fileChooser.setCurrentDirectory(DiagramEditorPreferences.getLastDirectory());

		resourceLocator = new FileSystemResourceLocatorImpl();
		if (DiagramEditorPreferences.getLastDirectory() != null) {
			resourceLocator.appendToDirectories(DiagramEditorPreferences.getLastDirectory().getAbsolutePath());
		}
		resourceLocator.appendToDirectories(System.getProperty("user.home"));
		ResourceLocator.appendDelegate(resourceLocator);

		frame.setPreferredSize(new Dimension(1100, 800));

		toolFactory = new SwingToolFactory(frame);

		// inspector = new JFIBInspectorController(frame);

		frame.setTitle("DIANA - Drawing Editor");

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

		JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		topPanel.add(toolSelector.getComponent());
		topPanel.add(stylesWidget.getComponent());
		topPanel.add(layoutWidget.getComponent());
		topPanel.add(scaleSelector.getComponent());

		mainPanel.add(topPanel, BorderLayout.NORTH);

		commonPaletteModel = new DiagramEditorPalette();
		commonPalette = toolFactory.makeDianaPalette(commonPaletteModel);

		paletteDialog = new JDialog(frame, "Palette", false);
		paletteDialog.getContentPane().add(commonPalette.getComponent());
		paletteDialog.setLocation(1010, 0);
		paletteDialog.pack();
		paletteDialog.setVisible(true);
		paletteDialog.setFocusableWindowState(false);

		manager = new PropertyChangeListenerRegistrationManager();

		menuBar = new MenuBar();

		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				quit();
			}
		});
		frame.setJMenuBar(menuBar);

		/*JMenuBar mb = new JMenuBar();
		JMenu fileMenu = new JMenu(FlexoLocalization.localizedForKey(LOCALIZATION, "file"));
		JMenu editMenu = new JMenu(FlexoLocalization.localizedForKey(LOCALIZATION, "edit"));
		JMenu viewMenu = new JMenu(FlexoLocalization.localizedForKey(LOCALIZATION, "view"));
		JMenu toolsMenu = new JMenu(FlexoLocalization.localizedForKey(LOCALIZATION, "tools"));
		JMenu helpMenu = new JMenu(FlexoLocalization.localizedForKey(LOCALIZATION, "help"));
		
		JMenuItem newItem = makeJMenuItem("new_drawing", NEW_ICON, KeyStroke.getKeyStroke(KeyEvent.VK_N, META_MASK), new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				newDiagramEditor();
			}
		});
		
		JMenuItem openItem = makeJMenuItem("open_drawing", OPEN_ICON, KeyStroke.getKeyStroke(KeyEvent.VK_O, META_MASK),
				new AbstractAction() {
					@Override
					public void actionPerformed(ActionEvent e) {
						loadDiagramEditor();
					}
				});
		
		JMenu openRecent = new JMenu(FlexoLocalization.localizedForKey(LOCALIZATION, "open_recent"));
		DiagramEditorPreferences.addPreferenceChangeListener(this);
		updateOpenRecent();
		
		JMenuItem saveItem = makeJMenuItem("save_drawing", SAVE_ICON, KeyStroke.getKeyStroke(KeyEvent.VK_S, META_MASK),
				new AbstractAction() {
					@Override
					public void actionPerformed(ActionEvent e) {
						saveDiagramEditor();
					}
				});
		
		JMenuItem saveAsItem = makeJMenuItem("save_drawing_as...", SAVE_AS_ICON, null, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveDrawingAs();
			}
		});
		
		JMenuItem closeItem = makeJMenuItem("close_drawing", null, null, new AbstractAction() {
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
		fileMenu.add(openItem);
		fileMenu.add(openRecent);
		fileMenu.add(saveItem);
		fileMenu.add(saveAsItem);
		fileMenu.add(closeItem);
		fileMenu.addSeparator();
		fileMenu.add(quitItem);*/

		/*copyItem = makeSynchronizedMenuItem("copy", COPY_ICON, KeyStroke.getKeyStroke(KeyEvent.VK_C, META_MASK), new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("copy");
				try {
					currentDiagramEditor.getController().copy();
				} catch (CopyException e1) {
					e1.printStackTrace();
				}
			}
		}, new Synchronizer() {
			@Override
			public void synchronize(HasPropertyChangeSupport observable, SynchronizedMenuItem menuItem) {
				if (observable instanceof DianaDrawingEditor) {
					menuItem.setEnabled(((DianaDrawingEditor) observable).isCopiable());
				}
			}
		});
		
		cutItem = makeSynchronizedMenuItem("cut", CUT_ICON, KeyStroke.getKeyStroke(KeyEvent.VK_X, META_MASK), new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("cut");
				try {
					currentDiagramEditor.getController().cut();
				} catch (CutException e1) {
					e1.printStackTrace();
				}
			}
		}, new Synchronizer() {
			@Override
			public void synchronize(HasPropertyChangeSupport observable, SynchronizedMenuItem menuItem) {
				if (observable instanceof DianaDrawingEditor) {
					menuItem.setEnabled(((DianaDrawingEditor) observable).isCutable());
				}
			}
		});
		
		pasteItem = makeSynchronizedMenuItem("paste", PASTE_ICON, KeyStroke.getKeyStroke(KeyEvent.VK_V, META_MASK), new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("paste");
				if (currentDiagramEditor != null) {
					try {
						currentDiagramEditor.getController().paste();
					} catch (PasteException e1) {
						e1.printStackTrace();
					}
				}
			}
		}, new Synchronizer() {
			@Override
			public void synchronize(HasPropertyChangeSupport observable, SynchronizedMenuItem menuItem) {
				if (observable instanceof DianaInteractiveViewer) {
					menuItem.setEnabled(currentDiagramEditor.getController().isPastable());
				}
			}
		});
		
		undoItem = makeSynchronizedMenuItem("undo", UNDO_ICON, KeyStroke.getKeyStroke(KeyEvent.VK_Z, META_MASK), new AbstractAction() {
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
					}
					else {
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
					}
					else {
						menuItem.setText(FlexoLocalization.localizedForKey(LOCALIZATION, "redo"));
					}
				}
			}
		});
		
		editMenu.add(copyItem);
		editMenu.add(cutItem);
		editMenu.add(pasteItem);
		editMenu.addSeparator();
		editMenu.add(undoItem);
		editMenu.add(redoItem);
		
		WindowMenuItem foregroundInspectorItem = new WindowMenuItem(FlexoLocalization.localizedForKey(LOCALIZATION, "foreground_inspector"),
				inspectors.getForegroundStyleInspector());
		WindowMenuItem backgroundInspectorItem = new WindowMenuItem(FlexoLocalization.localizedForKey(LOCALIZATION, "background_inspector"),
				inspectors.getBackgroundStyleInspector());
		WindowMenuItem textInspectorItem = new WindowMenuItem(FlexoLocalization.localizedForKey(LOCALIZATION, "text_inspector"),
				inspectors.getTextStyleInspector());
		WindowMenuItem shapeInspectorItem = new WindowMenuItem(FlexoLocalization.localizedForKey(LOCALIZATION, "shape_inspector"),
				inspectors.getShapeInspector());
		WindowMenuItem connectorInspectorItem = new WindowMenuItem(FlexoLocalization.localizedForKey(LOCALIZATION, "connector_inspector"),
				inspectors.getConnectorInspector());
		WindowMenuItem shadowInspectorItem = new WindowMenuItem(FlexoLocalization.localizedForKey(LOCALIZATION, "shadow_inspector"),
				inspectors.getShadowStyleInspector());
		WindowMenuItem locationSizeInspectorItem = new WindowMenuItem(
				FlexoLocalization.localizedForKey(LOCALIZATION, "location_size_inspector"), inspectors.getLocationSizeInspector());
		WindowMenuItem layoutManagerInspectorItem = new WindowMenuItem(
				FlexoLocalization.localizedForKey(LOCALIZATION, "layout_manager_inspector"), inspectors.getLayoutManagersInspector());
		
		WindowMenuItem paletteItem = new WindowMenuItem(FlexoLocalization.localizedForKey(LOCALIZATION, "palette"), paletteDialog);
		
		viewMenu.add(foregroundInspectorItem);
		viewMenu.add(backgroundInspectorItem);
		viewMenu.add(textInspectorItem);
		viewMenu.add(shapeInspectorItem);
		viewMenu.add(connectorInspectorItem);
		viewMenu.add(shadowInspectorItem);
		viewMenu.add(locationSizeInspectorItem);
		viewMenu.add(layoutManagerInspectorItem);
		viewMenu.addSeparator();
		viewMenu.add(paletteItem);
		
		JMenuItem logsItem = new JMenuItem(FlexoLocalization.localizedForKey(LOCALIZATION, "logs"));
		logsItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				FlexoLoggingViewer.showLoggingViewer(FlexoLoggingManager.instance(), frame);
			}
		});
		
		JMenuItem localizedItem = new JMenuItem(FlexoLocalization.localizedForKey(LOCALIZATION, "localized_editor"));
		localizedItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (localizedEditor == null) {
					localizedEditor = new LocalizedEditor(frame, "localized_editor", LOCALIZATION, MAIN_LOCALIZER, true, false);
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
		
		frame.setJMenuBar(mb);*/

		frame.getContentPane().add(mainPanel);
		frame.validate();
		frame.pack();

	}

	public JFrame getFrame() {
		return frame;
	}

	public DiagramEditingContext getEditingContext() {
		return editingContext;
	}

	private class MyDrawingViewScrollPane extends JScrollPane {
		private final DiagramEditorView drawingView;

		private MyDrawingViewScrollPane(DiagramEditorView v) {
			super(v);
			drawingView = v;
		}
	}

	public DiagramFactory getFactory() {
		return factory;
	}

	public SwingToolFactory getToolFactory() {
		return toolFactory;
	}

	/*public Injector getInjector() {
		return injector;
	}*/

	public void addDiagramEditor(DiagramEditor diagramEditor) {
		if (tabbedPane == null) {
			tabbedPane = new JTabbedPane();
			tabbedPane.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					MyDrawingViewScrollPane c = (MyDrawingViewScrollPane) tabbedPane.getSelectedComponent();
					if (c != null) {
						drawingSwitched(c.drawingView.getDrawing().getModel());
					}
				}
			});
			mainPanel.add(tabbedPane, BorderLayout.CENTER);
			// mainPanel.add(drawing.getEditedDrawing().getPalette().getPaletteView(),BorderLayout.EAST);
		}
		diagramEditors.add(diagramEditor);

		// DianaEditor controller = new DianaEditor(diagramEditor.getEditedDrawing(), diagramEditor.getFactory());
		// AbstractDianaEditor<DiagramDrawing> controller = new AbstractDianaEditor<DiagramDrawing>(aDrawing, factory)

		tabbedPane.add(diagramEditor.getTitle(), new MyDrawingViewScrollPane(diagramEditor.getController().getDrawingView()));
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

	// FD : never used
	// private void removeDiagramEditor(DiagramEditor diagramEditor) {

	// }

	public void switchToDiagramEditor(DiagramEditor diagramEditor) {
		tabbedPane.setSelectedIndex(diagramEditors.indexOf(diagramEditor));
	}

	private void drawingSwitched(Diagram diagram) {
		for (DiagramEditor editor : diagramEditors) {
			if (editor.getDiagram() == diagram) {
				drawingSwitched(editor);
				return;
			}
		}
	}

	private void drawingSwitched(DiagramEditor diagramEditor) {

		logger.info("Switch to editor " + diagramEditor);

		/*if (currentDiagramEditor != null) {
			// mainPanel.remove(currentDiagramEditor.getController().getScalePanel());
			currentDiagramEditor.getController().deleteObserver(inspector);
		}*/
		currentDiagramEditor = diagramEditor;
		toolSelector.attachToEditor(diagramEditor.getController());
		stylesWidget.attachToEditor(diagramEditor.getController());
		scaleSelector.attachToEditor(diagramEditor.getController());
		layoutWidget.attachToEditor(diagramEditor.getController());
		commonPaletteModel.setEditor(diagramEditor.getController());
		commonPalette.attachToEditor(diagramEditor.getController());
		inspectors.attachToEditor(diagramEditor.getController());

		/*JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		topPanel.add(currentDiagramEditor.getController().getToolbox().getStyleToolBar());
		topPanel.add(currentDiagramEditor.getController().getScalePanel());
		
		mainPanel.add(topPanel, BorderLayout.NORTH);*/

		menuBar.copyItem.synchronizeWith(diagramEditor.getController());
		menuBar.cutItem.synchronizeWith(diagramEditor.getController());
		menuBar.pasteItem.synchronizeWith(diagramEditor.getController());

		menuBar.undoItem.synchronizeWith(diagramEditor.getController().getFactory().getUndoManager());
		menuBar.redoItem.synchronizeWith(diagramEditor.getController().getFactory().getUndoManager());

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
		DiagramEditor newDiagramEditor = DiagramEditor.newDiagramEditor(factory, this);
		addDiagramEditor(newDiagramEditor);
	}

	public void loadDiagramEditor() {
		if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			loadDiagramEditor(file);
		}
	}

	public void loadDiagramEditor(File file) {
		DiagramEditor loadedDiagramEditor = DiagramEditor.loadDiagramEditor(file, factory, this);
		if (loadedDiagramEditor != null) {
			addDiagramEditor(loadedDiagramEditor);
		}
		DiagramEditorPreferences.setLastFile(file);
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
			if (!file.getName().endsWith(".drw")) {
				file = new File(file.getParentFile(), file.getName() + ".drw");
			}
			currentDiagramEditor.setFile(file);
			DiagramEditorPreferences.setLastFile(file);
			updateFrameTitle();
			updateTabTitle();
			return currentDiagramEditor.save();
		}
		else {
			return false;
		}
	}

	// FD : never used
	// private JMenuItem makeJMenuItem(String actionName, Icon icon, KeyStroke accelerator, AbstractAction action) {

	// JMenuItem returned = new JMenuItem(FlexoLocalization.localizedForKey(LOCALIZATION, actionName));
	// returned.addActionListener(action);
	// returned.setIcon(icon);
	// returned.setAccelerator(accelerator);
	// frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(accelerator, actionName);
	// frame.getRootPane().getActionMap().put(actionName, action);
	// return returned;
	// }

	private SynchronizedMenuItem makeSynchronizedMenuItem(String actionName, Icon icon, KeyStroke accelerator, AbstractAction action,
			Synchronizer synchronizer) {

		String localizedName = DIAGRAM_EDITOR_LOCALIZATION.localizedForKey(actionName);
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
