package application;

import javafx.scene.shape.Rectangle;
/*
	Class used to support the storing position history of every piece
	Used in method movePiece of Engine class

*/
public class MoveInfo {
    Rectangle piece;
    int oldRow;
    int oldColumn;

    public MoveInfo(Rectangle piece, int oldRow, int oldColumn) {
        this.piece = piece;
        this.oldRow = oldRow;
        this.oldColumn = oldColumn;
    }
}