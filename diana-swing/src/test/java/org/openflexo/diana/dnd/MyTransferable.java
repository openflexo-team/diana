package org.openflexo.diana.dnd;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public class MyTransferable implements Transferable {

	final public static DataFlavor MY_FLAVOR = new DataFlavor(MyTransferable.class, "MyFlavor");
	static DataFlavor FLAVORS[] = { MY_FLAVOR };

	private String data;

	public MyTransferable(String data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return data;
	}

	@Override
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
		return data;
	}

	@Override
	public DataFlavor[] getTransferDataFlavors() {
		return FLAVORS;
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return true;
	}
}
