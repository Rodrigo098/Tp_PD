package pt.isec.pd.trabalhoPratico.model.classesPrograma;

import pt.isec.pd.trabalhoPratico.model.classesComunication.*;
import pt.isec.pd.trabalhoPratico.model.recordDados.Evento;
import pt.isec.pd.trabalhoPratico.model.recordDados.Utilizador;

import java.beans.PropertyChangeListener;
import java.io.*;
import java.net.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

///////////////////////////////////// PROGRAMA CLIENTE ///////////////////////
public class ProgramaCliente {
    // TEMPO
    private static final int TEMPO_MAXIMO = 60; // 10 segundos
    private final Timer temporizador = new Timer();
    private int contagem = 0;
    private TimerTask tarefa;
    private boolean terminou = false;
    private static boolean fezLogin = false;
    private static Socket socketAtualizacao;

    // EVENTOS
    private static GereMudancasPLC gereMudancasPLC;

    // COMUNICAÇÃO
    private Socket socketPedidos;
    private int portoServidor;
    private ObjectOutputStream oout;
    private ObjectInputStream oin;
    private InetAddress ipServidor;

//-------------------- ATUALIZACAO ASSINCRONA -----------------
    static class AtualizacaoAsync implements Runnable {
        public AtualizacaoAsync(int porto, InetAddress ip) {
            try {
                System.out.println("threadAtualizacao");
                socketAtualizacao = new Socket(ip, porto);
                PrintStream out = new PrintStream(socketAtualizacao.getOutputStream(), true);
                out.println(new String("socketAtualizacao"));
            } catch (IOException e) {
                gereMudancasPLC.setErros();
            }
        }
        @Override
        public void run() {
            String msgConteudo;
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socketAtualizacao.getInputStream()))){
                while (fezLogin) {
                    msgConteudo = in.readLine();
                    if(msgConteudo != null) {
                        if (msgConteudo.equals("fimServidor")) {
                            System.out.println("<CLIENTE> Servidor terminou.");
                            gereMudancasPLC.setEstadoNaAplicacao(EstadoNaAplicacao.FIM);
                            break;
                        } else if (msgConteudo.equals("atualizacao")) {
                            gereMudancasPLC.setNovaAtualizacao();
                            System.out.println("<CLIENTE> Nova atualizacao.");
                        }
                    }
                }
            } catch (IOException ex) {
                System.out.println("<CLIENTE> Ligacao assíncrona terminou.");
                gereMudancasPLC.setEstadoNaAplicacao(EstadoNaAplicacao.FIM);
            }
        }
    }

//-------------------- VERIFICA LIGACAO -----------------
    class VerificaLigacao extends TimerTask {
        @Override
        public void run() {
            contagem++;
            if ((contagem == TEMPO_MAXIMO && !fezLogin) || terminou) {
                gereMudancasPLC.setEstadoNaAplicacao(EstadoNaAplicacao.EXCEDEU_TEMPO);
                termina();
            }
            System.out.println(contagem);
        }
    }

