package cl.pedidos360.order;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class OrderTest {
  @Test void nuevoPedidoConservaDatosYEstadoInicial(){
    var pedido=new Order("Camila","Café",2,new BigDecimal("4990"));
    assertEquals("Camila",pedido.getCliente());
    assertEquals("Café",pedido.getProducto());
    assertEquals(2,pedido.getCantidad());
    assertEquals(new BigDecimal("4990"),pedido.getTotal());
    assertEquals("RECIBIDO",pedido.getEstado());
    assertNotNull(pedido.getCreado());
  }
  @Test void permiteActualizarEstado(){
    var pedido=new Order("Camila","Café",1,new BigDecimal("2495"));
    pedido.setEstado("EN_PREPARACION");
    assertEquals("EN_PREPARACION",pedido.getEstado());
  }
}
