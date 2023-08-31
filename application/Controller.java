package application;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Stack;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

/*
	Controller class to interrogate the javaFX GUI
	Implements move counter, a map to store the initial positions and a stack used to track moves history
*/

public class Controller implements Initializable {
	
	private int moves=0;
	private boolean legal;
	private String configFile="C:\\Programmazione\\eclipse\\KlotskiTest\\KlotskiTest\\src\\application\\config.json";
	private String saveFile="C:\\Programmazione\\eclipse\\KlotskiTest\\KlotskiTest\\board_state.json";
    @FXML
    private AnchorPane anchorPane;
    
    @FXML
    private GridPane board;
    
    private Stage stage;
    
    private Map<Rectangle, Integer[]> initialPositions = new HashMap<>(); // Map to store initial positions
    private Map<Rectangle, Integer[]> currentPositions = new HashMap<>(); //Map to store current positions of every piece in order to be able to save/load the board
    private Stack<MoveInfo> moveHistory = new Stack<>(); //Stack to store moves history
    private Engine result;

	
    private Rectangle selectedPiece;
    
	@FXML
	private Rectangle[] pieces;
	@FXML
	private Rectangle piece1;
	@FXML
	private Rectangle piece2;
	@FXML
	private Rectangle piece3;
	@FXML
	private Rectangle piece4;
	@FXML
	private Rectangle piece5;
	@FXML
	private Rectangle piece6;
	@FXML
	private Rectangle piece7;
	@FXML
	private Rectangle piece8;
	@FXML
	private Rectangle piece9;
	@FXML
	private Rectangle piece10;
	
	@FXML
	private Label MoveCounter;
	
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        anchorPane.addEventHandler(KeyEvent.KEY_PRESSED, keyEventHandler);
        
        pieces = new Rectangle[]{piece1, piece2, piece3, piece4,piece5,piece6,piece7,piece8,piece9,piece10};
      
        // Store initial positions for pieces
        for (Rectangle piece : pieces) {
        	initialPositions.put(piece, new Integer[]{GridPane.getRowIndex(piece), GridPane.getColumnIndex(piece)});
        	Integer[] initialPosition = initialPositions.get(piece);
            currentPositions.put(piece, initialPosition.clone());
        }
        
        // Attach event handlers to the pieces
        for (Rectangle piece : pieces) {
        	piece.setOnMouseClicked(this::pieceClicked);
        }
    }
    

    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
    private void pieceClicked(MouseEvent event) {
    	if (selectedPiece != null) {
            selectedPiece.setStroke(Color.BLACK); // Reset the stroke color of previously selected piece
            
        }

        selectedPiece = (Rectangle) event.getSource();
        selectedPiece.setStroke(Color.YELLOW); // Highlight the selected piece
        System.out.println("pieceClicked");
    }
    
    private final EventHandler<KeyEvent> keyEventHandler = event -> {
        result = Engine.movePiece(event, selectedPiece, board, initialPositions, legal, moveHistory, moves);
        legal = result.isLegal();


        if (legal) {
            moves++;
            MoveCounter.setText("Moves: " + moves);
            
            //store the position of every piece
            for (Rectangle piece : pieces) {
                int pieceRow = GridPane.getRowIndex(piece);
                int pieceColumn = GridPane.getColumnIndex(piece);
                currentPositions.put(piece, new Integer[]{pieceRow, pieceColumn});
            }
        }

        if (selectedPiece.getWidth() == 200 && selectedPiece.getHeight() == 200) {
            Support.isWin(board, selectedPiece);
        }
    };
    
    public void reset(ActionEvent e) {
    	Engine.reset(initialPositions);
		moves=0;
		MoveCounter.setText("Moves: "+moves);
	}
	
	public void undo(ActionEvent e) {
		if(moves>0) {
			Engine.undo(moveHistory);
			moves--;
			MoveCounter.setText("Moves: " + moves);
		}
	}

	public void nbm(ActionEvent e) {
		System.out.println("NBM");
		moves+=1;
		MoveCounter.setText("Moves: "+moves);
	}
	
	public void save(ActionEvent e) {
		Engine.save(board, currentPositions, moves);
		System.out.println("SAVE");
	}
	
	public void load(ActionEvent e) {
		moves=Engine.load(saveFile, board, currentPositions);
		MoveCounter.setText("Moves: "+moves);
		System.out.println("LOAD");
	}
	
	public void exit(ActionEvent e) {
		System.out.println("EXIT");
		stage.close();
	}
	
	public void Config1(ActionEvent e) {
		Engine.loadConfiguration(configFile, board, initialPositions);
		//Engine.clearBoard(board);
		System.out.println("CONFIG1");
	}
	
	public void Config2(ActionEvent e) {
		System.out.println("CONFIG2");
	}
	
	public void Config3(ActionEvent e) {
		System.out.println("CONFIG3");
	}
	
	public void Config4(ActionEvent e) {
		System.out.println("CONFIG4");
	}

}
