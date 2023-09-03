package application;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Stack;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.Gson;

import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;


/*
	Engine of the project. 
	Class used to move pieces, reset the board, roll back last move until the start of the game, save/load the game and select a configuration
*/

public class Engine {
	
	private boolean legal;
    
    public Engine(boolean legal) {
        this.legal = legal;
    }
    
    public boolean isLegal() {
        return legal;
    }

	/**
	 * Method to move the piece
	 * @param event the key w a s d in order to move the piece 
	 * @param selectedPiece the piece that has to be moved
	 * @param board
	 * @param initialPositions 
	 * @param legal
	 * @param moveHistory
	 * @param moves
	 * @return return an Engine object which is used to validate the move
	 */
    
    public static Engine movePiece(KeyEvent event, Rectangle selectedPiece, GridPane board, Map<Rectangle, Integer[]> initialPositions, boolean legal, Stack<MoveInfo> moveHistory, int moves) {
    	KeyCode keyCode = event.getCode();
    	if (selectedPiece == null) {
	    	return new Engine(false);
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
                return new Engine(false); // Return a default value for unknown key events
        }
        
        // save the old column and row index for the move history
	    int oldRowIndex = GridPane.getRowIndex(selectedPiece);
	    int oldColumnIndex = GridPane.getColumnIndex(selectedPiece);
	    
	    // Calculate new position
	    int rowIndex = GridPane.getRowIndex(selectedPiece);
	    int columnIndex = GridPane.getColumnIndex(selectedPiece);
	    int newRow = rowIndex + rowChange;
	    int newColumn = columnIndex + columnChange;
	    
	    boolean newPositionLegal = false;
	    
	    // set the type of the selected piece in order to be able to control its movement conditions
	    int type=4;
	    if((selectedPiece.getWidth()==200) && (selectedPiece.getHeight()==100)) {
		    type=1;
	    }else if((selectedPiece.getWidth()==100) && (selectedPiece.getHeight()==200)) {
		    type=2;
	    }else if ((selectedPiece.getWidth()==200) && (selectedPiece.getHeight()==200)) {
		    type=3;
	    }else {
		    type=4;
	    }
	   
	    // Check if the new position is within grid limits for the selected piece
	    if (Support.isMoveWithinBounds(newRow, newColumn, type)) {
	        // Check if the new position is not occupied by another piece
	        Node occupant = Support.getNodeAtPosition(board, newRow, newColumn);
	        if (occupant == null) {
	            // Check for overlap with other pieces
	            boolean overlap = Support.checkForOverlap(selectedPiece, newRow, newColumn, initialPositions);
	            
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
	    	
	        // Update UI
	        legal = newPositionLegal;
	        return new Engine(legal);
	    }
	    return new Engine(false); // Return a default value if the move is not legal
	}
    
    /**
     * Method to reset the board
     * @param initialPositions
     */
    
    public static void reset (Map<Rectangle, Integer[]> initialPositions) {
		for (Map.Entry<Rectangle, Integer[]> entry : initialPositions.entrySet()) {
	        Rectangle rectangle = entry.getKey();
	        Integer[] initialPosition = entry.getValue();
	        GridPane.setRowIndex(rectangle, initialPosition[0]);
	        GridPane.setColumnIndex(rectangle, initialPosition[1]);

	    }
	}
    
    /**
     * Method to roll back the last move done
     * @param moveHistory
     */
    
	public static void undo(Stack<MoveInfo> moveHistory) {
	    if (!moveHistory.isEmpty()) {
	        MoveInfo lastMove = moveHistory.pop();
	        Rectangle piece = lastMove.piece;
	        int oldRow = lastMove.oldRow;
	        int oldColumn = lastMove.oldColumn;

	        GridPane.setRowIndex(piece, oldRow);
	        GridPane.setColumnIndex(piece, oldColumn);
	    }
	}
    
	/**
	 * Method to save the board state in a json file called "board_state.json"
	 * @param board
	 * @param currentPositions
	 * @param numberOfMoves 
	 */
	
	public static void save(GridPane board, Map<Rectangle, Integer[]> currentPositions, int numberOfMoves) {
	    try {
	    	// construction of the save file 
	        JsonObject saveFile = new JsonObject();
	        saveFile.addProperty("numberOfMoves", numberOfMoves);
	        JsonObject initialPositionsObject = new JsonObject();
	        for (Map.Entry<Rectangle, Integer[]> entry : currentPositions.entrySet()) {
	            Rectangle piece = entry.getKey();
	            Integer[] position = entry.getValue();
	            JsonObject pieceInfo = new JsonObject();
	            pieceInfo.addProperty("y", position[0]);
	            pieceInfo.addProperty("x", position[1]);
	            initialPositionsObject.add(piece.getId(), pieceInfo);
	        }

	        saveFile.add("initialPositions", initialPositionsObject);

	        Gson gson = new GsonBuilder().setPrettyPrinting().create();

	        // Define the file name and extension
	        String directoryPath="src\\Save-Config\\";
	        String fileName = directoryPath+"board_state.json";

	        FileWriter writer = new FileWriter(fileName);
	        gson.toJson(saveFile, writer);
	        writer.close();
	        
	        // Alert to signal to the player that the game has been saved
			Alert saved=new Alert(AlertType.INFORMATION);
			saved.setTitle("Saving");
			saved.setHeaderText("Saving");
			saved.setContentText("Game saved in file board_state.json");
			saved.showAndWait();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}

	
	public static int load(String jsonFilePath, GridPane board, Map<Rectangle, Integer[]> initialPositions ) {
		int numberOfMoves = 0; // Initialize with a default value
		try {
	        Gson gson = new Gson();
	        FileReader reader = new FileReader(jsonFilePath);
	        JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
	        JsonElement numberOfMovesElement = jsonObject.get("numberOfMoves");
	        if (numberOfMovesElement != null) {
	            numberOfMoves = numberOfMovesElement.getAsInt();
	        }
	        
	        for (Map.Entry<String, JsonElement> entry : jsonObject.getAsJsonObject("initialPositions").entrySet()) {
	            String pieceId = entry.getKey();
	            JsonObject pieceInfo = entry.getValue().getAsJsonObject();
	            Rectangle piece = null;
	            for (Map.Entry<Rectangle, Integer[]> positionEntry : initialPositions.entrySet()) {
	                if (positionEntry.getKey().getId().equals(pieceId)) {
	                    piece = positionEntry.getKey();
	                    break;
	                }
	            }

	            if (piece != null) {
	                int newRow, newColumn;

	                JsonElement rowElement = pieceInfo.get("row");
	                JsonElement columnElement = pieceInfo.get("column");

	                if (rowElement != null && columnElement != null) {
	                    newRow = rowElement.getAsInt();
	                    newColumn = columnElement.getAsInt();
	                } else {
	                    newRow = pieceInfo.get("y").getAsInt();
	                    newColumn = pieceInfo.get("x").getAsInt();
	                }

	                // Update the position of the piece on the board
	                GridPane.setRowIndex(piece, newRow);
	                GridPane.setColumnIndex(piece, newColumn);
	                
	                // Update the initialPositions map
	                initialPositions.put(piece, new Integer[]{newRow, newColumn});
	            }
	        }
			Alert loaded=new Alert(AlertType.INFORMATION);
			loaded.setTitle("Loading");
			loaded.setHeaderText("Loading");
			loaded.setContentText("Game loaded from saved file");
			loaded.showAndWait();
	    } catch (IOException e) {
	    	Alert err=new Alert(AlertType.INFORMATION);
	    	err.setTitle("Error");
	    	err.setHeaderText("Error");
			err.setContentText("No saved data found");
			err.showAndWait();
	    }
		return numberOfMoves;
	}
	
	public static void loadConfiguration(String jsonFilePath, GridPane board, Map<Rectangle, Integer[]> initialPositions) {
	    try {
	        Gson gson = new Gson();
	        FileReader reader = new FileReader(jsonFilePath);
	        JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);

	        for (Map.Entry<String, JsonElement> entry : jsonObject.getAsJsonObject("initialPositions").entrySet()) {
	            String pieceId = entry.getKey();
	            JsonObject pieceInfo = entry.getValue().getAsJsonObject();

	            Rectangle piece = null;
	            for (Map.Entry<Rectangle, Integer[]> positionEntry : initialPositions.entrySet()) {
	                if (positionEntry.getKey().getId().equals(pieceId)) {
	                    piece = positionEntry.getKey();
	                    break;
	                }
	            }

	            if (piece != null) {
	                int newRow, newColumn;

	                JsonElement rowElement = pieceInfo.get("row");
	                JsonElement columnElement = pieceInfo.get("column");

	                if (rowElement != null && columnElement != null) {
	                    newRow = rowElement.getAsInt();
	                    newColumn = columnElement.getAsInt();
	                } else {
	                    newRow = pieceInfo.get("y").getAsInt();
	                    newColumn = pieceInfo.get("x").getAsInt();
	                }

	                // Update the position of the piece on the board
	                GridPane.setRowIndex(piece, newRow);
	                GridPane.setColumnIndex(piece, newColumn);
	                
	                // Update the initialPositions map
	                initialPositions.put(piece, new Integer[]{newRow, newColumn});

	            }
	        }
    		Alert config=new Alert(AlertType.INFORMATION);
    		config.setTitle("Configuration 1");
    		config.setHeaderText("Configuration loaded");
    		config.setContentText("Configuration loaded from file, good game");
    		config.showAndWait();
	    } catch (IOException e) {
	    	Alert err=new Alert(AlertType.INFORMATION);
	    	err.setTitle("Error");
	    	err.setHeaderText("Error");
			err.setContentText("No configuration data found");
			err.showAndWait();
	        e.printStackTrace();
	    }
	}
	
}