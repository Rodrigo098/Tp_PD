package pt.isec.pd.trabalhoPratico.classescomunication;

public class Login extends Geral{
    private String email,password;

    public Login(String email, String password) {
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
