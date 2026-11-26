package cl.pedidos360.events;

import java.util.Map;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaConfig {
  @Bean NewTopic created(@Value("${app.kafka.topics.created}") String name){return TopicBuilder.name(name).partitions(1).replicas(1).config("retention.ms","86400000").build();}
  @Bean NewTopic status(@Value("${app.kafka.topics.status}") String name){return TopicBuilder.name(name).partitions(1).replicas(1).config("retention.ms","604800000").build();}
  @Bean NewTopic dlq(@Value("${app.kafka.topics.dlq}") String name){return TopicBuilder.name(name).partitions(1).replicas(1).build();}
  @Bean DefaultErrorHandler kafkaErrorHandler(KafkaTemplate<Object,Object> template){
    var recoverer=new DeadLetterPublishingRecoverer(template,(record,ex)->new TopicPartition("pedidos.dlq",0));
    return new DefaultErrorHandler(recoverer,new FixedBackOff(1000L,2));
  }
}
