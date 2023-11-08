import org.sqlite.core.DB;
import pt.isec.pd.trabalhoPratico.dataAccess.DbManage;
import pt.isec.pd.trabalhoPratico.model.classesComunication.Cria_evento;
import pt.isec.pd.trabalhoPratico.model.classesDados.Evento;
import pt.isec.pd.trabalhoPratico.model.classesDados.Utilizador;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

public class MainServidor {
    public static void main(String[] args) throws ParseException {
        //Para termos dados para testar as horas e datas dos eventos
        LocalTime horainicio = LocalTime.of(05, 00);
        LocalTime horafim = LocalTime.of(23, 55);
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        Date data = df.parse("08-11-2023");

        //ProgServidor prog=new ProgServidor();
        //prog.servico();
      // DbManage dbManage = new DbManage();
     //  DbManage.Registonovouser(new Utilizador("Joao","eu@tu.isec","9876"),"12345");
    //  DbManage.autentica_user("eu","12345");
         //DbManage.edita_registo(new Utilizador("Joao","eu@tu.isec","9876"),"123");
      //  System.out.println( DbManage.autentica_user(new Utilizador("Joao","eu@tu.isec","9876"),"12345"));
       // System.out.println( DbManage.autentica_user(new Utilizador("Joao","eu@tu.isec","9876"),"123"));
        //DbManage.Cria_evento("Testenovo2","Isec",data,horainicio,horafim);
      DbManage.EliminaPresencas("Testenovo2",new String[]{"eu@tu.isec"} );
        DbManage.Edita_evento(new Cria_evento("Testenovo2","Isec",data,horainicio,horafim),"Testenovo2");
      int code=DbManage.GeraCodigoRegisto(new Cria_evento("Testenovo2","Isec",data,horainicio,horafim),50);
      System.out.println(DbManage.submitcod(code,"Testenovo2","eu@tu.isec"));
      /*  List<String> teste=DbManage.Presencas_evento("Chelsea");
        for (String a:teste
             ) {
            System.out.println(a);

        }*/

        //------------------------ Testes Chelsea, tudo a funcionar ------------------------
        //DbManage.Cria_evento("TesteCod","Lua",data,horainicio,horafim);
        //DbManage.Cria_evento("Chelsea3","Casa",data,horainicio,horafim);
        //DbManage.Edita_evento(new Cria_evento("TesteCod2","Lua",data,horainicio,horafim),"TesteCod");
        //DbManage.Elimina_evento("Chelsea2");
         /*List <Evento> eventos2 =DbManage.Consulta_eventos(new Cria_evento(null,"Casa", data,horainicio,null));
            System.out.println("Eventos encontrados:");
            for (Evento evento : eventos2) {
                System.out.println(evento.toString());
            }*/
        //DbManage.InserePresencas("Chelsea", new String[]{"eu@tu.isec"});
        //DbManage.InserePresencas("Chelsea2", new String[]{"eu@tu.isec"});
        //DbManage.InserePresencas("Chelsea3", new String[]{"eu@tu.isec"});
        //DbManage.EliminaPresencas("Chelsea",new String[]{"eu@tu.isec"} );
     /* int code =  DbManage.GeraCodigoRegisto(new Cria_evento("TesteCod2","Lua",data,horainicio,horafim),30);// estive a alterar esta funcao
        System.out.println(code);*/
/*
        List <Evento> presencasUser = DbManage.ConsultaPresencas_user("eu@tu.isec",null,null,null,null,null);
        System.out.println("Presenças registadas nos seguintes eventos:");
        for (Evento evento : presencasUser) {
            System.out.println(evento.toString());
        }

        DbManage.PresencasCSV(presencasUser,"minhasPresencas.csv");*/
    }


}
