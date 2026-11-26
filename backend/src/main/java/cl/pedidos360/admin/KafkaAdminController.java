package cl.pedidos360.admin;

import java.util.*;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.config.ConfigResource;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/admin/kafka")
public class KafkaAdminController {
  private final KafkaAdmin admin;
  public KafkaAdminController(KafkaAdmin admin){this.admin=admin;}
  @GetMapping("/topics") public List<String> list()throws Exception{try(var c=AdminClient.create(admin.getConfigurationProperties())){return c.listTopics().names().get(10,TimeUnit.SECONDS).stream().sorted().toList();}}
  @GetMapping("/topics/{name}") public Map<String,Object> describe(@PathVariable String name)throws Exception{try(var c=AdminClient.create(admin.getConfigurationProperties())){var resource=new ConfigResource(ConfigResource.Type.TOPIC,name);var config=c.describeConfigs(List.of(resource)).all().get(10,TimeUnit.SECONDS).get(resource);var values=new TreeMap<String,String>();config.entries().forEach(e->values.put(e.name(),e.value()));var topic=c.describeTopics(List.of(name)).allTopicNames().get(10,TimeUnit.SECONDS).get(name);return Map.of("name",name,"partitions",topic.partitions().size(),"replicas",topic.partitions().isEmpty()?0:topic.partitions().get(0).replicas().size(),"config",values);}}
  @PostMapping("/topics") public Map<String,String> create(@RequestBody TopicRequest r)throws Exception{try(var c=AdminClient.create(admin.getConfigurationProperties())){c.createTopics(List.of(new NewTopic(r.name(),r.partitions(),r.replicas()).configs(Map.of("retention.ms",String.valueOf(r.retentionMs()),"cleanup.policy",r.cleanupPolicy())))).all().get(10,TimeUnit.SECONDS);}return Map.of("resultado","creado","topic",r.name());}
  @PutMapping("/topics/{name}") public Map<String,String> update(@PathVariable String name,@RequestBody Map<String,String> values)throws Exception{var cfg=new ArrayList<AlterConfigOp>();values.forEach((k,v)->cfg.add(new AlterConfigOp(new org.apache.kafka.clients.admin.ConfigEntry(k,v),AlterConfigOp.OpType.SET)));try(var c=AdminClient.create(admin.getConfigurationProperties())){c.incrementalAlterConfigs(Map.of(new ConfigResource(ConfigResource.Type.TOPIC,name),cfg)).all().get(10,TimeUnit.SECONDS);}return Map.of("resultado","actualizado","topic",name);}
  @DeleteMapping("/topics/{name}") public Map<String,String> delete(@PathVariable String name)throws Exception{try(var c=AdminClient.create(admin.getConfigurationProperties())){c.deleteTopics(List.of(name)).all().get(10,TimeUnit.SECONDS);}return Map.of("resultado","eliminado","topic",name);}
  @GetMapping("/groups") public List<String> groups()throws Exception{try(var c=AdminClient.create(admin.getConfigurationProperties())){return c.listConsumerGroups().all().get(10,TimeUnit.SECONDS).stream().map(g->g.groupId()+" ("+g.state()+")").sorted().toList();}}
  public record TopicRequest(String name,int partitions,short replicas,long retentionMs,String cleanupPolicy){public TopicRequest{if(name==null||name.isBlank()||partitions<1||replicas<1||retentionMs<1000)throw new IllegalArgumentException("Configuración de tópico inválida");if(cleanupPolicy==null||cleanupPolicy.isBlank())cleanupPolicy="delete";}}
}
