package pt.isec.pd.trabalhoPratico.model.classesComunication;

public class Login extends Geral{
    private String email, password;

    public Login(String email, String password) {
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
