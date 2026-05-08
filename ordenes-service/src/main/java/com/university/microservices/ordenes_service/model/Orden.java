package com.university.microservices.ordenes_service.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.math.BigDecimal;
import java.util.List;

@Document(collection = "ordenes")
public class Orden {
    @Id
    private String id;
    private String usuarioId;
    private List<String> productosIds;
    private BigDecimal total;
    private String status;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUsuarioId() { return usuarioId; }
    public void setUsuarioId(String usuarioId) { this.usuarioId = usuarioId; }
    public List<String> getProductosIds() { return productosIds; }
    public void setProductosIds(List<String> productosIds) { this.productosIds = productosIds; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
