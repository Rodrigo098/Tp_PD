package pt.isec.pd.trabalhoPratico.model.classesPrograma;

import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;

class ParesComunicacaoCliente {
    private Socket socketAtualizacao, socketPedidos;
    private PrintStream psClienteAtualizacao;
    private String cliente, idCliente;
    private boolean logado = false;

    protected ParesComunicacaoCliente(String idCliente ,Socket cli) {
        this.idCliente = idCliente;
        this.socketPedidos = cli;
    }
    protected void setUserName(String cliente) {
        this.cliente = cliente;
    }
    protected void setDadosAtualizacao(Socket socketAtualizacao, PrintStream clientesAtualizacao) {
        this.socketAtualizacao = socketAtualizacao;
        this.psClienteAtualizacao = clientesAtualizacao;
        logado = true;
    }
    protected PrintStream getClientesAtualizacaoPS() {
        return psClienteAtualizacao;
    }
    protected Socket getSocketPedidos() {
        return socketPedidos;
    }

    protected String getCliente() {
        return cliente;
    }
    protected String getSocketID() {
        return idCliente;
    }

    public void removeDadosAtualizacao() {
        try {
            socketAtualizacao.close();
            psClienteAtualizacao.close();
            logado = false;
        } catch (Exception e) {
            System.out.println("<GERE LIGACAO CLIENTES> Erro ao fechar socket de atualizacao do cliente [" + cliente + "]");
        }
    }

    public void terminaLigacoes() {
        try {
            removeDadosAtualizacao();
            socketPedidos.close();
        } catch (Exception e) {
            System.out.println("<GERE LIGACAO CLIENTES> Erro a terminar ligacao com o cliente [" + cliente + "]");
        }
    }

    public boolean isLogado() {
        return logado;
    }
}

public class GereRecursosClientes {
    private int clientesLigados;
    private ArrayList<ParesComunicacaoCliente> paresComunicacaoClientes;

    public GereRecursosClientes() {
        paresComunicacaoClientes = new ArrayList<>();
        clientesLigados = 0;
    }
    public String novaLigacao(String idCliente, Socket cli) {
        paresComunicacaoClientes.add(new ParesComunicacaoCliente(idCliente, cli));
        return idCliente;
    }
    public Socket getClienteSocketPedidos(String idCliente) {
        for (ParesComunicacaoCliente par : paresComunicacaoClientes) {
            if (par.getSocketID().equals(idCliente)) {
                return par.getSocketPedidos();
            }
        }
        return null;
    }
    public void setClienteDadosAtualizacao(String idCliente, Socket socketAtualizacao, PrintStream psAtualizacao, String nomeCli) {
        for (ParesComunicacaoCliente par : paresComunicacaoClientes) {
            if (par.getSocketID().equals(idCliente)) {
                clientesLigados++;
                //System.out.println("----------------> " + ipCliente);
                par.setUserName(nomeCli);
                par.setDadosAtualizacao(socketAtualizacao, psAtualizacao);
                break;
            }
        }
    }

    public void removeLigacao(String idCliente) {
        for (ParesComunicacaoCliente par : paresComunicacaoClientes) {
            if (par.getSocketID().equals(idCliente)) {
                paresComunicacaoClientes.remove(par);
                break;
            }
        }
    }

    public void removeLogado(String email) {
        for(ParesComunicacaoCliente par : paresComunicacaoClientes) {
            if(par.getCliente().equals(email) && email!= null) {
                par.removeDadosAtualizacao();
                clientesLigados--;
                break;
            }
        }
    }

    public void terminarLigacoes() {
        for (ParesComunicacaoCliente par : paresComunicacaoClientes) {
            par.terminaLigacoes();
        }
    }

    public void enviaAvisoAtualizacao(String msgAtaulizacao) {
        if(clientesLigados > 0)
            for(ParesComunicacaoCliente par : paresComunicacaoClientes) {
                if(par.isLogado()) {
                    par.getClientesAtualizacaoPS().println(msgAtaulizacao);
                    par.getClientesAtualizacaoPS().flush();
                    System.out.println("\t > " + par.getCliente() + ";");
                }
            }
        else
            System.out.println("<GERE LIGACAO CLIENTES> Nao existem clientes ligados");
    }
}