/*
  Created by Jose Rogado on 28-11-2016.
  Uses RecursiveTask to recursively calculate the minimum of N values
 */

import java.text.DecimalFormat;
import java.util.Random;
import java.util.concurrent.*;
import static java.lang.Runtime.*;

public class RecursiveMinParallel {
    private static int sequentialThreshold;
    private static int[] globalArray;
    private final ForkJoinPool pool;

    private RecursiveMinParallel(int n) {
        globalArray = new int[n];
        Random randomNumber = new Random();
        for (int i = 0; i < n; i++) {
            globalArray[i] = randomNumber.nextInt(n);
        }
        System.out.println("Calculating the minimum of " + globalArray.length + " values");
        pool = new ForkJoinPool();
    }
    public static void main(String[] args) throws InterruptedException {

        int nvalues = 100_000_000;
        int final_min;
        long startTime;
        long stopTime;

        // Number of available processors
        int nCores = getRuntime().availableProcessors();
        if (args.length > 0)
            nvalues = Integer.decode(args[0]);

        sequentialThreshold = nvalues/(8*nCores);
        int depthRemaining = (int) (Math.log(nCores)/Math.log(2)) + 4;

        System.out.println("Number of cores: " + nCores + " Number of values: "
                                   + nvalues + " Threshold: " + sequentialThreshold + " Depth: " + depthRemaining);
        // Initialization
        RecursiveMinParallel ParallelMin = new RecursiveMinParallel(nvalues);
        // Serial minimum
        startTime = System.currentTimeMillis();
        // Calculate minimum here
        final_min = RecursiveMinSerial(globalArray, 0, nvalues);
        stopTime = System.currentTimeMillis();
        long serialTime = stopTime - startTime;
        System.out.println("Serial Minimum = " + final_min + " took " + serialTime + " ms");
        startTime = System.currentTimeMillis();
        final_min = ParallelMin.processInParallel(globalArray, 0, nvalues);
        stopTime = System.currentTimeMillis();
        long parallelTime = stopTime - startTime;
        System.out.println("Parallel Min = " + final_min + " took " + parallelTime + " ms");
        //float ratio = (float)serialTime / (float)parallelTime;
        DecimalFormat df = new DecimalFormat("#.##");
        System.out.println("Performance Improvement = " + df.format((float) serialTime / (float) parallelTime));

    }
    private int processInParallel(int[] Array, int index, int arrayLength) {
        RecursiveMin rm = new RecursiveMin(Array, index, arrayLength);
        return pool.invoke(rm);
    }
    class RecursiveMin extends RecursiveTask<Integer> {
        private int myIndex;
        private int myLength;
        int[] myArray;

        RecursiveMin(int[] GlobalArray, int index, int length) {
            myArray = GlobalArray;
            myIndex = index;
            myLength = length;
        }
        protected Integer compute() {
            int min;
            if (myLength < sequentialThreshold){
                // Calculate min serially
                min = RecursiveMinSerial(myArray, myIndex, myLength);
            } else {
                RecursiveMin leftMin = new RecursiveMin(myArray, myIndex, myLength/2);
                RecursiveMin rightMin = new RecursiveMin(myArray, myIndex+myLength/2, myLength-myLength/2);
                leftMin.fork();
                int lmin = rightMin.compute();
                int rmin = leftMin.join();
                if (lmin < rmin)
                    min = lmin;
                else
                    min = rmin;
            }
            return min;
        }
    }
    private static int RecursiveMinSerial(int[] Array, int index, int arrayLength) {
        int min, rmin, lmin;
        if (arrayLength == 1) {
            min = Array[index];
        } else {
            lmin = RecursiveMinSerial(Array, index, arrayLength/2);
            rmin = RecursiveMinSerial(Array, index+arrayLength/2, arrayLength-arrayLength/2);
            if (lmin < rmin)
                min = lmin;
            else
                min = rmin;
        }
        return min;
    }
}
