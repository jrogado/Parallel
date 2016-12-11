/**
 * Created by Jose Rogado on 22-11-2014.
 * Uses RecursiveTask and fork/join to calculate the sum of N values
 */

import java.lang.Math;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.*;

public class SumForkJoin {
    static double[] GlobalArray;

    SumForkJoin (int n)
    {
        GlobalArray = new double[n];
        Random randomNumber = new Random();
        for (int i = 0; i < n; i++)
        {
            GlobalArray[i] = (double)randomNumber.nextInt(n);
        }
    }

    private final ForkJoinPool forkJoinPool = new ForkJoinPool();

    public static void main(String[] args) throws InterruptedException {

        int i, nthreads, nvalues = 10_000_000;
        double total_sum = 0.0;
        long startTime;
        long stopTime;

        // Number of available processors
        int ncores = Runtime.getRuntime().availableProcessors();
        System.out.println("Number of cores: " + ncores);

        if (args.length > 0)
            nvalues = Integer.decode(args[0]);

        if (args.length > 1)
            nthreads = Integer.decode(args[1]);
        else
            nthreads = ncores;

        System.out.println("nthreads: " + nthreads + " nvalues: " + nvalues);

        // Initialization
        SumForkJoin ParallelAdder = new SumForkJoin(nvalues);

        // Serial sum
        startTime = System.currentTimeMillis();
        for (i = 0; i < nvalues; i++)
        {
            total_sum += Math.tan(GlobalArray[i]);
        }
        stopTime = System.currentTimeMillis();
        long serialTime = stopTime - startTime;
        System.out.println("Serial Total Sum = " + total_sum + " took " + serialTime + "ms");


        // Parallel Sum
        startTime = System.currentTimeMillis();
        total_sum = ParallelAdder.sumInParallel(nthreads, nvalues, GlobalArray);

        stopTime = System.currentTimeMillis();
        long parallelTime = stopTime - startTime;
        System.out.println("Parallel Total Sum = " + total_sum + " took " + parallelTime + "ms");
        //float ratio = (float)serialTime / (float)parallelTime;
        DecimalFormat df = new DecimalFormat("#.##");
        System.out.println("Performance Improvement = " + df.format((float)serialTime / (float)parallelTime));

    }

    Double sumInParallel(int nthreads, int nvalues, double[] Array) {
        return forkJoinPool.invoke(new Sum(nthreads, nvalues, Array));
    }

    class Sum extends RecursiveTask<Double> {
        private static final long serialVersionUID = -6223554471206651320L;
        private int ntasks;
        private int nvalues;
        double [] Array;

        Sum(int nthreads, int values, double[] GlobalArray) {
            ntasks = nthreads;
            nvalues = values;
            Array = GlobalArray;
        }

        protected Double compute() {

            LinkedList<RecursiveTask<Double>> forks = new LinkedList<RecursiveTask<Double>>();
            Double sum = 0.0;
            int slot = nvalues / ntasks;
            for (int i = 0; i < ntasks; i++)
            {
                int begin = slot * i;
                int end = slot * (i + 1);
                PartialSum task = new PartialSum(begin, end, GlobalArray);
                forks.add(task);
                task.fork();
            }
            for (RecursiveTask<Double> task : forks) {
                sum = sum + task.join();
            }
            return sum;
        }
    }

    class PartialSum extends RecursiveTask<Double>  {

        private static final long serialVersionUID = -1851396442173430909L;
        private double mySum;
        private int my_begin, my_end;
        private double[] myArray;

        public PartialSum(int begin, int end, double[] GlobalArray) {
            mySum = 0.0;
            my_begin = begin;
            my_end = end;
            myArray = GlobalArray;
            //		System.out.println("begin: " + my_begin + " end: " + my_end);
        }

        protected Double compute() {
            for (int i = my_begin; i < my_end; i++)
            {
                mySum += Math.tan(myArray[i]);
            }
            return mySum;
        }
    }
}