//-------------------------------------------------------------

    public ProgramaCliente() {
        gereMudancasPLC = new GereMudancasPLC();
        gereMudancasPLC.addPropertyChangeListener(gereMudancasPLC.PROP_ESTADO, est -> verificacaoLigacao());
        gereMudancasPLC.setEstadoNaAplicacao(EstadoNaAplicacao.ENTRADA);
    }


    //////////////////////////// GERE MUDANCAS PCS ////////////////////////////////////

    public void addPropertyChangeListener(String propriedade,  PropertyChangeListener novoListener) {
        gereMudancasPLC.addPropertyChangeListener(propriedade, novoListener);
    }
    private void terminaRecursos() {
        try {
            oin.close();
            oout.close();
            if (socketPedidos != null)
                socketPedidos.close();
            if(socketAtualizacao != null)
                socketAtualizacao.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void termina() {
        terminou = true;
        temporizador.cancel();
        tarefa.cancel();
        gereMudancasPLC.removePropertyChangeListener(gereMudancasPLC.PROP_ESTADO, evt -> verificacaoLigacao());
        terminaRecursos();
    }

    private void verificacaoLigacao() {
        switch (gereMudancasPLC.getEstadoNaAplicacao()) {
            case ENTRADA -> {
                tarefa = new VerificaLigacao();
                temporizador.schedule(tarefa, 0, 1000);
            }
            case SAIR, FIM -> {
                if (!terminou)
                    termina();
            }
            case ADMINISTRADOR, UTILIZADOR -> {
                tarefa.cancel();
                contagem = 0;
                System.out.println("cancelou a tarefa");
            }
        }
    }

    public String getEstadoNaAplicacao() {
        return gereMudancasPLC.getEstadoNaAplicacao().name();
    }
    /////////////////////////////////// FUNCIONALIDADES: ////////////////////////////////
    public boolean verificaFormato(String email) {
        if (email == null || email.isBlank())
            return true;
        if (email.indexOf('@') <= 0 || !(email.indexOf('@') <= email.indexOf('.') - 2))
            return true;
        return email.split("[@.]").length != 3;
    }

    /*---------------------------------- COMUNS: --------------------------------------*/
    public ParResposta criaSocket(List<String> list) {
        ParResposta pontoSituacao = new ParResposta(false, "Erro na criação do socket");
        if (list.size() == 2) {
            try {
                portoServidor = Integer.parseInt(list.get(1));
                ipServidor = InetAddress.getByName(list.get(0));
                socketPedidos = new Socket(ipServidor, portoServidor);
                PrintStream out = new PrintStream(socketPedidos.getOutputStream(), true);
                out.println(new String("socketPedidos"));
                oin = new ObjectInputStream(socketPedidos.getInputStream());
                oout = new ObjectOutputStream(socketPedidos.getOutputStream());

                return new ParResposta(true, "Conexão bem sucedida");
            } catch (IllegalArgumentException e) {
                pontoSituacao = new ParResposta(false, "Introduziu um porto inválido.");
            } catch (NullPointerException e) {
                pontoSituacao = new ParResposta(false, "Introduziu um endereço inválido.");
            } catch (IOException e) {
                pontoSituacao = new ParResposta(false, "Ocorreu uma exceção I/O na criação do socket.");
            }
        } else
            pontoSituacao = new ParResposta(false, "Não foram introduzidos dados suficientes como argumento.");
        terminou = true;
        return pontoSituacao;
    }

    public String login(String email, String password) {
        if(!fezLogin) {
            if (password == null || password.isBlank() || verificaFormato(email))
                return "Tem que preencher os dados corretamente!!";

            Msg_Login dadosLogin = new Msg_Login(email, password);
            try {
                oout.writeObject(dadosLogin);
                oout.flush();

                Object validacao = oin.readObject();

                if (validacao instanceof Geral g) {
                    switch (g.getTipo()) {
                        case WRONG_PASS -> {
                            return "Password incorreta :(";
                        }
                        case INVALIDO -> { return "Não está registado na app :(";}
                        case ERRO -> { return "Ocorreu um erro na BD.";}
                        default -> {
                            try {
                                new Thread(new AtualizacaoAsync(portoServidor, ipServidor)).start();
                                gereMudancasPLC.setEstadoNaAplicacao(g.getTipo() == Message_types.UTILIZADOR ? EstadoNaAplicacao.UTILIZADOR : EstadoNaAplicacao.ADMINISTRADOR);
                                fezLogin = true;
                                return "Estabeleceu ligação!!";
                            } catch (Exception e) {
                                gereMudancasPLC.setErros();
                                gereMudancasPLC.setEstadoNaAplicacao(EstadoNaAplicacao.FIM);
                            }
                            return "Erro ao preparar a aplicação";
                        }
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                gereMudancasPLC.setErros();
                gereMudancasPLC.setEstadoNaAplicacao(EstadoNaAplicacao.FIM);
            }
            return "Tente novamente...";
        }
        return "Já fez login!";
    }

    public void logout(String fonte) {
        Geral logout = new Geral(fonte.equals("WND") ? Message_types.FECHOU_APP : Message_types.LOGOUT);
        try {
            System.out.println("Vai enviar logout");
            oout.writeObject(logout);
            oout.flush();
            gereMudancasPLC.setEstadoNaAplicacao(fonte.equals("WND") ? EstadoNaAplicacao.SAIR : EstadoNaAplicacao.ENTRADA);
            fezLogin = false;
        } catch (IOException e) {
            gereMudancasPLC.setErros();
        }
    }

    public boolean validaHorario(String horaInicio, String horaFim) {
        if(horaInicio != null && !horaInicio.isBlank() && horaFim != null && !horaFim.isBlank()) {
            LocalTime ini, fim;
            try {
                ini = LocalTime.parse(horaInicio, DateTimeFormatter.ofPattern("HH:mm"));
                fim = LocalTime.parse(horaFim, DateTimeFormatter.ofPattern("HH:mm"));
            } catch (Exception e) {
                return false;
            }
            return !ini.isAfter(fim);
        }
        return true;
    }
    public boolean validaDatas(String dataInicio, String dataFim) {
        if(dataInicio != null && !dataInicio.isBlank() && dataFim != null && !dataFim.isBlank()) {
            LocalDate ini, fim;
            try {
                ini = LocalDate.parse(dataInicio, DateTimeFormatter.ofPattern("yyyy/MM/dd"));
                fim = LocalDate.parse(dataFim, DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            } catch (Exception e) {
                return false;
            }
            return !ini.isAfter(fim);
        }
        return true;
    }

    public Evento[] obterListaConsultaEventos(Message_types tipo, String nome, String local, String limData1, String limData2, String horaInicio, String horaFim) {
        if(fezLogin) {
            //return new Evento[]{new Evento("ola", "HelloMate", LocalDate.now(), 11, 12)};
            if(!validaDatas(limData1, limData2)) {
                return null;
            }
            else {
                limData1 = limData1 == null || limData1.isBlank()? null : limData1.toString();
                limData2 = limData2 == null || limData2.isBlank()? null : limData2.toString();
            }

            if(!validaHorario(horaInicio, horaFim))
                return null;
            else {
                horaInicio = horaInicio == null || horaInicio.isBlank()? null : horaInicio;
                horaFim = horaFim == null || horaFim.isBlank()? null : horaFim;
            }

            Msg_ConsultaComFiltros consultaEventos = new Msg_ConsultaComFiltros(tipo, nome, local, limData1, limData2, horaInicio, horaFim);
                try {
                    oout.writeObject(consultaEventos);
                    oout.flush();

                    Object lista = oin.readObject();

                    if (lista instanceof Geral g && g.getTipo() == Message_types.ERRO)
                        return null;//ver os pares personalizados "Ocorreu um erro na BD.";
                    else if (lista instanceof Msg_ListaEventos l && l.getTipo() == Message_types.VALIDO)
                        return l.getLista();
                } catch (IOException | ClassNotFoundException e) {
                    gereMudancasPLC.setErros();
                }
            return null;//new Evento[]{};
        }
        return null;
    }

    public String obterCSV(String caminhoCSV, String nomeFicheiro, Message_types tipoCSV) {
        if(fezLogin) {
            if (caminhoCSV == null || caminhoCSV.isBlank() || nomeFicheiro == null || nomeFicheiro.isBlank())
                return "É necessário inserir um caminho para guardar o ficheiro!";

            String localCSVCaminho;
            File destinoCSV = new File(caminhoCSV);
            byte[] fileChunk = new byte[4000];
            int nbytes;

            if (!destinoCSV.exists()) {
                return "A directoria inserida [" + caminhoCSV + "] não existe!";
            }

            if (!destinoCSV.isDirectory()) {
                return "O caminho [" + caminhoCSV + "] não é uma diretoria!";
            }

            if (!destinoCSV.canWrite()) {
                return "Não pode guardar o .csv em: " + destinoCSV;
            }

            try {
                localCSVCaminho = destinoCSV.getCanonicalPath() + File.separator + nomeFicheiro + ".csv";
            } catch (IOException e) {
                return "Ocorreu um erro ao gerar o csv!";
            }

            Geral csv = new Geral(tipoCSV);

            try (FileOutputStream localFileOutputStream = new FileOutputStream(localCSVCaminho)) {

                oout.writeObject(csv);
                oout.flush();

                InputStream inStream = socketPedidos.getInputStream();

                while ((nbytes = inStream.read(fileChunk)) > 0) {
                    localFileOutputStream.write(fileChunk, 0, nbytes);
                }
                return "CSV gerado com sucesso guardado em: " + localCSVCaminho;
            } catch (IOException e) {
                gereMudancasPLC.setErros();
            }
            return "Erro ao gerar CSV";
        }
        return "Deve fazer login para usufruir da app!";
    }


    /*---------------------------------- UTILIZADOR: --------------------------------------*/
    public ParResposta registarConta(String nome, String email, String numIdentificacao, String password, String confPass) {

        if (nome == null || nome.isBlank() || password == null || password.isBlank() ||
            confPass == null || confPass.isBlank() || !password.equals(confPass) ||
            verificaFormato(email) || numIdentificacao == null || numIdentificacao.isBlank())
            return new ParResposta(false,"Os dados inseriados são inválidos :(");

        int numID;
        try {
            numID = Integer.parseInt(numIdentificacao);
        } catch (NumberFormatException e) {
            return new ParResposta(false,"Insira um número de identificação válido!");
        }

        Mgs_RegistarEditar_Conta dadosRegisto = new Mgs_RegistarEditar_Conta(nome, email, password, numID, Message_types.REGISTO);

        try {
            oout.writeObject(dadosRegisto);
            oout.flush();

            Object validacao = oin.readObject();

            if(validacao instanceof Geral g)
                switch(g.getTipo()) {
                    case ERRO -> { return new ParResposta(false, "Ocorreu um erro na BD.");}
                    case INVALIDO -> { return new ParResposta(false, "O seu registo é inválido");}
                    default -> {
                        if(validacao instanceof Msg_String info) {
                            try {
                                new Thread(new AtualizacaoAsync(portoServidor, ipServidor)).start();
                                gereMudancasPLC.setEstadoNaAplicacao(EstadoNaAplicacao.UTILIZADOR);
                                fezLogin = true;
                                return new ParResposta(true, "Registou-se com sucesso!");
                            } catch (Exception e) {
                                gereMudancasPLC.setErros();
                                gereMudancasPLC.setEstadoNaAplicacao(EstadoNaAplicacao.FIM);
                            }
                        }
                        return new ParResposta(false, "Ocorreu um erro ao preparar a aplicação.");
                    }
                }
        } catch (IOException | ClassNotFoundException ignored) {
            gereMudancasPLC.setErros();
        }
        return new ParResposta(false, "Erro ao registar conta!");
    }

    public String registarPresenca(String evento, String codigoEvento) {
        if(fezLogin) {
            if (evento == null || evento.isBlank() || codigoEvento == null || codigoEvento.isBlank())
                return "Tem de preencher os campos!!";

            try {
                int codigo = Integer.parseInt(codigoEvento);
                Msg_String_Int registoPresenca = new Msg_String_Int(evento, codigo, Message_types.SUBMICAO_COD);

                try {
                    oout.writeObject(registoPresenca);
                    oout.flush();

                    Object validacao = oin.readObject();

                    if (validacao instanceof Geral g)
                        return  g.getTipo() == Message_types.VALIDO ?
                                "Registou-se no evento com sucesso!"
                                : "Ocorreu um erro na BD.";

                } catch (IOException | ClassNotFoundException ignored) {
                    gereMudancasPLC.setErros();
                }
            } catch (NumberFormatException ignored) {
                return "O código deve ser numérico!";
            }
            return "Erro...";
        }
        return "Deve fazer login para usufruir da app!";
    }

    public String editarRegisto(String nome, String numIdentificacao, String password, String confPass) {
        if(fezLogin) {
            if (nome == null || nome.isBlank() || password == null || password.isBlank() || !password.equals(confPass) || numIdentificacao == null || numIdentificacao.isBlank())
                return "Dados de input inválidos :(";

            int numID;
            try {
                numID = Integer.parseInt(numIdentificacao);//?? como é que ponho para long?
                if (numID < 0)
                    return "O teu número acho que não é negativo...";

                Mgs_RegistarEditar_Conta dadosRegisto = new Mgs_RegistarEditar_Conta(nome, null, password, numID, Message_types.EDITAR_REGISTO);

                oout.writeObject(dadosRegisto);
                oout.flush();

                Object validacao = oin.readObject();

                if (validacao instanceof Geral g)
                    return  g.getTipo() == Message_types.VALIDO ?
                            "Registo editado com sucesso!"
                            : "Ocorreu um erro na BD.";

            } catch (NumberFormatException e) {
                return "O teu número deve ser inteiro!";
            } catch (ClassNotFoundException | IOException ignored) {
                gereMudancasPLC.setErros();
            }
            return "Erro...";
        }
        return "Deve fazer login para usufruir da app!";
    }


    /*---------------------------------- ADMINISTRADOR: --------------------------------------*/
    public String criar_Evento(String nome, String local, String data, String horaInicio, String horaFim) {
        if(fezLogin) {
            if (nome == null || nome.isBlank() || local == null || local.isBlank() || data == null || data.isBlank() ||
                    horaInicio == null || horaInicio.isBlank() || horaFim == null || horaFim.isBlank())
                return "Dados devem ser todos preenchidos.";

            LocalDate dataAtual = LocalDate.now(), dataEvento;
            LocalTime horaAtual = LocalTime.now(), HoraInicio, HoraFim;

            try {
                dataEvento = LocalDate.parse(data, DateTimeFormatter.ofPattern("yyyy/MM/dd"));
                HoraInicio = LocalTime.parse(horaInicio, DateTimeFormatter.ofPattern("HH:mm"));
                HoraFim = LocalTime.parse(horaFim, DateTimeFormatter.ofPattern("HH:mm"));
                if (dataEvento.isBefore(dataAtual) || HoraInicio.isBefore(horaAtual))
                    return "Não pode marcar no passado!";
                if(HoraInicio.isAfter(HoraFim))
                    return "A hora de início não pode ser depois da hora de fim!";
            } catch (Exception e) {
                return "Verifique o formato da hora/data!";
            }

            Msg_Cria_Evento evento = new Msg_Cria_Evento(new Evento(nome, local, data, horaInicio, horaFim));

            try {
                oout.writeObject(evento);
                oout.flush();

                Object validacao = oin.readObject();

                if (validacao instanceof Geral g)
                    return  g.getTipo() == Message_types.VALIDO ?
                            "Evento criado com sucesso!"
                            : "Ocorreu um erro na BD.";

            } catch (IOException | ClassNotFoundException ignored) {
                gereMudancasPLC.setErros();
            }
            return "Erro...";
        }
        return "Deve fazer login para usufruir da app!";
    }

    public String editar_Evento(String eventoNomeAntigo, String novoNome, String local, String data, String horaInicio, String horaFim) {
        if(fezLogin) {
            if (eventoNomeAntigo == null || eventoNomeAntigo.isBlank() || novoNome == null || novoNome.isBlank()
                || local == null || local.isBlank() || data == null || data.isBlank()
                || horaInicio == null || horaInicio.isBlank() || horaFim == null || horaFim.isBlank())
                return "Dados devem ser todos preenchidos.";

            LocalDate dataAtual = LocalDate.now(), dataEvento;
            LocalTime horaAtual = LocalTime.now(), HoraInicio, HoraFim;

            try {
                dataEvento = LocalDate.parse(data, DateTimeFormatter.ofPattern("yyyy/MM/dd"));
                HoraInicio = LocalTime.parse(horaInicio, DateTimeFormatter.ofPattern("HH:mm"));
                HoraFim = LocalTime.parse(horaFim, DateTimeFormatter.ofPattern("HH:mm"));
                if (dataEvento.isBefore(dataAtual) || HoraInicio.isBefore(horaAtual))
                    return "Não pode mudar para o passado!";
                if(HoraInicio.isAfter(HoraFim))
                    return "A hora de início não pode ser depois da hora de fim!";
            } catch (Exception e) {
                return "Verifique o formato da hora/data!";
            }

            Msg_Edita_Evento evento = new Msg_Edita_Evento(new Evento(eventoNomeAntigo, local, data, horaInicio, horaFim), novoNome);

            try {
                oout.writeObject(evento);
                oout.flush();

                Object validacao = oin.readObject();

                if (validacao instanceof Geral g)
                    return  g.getTipo() == Message_types.VALIDO ?
                            "Evento editado com sucesso!"
                            : "Ocorreu um erro na BD.";

            } catch (IOException | ClassNotFoundException ignored) {
                gereMudancasPLC.setErros();
            }
            return "Evento não editado!";
        }
        return "Deve fazer login para usufruir da app!";
    }

    public String eliminarEvento(String eventoNome) {
        if(fezLogin) {
            if (eventoNome == null || eventoNome.isBlank())
                return "Evento inexistente...";

            Msg_String evento = new Msg_String(eventoNome, Message_types.ELIMINAR_EVENTO);

            try {
                oout.writeObject(evento);
                oout.flush();

                Object validacao = oin.readObject();

                if (validacao instanceof Geral g)
                    return  g.getTipo() == Message_types.VALIDO ?
                            "Evento eliminado com sucesso!"
                            : "Ocorreu um erro na BD.";

            } catch (IOException | ClassNotFoundException ignored) {
                gereMudancasPLC.setErros();
            }
            return "Evento não eliminado!";
        }
        return "Deve fazer login para usufruir da app!";
    }

    public String eliminaInserePresencas_Eventos(Message_types tipo, String evento, String emailsP) {
        if(fezLogin) {
            if (evento == null || evento.isBlank() || emailsP == null || emailsP.length() == 0 || emailsP.isBlank())
                return "Não foram inseridos emails!";

            ArrayList<String> emails = new ArrayList<>();
            for (String email : emailsP.trim().split(" ")) {
                if (!verificaFormato(email))
                    emails.add(email);
            }

            Msg_EliminaInsere_Presencas interacao = new Msg_EliminaInsere_Presencas(tipo, evento, emails.toArray(new String[0]));

            try {
                oout.writeObject(interacao);
                oout.flush();

                Object validacao = oin.readObject();

                if (validacao instanceof Geral g)
                        return  g.getTipo() == Message_types.VALIDO ?
                                tipo == Message_types.ELIMINA_PRES ? "Presenças eliminadas com sucesso!" : "Presenças inseridas com sucesso!"
                                : "Ocorreu um erro na BD.";

            } catch (IOException | ClassNotFoundException ignored) {
                gereMudancasPLC.setErros();
            }
            return "Tente novamente!";
        }
        return "Deve fazer login para usufruir da app!";
    }

    public String gerarCodPresenca(String evento, String tempoValido) {
        if (fezLogin) {
            if (evento == null || evento.isBlank())
                return "Evento inexistente...";
            if (tempoValido == null || tempoValido.isBlank())
                return "Insira o tempo de validade!";

            int tempo;
            try {
                tempo = Integer.parseInt(tempoValido);
                if (tempo < 0)
                    return "O tempo não pode ser negativo!";
            } catch (NumberFormatException e) {
                return "Tempo deve ser numérico!";
            }

            Msg_String_Int geraCod = new Msg_String_Int(evento, tempo, Message_types.GERAR_COD);

            try {
                oout.writeObject(geraCod);
                oout.flush();

                Object codigo = oin.readObject();

                if (codigo instanceof Geral g)
                    if(g.getTipo() != Message_types.VALIDO)
                        return "Ocorreu um erro ao gerar o código";
                    else if(codigo instanceof Msg_String cod && cod.getTipo() == Message_types.VALIDO)
                            return cod.getConteudo();
            } catch (IOException | ClassNotFoundException ignored) {
                gereMudancasPLC.setErros();
            }
            return "Erro...";
        }
        return "Deve fazer login para usufruir da app!";
    }

    public Utilizador[] consultaPresencasEvento(String evento) {
        if(fezLogin) {
            if (evento != null && !evento.isBlank()) {

                Msg_String consulta = new Msg_String(evento, Message_types.CONSULTA_PRES_EVENT);

                try {
                    oout.writeObject(consulta);
                    oout.flush();

                    Object lista = oin.readObject();

                    if (lista instanceof Geral g && g.getTipo() == Message_types.ERRO)
                        return null;//ver class Par personalizada
                    else if (lista instanceof Msg_ListaRegistos l && l.getTipo() == Message_types.VALIDO)
                            return l.getLista();

                } catch (IOException | ClassNotFoundException ignored) {
                    gereMudancasPLC.setErros();
                }
            }
            return new Utilizador[]{};
        }
        return null;
    }

    public Evento[] consultaEventosDeUmUtilizador(String utilizador) {
        if(fezLogin) {
            if (!verificaFormato(utilizador)) {
                Msg_String consulta = new Msg_String(utilizador, Message_types.CONSULTA_PRES_UTILIZADOR);
                try {
                    oout.writeObject(consulta);
                    oout.flush();

                    Object lista = oin.readObject();

                    if (lista instanceof Geral g && g.getTipo() == Message_types.ERRO)
                        return null;//ver class Par personalizada
                    else if (lista instanceof Msg_ListaEventos l && l.getTipo() == Message_types.VALIDO)
                            return l.getLista();
                } catch (IOException | ClassNotFoundException e) {
                    gereMudancasPLC.setErros();
                }
            }
            return new Evento[]{};
        }
        return null;
    }
}

    /*public void addLogadoListener(InvalidationListener listener) {
        logado.addListener(listener);
    }

    public void addAtualizacaoListener(InvalidationListener listener) {
        atualizacao.addListener(listener);
    }

    public void addErroListener(InvalidationListener listener) {
        erro.addListener(listener);
    }

    protected static synchronized void setErro() {
        erro.set(erro.getValue() + 1);
    }

    public static synchronized void setLogado(String valor) {
        logado.set(valor);
    }

    public String getLogado() {
        return logado.getValue();
    }*/