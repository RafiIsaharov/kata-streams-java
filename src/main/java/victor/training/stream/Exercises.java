package victor.training.stream;

import victor.training.stream.support.*;
import victor.training.stream.support.Order.PaymentMethod;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;
import static java.util.Comparator.comparingDouble;
import static java.util.function.Predicate.*;
import static victor.training.stream.support.Order.*;

public class Exercises {
  // imagine @Autowired/@Inject here
  private final OrderMapper orderMapper = new OrderMapper();// in reality this would be injected

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
    Function<Order, OrderDto> toDto1 = orderMapper::toDto;
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
    return orders.stream().filter(Order::isCompleted).map(orderMapper::toDto).toList();

  }

  private OrderDto toDto(Order order) {
    return new OrderDto(order.total(), order.createdOn(), order.paymentMethod(), order.status());
  }

  public Optional<Order> p2_findOrderById(List<Order> orders, int orderId) {
    // TODO 1: rewrite with streams
    // TODO 2: return Optional<> and fix the tests
//    for (Order order : orders) {
//      if (order.id() == orderId) {
//        return order;
//      }
//    }

//    return orders.parallelStream()// useful when doing HEAVY CPUs (N-1 threads, {if 10 CPUS => 9 threads for streaming + 1main thread}) work per
//    element
//    .findAny() first one found by any threads (of the parallel stream) => faster

    return orders.stream()
            .filter(order -> order.id() == orderId)
            .findFirst();// really the first one found === findAny
            //.orElse(null);
  }

  // TODO all the following: rewrite with streams
  public boolean p3_hasActiveOrders(List<Order> orders) {
//    for (Order order : orders) {
//      if (order.isCompleted()) {
//        return true;
//      }
//    }
//    return false;
    // any match returning boolean
//    return orders.stream().filter(Order::isCompleted).collect(toList()).size() > 0;
//    return !orders.stream().filter(Order::isCompleted).collect(toList()).isEmpty();
//    return orders.stream().filter(Order::isCompleted).count() > 0;
    return orders.stream().anyMatch(Order::isCompleted); // simpler to read, and less memory wasteful
  }

  /**
   * @return order with the max total() that does NOT contain a special offer line
   */
  public Order p4_maxPriceOrder(List<Order> orders) {
//    Order maxOrder = null;
//    for (Order order : orders) {
//      boolean hasSpecialOffer = false;
//      for (OrderLine orderLine : order.orderLines()) {
//        if (orderLine.isSpecialOffer()) {
//          hasSpecialOffer = true;
//          break;
//        }
//      }
//      if (hasSpecialOffer) {
//        continue;
//      }
//      if (maxOrder == null || order.total() > maxOrder.total()) {
//        maxOrder = order;
//      }
//    }
//    return maxOrder;

    // I want to create another List of Orders that contains only the orders that don't have special offers
    // classic refactor when you see a for loop with continue and break
    // you need to create a new collection with the elements and you interesting in and loop threw those => filter
//    Predicate<Order> noSpecialOffers = Predicate.not(Order::hasSpecialOffer); // higher-order function (function that returns a function)
//      List<Order> regularOrders = orders.stream()
    // .filter(order -> !order.hasSpecialOffer()).toList();
      // a bit to geek for the first years
      //BIG PROBLEM: When I do order of the total,
    // i will change the order of the elements in the list regularOrders
//    Comparator<Order> compareByTotal = new Comparator<>() {
//      @Override
//      public int compare(Order o1, Order o2) {
//        return Double.compare(o2.total(), o1.total());
//      }
//    };
//    Comparator<Order> compareByTotal = (o1, o2) ->
//        Double.compare(o2.total(), o1.total());


//    Comparator<Order> comparing = comparing(Order::total);
//    Collections.sort(regularOrders, comparing.reversed());
//    if (regularOrders.isEmpty()) return null;
//    return regularOrders.get(0);


      return orders.stream()
              .filter(not(Order::hasSpecialOffer)).toList().stream()
              .max(comparing(Order::total))
              .orElse(null);

  }

  /**
   * @return last 3 returnReason()s sorted descending by Order.createdOn
   */
  public List<String> p5_last3Orders(List<Order> orders) {
//    List<Order> copy = new ArrayList<>(orders);
//    copy.sort(new LatestOrderComparator());
//    List<String> returnReasons = new ArrayList<>();
//    for (Order order : copy) {
//      if (order.returnReason().isPresent()) {
//        returnReasons.add(order.returnReason().get());
//        if (returnReasons.size() == 3) {
//          break;
//        }
//      }
//    }
//    return returnReasons;
// Hint: Optional#stream()
    Order o = new Order();
    Optional<String> optionalS = o.returnReason();
    Stream<String> stream = optionalS.stream(); // 0 or 1 element
    // CTRL + Q to see the doc
    // CTRL + SHIFT + I to see the implementation
    // CTRL + SHIFT + P to see the type of parameters
    //Baby Steps:
    // 1. will start with Orders stream and sort them by createdOn
    // 2. will filter the orders that have a returnReason
    // 3. will limit the stream to 3 elements
    // 4. will map the orders to their returnReason
    // 5. will collect the returnReasons in a List
    return orders.stream()
            .sorted(new LatestOrderComparator())
//            .sorted(Comparator.<Order, LocalDate>comparing(Order::createdOn).reversed())
            // java 8 way best:
//            .filter(order -> order.returnReason().isPresent())
//            .map(order -> order.returnReason().orElseThrow())//this is misleading, will NEVER throw, because of the filter above (if it's not present, it won't get here)

            // java 11 way best:// Strem of Stream of String - Strem<Stream<String>>
            // we will use flatMap to flatten the Stream<Stream<String>> to Stream<String>
            .flatMap(order -> order.returnReason().stream())
            //I am joining a series of streams of returnReasons into a single stream with 0 or 1  element (if the returnReason is present)

            // :: - mania of java 8
//            .map(Order::returnReason)
//            .flatMap(Optional::stream)

            .limit(3)
            .toList();

  }

  /**
   * @return sum of all Order.total(), truncated to int.
   */
  public int p6_completedTotalSum(List<Order> orders) {
//    double sum = 0;
//    for (Order order : orders) {
//      if (order.isCompleted())
//        sum += order.total();
//    }
//    return (int) sum;
    // BAbY STEPS:
    // 1. I will start with the Orders stream
    // 2. I will filter the orders that are completed
    // 3. I will map the orders to their total
    // 4. I will sum the totals
    // 5. I will truncate the sum to int
    // 6. I will return the int
      return (int)orders.stream()
            .filter(Order::isCompleted)
            .mapToDouble(Order::total)
            .sum(); // belong to the DoubleStream/IntStream/LongStream - NumericStream

    // Using reduce:
//    double sum = orders.stream()
//            .filter(Order::isCompleted)
//            .map(o -> o.total())
//            .reduce(0d, (a+b) -> a+b);
//            .reduce(0d, Double::sum);
//    return (int) sum;

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
