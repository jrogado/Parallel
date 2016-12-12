import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.lang.Runtime.getRuntime;

/**
 * Created by Jose Rogado on 11-12-2016.
 * Uses RecursiveAction to perform the quicsort algorithm
 */
public class QuickSortParallel {
    private static int sequentialThreshold;
    private static int[] globalArray;
    private final ForkJoinPool forkJoinPool;

    private QuickSortParallel(int n) {
            
        globalArray = new int[n];
        Random randomNumber = new Random();
        for (int i = 0; i < n; i++) {
            globalArray[i] = randomNumber.nextInt(n);
        }
        System.out.println("Sorting " + globalArray.length + " values");
        // for(int i:globalArray)
        //    System.out.print(i + " ");
        // for(int i = 0; i < 100; i++)
        //    System.out.print(globalArray[i] + " ");
        // System.out.println();
        forkJoinPool = new ForkJoinPool();
    }
    
    public static void main(String[] args) throws InterruptedException {

        int nvalues = 100_000_000;
        long startTime;
        long stopTime;

        // Number of available processors
        int nCores = getRuntime().availableProcessors();
        if (args.length > 0)
            nvalues = Integer.decode(args[0]);

        sequentialThreshold = nvalues / (8 * nCores);
        int depthRemaining = (int) (Math.log(nCores) / Math.log(2)) + 4;

        System.out.println("Number of cores: " + nCores + " Number of values: "
                + nvalues + " Threshold: " + sequentialThreshold + " Depth: " + depthRemaining);
        // Initialization
        QuickSortParallel parallelSort = new QuickSortParallel(nvalues);
//        quickSortParallel qs = new quickSortParallel(0, nvalues);
//        forkJoinPool.invoke(qs);
        // Perform the quicksort
        startTime = System.currentTimeMillis();
        parallelSort.qsort(0, nvalues-1);
        stopTime = System.currentTimeMillis();
        long parallelTime = stopTime - startTime;
        System.out.println("Quicksort took " + parallelTime + " ms");
//        for (int i:globalArray)
//            System.out.print(i + " ");
        for(int i = 0; i < 100; i++)
            System.out.print(globalArray[i] + " ");
        System.out.println();
    }
    private void qsort(int lowerIndex, int higherIndex) {
        forkJoinPool.invoke(new quickSortParallel(lowerIndex, higherIndex));
    }

    class quickSortParallel extends RecursiveAction {
        private int myLower;
        private int myHigher;

        private quickSortParallel(int lowerIndex, int higherIndex) {
            myLower = lowerIndex;
            myHigher = higherIndex;
            // System.out.print(myLower + "-" + myHigher + " ");
        }
        protected void compute() {
            int i = myLower;
            int j = myHigher;
            boolean left = FALSE;
            boolean right = FALSE;
            quickSortParallel rightqSort = null;
            quickSortParallel leftqSort = null;
            // calculate pivot number, I am taking pivot as middle index number
            int pivot = globalArray[i+(j-i)/2];
            // Divide into two arrays
            while (i <= j) {
                /*
                  In each iteration, we will identify a number from left side which
                  is greater then the pivot value, and also we will identify a number
                  from right side which is less then the pivot value. Once the search
                  is done, then we exchange both numbers.
                 */
                while (globalArray[i] < pivot) {
                    i++;
                }
                while (globalArray[j] > pivot) {
                    j--;
                }
                if (i <= j) {
                    exchangeNumbers(i, j);
                    /* move index to next position on both sides */
                    i++;
                    j--;
                }
            }
            // call quickSort() method recursively
            if (myLower < j)
                if ((j-myLower) < sequentialThreshold) {
                    quickSortSerial(myLower, j);
                } else {
                    leftqSort = new quickSortParallel(myLower, j);
                    leftqSort.fork();
                    left = TRUE;
                }
            if (i < myHigher)
                if ((myHigher-i) < sequentialThreshold) {
                    quickSortSerial(i, myHigher);
                } else {
                    rightqSort = new quickSortParallel(i, myHigher);
                    rightqSort.fork();
                    right = TRUE;
                }
            if (left)
                leftqSort.join();
            if(right)
                rightqSort.join();
        }
    }
    private void quickSortSerial(int lowerIndex, int higherIndex) {

        int i = lowerIndex;
        int j = higherIndex;
        // calculate pivot number, I am taking pivot as middle index number
        int pivot = globalArray[lowerIndex+(higherIndex-lowerIndex)/2];
        // Divide into two arrays
        while (i <= j) {
            /*
              In each iteration, we will identify a number from left side which
              is greater then the pivot value, and also we will identify a number
              from right side which is less then the pivot value. Once the search
              is done, then we exchange both numbers.
             */
            while (globalArray[i] < pivot) {
                i++;
            }
            while (globalArray[j] > pivot) {
                j--;
            }
            if (i <= j) {
                exchangeNumbers(i, j);
                /* move index to next position on both sides */
                i++;
                j--;
            }
        }
        // call quickSort() method recursively
        if (lowerIndex < j)
            quickSortSerial(lowerIndex, j);
        if (i < higherIndex)
            quickSortSerial(i, higherIndex);
    }

    private void exchangeNumbers(int i, int j) {
        int temp = globalArray[i];
        globalArray[i] = globalArray[j];
        globalArray[j] = temp;
    }


    }
