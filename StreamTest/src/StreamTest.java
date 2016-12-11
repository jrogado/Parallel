/**
 * Created by Jose Rogado on 05-12-2014.
 */
import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.*;

public class StreamTest {

    public static void main(String... args) {
        List<String> myList =
                Arrays.asList("a1", "a2", "b1", "c2", "c1", "d1", "e1", "ca");

        myList.forEach(System.out::println);

        System.out.println();

        myList.stream()
                .filter(s -> s.startsWith("c"))
                .map(s -> s.toUpperCase()) //.map(String::toUpperCase)
                .sorted()
                .forEach(System.out::println);//.forEach(x -> System.out.println(x));

        System.out.println();

        List<String> words = Arrays.asList("Oracle", "Java", "Magazine", "Jose", "Manuel", "Conceicao");
        List<Integer> wordLengths = words.stream()
                                        .map(String::length)
                                        .collect(Collectors.toList());
        wordLengths.forEach(System.out::println);
        System.out.println();
        System.out.println(wordLengths);

        System.out.println();

        IntStream oddNumbers =
                IntStream.rangeClosed(10, 30)
                        .filter(n -> n % 2 == 1);

       for (int i : oddNumbers.toArray())
            System.out.printf("%d ", i);

        System.out.println();

        int sumNumbers =
                IntStream.rangeClosed(0, 30).sum();

        System.out.println();

        System.out.printf("%d ", sumNumbers);

        System.out.println();

        IntStream.range(0, 10)
                .forEach(System.out::print);

        System.out.println();

/*        long a = IntStream.range(0, 10).mapToLong(x -> {
            for (int i = 0; i < 1; i++) {
                System.out.println("X:" + i);
            }
            return x;
        }).sum();*/

        long a = IntStream.range(0, 10).mapToLong(x -> (long) x).sum();

        System.out.println(a);

        System.out.println(IntStream.range(0, 20).sum());

        System.out.println(countSimple1());

        System.out.println(countSimple2());
    }

    private static int countSimple1()
    {
        String myList[] = {"a1", "a2", "b1", "c2", "c1", "d1", "e1", "ca"};
        int count = 0;
        for (String s: myList) {
            count++;
        }
        return count;
    }

    private static int countSimple2()
    {
        String myList[] = {"a1", "a2", "b1", "c2", "c1", "d1", "e1", "ca"};
        return (int) Arrays.stream(myList).count();
    }

}
