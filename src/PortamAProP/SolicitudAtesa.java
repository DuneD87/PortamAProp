package PortamAProP;

import java.time.LocalTime;

public class SolicitudAtesa {
    
    private Solicitud _instancia;
    private LocalTime _horaRecollida;
    private LocalTime _horaArribada;
    
    public SolicitudAtesa(Solicitud sol) {
        _instancia = sol;
    }
    
    public SolicitudAtesa(Solicitud instancia, LocalTime horaRecollida, LocalTime horaArribada) {
        
    }
}
