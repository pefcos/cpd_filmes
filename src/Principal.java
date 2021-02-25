import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/*
    TRABALHO FINAL DA DISCIPLINA DE CLASSIFICACAO E PESQUISA DE DADOS, UFRGS 2020/1.
    FEITO POR: Pedro Fronchetti Costa da Silva.

    Essa eh a classe principal, que contem a funcao principal e suas auxiliares.
 */
public class Principal {
    private static void displayHelp(){
        System.out.println("COMANDOS:");
        System.out.println("movie <titulo ou prefixo> - Utilize para obter dados sobre um filme, como nota media e numero total de avaliacoes.");
        System.out.println("user <ID do usuario> - Utilize para obter dados sobre filmes avaliados por determinado usuario.");
        System.out.println("top<N> <genero> - Utilize para obter os top N filmes do genero escolhido.");
        System.out.println("tags <lista de tags> - Utilize para obter dados sobre filmes com as tags escolhidas.");
        System.out.println("exit - Encerra o programa.\n");
    }

    private static String[] splitTags(String tags) {
        String aux = tags;
        String result[] = new String[0];
        //Enquanto houverem tags.
        while (aux.indexOf('\'') >= 0) {
            aux = aux.substring(aux.indexOf('\'')+1);
            if (aux.indexOf('\'') >= 0) {
                String[] new_result = new String[result.length+1];
                for (int i = 0; i < result.length; i++) {
                    new_result[i] = result[i];
                }
                new_result[new_result.length-1] = aux.substring(0,aux.indexOf('\''));
                aux = aux.substring(aux.indexOf('\'')+1);
                result = new_result;
            } else {
                System.out.println("Erro! Formato de entrada das tags invalido.");
                return null;
            }
        }
        return result;
    }

    /*
        FUNCAO PRINICIPAL DO PROGRAMA, constroi database e realiza buscas.

        Para informacoes sobre as estruturas de dados envolvidas, veja a classe Database no arquivo Database.java.
     */
    public static void main(String args[]) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        boolean end_flag = false;
        try {
            System.out.println("Coloque o caminho para o arquivo movie.csv:");
            String moviepath = reader.readLine();
            System.out.println("Agora coloque o caminho para o arquivo rating.csv:");
            String ratingpath = reader.readLine();
            System.out.println("Agora coloque o caminho para o arquivo tag.csv:");
            String tagpath = reader.readLine();
            System.out.println("\nConstruindo base de dados...");
            Database db = new Database();
            if (db.buildDatabase(moviepath,tagpath,ratingpath)) {
                System.out.println("Base de dados construida com sucessso!");

                System.out.println("\nAgora se tornou possivel realizar buscas na base de dados. Caso precise de ajuda, digite \"-help.\"");
                System.out.println("Para ter uma melhor experiencia, utilize em fullscreen!");
                while (!end_flag) {
                    System.out.println("Digite seu comando abaixo:");
                    String command = reader.readLine();
                    if (command.equals("-help")) {
                        displayHelp();
                    } else if (command.equals("exit")) {
                        end_flag = true;
                    } else if (command.startsWith("movie ")) {
                        String info = command.substring(6);
                        db.getAllMoviesWith(info);
                    } else if (command.startsWith("user ")) {
                        String info = command.substring(5);
                        try {
                            db.getAllFromUser(Integer.parseInt(info));
                        } catch (Exception ex) {
                            System.out.println("ERRO: O argumento do comando user deve ser um numero.");
                        }
                    } else if (command.startsWith("top")) {
                        String info = command.substring(3);
                        String arguments[] = new String[2];
                        try {
                            arguments[0] = info.substring(0, info.indexOf(' '));
                            arguments[1] = info.substring(info.indexOf(' ') + 1);
                            try {
                                int top_number = Integer.parseInt(arguments[0]);
                                db.getTopNMoviesWithGenre(top_number, arguments[1]);
                            } catch (Exception ex) {
                                System.out.println("ERRO: O primeiro argumento do comando user deve ser um numero.");
                            }
                        } catch (Exception ex) {
                            System.out.println("ERRO: Entrada do comando no formato errado");
                        }
                    } else if (command.startsWith("tags ")) {
                        String info = command.substring(5);
                        String tags[] = splitTags(info);
                        db.getAllWithTags(tags);
                    } else {
                        System.out.println("Comando nao reconhecido!");
                    }
                }
            } else {
                System.out.println("Erro ao construir base de dados.");
            }
            System.out.println("Terminando execucao...");
        } catch (IOException ex) {
            System.err.println("An IO Exception was caught!");
            ex.printStackTrace();
        }

    }
}
