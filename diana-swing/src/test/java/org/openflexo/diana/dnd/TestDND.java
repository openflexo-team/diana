package org.openflexo.diana.dnd;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragSource;
import java.awt.dnd.DropTarget;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import org.openflexo.gina.utils.FIBIconLibrary;
import org.openflexo.toolbox.ToolBox;

public class TestDND {

	private static final Image DROP_OK_IMAGE = FIBIconLibrary.DROP_OK_CURSOR.getImage();
	private static final Image DROP_KO_IMAGE = FIBIconLibrary.DROP_KO_CURSOR.getImage();

	public static Cursor dropOK = ToolBox.isMacOS()
			? Toolkit.getDefaultToolkit().createCustomCursor(DROP_OK_IMAGE, new Point(16, 16), "Drop OK") : DragSource.DefaultMoveDrop;
	public static Cursor dropKO = ToolBox.isMacOS()
			? Toolkit.getDefaultToolkit().createCustomCursor(DROP_KO_IMAGE, new Point(16, 16), "Drop KO") : DragSource.DefaultMoveNoDrop;

	private static MyDGListener dgListener;
	static MyDSListener dsListener;

	public static void main(String[] args) {

		JFrame frame = new JFrame();

		DefaultMutableTreeNode style = new DefaultMutableTreeNode(new MyTransferable("Style"));
		DefaultMutableTreeNode color = new DefaultMutableTreeNode(new MyTransferable("color"));
		DefaultMutableTreeNode font = new DefaultMutableTreeNode(new MyTransferable("font"));
		style.add(color);
		style.add(font);
		DefaultMutableTreeNode red = new DefaultMutableTreeNode(new MyTransferable("red"));
		DefaultMutableTreeNode blue = new DefaultMutableTreeNode(new MyTransferable("blue"));
		DefaultMutableTreeNode black = new DefaultMutableTreeNode(new MyTransferable("black"));
		DefaultMutableTreeNode green = new DefaultMutableTreeNode(new MyTransferable("green"));
		color.add(red);
		color.add(blue);
		color.add(black);
		color.add(green);

		MyDnDJTree tree = new MyDnDJTree(new MyTreeModel(style));
		tree.setPreferredSize(new Dimension(200, 300));

		dgListener = new MyDGListener(tree);
		dsListener = new MyDSListener();

		tree.registerDragGestureListener(dgListener);

		JPanel contentPane = new JPanel(new BorderLayout());
		contentPane.add(tree, BorderLayout.WEST);

		JPanel rightPane = new JPanel(new BorderLayout());
		rightPane.add(new JLabel("Drag in the above area"), BorderLayout.NORTH);

		JPanel draggedArea = new JPanel();
		draggedArea.setOpaque(true);
		draggedArea.setBackground(Color.yellow);
		draggedArea.setPreferredSize(new Dimension(400, 300));
		rightPane.add(draggedArea, BorderLayout.CENTER);

		MyDropTargetListener dropListener = new MyDropTargetListener();
		draggedArea.setDropTarget(new DropTarget(draggedArea, DnDConstants.ACTION_COPY | DnDConstants.ACTION_MOVE, dropListener, true));

		contentPane.add(rightPane, BorderLayout.CENTER);

		frame.setContentPane(contentPane);

		frame.pack();
		frame.setVisible(true);
	}

	public static class MyTreeModel extends DefaultTreeModel {

		public MyTreeModel(TreeNode root) {
			super(root);
		}
	}

}
