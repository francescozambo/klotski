package application;

import java.util.Map;

import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;

/*
	Class of support used to control overlap/out of bound illegal movements and get pieces from the save file
	
*/

public class Support {

    public static boolean isMoveWithinBounds(int newRow, int newColumn, int type) {
		int maxRows=5;
		int maxColumns=4;
		switch(type) {
			case 1:
				maxRows=5;
				maxColumns=3;
				break;
			case 2:
				maxRows=4;
				maxColumns=4;
				break;
			case 3:
				maxRows=4;
				maxColumns=3;
				break;
			default:
				break;
				
		}
	    return newRow >= 0 && newRow < maxRows && newColumn >= 0 && newColumn < maxColumns;
	}

	public static boolean checkForOverlap(Rectangle pieceToCheck, int newRow, int newColumn, Map<Rectangle, Integer[]> initialPositions) {
	    int pieceRowCount = (int) Math.ceil(pieceToCheck.getHeight() / 100);
	    int pieceColumnCount = (int) Math.ceil(pieceToCheck.getWidth() / 100);
	    
	    for (Rectangle piece : initialPositions.keySet()) {
	        if (piece != pieceToCheck) {
	            int pieceRow = GridPane.getRowIndex(piece);
	            int pieceColumn = GridPane.getColumnIndex(piece);
	            
	            int otherPieceRowCount = (int) Math.ceil(piece.getHeight() / 100);
	            int otherPieceColumnCount = (int) Math.ceil(piece.getWidth() / 100);
	            
	            if (newRow + pieceRowCount > pieceRow && newRow < pieceRow + otherPieceRowCount &&
	                newColumn + pieceColumnCount > pieceColumn && newColumn < pieceColumn + otherPieceColumnCount) {
	                return true; // Overlap detected
	            }
	        }
	    }
	    
	    return false; // No overlap detected
	}

    public static Node getNodeAtPosition(GridPane board, int row, int column) {
        for (Node node : board.getChildren()) {
            Integer rowIndex = GridPane.getRowIndex(node);
            Integer columnIndex = GridPane.getColumnIndex(node);

            // Check if the node has row and column indexes set
            if (rowIndex != null && columnIndex != null && rowIndex == row && columnIndex == column) {
                return node;
            }
        }
        return null;
    }
    
    public static Rectangle getPieceById(String id, GridPane board) {
        for (Node node : board.getChildren()) {
            if (node instanceof Rectangle) {
                Rectangle piece = (Rectangle) node;
                if (id.equals(piece.getId())) {
                    return piece;
                }
            }
        }
        return null; // If no piece with the given ID is found
    }

   
    
    public static boolean isWin(GridPane board, Rectangle piece) {
        int targetRow = 3;
        int targetColumn = 1;

        Integer blockRow = GridPane.getRowIndex(piece);
        Integer blockColumn = GridPane.getColumnIndex(piece);

        if (blockRow != null && blockColumn != null && blockRow == targetRow && blockColumn == targetColumn) {
            return true; // Block is in the target position
        }

        return false; // Block is not in the target position
    }
    
}
