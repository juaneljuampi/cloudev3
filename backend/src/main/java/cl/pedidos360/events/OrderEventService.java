package cl.pedidos360.events;

import cl.pedidos360.order.Order;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderEventService {
  private static final Logger log=LoggerFactory.getLogger(OrderEventService.class);
  private final KafkaTemplate<String,OrderEvent> kafka;
  private final String createdTopic,statusTopic;
  private final List<OrderEvent> received=new CopyOnWriteArrayList<>();
  public OrderEventService(KafkaTemplate<String,OrderEvent> kafka,@Value("${app.kafka.topics.created}") String createdTopic,@Value("${app.kafka.topics.status}") String statusTopic){this.kafka=kafka;this.createdTopic=createdTopic;this.statusTopic=statusTopic;}
  public void publishCreated(Order order){publish(createdTopic,event(order));}
  public void publishStatus(Order order){publish(statusTopic,event(order));}
  private OrderEvent event(Order o){return new OrderEvent(o.getId(),o.getCliente(),o.getProducto(),o.getCantidad(),o.getTotal(),o.getEstado(),Instant.now());}
  private void publish(String topic,OrderEvent event){try{kafka.send(topic,String.valueOf(event.pedidoId()),event).whenComplete((r,e)->{if(e!=null)log.error("Error publicando evento en {}",topic,e);});}catch(Exception e){log.error("No se pudo publicar en {}",topic,e);}}
  @KafkaListener(topics="${app.kafka.topics.created}",groupId="pedidos-analitica")
  public void onCreated(OrderEvent e){accept(e,"creado");}
  @KafkaListener(topics="${app.kafka.topics.status}",groupId="pedidos-analitica")
  public void onStatus(OrderEvent e){accept(e,"estado");}
  private void accept(OrderEvent e,String source){if(e==null||e.pedidoId()==null||e.cliente()==null)throw new IllegalArgumentException("Evento de pedido inválido");received.add(e);log.info("Evento {} procesado para pedido {}",source,e.pedidoId());}
  public List<OrderEvent> received(){return List.copyOf(received);}
}
