package Protocol;

import Model.*;

public class APDUDATA extends APDU {
    private AdministrarMensagensPendentes administraMensagem;
    private ListaDupla<Grupos> listaGrupos;
    private EstruturaControle estruturaControle;
    public APDUDATA(String operacao, int portaClienteTCP, int portaClienteUDP, String endereco, ListaDupla<Grupos> listaGrupos, EstruturaControle estruturaControle, AdministrarMensagensPendentes administraMensagem) {
        super(operacao, portaClienteTCP, portaClienteUDP, endereco);
        this.listaGrupos = listaGrupos;
        this.estruturaControle = estruturaControle;
        this.administraMensagem = administraMensagem;
    }

    public ListaDupla<Grupos> obterGrupo() {
        return listaGrupos;
    }
    
    public EstruturaControle obterEstruturaControle() {
        return estruturaControle;
    }

    public AdministrarMensagensPendentes obterAdminstradorMensagens() {
        return administraMensagem;
    }
}