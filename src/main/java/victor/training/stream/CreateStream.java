package victor.training.stream;

import java.util.UUID;
import java.util.stream.Stream;

public class CreateStream {
    public static void main(String[] args) {
    // 1. way to create a Stream: of
//        Stream.of("a", "b", "c").forEach(System.out::println);
    // 2. way to create a Stream: generate
//    Stream.generate(() -> UUID.randomUUID().toString())
//        .limit(10)
//        .forEach(System.out::println);
//    }

    // 3. way to create a Stream: iterate
    Stream.iterate(1, i -> i + 1)
        .limit(10)
        .forEach(System.out::println);
    }
}
