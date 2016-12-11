/**
 * This program uses the Java thread programming model
 * to compute the sum of the square roots of N numbers in parallel
 * and calculates the performance improvement against the serial implementation
 *
 * Created by Jose Rogado on 21-11-2014.
 */

import java.lang.Math;
import java.text.*;
import java.util.Random;

public class Sum {

    static double[] GlobalArray;

    Sum (int n)
    {
        GlobalArray = new double[n];
        Random randomNumber = new Random();
        for (int i = 0; i < n; i++)
        {
            GlobalArray[i] = (double)randomNumber.nextInt(n);
        }
    }

    public static void main(String[] args) throws InterruptedException {

        int i, nThreads, nValues = 10_000_000;
        double total_sum = 0.0;
        long startTime;
        long stopTime;

        // Number of available processors
        int ncores = Runtime.getRuntime().availableProcessors();
        System.out.println("Number of cores: " + ncores);

        if (args.length > 0)
            nValues = Integer.decode(args[0]);

        if (args.length > 1)
            nThreads = Integer.decode(args[1]);
        else
            nThreads = ncores;

        System.out.println("Threads: " + nThreads + " Values: " + nValues);

        // Initialize global Array
        new Sum(nValues);

        // Serial sum
        startTime = System.currentTimeMillis();
        for (i = 0; i < nValues; i++)
        {
            total_sum += Math.tan(GlobalArray[i]);
        }
        stopTime = System.currentTimeMillis();
        long serialTime = stopTime - startTime;
        System.out.println("Serial Total Sum = " + total_sum + " took " + serialTime + "ms");

        // Initialize worker structures
        PartialSum worker[] = new PartialSum[nThreads];
        Thread thread[] = new Thread[nThreads];
        int slot = nValues / nThreads;

        // Create worker threads
        startTime = System.currentTimeMillis();
        for (i = 0; i < nThreads; i++)
        {
            int begin = slot * i;
            int end = slot * (i +1);
            worker[i] = new PartialSum(begin, end, GlobalArray);
            thread[i] = new Thread(worker[i]);
            thread[i].start();
        }
        // Join worker threads and collect partial results
        total_sum = 0.0;
        for (i = 0; i < nThreads; i++)
        {
            thread[i].join();
            total_sum += worker[i].getPartialSum();
        }
        stopTime = System.currentTimeMillis();
        long parallelTime = stopTime - startTime;
        System.out.println("Parallel Total Sum = " + total_sum + " took " + parallelTime + "ms");
        //float ratio = (float)serialTime / (float)parallelTime;
        DecimalFormat df = new DecimalFormat("#.##");
        System.out.println("Performance Improvement = " + df.format((float)serialTime / (float)parallelTime));
    }
}
/* This class has three methods:
 * 1 - worker context initialization
 * 2 - worker thread execution
 * 3 - result collection
 */
class PartialSum implements Runnable {

    double mySum;
    int my_begin, my_end;
    double[] myArray;

    public PartialSum(int begin, int end, double[] GlobalArray) {
        mySum = 0.0;
        my_begin = begin;
        my_end = end;
        myArray = GlobalArray;
//		System.out.println("begin: " + my_begin + " end: " + my_end);
    }

    //	@Override
    public void run() {
        for (int i = my_begin; i < my_end; i++)
        {
            mySum += Math.tan(myArray[i]);
        }

    }
    public double getPartialSum () {
        return mySum;
    }

}

