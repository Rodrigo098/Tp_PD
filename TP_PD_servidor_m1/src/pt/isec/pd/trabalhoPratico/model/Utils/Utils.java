package pt.isec.pd.trabalhoPratico.model.Utils;


import pt.isec.pd.trabalhoPratico.model.recordDados.Evento;
import pt.isec.pd.trabalhoPratico.model.recordDados.Utilizador;

import java.io.*;

import java.util.List;

public class Utils {
    public static File eventosPresencasCSV(List<Utilizador> users, File csvFile ) {
        String csvSplit = ","; // Delimitador!!

        try (FileWriter writer = new FileWriter(csvFile)) {
            // Escrita do cabeçalho:
            writer.append("Nome");
            writer.append(csvSplit);
            writer.append("Email");
            writer.append(csvSplit);
            writer.append("Numero de estudante");
            writer.append(csvSplit);
            writer.append("\n");

            //Escrita dos dados obtidos da base de dados:
            for (Utilizador user : users) {
                writer.append(user.nome());
                writer.append(csvSplit);
                writer.append(user.email());
                writer.append(csvSplit);
                writer.append((char) user.numIdentificacao());
                writer.append(csvSplit);
                writer.append("\n");
            }
            System.out.println("Ficheiro CSV criado com sucesso");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return csvFile;
    }

    //Ficheiros CSV
    public static File presencasUtilizadorCSV(List<Evento> eventos, File csvFile ) {
        String csvSplit = ","; // Delimitador!!
        System.out.println(eventos.size());

        try (FileWriter writer = new FileWriter(csvFile)) {
            // Escrita do cabeçalho:
            writer.append("Nome do Evento");
            writer.append(csvSplit);
            writer.append("Local");
            writer.append(csvSplit);
            writer.append("Data de Realizacao");
            writer.append(csvSplit);
            writer.append("Hora de Inicio");
            writer.append(csvSplit);
            writer.append("Hora de Fim");
            writer.append("\n");

            //Escrita dos dados obtidos da base de dados:
            for (Evento evento : eventos) {
                writer.append(evento.nomeEvento());
                writer.append(csvSplit);
                writer.append(evento.local());
                writer.append(csvSplit);
                writer.append(evento.data().toString());
                writer.append(csvSplit);
                writer.append(evento.horaInicio() + "");
                writer.append(csvSplit);
                writer.append(evento.horaFim() + "");
                writer.append("\n");
            }
            System.out.println("Ficheiro CSV criado com sucesso");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return csvFile;
    }


}
