package application;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javafx.scene.shape.Rectangle;
import javafx.scene.layout.GridPane;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class BoardForSolver {

	public static void Traduction(GridPane board, Map<Rectangle, Integer[]> currentPositions) {
	    String boardName="klotski game";
		try {
	        // Create a JSON object to represent the board state
	        JsonObject boardState = new JsonObject();
	        boardState.addProperty("name", boardName);

	        // Create a JSON array to store the blocks
	        JsonArray blocks = new JsonArray();
	        
	        Rectangle firstPiece = null;
	        for (Map.Entry<Rectangle, Integer[]> entry : currentPositions.entrySet()) {
	            Rectangle piece = entry.getKey();
	            if (piece.getWidth() == 200 && piece.getHeight() == 200) {
	                firstPiece = piece;
	                break;
	            }
	        }
	        
	        // Ensure the 200x200 rectangle is found
	        if (firstPiece != null) {
	            JsonObject firstPieceInfo = createPieceInfo(firstPiece, currentPositions.get(firstPiece));
	            blocks.add(firstPieceInfo);
	        }
	        
	        for (Map.Entry<Rectangle, Integer[]> entry : currentPositions.entrySet()) {
	            Rectangle piece = entry.getKey();
	            if (piece != firstPiece) {
	                JsonObject pieceInfo = createPieceInfo(piece, entry.getValue());
	                blocks.add(pieceInfo);
	            }
	        }

	        // Add blocks array to the board state
	        boardState.add("blocks", blocks);

	        // Add board size and escape point
	        int numRows = 5;
	        int numCols = 4;
	        JsonArray boardSize = new JsonArray();
	        boardSize.add(numRows);
	        boardSize.add(numCols);
	        boardState.add("boardSize", boardSize);

	        JsonObject escapePointInfo = new JsonObject();
	        escapePointInfo.add("position", createEscapePoint(3,1));
	        boardState.add("escapePoint", escapePointInfo);

	        Gson gson = new GsonBuilder().create();

	        // Define the file name and extension
	        String fileName = "solver.json";

	        FileWriter writer = new FileWriter(fileName);
	        gson.toJson(boardState, writer);
	        writer.close();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}

	private static JsonObject createPieceInfo(Rectangle piece, Integer[] position) {
	    JsonObject pieceInfo = new JsonObject();
	    pieceInfo.add("shape", createShapeArray(piece));
	    pieceInfo.add("position", createPositionArray(piece));
	    pieceInfo.add("directions", createDirectionsArray(piece));
	    return pieceInfo;
	}

	private static JsonArray createShapeArray(Rectangle piece) {
	    JsonArray shape = new JsonArray();
	    shape.add((int) piece.getWidth());
	    shape.add((int) piece.getHeight());
	    return shape;
	}

	private static JsonArray createEscapePoint(int row, int column) {
	    JsonArray position = new JsonArray();
	    position.add(row);
	    position.add(column);
	    return position;
	}

	private static JsonArray createPositionArray(Rectangle piece) {
	    JsonArray position = new JsonArray();
	    position.add((int) GridPane.getRowIndex(piece));
	    position.add((int) GridPane.getColumnIndex(piece));
	    
	    return position;
	}

	private static JsonArray createDirectionsArray(Rectangle piece) {
	    JsonArray directions = new JsonArray();
	    directions.add(piece.getRotate() == 90 || piece.getRotate() == 270 ? 1 : 0); // Vertical
	    directions.add(piece.getRotate() == 180 || piece.getRotate() == 270 ? 1 : 0); // Horizontal
	    return directions;
	}

}
