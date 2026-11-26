package cl.pedidos360.order;

import cl.pedidos360.events.OrderEvent;
import cl.pedidos360.events.OrderEventService;
import cl.pedidos360.messaging.RabbitOrderPublisher;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController @RequestMapping("/api")
public class OrderController {
  private final OrderRepository orders; private final OrderEventService events; private final RabbitOrderPublisher rabbit;
  public OrderController(OrderRepository orders,OrderEventService events,RabbitOrderPublisher rabbit){this.orders=orders;this.events=events;this.rabbit=rabbit;}
  public record CreateOrder(@NotBlank String cliente,@NotBlank String producto,@Min(1) int cantidad,@DecimalMin("0.01") BigDecimal total){}
  @PostMapping("/pedidos") public Order create(@Valid @RequestBody CreateOrder in){var o=orders.save(new Order(in.cliente(),in.producto(),in.cantidad(),in.total()));events.publishCreated(o);rabbit.publish(o);return o;}
  @GetMapping("/pedidos") public List<Order> list(){return orders.findAll();}
  @PutMapping("/pedidos/{id}/estado") public Order status(@PathVariable Long id,@RequestParam @NotBlank String valor){var o=orders.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));o.setEstado(valor);o=orders.save(o);events.publishStatus(o);return o;}
  @GetMapping("/eventos") public List<OrderEvent> events(){return events.received();}
}
