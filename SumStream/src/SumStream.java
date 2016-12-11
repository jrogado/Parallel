/**
 * Created by Jose Rogado on 08-12-2014.
 */

import java.util.Random;
import java.util.*;
import java.text.*;
import java.util.stream.*;

public class SumStream {
    static double[] GlobalArray;
    private int n;

    public SumStream(int n)
    {
        this.n = n;
        GlobalArray = new double[n];
        Random randomNumber = new Random();
        for (int i = 0; i < n; i++)
        {
            GlobalArray[i] = (double)randomNumber.nextInt(n);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        int nValues = 10_000_000;
        double total_sum = 0;
        long startTime;
        long stopTime;

        if (args.length > 0)
            nValues = Integer.decode(args[0]);

        // Initialize global Array
        new SumStream(nValues);

        System.out.println("Performing the sum of: " + nValues + " values");

        // Serial sum
        startTime = System.currentTimeMillis();
        for (int i = 0; i < nValues; i++)
        {
            total_sum += Math.tan(GlobalArray[i]);
        }
        stopTime = System.currentTimeMillis();
        long serialTime = stopTime - startTime;
        System.out.println("Serial Total Sum = " + total_sum + " took " + serialTime + "ms");
        DecimalFormat df = new DecimalFormat("#.##");

/*        startTime = System.currentTimeMillis();
        DoubleStream ArrayStream = Arrays.stream(GlobalArray);
        // total_sum = ArrayStream.sum();
        total_sum = ArrayStream.reduce(0, (a, b) -> (a + Math.tan(b)));
        stopTime = System.currentTimeMillis();
        long streamTime = stopTime - startTime;
        System.out.println("Stream Total Sum = " + total_sum + " took " + streamTime + "ms");
        System.out.println("Performance Improvement = " + df.format((float)serialTime / (float)streamTime));
*/
        startTime = System.currentTimeMillis();
        DoubleStream ArrayParallelStream = Arrays.stream(GlobalArray);
        //total_sum = ArrayParallelStream.parallel().sum();
        //total_sum = ArrayParallelStream.parallel().reduce(0, (a, b) -> (a + Math.tan(b)));
        total_sum = ArrayParallelStream.parallel().map(a -> Math.tan(a)).sum();

        stopTime = System.currentTimeMillis();
        long parallelStreamTime = stopTime - startTime;
        System.out.println("ParallelStream Total Sum = " + total_sum + " took " + parallelStreamTime + "ms");
        System.out.println("Performance Improvement = " + df.format((float)serialTime / (float)parallelStreamTime));

    }
}
