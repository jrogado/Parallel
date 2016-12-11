import java.util.Random;

/**
 * Created by Jose Rogado on 22-11-2016.
 */
public class RecursiveMin {
    private int array[];
    private int length;

    public int min(int[] inputArr) {

        if (inputArr == null || inputArr.length == 0) {
            return 0;
        }
        length = inputArr.length;
        return RecursiveMin(inputArr, 0, length);
    }

    private int RecursiveMin(int[] Array, int index, int arrayLength) {
        int min, rmin, lmin;
        if (arrayLength == 1) {
            min = Array[index];
        } else {
            lmin = RecursiveMin(Array, index, arrayLength/2);
            rmin = RecursiveMin(Array, index+arrayLength/2, arrayLength-arrayLength/2);
            if (lmin < rmin)
                min = lmin;
            else
                min = rmin;
        }
        return min;
    }
    public static void main(String a[]){

        int nValues = 40;
        int[] globalArray = new int[nValues];

        Random randomNumber = new Random();
        for (int i = 0; i < nValues; i++)
        {
            globalArray[i] = randomNumber.nextInt(nValues);
        }
        RecursiveMin operator = new RecursiveMin();

        // int[] globalArray = {24,100,45,20,56,75,2,56,99,53,12,24,0,45,20,56,75,2,56,99,53,12,34,-1};
        System.out.println("Calculating the minimum of " + globalArray.length + " values");
        for(int i:globalArray){
            System.out.print(i + " ");
        }
        System.out.println();
        System.out.println("The minimum is " + operator.min(globalArray));
    }
}