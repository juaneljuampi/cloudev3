package cl.pedidos360.order;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import cl.pedidos360.events.OrderEventService;
import cl.pedidos360.messaging.RabbitOrderPublisher;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class OrderControllerTest {
  @Test void createPersistsOrderAndPublishesBothEvents(){
    var repository=mock(OrderRepository.class);
    var kafka=mock(OrderEventService.class);
    var rabbit=mock(RabbitOrderPublisher.class);
    when(repository.save(any(Order.class))).thenAnswer(call->call.getArgument(0));
    var controller=new OrderController(repository,kafka,rabbit);
    var created=controller.create(new OrderController.CreateOrder("Camila","Café",2,new BigDecimal("4990")));
    assertEquals("Camila",created.getCliente());
    verify(repository).save(any(Order.class));
    verify(kafka).publishCreated(created);
    verify(rabbit).publish(created);
  }
}
