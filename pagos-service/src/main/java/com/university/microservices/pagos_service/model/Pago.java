package com.university.microservices.pagos_service.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.math.BigDecimal;

@Document(collection = "pagos")
public class Pago {
    @Id
    private String id;
    private String ordenId;
    private BigDecimal monto;
    private String status;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getOrdenId() { return ordenId; }
    public void setOrdenId(String ordenId) { this.ordenId = ordenId; }
    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
