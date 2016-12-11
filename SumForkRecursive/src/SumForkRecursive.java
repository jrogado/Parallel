/**
 * Created by Jose Rogado on 22-11-2014.
 * Uses RecursiveTask to recursively calculate the sum of N values
 */

import java.lang.Math;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.*;
import static java.lang.Runtime.*;

public class SumForkRecursive {
    private static double[] GlobalArray;
    private final ForkJoinPool forkJoinPool;
    private final int SEQUENTIAL_THRESHOLD;


    private SumForkRecursive(int n, int cores) {
        GlobalArray = new double[n];
        Random randomNumber = new Random();
        for (int i = 0; i < n; i++) {
            GlobalArray[i] = (double) randomNumber.nextInt(n);
        }
        forkJoinPool = new ForkJoinPool();
        SEQUENTIAL_THRESHOLD = n/(4*cores);
        // SEQUENTIAL_THRESHOLD = 5000000;
    }

    public static void main(String[] args) throws InterruptedException {

        int i, nvalues = 10_000_000;
        double total_sum = 0.0;
        long startTime;
        long stopTime;

        // Number of available processors
        int ncores = getRuntime().availableProcessors();

        if (args.length > 0)
            nvalues = Integer.decode(args[0]);

        System.out.println("Number of cores: " + ncores + " Number of values: " + nvalues);

        // Initialization
        SumForkRecursive ParallelAdder = new SumForkRecursive(nvalues, ncores);
        // Serial sum
        startTime = System.currentTimeMillis();
        for (i = 0; i < nvalues; i++) {
            total_sum += Math.tan(GlobalArray[i]);
        }
        stopTime = System.currentTimeMillis();
        long serialTime = stopTime - startTime;
        System.out.println("Serial Total Sum = " + total_sum + " took " + serialTime + " ms");
        // Parallel Sum
        startTime = System.currentTimeMillis();
        total_sum = ParallelAdder.sumInParallel(GlobalArray, 0, nvalues);
        stopTime = System.currentTimeMillis();
        long parallelTime = stopTime - startTime;
        System.out.println("Parallel Total Sum = " + total_sum + " took " + parallelTime + " ms");
        //float ratio = (float)serialTime / (float)parallelTime;
        DecimalFormat df = new DecimalFormat("#.##");
        System.out.println("Performance Improvement = " + df.format((float) serialTime / (float) parallelTime));
    }

    Double sumInParallel(double[] Array, int low, int hi) {
        return forkJoinPool.invoke(new Sum(Array, low, hi));
    }

    class Sum extends RecursiveTask<Double> {
        private int low;
        private int high;
        double[] Array;

        Sum(double[] GlobalArray, int start, int end) {
            Array = GlobalArray;
            low = start;
            high = end;
            // System.out.println("begin: " + low + " end: " + high);
        }

        protected Double compute() {
            if (high - low <= SEQUENTIAL_THRESHOLD) {
                Double sum = 0.0;
                for (int i = low; i < high; ++i)
                    sum += Math.tan(Array[i]);
                return sum;
            } else {
                int mid = low + (high - low) / 2;
                Sum left = new Sum(Array, low, mid);
                Sum right = new Sum(Array, mid, high);
                left.fork();
                Double rightAns = right.compute();
                Double leftAns = left.join();
                return leftAns + rightAns;
            }
        }
    }
}
