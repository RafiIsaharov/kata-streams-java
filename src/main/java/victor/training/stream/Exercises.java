package victor.training.stream;

import victor.training.stream.support.*;
import victor.training.stream.support.Order.PaymentMethod;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Month;
import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static victor.training.stream.support.Order.*;

public class Exercises {
  //private final OrderMapper orderMapper = new OrderMapper();// in reality this would be injected

  public List<OrderDto> p1_activeOrders(List<Order> orders) {
    // TODO 1: simplify
    // TODO 2: use the OrderDto constructor
    // TODO 3: use the OrderMapper.toDto method
//    List<OrderDto> dtos = new ArrayList<>();
//    for (Order order : orders) {
//      if (order.isCompleted()) {
//        OrderDto dto = new OrderDto(
//            order.total(),
//            order.createdOn(),
//            order.paymentMethod(),
//            order.status());
//        dtos.add(dto);
//      }
//    }
//  Predicate<Order> isCompleted = new Predicate<Order>() {
//      @Override
//      public boolean test(Order order) {
//          return order.isCompleted();
//      }
//  };

    //1) ref to an instance method that I don't have yet, but i will have
    Predicate<Order> isCompleted1 = (Order order) -> {return order.isCompleted();};// boilerplate code
    Predicate<Order> isCompleted2 = (Order order) -> order.isCompleted(); // boilerplate code
    Predicate<Order> isCompleted3 = order -> order.isCompleted(); // lambda syntax
    Predicate<Order> isCompleted4 = Order::isCompleted;  //Syntax sugar
    Function<Order, Boolean> isCompleted5 = Order::isCompleted; //Syntax sugar
    MyPredicate isCompleted6 = Order::isCompleted; //Syntax sugar
//    Object o = Order::isCompleted; doesn't compile
    Object o = (MyPredicate) Order::isCompleted; //Syntax sugar
    Function<Order, OrderDto> toDto1 = OrderMapper::toDto;
    // Function<Order, OrderDto> toDto2 = orderMapper::toDto;

// 2) ref to an instance method (println) from the instance that I HAVE already (System.out)
    //ref to an instance method of an arbitrary object of a particular type
    //Note! here the order to print is not yet available
    //Note: the method println is void, so it doesn't return anything
    Consumer<Order> print1 = order -> System.out.println(order);
    Consumer<String> print3 = order -> System.out.println(order);//overloaded method
    //missing the argument(order) in future call i'm going to receive an order to print
    Consumer<Order> print2 = System.out::println;

    Consumer<Order> total = order -> order.setTotal(0); //takes an Order, returns (anything back) void
//    Consumer<Order> total2 = Order::setTotal(0); //takes an Order, returns (anything back) void
  long l= System.currentTimeMillis();
  //3) ref to a static method
    Supplier<Long> currentTime = () -> System.currentTimeMillis(); // takes nothing, returns a Long
    Supplier<Long> currentTime2 = System::currentTimeMillis; // takes nothing, returns a Long
  //4) ref to a constructor
    Supplier<Order> orderSupplier = () -> new Order(); // takes nothing, returns an Order // like a factory
    Supplier<Order> orderSupplier1 = Order::new; //syntax sugar
    Function<Integer, Order> orderSupplier2 = Order::new; //syntax sugar
    BiFunction<Integer, Status, Order> orderSupplier3 = Order::new; //syntax sugar

    /////////////////////////////
//    Function<Order, OrderDto> no = Exercises::toDto; //java compiler won't know on which object to call toDto function
    // to call it later, I will have to provide an instance of  Exercises
    BiFunction<Exercises/*the instance that will call the method*/,
            Order/*parameter to pass*/,
            OrderDto /*return obj*/> aRefToAnInstanceMethodWithoutSpecifyingTheInstance = Exercises::toDto;
    OrderDto dto = aRefToAnInstanceMethodWithoutSpecifyingTheInstance.apply(this, new Order());

//    return orders.stream().filter(isCompleted4).map(toDto).toList();
    // grab a ref to an instance method from a Type => I will need the instance to call the method
    // in other words from the  type Order grab the method isCompleted
    return orders.stream().filter(Order::isCompleted).map(OrderMapper::toDto).toList();

  }

  private OrderDto toDto(Order order) {
    return new OrderDto(order.total(), order.createdOn(), order.paymentMethod(), order.status());
  }

  public Order p2_findOrderById(List<Order> orders, int orderId) {
    // TODO 1: rewrite with streams
    // TODO 2: return Optional<> and fix the tests
    for (Order order : orders) {
      if (order.id() == orderId) {
        return order;
      }
    }
    return null;
  }

  // TODO all the following: rewrite with streams
  public boolean p3_hasActiveOrders(List<Order> orders) {
    for (Order order : orders) {
      if (order.isCompleted()) {
        return true;
      }
    }
    return false;
  }

