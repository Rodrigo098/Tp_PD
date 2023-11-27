import pt.isec.pd.trabalhoPratico.model.classesPrograma.ProgServidor;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.RemoteException;

public class MainServidor {
    private static String SERVICE_NAME = "servidor";//por enquanto, este devia ser o arg[2]
    public static void main(String[] args) {
        // VALIDA ARGUMENTOS
        if(args.length != 4){
            System.out.println("<SERVIDOR> Argumentos inválidos:\n" +
                    "\t[1] - Porto escuta para conexao e clientes;" +
                    "\t[2] - Caminho da diretoria de armazenamento da BD SQLite;" +
                    "\t[3] - Nome de registo do servico RMI;" +
                    "\t[4] - Porto escuta para lancar o resgistry local.");

            sair("<SERVIDOR> Número de argumentos inválidos!");
        }
        int portoCli, portoReg;
        String caminhoBD = null;
        try {
            portoCli = Integer.parseInt(args[0]);//verifica validade do porto inserido para conexao - cliente
            portoReg = Integer.parseInt(args[3]);//verifica validade do porto inserido para registry

            File localDirectory = new File(args[1].trim());
            SERVICE_NAME = args[2];

            if(!localDirectory.exists()){
                sair("<SERVIDOR> A directoria da base de dados " + localDirectory + " nao existe!");
            }
            if(!localDirectory.isDirectory()){
                sair("<SERVIDOR> O caminho para a base de dados " + localDirectory + " nao se refere a uma directoria!");
            }
            if(!localDirectory.canWrite()){
                sair("<SERVIDOR> Sem permissoes de escrita na directoria " + localDirectory);
            }
            try {
                caminhoBD = localDirectory.getCanonicalPath();
            } catch (IOException e) {
                sair("<SERVIDOR> Erro ao obter o caminho da base de dados!");
            }
        } catch (NumberFormatException e) {
            throw new NumberFormatException("<SERVIDOR> Os portos inseridos devem ter ser inteiros! [ERRO] " + e.getCause());
        }

        ProgServidor progServidor = null;
        try {
            progServidor = new ProgServidor(portoCli, SERVICE_NAME, caminhoBD);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }

        try {
            progServidor.setRegistry(InetAddress.getLocalHost().getHostAddress(), portoReg);
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
