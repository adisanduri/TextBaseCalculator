import java.util.ArrayList;

public class App {

    // Driver method to test above methods
    public static void main(String[] args)
    {
        ArrayList<String> evaluations = new ArrayList<String>();

        // Basic operators and --
        evaluations.add("j = 4+1");;
        evaluations.add("j += 6");
        evaluations.add("i = j-- + 2 * 4");

        // *=
        evaluations.add("x = i + j");
        evaluations.add("x *= i");

        // ++ and braces
        evaluations.add("y = ++x");
        evaluations.add("y += (2 + 3) * 4");
        evaluations.add("y /= 10");

        // Check double value
        evaluations.add("y = ++x");
        evaluations.add("y += (2 + 3) * 4");
        evaluations.add("y /= 10");

        // Check minus result -> error
        evaluations.add("t = 10 - i");

        evaluations.forEach(currEvaluation -> EvaluateString.evaluate(currEvaluation));

        EvaluateString.variables.forEach((k,v) -> {
            System.out.println(k +" : " + v);
        });
    }
}
