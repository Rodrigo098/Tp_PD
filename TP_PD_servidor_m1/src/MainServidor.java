import org.sqlite.core.DB;
import pt.isec.pd.trabalhoPratico.dataAccess.DbManage;
import pt.isec.pd.trabalhoPratico.model.classesDados.Utilizador;

public class MainServidor {
    public static void main(String[] args) {
        //ProgServidor prog=new ProgServidor();
        //prog.servico();

      // DbManage dbManage = new DbManage();
      // DbManage.Registonovouser(new Utilizador("Joao","eu@tu.isec","9876"),"12345");
    //     DbManage.autentica_user(new Utilizador("Joao","eu@tu.isec","9876"),"12345");
         DbManage.edita_registo(new Utilizador("Joao","eu@tu.isec","9876"),"123");
      //  System.out.println( DbManage.autentica_user(new Utilizador("Joao","eu@tu.isec","9876"),"12345"));
       // System.out.println( DbManage.autentica_user(new Utilizador("Joao","eu@tu.isec","9876"),"123"));


    }
}
