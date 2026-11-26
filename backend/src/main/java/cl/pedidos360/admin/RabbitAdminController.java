package cl.pedidos360.admin;

import java.util.Map;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/admin/rabbit")
public class RabbitAdminController {
  private final RabbitAdmin admin;
  public RabbitAdminController(RabbitAdmin admin){this.admin=admin;}
  @PostMapping("/queues") public Map<String,String> createQueue(@RequestBody QueueRequest r){admin.declareQueue(QueueBuilder.durable(r.name()).build());return Map.of("resultado","cola creada","nombre",r.name());}
  @DeleteMapping("/queues/{name}") public Map<String,String> deleteQueue(@PathVariable String name){admin.deleteQueue(name);return Map.of("resultado","cola eliminada","nombre",name);}
  @PostMapping("/exchanges") public Map<String,String> createExchange(@RequestBody ExchangeRequest r){admin.declareExchange(switch(r.type()){case "topic"->new TopicExchange(r.name());case "fanout"->new FanoutExchange(r.name());default->new DirectExchange(r.name());});return Map.of("resultado","exchange creado","nombre",r.name());}
  @DeleteMapping("/exchanges/{name}") public Map<String,String> deleteExchange(@PathVariable String name){admin.deleteExchange(name);return Map.of("resultado","exchange eliminado","nombre",name);}
  @PostMapping("/bindings") public Map<String,String> bind(@RequestBody BindingRequest r){admin.declareBinding(BindingBuilder.bind(new Queue(r.queue())).to(new DirectExchange(r.exchange())).with(r.routingKey()));return Map.of("resultado","binding creado");}
  @DeleteMapping("/bindings") public Map<String,String> unbind(@RequestParam String queue,@RequestParam String exchange,@RequestParam String routingKey){admin.removeBinding(BindingBuilder.bind(new Queue(queue)).to(new DirectExchange(exchange)).with(routingKey));return Map.of("resultado","binding eliminado");}
  public record QueueRequest(String name){}
  public record ExchangeRequest(String name,String type){}
  public record BindingRequest(String queue,String exchange,String routingKey){}
}
