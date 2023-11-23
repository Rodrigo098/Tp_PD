package pt.isec.pd.trabalhoPratico.model.classesComunication;

public class Mgs_RegistarEditar_Conta extends Geral{
   private String nome, email, password;
   private int num_estudante;// este aqui criei como long mas talvez usar outro tipo de dados seja preferível

    public Mgs_RegistarEditar_Conta(String nome, String email, String password, int num_estudante, Message_types tipo) {
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

    public int getNum_estudante() {
        return num_estudante;
    }
}
