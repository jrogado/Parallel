/**
 * Created by Jose Rogado on 05-12-2014.
 */
public class Calculator {
    interface IntegerMath {
        int operation(int a, int b);
    }

    public int Operate(int a, int b, IntegerMath op) {
        return op.operation(a, b);
    }

    public static void main(String... args) {
        int op1 = 1, op2 = 2;
        String Operation = "+";

        if (args.length == 3) {
            op1 = Integer.decode(args[0]);
            op2 = Integer.decode(args[1]);
            Operation = args[2];
        } else {
            System.out.println("Wrong number of arguments: use <op1> <op2> <+,-,*,/>");
            System.exit(0);
        }

        Calculator myApp = new Calculator();
        IntegerMath addition = (a, b) -> a + b;
        IntegerMath subtraction = (a, b) -> a - b;
        IntegerMath multiplication = (a, b) -> a * b;
        IntegerMath division = (a, b) -> a / b;

        if (Operation.equals("add"))
            System.out.println(op1 + " + " + op2 + " = " + myApp.Operate(op1, op2, addition));
        if (Operation.equals("sub"))
            System.out.println(op1 + " - " + op2 + " = " + myApp.Operate(op1, op2, subtraction));
        if (Operation.equals("mul"))
            System.out.println(op1 + " * " + op2 + " = " + myApp.Operate(op1, op2, multiplication));
        if (Operation.equals("div"))
            System.out.println(op1 + " / " + op2 + " = " + myApp.Operate(op1, op2, division));
    }
}

