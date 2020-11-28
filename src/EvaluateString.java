import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class EvaluateString
{
    // Map the variables
    protected static Map<String,Double> variables = new HashMap<>();

    // Stack for numbers or variables: 'values'
    protected static Stack<String> values = new Stack<String>();

    // Stack for Operators: 'ops'
    protected static Stack<Character> ops = new Stack<Character>();

    // Stacks for handling variables at the end of evaluate
    protected static Stack<String> variablesToChangeAtEnd = new Stack<String>();
    protected static Stack<Character> applyOperatorsAtEnd = new Stack<Character>();

    // Characters in expression
    protected static char[] tokens;

    public static void evaluate(String expression)
    {
        tokens = expression.toCharArray();

        for (int i = 0; i < tokens.length; i++)
        {
            // Current token is a whitespace, skip it
            if (tokens[i] == ' ')
                continue;

            // Current token is a number, push it to stack for numbers
            if (isDigit(tokens[i]))
            {
                i = pushVariableOrNumberToStack(i, '0', '9');
            }

            // Current token is a variable, push it to stack for variables
            if (isVariable(tokens[i])) {
                i = pushVariableOrNumberToStack(i, 'a', 'z');
            }

            // Current token is an opening brace or equal operator, push it to 'ops'
            else if (tokens[i] == '(' || tokens[i] == '=')
                ops.push(tokens[i]);

            // Closing brace encountered, solve entire brace
            else if (tokens[i] == ')')
            {
                while (ops.peek() != '(')
                    values.push(applyOp(ops.pop(),
                            values.pop(),
                            values.pop()));
                ops.pop();
            }

            // Current token is an operator.
            else if (tokens[i] == '+' ||
                    tokens[i] == '-' ||
                    tokens[i] == '*' ||
                    tokens[i] == '/')
            {

                // If we got += OR -= OR /= OR *=
                if (tokens[i+1] == '=') {
                    splitEqualOperator(i);
                }
                // ++ OR --
                else if (tokens[i] == tokens[i+1]) {
                    i = sameOperatorHandling(i);
                    continue;
                }

                applyOperatorIfNeeded(i);

                // Push current token to 'ops'.
                ops.push(tokens[i]);
            }
        }

        // Entire expression has been parsed at this point, apply remaining
        // ops to remaining values
        while (!ops.empty())
            values.push(applyOp(ops.pop(),
                    values.pop(),
                    values.pop()));

        applyVariablesAtEnd();
    }

    private static int sameOperatorHandling(int i) {
        char op = tokens[i];

        // If the operator(++/--) is before the variable
        // Push the operator to ops stack
        // Push the number 1 and the variable to values stack
        if (i+2 < tokens.length && tokens[i+2] != ' ') {
            ops.push(tokens[i]);
            tokens[i+1] = ' ';
            i = pushVariableOrNumberToStack(i+2, 'a', 'z') +1;
            values.push("1");
        }
        // Increment at the end - skip the same operator
        else {
            i=i+2;
        }

        // We should increment/decrement by 1 (at the end)
        // Get the variable
        String variable = isVariable(values.peek().charAt(0)) ?
                values.peek() : values.get(values.size()-2);
        variablesToChangeAtEnd.push(variable);
        applyOperatorsAtEnd.push(op);

        return i;
    }

    private static void splitEqualOperator(int i) {
        ops.push(tokens[i+1]);
        values.push(values.peek());
        tokens[i+1] = ' ';
    }

    /**
     *  While top of 'ops' has same or greater precedence to current
     *  token, which is an operator.
     *  Apply operator on top of 'ops' to top two elements in values stack **/
    private static void applyOperatorIfNeeded(int i) {
        while (!ops.empty() &&
                hasPrecedence(tokens[i],
                        ops.peek()))
            values.push(applyOp(ops.pop(),
                    values.pop(),
                    values.pop()));
    }

    /** Returns true if 'op2' has higher or same precedence as 'op1',
     * otherwise returns false. **/
    private static boolean hasPrecedence(
            char op1, char op2)
    {
        if (op2 == '(' || op2 == ')')
            return false;
        if (((op1 == '*' || op1 == '/') &&
                (op2 == '+' || op2 == '-')) ||
                op2 == '=')
            return false;
        else
            return true;
    }

    /** A utility method to apply an operator 'op' on operands 'a' and 'b'.
     * Return the result. */
    private static String applyOp(char op,
                              String sec, String first)
    {
        Double a = (isDigit(first.charAt(0))) ? Double.parseDouble(first) : variables.get(first);
        Double b = (isDigit(sec.charAt(0))) ? Double.parseDouble(sec) : variables.get(sec);

        Double result = 0.0;

        switch (op)
        {
            case '+':
                result = a + b;
                break;
            case '-':
                result = a - b;
                break;
            case '*':
                result = a * b;
                break;
            case '/':
                if (b == 0)
                    throw new
                            UnsupportedOperationException(
                            "Cannot divide by zero");
                result = a / b;
                break;
            case '=':
                variables.put(first, b);
        }
        return result.toString();
    }

     /** Push variable or number to the corresponding stack.
      *  The function return the correct offset index. */
    private static int pushVariableOrNumberToStack(int i, char from, char to) {
        StringBuffer sbuf = new StringBuffer();

        // There may be more than one digits in number/characters in variable
        while (i < tokens.length &&
                tokens[i] >= from &&
                tokens[i] <= to)
            sbuf.append(tokens[i++]);
        values.push(sbuf.toString());

        // right now the i points to the character next to the digit/characters in variable,
        // since the for loop also increases the i,
        // we would skip one token position;
        // We need to decrease the value of i by 1 to correct the offset.
        i--;

        return i;
    }

    private static boolean isDigit(char ch) {
        if (ch >= '0' && ch <= '9')
            return true;
        return false;
    }

    private static boolean isVariable(char ch) {
        if (ch >= 'a' && ch <= 'z')
            return true;
        return false;
    }

    // Check if we should increment/decrement some variables because ++/-- operator
    private static void applyVariablesAtEnd() {

        while (!variablesToChangeAtEnd.empty()) {
            String keyVariable = variablesToChangeAtEnd.pop();
            variables.put(keyVariable,
                    Double.parseDouble(
                            applyOp(applyOperatorsAtEnd.pop(),
                                    "1",
                                    keyVariable)));
        }
    }
}