package victor.training.stream;

import victor.training.stream.support.Order;

@FunctionalInterface
public interface MyPredicate {
    boolean test(Order order);
}
