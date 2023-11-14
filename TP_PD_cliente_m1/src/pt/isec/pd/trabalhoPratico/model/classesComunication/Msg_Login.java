package pt.isec.pd.trabalhoPratico.model.classesComunication;

public class Msg_Login extends Geral{
    private String email, password;

    public Msg_Login(String email, String password) {
        super(Message_types.LOGIN);
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
