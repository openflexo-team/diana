package org.openflexo.checkers;

public class TestCheckersBoard {
	private final TestCheckersPiece[][] content;

	public TestCheckersBoard() {
		content = new TestCheckersPiece[8][8];
	}

	public void addPiece(int x, int y, TestCheckersPiece.Color color) {
		if (x < 8 && y < 8) {
			if (content[x][y] == null) {
				content[x][y] = new TestCheckersPiece(color);
			}
		}
	}

	public void removePiece(int x, int y) {
		content[x][y] = null;
	}

	public void movePiece(int xStart, int yStart, int xEnd, int yEnd) {
		if (content[xStart][yStart] != null) {
			content[xEnd][yEnd] = content[xStart][yStart];
			content[xStart][yStart] = null;
		}
	}

	public TestCheckersPiece[][] getContent() {
		return content;
	}

}
