package application;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Stack;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;

/*
	Engine of the project. 
	Class used to move pieces, reset the board, roll back last move until the start of the game, save/load the game and select the configurations
*/

public class Engine {
	
	private boolean legal;
    private StringBuilder moveLog;
    
    public Engine(boolean legal, StringBuilder moveLog) {
        this.legal = legal;
        this.moveLog = moveLog;
    }
    
    public boolean isLegal() {
        return legal;
    }

    public StringBuilder getMoveLog() {
        return moveLog;
    }
	
    public static Engine movePiece(KeyEvent event, Rectangle selectedPiece, GridPane board, Map<Rectangle, Integer[]> initialPositions, boolean legal, Stack<MoveInfo> moveHistory, int moves, StringBuilder moveLog) {
    	KeyCode keyCode = event.getCode();
    	if (selectedPiece == null) {
	    	return new Engine(false, moveLog);
	    }
        int rowChange = 0;
        int columnChange = 0;

        switch (keyCode) {
            case W:
                rowChange = -1;
                break;
            case S:
                rowChange = 1;
                break;
            case A:
                columnChange = -1;
                break;
            case D:
                columnChange = 1;
                break;
            default:
                return new Engine(false, moveLog); // Return a default value for unknown key events
        }
	    int oldRowIndex = GridPane.getRowIndex(selectedPiece);
	    int oldColumnIndex = GridPane.getColumnIndex(selectedPiece);
	    // Calculate new position
	    int rowIndex = GridPane.getRowIndex(selectedPiece);
	    int columnIndex = GridPane.getColumnIndex(selectedPiece);
	    int newRow = rowIndex + rowChange;
	    int newColumn = columnIndex + columnChange;
	    
	    // Check if new position is within grid limits
	    boolean newPositionLegal = false;
	    
	   int type=4;
	   String ID="Small";
	   if((selectedPiece.getWidth()==200) && (selectedPiece.getHeight()==100)) {
		   type=1;
		   ID="Horizontal medium";
	   }else if((selectedPiece.getWidth()==100) && (selectedPiece.getHeight()==200)) {
		   type=2;
		   ID="Vertical medium";
	   }else if ((selectedPiece.getWidth()==200) && (selectedPiece.getHeight()==200)) {
		   type=3;
		   ID="Goal";
	   }else {
		   type=4;
		   ID="Small";
	   }
	   
	    // Check if the new position is within grid limits for the selected piece
	    if (Checks.isMoveWithinBounds(newRow, newColumn, type)) {
	        // Check if the new position is not occupied by another piece
	        Node occupant = Checks.getNodeAtPosition(board, newRow, newColumn);
	        if (occupant == null) {
	            // Check for overlap with other pieces
	            boolean overlap = Checks.checkForOverlap(selectedPiece, newRow, newColumn, initialPositions);
	            
	            if (!overlap) {
	                GridPane.setRowIndex(selectedPiece, newRow);
	                GridPane.setColumnIndex(selectedPiece, newColumn);
	                newPositionLegal = true;
	            }
	        }
	    }
	    if (newPositionLegal) {
	    	
	        // Store the previous position before making the move
	        MoveInfo moveInfo = new MoveInfo(selectedPiece, oldRowIndex, oldColumnIndex);
	        moveHistory.push(moveInfo);
	    	
	        // Append move information to the log
	        String Info = String.format("Move %d: %s piece at (%d, %d) moved to (%d, %d)%n", moves, ID, rowIndex, columnIndex, newRow, newColumn);
	        moveLog.append(Info);

	        // Update UI
	        legal = newPositionLegal;
	        return new Engine(legal,moveLog);
	    }
	    return new Engine(false, moveLog); // Return a default value if the move is not legal
	}
    
    public static void reset (Map<Rectangle, Integer[]> initialPositions) {
		for (Map.Entry<Rectangle, Integer[]> entry : initialPositions.entrySet()) {
	        Rectangle rectangle = entry.getKey();
	        Integer[] initialPosition = entry.getValue();
	        GridPane.setRowIndex(rectangle, initialPosition[0]);
	        GridPane.setColumnIndex(rectangle, initialPosition[1]);

	    }
	}
    
	public static void undo(Stack<MoveInfo> moveHistory,  StringBuilder moveLog) {
	    if (!moveHistory.isEmpty()) {
	        MoveInfo lastMove = moveHistory.pop();
	        Rectangle piece = lastMove.piece;
	        int oldRow = lastMove.oldRow;
	        int oldColumn = lastMove.oldColumn;

	        GridPane.setRowIndex(piece, oldRow);
	        GridPane.setColumnIndex(piece, oldColumn);
	        int lastIndex = moveLog.lastIndexOf("Move");
	        if (lastIndex >= 0) {
	            moveLog.setLength(lastIndex);
	        }
	    }
	}
    
	public static void save(StringBuilder moveLog) {
	    try {
	        File logFile = new File("move_log.txt");
	        FileWriter writer = new FileWriter(logFile);
	        writer.write(moveLog.toString());
	        writer.close();
	        System.out.println("Move log saved to 'move_log.txt'");
	    } catch (IOException ex) {
	        System.err.println("Error saving move log: " + ex.getMessage());
	    }
		System.out.println("SAVE");
	}
	
}