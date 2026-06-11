package com.example.ProyectoBoletera.dominio;

@Entity
public class BoletoCliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String idQr;

    @Enumerated(EnumType.STRING)
    private EstadoBoleto estado;

    @ManyToOne
    @JoinColumn(name = "compra_id")
    private Compra compra;

    @ManyToOne
    @JoinColumn(name = "boleto_id")
    private Boleto boleto;

    public BoletoCliente() {
    }

    public BoletoCliente(String idQr, EstadoBoleto estado, Compra compra, Boleto boleto) {
        this.idQr = idQr;
        this.estado = estado;
        this.compra = compra;
        this.boleto = boleto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdQr() {
        return idQr;
    }

    public void setIdQr(String idQr) {
        this.idQr = idQr;
    }

    public EstadoBoleto getEstado() {
        return estado;
    }

    public void setEstado(EstadoBoleto estado) {
        this.estado = estado;
    }

    public Compra getCompra() {
        return compra;
    }

    public void setCompra(Compra compra) {
        this.compra = compra;
    }

    public Boleto getBoleto() {
        return boleto;
    }

    public void setBoleto(Boleto boleto) {
        this.boleto = boleto;
    }
}