  /**
   * @return order with the max total() that does NOT contain a special offer line
   */
  public Order p4_maxPriceOrder(List<Order> orders) {
    Order maxOrder = null;
    for (Order order : orders) {
      boolean hasSpecialOffer = false;
      for (OrderLine orderLine : order.orderLines()) {
        if (orderLine.isSpecialOffer()) {
          hasSpecialOffer = true;
          break;
        }
      }
      if (hasSpecialOffer) {
        continue;
      }
      if (maxOrder == null || order.total() > maxOrder.total()) {
        maxOrder = order;
      }
    }
    return maxOrder;
  }

  /**
   * @return last 3 returnReason()s sorted descending by Order.createdOn
   */
  public List<String> p5_last3Orders(List<Order> orders) {
    List<Order> copy = new ArrayList<>(orders);
    copy.sort(new LatestOrderComparator());
    List<String> returnReasons = new ArrayList<>();
    for (Order order : copy) {
      if (order.returnReason().isPresent()) {
        returnReasons.add(order.returnReason().get());
        if (returnReasons.size() == 3) {
          break;
        }
      }
    }
    return returnReasons;
    // Hint: Optional#stream()
  }

  /**
   * @return sum of all Order.total(), truncated to int.
   */
  public int p6_completedTotalSum(List<Order> orders) {
    double sum = 0;
    for (Order order : orders) {
      if (order.isCompleted())
        sum += order.total();
    }
    return (int) sum;
  }

  /**
   * @return the products bought by the customer, with no duplicates, sorted by Product.name
   */
  public List<Product> p7_productsSorted(List<Order> orders) { // TODO simplify
    Set<Product> products = new HashSet<>();
    for (Order order : orders) {
      for (OrderLine line : order.orderLines()) {
        products.add(line.product());
      }
    }
    List<Product> sorted = new ArrayList<>(products);
    sorted.sort((o1, o2) -> o1.name().compareTo(o2.name()));
    return sorted;
  }

  /**
   * see tests for an example
   */
  public Map<PaymentMethod, List<Order>> p8_ordersGroupedByPaymentMethod(List<Order> orders) {
    Map<PaymentMethod, List<Order>> map = new HashMap<>();
    for (Order order : orders) {
      List<Order> list = map.get(order.paymentMethod());
      if (list == null) {
        list = new ArrayList<>();
        map.put(order.paymentMethod(), list);
      }
      list.add(order);
    }
    return map;
  }

  /**
   * @return the total number of products purchased across all orders (see test)
   */
  public Map<Product, Integer> p9_productCount(List<Order> orders) {
    List<OrderLine> allLines = new ArrayList<>();
    for (Order order : orders) {
      allLines.addAll(order.orderLines());
    }
    Map<Product, Integer> result = new HashMap<>();
    for (OrderLine line : allLines) {
      int old;
      if (!result.containsKey(line.product())) {
        result.put(line.product(), 0);
        old = 0;
      } else {
        old = result.get(line.product());
      }
      result.put(line.product(), old + line.count());
    }
    return result;
  }

  /**
   * @return the names of all products from previous exercise, joined with a ","
   */
  public String pA_productNames(List<Order> orders) {
    List<Product> products = p7_productsSorted(orders);
    StringBuilder sb = new StringBuilder();
    for (Product product : products) {
      sb.append(product.name()).append(",");
    }
    sb.deleteCharAt(sb.length() - 1); // remove the last comma
    return sb.toString();
  }

  /**
   * @return orders grouped by Month, and then by PaymentMethod
   */
  public Map<Month, Map<PaymentMethod, List<Order>>> pB_ordersByPaymentPerMonth(List<Order> orders) {
    Map<Month, Map<PaymentMethod, List<Order>>> result = new HashMap<>();
    for (Order order : orders) {
      Map<PaymentMethod, List<Order>> map = result.get(order.createdOn().getMonth());
      if (map == null) {
        map = new HashMap<>();
        result.put(order.createdOn().getMonth(), map);
      }
      List<Order> list = map.get(order.paymentMethod());
      if (list == null) {
        list = new ArrayList<>();
        map.put(order.paymentMethod(), list);
      }
      list.add(order);
    }
    return result;
  }

  /**
   * @return the first cell of a semicolon-separated file, as integers
   */
  public Set<Integer> pC_csvLinesInAllFilesInFolder(File file) throws IOException {
    try (Stream<String> lines = Files.lines(file.toPath())) {
      return lines
          .filter(s -> !s.isBlank())
          .map(line -> Integer.parseInt(line.split(";")[0]))
          .collect(Collectors.toSet());
    }
  }

  /**
   * @return the elements in Fibonacci sequence between startIndex and endIndex
   */
  public List<Integer> pD_fib(int startIndex, int endIndex) {
    List<Integer> result = new ArrayList<>();
    int a = 1;
    int b = 1;
    int c = a + b;
    int index = 0;
    while (index < endIndex) {
      if (index >= startIndex) {
        result.add(a);
      }
      a = b;
      b = c;
      c = a + b;
      index++;
    }
    return result;

  }

  static class LatestOrderComparator implements Comparator<Order> {
    @Override
    public int compare(Order o1, Order o2) {
      return o2.createdOn().compareTo(o1.createdOn());
    }
  }

}
