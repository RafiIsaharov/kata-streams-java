package victor.training.stream;

import java.util.List;
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

        Stream.iterate(
                fetchPage(0), // first call
                        page -> page.pageNumber() < page.totalPages(), // do I have more pages?
                        page -> fetchPage(page.pageNumber() + 1)// fetch the next page
                )
                .flatMap(page -> page.rows().stream())
                .forEach(System.out::println);// and then process the rows to a file or DB...(no to collect in memory want to stream through the rows)
    }

     private static Page fetchPage(int pageNumber) {
        // fetch page from the server
         return new Page(List.of("row1","row2"), pageNumber, 3);
    }

    record Page(List<String> rows, int pageNumber, int totalPages) {}
}
