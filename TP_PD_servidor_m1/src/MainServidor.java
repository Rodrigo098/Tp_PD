import pt.isec.pd.trabalhoPratico.model.classesPrograma.ProgServidor;

import java.io.File;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class MainServidor {
    private static String SERVICE_NAME = "servidor";//por enquanto, este devia ser o arg[2]
    public static void main(String[] args) {
        // VALIDA ARGUMENTOS
  /*      if(args.length != 4){
            System.out.println("<SERVIDOR> Argumentos inválidos:\n" +
                    "\t[1] - Porto escuta para conexao e clientes;" +
                    "\t[2] - Caminho da diretoria de armazenamento da BD SQLite;" +
                    "\t[3] - Nome de registo do servico RMI;" +
                    "\t[4] - Porto escuta para lancar o resgistry local.");

            exit(1);
        }
        try {
            Integer.parseInt(args[0]);//verifica validade do porto inserido para conexao - cliente
            Integer.parseInt(args[2]);//verifica validade do porto inserido para registry

            File localDirectory = new File(args[1].trim());
            SERVICE_NAME = args[2];

            if(!localDirectory.exists()){
                System.out.println("<SERVIDOR> A directoria da base de dados " + localDirectory + " nao existe!");
                return;
            }
            if(!localDirectory.isDirectory()){
                System.out.println("<SERVIDOR> O caminho para a base de dados " + localDirectory + " nao se refere a uma directoria!");
                return;
            }
            if(!localDirectory.canWrite()){
                System.out.println("<SERVIDOR> Sem permissoes de escrita na directoria " + localDirectory);
                return;
            }
        } catch (NumberFormatException e) {
            throw new NumberFormatException("<SERVIDOR> Os portos inseridos devem ter ser inteiros! [ERRO] " + e.getCause());
        }
*/
        ProgServidor progServidor = null;
        try {
            progServidor = new ProgServidor(6001, SERVICE_NAME);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }

        try {
            progServidor.setRegistry(InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException | RemoteException | MalformedURLException e) {
            sair(e.getMessage());
        }
        // HEARTBEAT SERVIDORES BACKUP
        String resultado = progServidor.setMulticastSocketBackup();
        if(resultado != null)
            sair(resultado);

        // PROGRAMA
        progServidor.servidorMainFunction();
        System.exit(0);
    }

    public static void sair(String msg) {
        System.out.println(msg);
        System.exit(1);
    }
}
