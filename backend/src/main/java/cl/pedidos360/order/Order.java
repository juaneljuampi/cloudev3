package cl.pedidos360.order;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity @Table(name="pedidos")
public class Order {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @Column(nullable=false) private String cliente;
  @Column(nullable=false) private String producto;
  @Column(nullable=false) private int cantidad;
  @Column(nullable=false, precision=12, scale=2) private BigDecimal total;
  @Column(nullable=false) private String estado="RECIBIDO";
  @Column(nullable=false) private Instant creado=Instant.now();
  protected Order() {}
  public Order(String cliente,String producto,int cantidad,BigDecimal total){this.cliente=cliente;this.producto=producto;this.cantidad=cantidad;this.total=total;}
  public Long getId(){return id;} public String getCliente(){return cliente;} public String getProducto(){return producto;}
  public int getCantidad(){return cantidad;} public BigDecimal getTotal(){return total;} public String getEstado(){return estado;} public Instant getCreado(){return creado;}
  public void setEstado(String estado){this.estado=estado;}
}
