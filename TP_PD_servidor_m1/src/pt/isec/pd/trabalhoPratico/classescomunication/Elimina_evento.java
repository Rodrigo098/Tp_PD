package pt.isec.pd.trabalhoPratico.classescomunication;

public class Elimina_evento extends Geral{// Podiamos usar esta classe tmb para o editar evento e para o consulta de presenças no evento
    // porque estas 3 classes só têm o atributo do nome
  private   String nome;

    public Elimina_evento(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }
}
