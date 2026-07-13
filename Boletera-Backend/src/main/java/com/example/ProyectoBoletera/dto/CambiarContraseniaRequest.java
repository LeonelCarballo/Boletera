package com.example.ProyectoBoletera.dto;

public class CambiarContraseniaRequest {
    private String contraseniaActual;
    private String contraseniaNueva;

    public String getContraseniaActual() { return contraseniaActual; }
    public void setContraseniaActual(String contraseniaActual) { this.contraseniaActual = contraseniaActual; }
    public String getContraseniaNueva() { return contraseniaNueva; }
    public void setContraseniaNueva(String contraseniaNueva) { this.contraseniaNueva = contraseniaNueva; }
}
