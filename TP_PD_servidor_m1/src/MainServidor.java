import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;

public class MainServidor {
    public static void main(String[] args) throws ParseException {
        if(args.length != 4){
            System.out.println("<SERVIDOR> Argumentos inválidos:\n" +
                    "\t[1] - Porto escuta para conexao e clientes;" +
                    "\t[2] - Caminho da diretoria de armazenamento da BD SQLite;" +
                    "\t[3] - Nome de registo do servico RMI;" +
                    "\t[4] - Porto escuta para lancar o resgistry local.");
            return;
        }

        //Para termos dados para testar as horas e datas dos eventos
        LocalTime horainicio = LocalTime.of(00, 00);
        LocalTime horafim = LocalTime.of(23, 55);
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        Date data = df.parse("09-11-2023");

        try {
            Integer.parseInt(args[0]);//verifica validade do porto inserido para conexao - cliente
            Integer.parseInt(args[3]);//verifica validade do porto inserido para registry

            File localDirectory = new File(args[1].trim());

            if(!localDirectory.exists()){
                System.out.println("<SERVIDOR> A directoria " + localDirectory + " nao existe!");
                return;
            }
            if(!localDirectory.isDirectory()){
                System.out.println("<SERVIDOR> O caminho " + localDirectory + " nao se refere a uma directoria!");
                return;
            }
            if(!localDirectory.canWrite()){
                System.out.println("<SERVIDOR> Sem permissoes de escrita na directoria " + localDirectory);
                return;
            }
        } catch (NumberFormatException e) {
            throw new NumberFormatException("<SERVIDOR> Os portos inseridos devem ter ser inteiros! [ERRO] " + e.getCause());
        }

        ProgServidor prog = new ProgServidor(Integer.parseInt(args[0]));
        prog.servico();

      // DbManage dbManage = new DbManage();
     //  DbManage.Registonovouser(new Utilizador("Joao","eu@tu.isec","9876"),"12345");
    //  DbManage.autentica_user("eu","12345");
         //DbManage.edita_registo(new Utilizador("Joao","eu@tu.isec","9876"),"123");
      //  System.out.println( DbManage.autentica_user(new Utilizador("Joao","eu@tu.isec","9876"),"12345"));
       // System.out.println( DbManage.autentica_user(new Utilizador("Joao","eu@tu.isec","9876"),"123"));
        //DbManage.Cria_evento("Testenovo2","Isec",data,horainicio,horafim);
  // DbManage.EliminaPresencas("Testenovo2",new String[]{"eu@tu.isec"} );
    //   DbManage.Edita_evento(new Cria_evento("Testenovo2","Isec",data,horainicio,horafim),"Testenovo2");
    // int code=DbManage.GeraCodigoRegisto(new Cria_evento("Testenovo2","Isec",data,horainicio,horafim),0);
      //System.out.println(DbManage.submitcod(795921,"Testenovo2","eu@tu.isec"));
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
