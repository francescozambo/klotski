package application;
import javax.script.*;
import java.io.FileReader;

public class KlotskiSolver {

    private ScriptEngine engine;

    public KlotskiSolver() {
        ScriptEngineManager manager = new ScriptEngineManager();
        this.engine = manager.getEngineByName("nashorn");
        if (engine == null) {
            throw new RuntimeException("Nashorn JavaScript engine not found.");
        }
    }

    public Object solveKlotskiPuzzle(String scriptFilePath, Object puzzleConfiguration) {
        try {
            // Load the script from the given file path
            FileReader reader = new FileReader(scriptFilePath);
            engine.eval(reader);

            // Get a reference to the Klotski module
            Object klotskiModule = engine.get("Klotski");

            // Invoke the solve method on the Klotski module
            Invocable invocable = (Invocable) engine;
            return invocable.invokeMethod(klotskiModule, "solve", puzzleConfiguration);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}