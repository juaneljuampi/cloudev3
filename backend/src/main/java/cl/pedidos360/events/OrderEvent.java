package cl.pedidos360.events;
import java.math.BigDecimal;
import java.time.Instant;
public record OrderEvent(Long pedidoId,String cliente,String producto,int cantidad,BigDecimal total,String estado,Instant fecha) {}
