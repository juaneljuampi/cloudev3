package cl.pedidos360.messaging;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration public class RabbitConfig {
  @Bean Queue ordersQueue(){return QueueBuilder.durable("pedidos.notificaciones").withArgument("x-dead-letter-exchange","pedidos.dlx").withArgument("x-dead-letter-routing-key","pedidos.fallidos").build();}
  @Bean DirectExchange ordersExchange(){return new DirectExchange("pedidos.exchange");}
  @Bean DirectExchange deadExchange(){return new DirectExchange("pedidos.dlx");}
  @Bean Queue deadQueue(){return QueueBuilder.durable("pedidos.notificaciones.dlq").build();}
  @Bean Binding ordersBinding(){return BindingBuilder.bind(ordersQueue()).to(ordersExchange()).with("pedido.creado");}
  @Bean Binding deadBinding(){return BindingBuilder.bind(deadQueue()).to(deadExchange()).with("pedidos.fallidos");}
}
