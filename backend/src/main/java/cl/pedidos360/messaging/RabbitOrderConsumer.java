package cl.pedidos360.messaging;
import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
@Component public class RabbitOrderConsumer {
  private static final Logger log=LoggerFactory.getLogger(RabbitOrderConsumer.class);
  @RabbitListener(queues="pedidos.notificaciones") public void receive(String message){if(message==null||message.isBlank())throw new IllegalArgumentException("Mensaje vacío");log.info("Notificación recibida: {}",message);}
}
