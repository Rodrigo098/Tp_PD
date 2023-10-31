package pt.isec.pd.trabalhoPratico.classesComunication;

public class Login extends Geral{
    private String email,password;

    public Login(String email, String password) {
        this.email = email;
        this.password = password;
        tipo=Message_types.LOGIN;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
