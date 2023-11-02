package pt.isec.pd.trabalhoPratico.model.classesComunication;

public class RegistoEdicao_Cliente extends Geral{
   private String nome, email, password;
   private long num_estudante;// este aqui criei como long mas talvez usar outro tipo de dados seja preferível

    public RegistoEdicao_Cliente(String nome, String email, String password, long num_estudante,Message_types tipo) {
        super(tipo);
        this.nome = nome;
        this.email = email;
        this.password = password;
        this.num_estudante = num_estudante;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public long getNum_estudante() {
        return num_estudante;
    }
}
