package victor.training.stream.support;

public class OrderMapper {
  public static OrderDto toDto(Order order) {
    return new OrderDto(
        order.total(),
        order.createdOn(),
        order.paymentMethod(),
        order.status());
  }
}
