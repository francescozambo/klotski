package application;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import javafx.scene.shape.Rectangle;

import java.io.IOException;

public class RectangleTypeAdapter extends TypeAdapter<Rectangle> {
    @Override
    public void write(JsonWriter out, Rectangle value) throws IOException {
        out.beginObject();
        out.name("x").value(value.getX());
        out.name("y").value(value.getY());
        out.name("width").value(value.getWidth());
        out.name("height").value(value.getHeight());
        out.endObject();
    }

    @Override
    public Rectangle read(JsonReader in) throws IOException {
        double x = 0;
        double y = 0;
        double width = 0;
        double height = 0;

        in.beginObject();
        while (in.hasNext()) {
            String name = in.nextName();
            switch (name) {
                case "x":
                    x = in.nextDouble();
                    break;
                case "y":
                    y = in.nextDouble();
                    break;
                case "width":
                    width = in.nextDouble();
                    break;
                case "height":
                    height = in.nextDouble();
                    break;
                default:
                    in.skipValue(); // Ignore unknown fields
                    break;
            }
        }
        in.endObject();

        return new Rectangle(x, y, width, height);
    }

    /*private Rectangle createPiece(double x, double y, double width, double height) {
        Rectangle piece = new Rectangle(width, height);
        piece.setX(x);
        piece.setY(y);
        return piece;
    }
    */
}
