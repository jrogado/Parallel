import java.util.Random;

/**
 * Created by Jose Rogado on 21-11-2014.
 * Quicksort serial version
 */
public class QuickSort {

    private int array[];
    private int length;

    public void sort(int[] inputArr) {

        if (inputArr == null || inputArr.length == 0) {
            return;
        }
        this.array = inputArr;
        length = inputArr.length;
        quickSort(0, length - 1);
    }

    private void quickSort(int lowerIndex, int higherIndex) {

        int i = lowerIndex;
        int j = higherIndex;
        // calculate pivot number, I am taking pivot as middle index number
        int pivot = array[lowerIndex+(higherIndex-lowerIndex)/2];
        // Divide into two arrays
        while (i <= j) {
            /**
             * In each iteration, we will identify a number from left side which
             * is greater then the pivot value, and also we will identify a number
             * from right side which is less then the pivot value. Once the search
             * is done, then we exchange both numbers.
             */
            while (array[i] < pivot) {
                i++;
            }
            while (array[j] > pivot) {
                j--;
            }
            if (i <= j) {
                exchangeNumbers(i, j);
                //move index to next position on both sides
                i++;
                j--;
            }
        }
        // call quickSort() method recursively
        if (lowerIndex < j)
            quickSort(lowerIndex, j);
        if (i < higherIndex)
            quickSort(i, higherIndex);
    }

    private void exchangeNumbers(int i, int j) {
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    public static void main(String a[]){
        int nValues = 100_000_000;
        int[] globalArray = new int[nValues];
        long startTime;
        long stopTime;

        Random randomNumber = new Random();
        for (int i = 0; i < nValues; i++) {
            globalArray[i] = randomNumber.nextInt(nValues);
        }
        QuickSort sorter = new QuickSort();
        // int[] input = {24,2,45,20,56,75,2,56,99,53,12,24,2,45,20,56,75,2,56,99,53,12};
        System.out.println("Sorting " + globalArray.length + " values");
        startTime = System.currentTimeMillis();
        sorter.sort(globalArray);
        stopTime = System.currentTimeMillis();
        long parallelTime = stopTime - startTime;
        System.out.println("Quicksort took " + parallelTime + " ms");
        for(int i = 0; i < 100; i++)
            System.out.print(globalArray[i] + " ");
        System.out.println();

    }
}
