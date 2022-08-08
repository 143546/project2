import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) {
        String[] array = { "1", "2", "3", "4", "5" };
        // String[] -> List<Integer>

        {
            List<Integer> list = new ArrayList<>();
            for (String s : array) {
                Integer i = Integer.parseInt(s);
                list.add(i);
            }
            System.out.println(list);
        }

        {
            Stream<String> stream = Arrays.stream(array);
            Stream<Integer> stream1 = stream.map(new Function<String, Integer>() {
                @Override
                public Integer apply(String s) {
                    return Integer.parseInt(s);
                }
            });    // 针对每个元素去执行 某某方法

            Collector<Integer, ?, List<Integer>> objectListCollector = Collectors.toList();
            List<Integer> collect = stream1.collect(objectListCollector);
        }

        {
            Stream<String> stream = Arrays.stream(array);
            Stream<Integer> stream1 = stream.map(s -> Integer.parseInt(s));    // 针对每个元素去执行 某某方法

            Collector<Integer, ?, List<Integer>> objectListCollector = Collectors.toList();
            List<Integer> collect = stream1.collect(objectListCollector);
        }

        {
            Stream<String> stream = Arrays.stream(array);
            Stream<Integer> stream1 = stream.map(Integer::parseInt);    // 针对每个元素去执行 某某方法

            Collector<Integer, ?, List<Integer>> objectListCollector = Collectors.toList();
            List<Integer> collect = stream1.collect(objectListCollector);
        }

        {
            List<Integer> collect = Arrays.stream(array)
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
        }
    }
}
