package Model;

public class EstruturaDados {
    private AdministrarMensagensPendentes administraMensagem;
    private ListaDupla<Grupos> listaGrupos;
    private EstruturaControle estruturaControle;
    public EstruturaDados(AdministrarMensagensPendentes administraMensagem, ListaDupla<Grupos> listaGrupos, EstruturaControle estruturaControle) {
           this.administraMensagem = administraMensagem;
           this.listaGrupos = listaGrupos;
           this.estruturaControle = estruturaControle;
    }

    public void setAdministrarMensagensPendentes(AdministrarMensagensPendentes administraMensagem) {
           this.administraMensagem = administraMensagem;
    }

    public void setEstruturaControle(EstruturaControle estruturaControle) {
           this.estruturaControle = estruturaControle;
    }

    public void setListaGrupos(ListaDupla<Grupos> listaGrupos) {
           this.listaGrupos = listaGrupos;
    }

    public AdministrarMensagensPendentes obterAdministrarMensagensPendentes() {
          return administraMensagem;
    }

    public ListaDupla<Grupos> obterListaGrupos() {
          return listaGrupos;
    } 

    public EstruturaControle obterEstruturaControle() {
          return estruturaControle;
    }
}