package cl.pedidos360.messaging;
import cl.pedidos360.order.Order;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
@Service public class RabbitOrderPublisher {
  private final RabbitTemplate rabbit;
  public RabbitOrderPublisher(RabbitTemplate rabbit){this.rabbit=rabbit;}
  public void publish(Order o){try{rabbit.convertAndSend("pedidos.exchange","pedido.creado","Pedido "+o.getId()+" creado");}catch(Exception e){org.slf4j.LoggerFactory.getLogger(getClass()).error("Fallo al publicar notificación RabbitMQ",e);}}
}
