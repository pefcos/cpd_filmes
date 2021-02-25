import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/*
    A classe Database eh responsavel por todas as estruturas de dados especificadas na parte 1 do trabalho.
    private TrieNode movies: Arvore trie de filmes.
    private MovieHashRow[] movie_hashtable: Tabela hash de filmes.
    private UserHashRow[] user_hashtable: Tabela hash de usuarios.
    private TagTrieNode tags: Arvore trie de tags.

    Justificativas para escolhas das estruturas livres:
        Estrutura Livre 1 (Tabela Hash):    Utilizando como base o numero do usuario, fica pratico gerar um endereco valido da tabela,
                                            assim como gera menores tempos de busca em relacao a arvores. Os conflitos nessa tabela sao
                                            resolvidos por encadeamento, utilizando um numero primo suficientemente grande para reduzir
                                            a quantidade de encadeamentos por endereco. Para cada usuario tambem existe uma lista
                                            simplesmente encadeada de avaliacoes. A escolha da lista se deve ao fato de que nao sabemos
                                            quantas avaliacoes o usuario fez, portanto, nao podemos alocar um array de tamanho fixo.
        Estrutura Livre 2 (Arvore Trie):    A escolha da arvore trie se deve ao fato de ser o melhor metodo para guardar strings, quando
                                            nao se sabe a quantidade de tags e seus variados tamanhos. Nos nodos base das tags temos uma
                                            lista simplesmente encadeada de ids de filmes que possuem determinada tag.
        As justificativas apresentadas me levaram a repetir as estruturas obrigatorias, decisao que nao me agradou inicialmente. Porem,
        com o tempo, a mesma se mostrou uma boa decisao, embora possa parecer pouco criativa.
 */
public class Database {
    private static final int MOVIE_TABLESIZE = 32749; //Constante que define o tamanho da tabela hash. Primeiro primo apos (numero de chaves + 20%).
    private static final int USER_TABLESIZE = 90001; //Numero primo suficientemente grande para tentar reduzir numero de encadeamentos por endereco.
    private TrieNode movies = null;
    private MovieHashRow[] movie_hashtable = new MovieHashRow[MOVIE_TABLESIZE];
    private UserHashRow[] user_hashtable = new UserHashRow[USER_TABLESIZE];
    private TagTrieNode tags = null;

    public TrieNode getMovies() {
        return movies;
    }

    public MovieHashRow[] getMovieHashtable() {
        return movie_hashtable;
    }

    /*
        FUNCOES DA TABELA HASH DE FILMES.

        Resolve conflitos por endereçamento aberto com busca quadratica.
     */
    private int generateMovieHash (int movieid, int tentativas) {
        return (movieid + tentativas + (tentativas * tentativas)) % MOVIE_TABLESIZE;
    }

    private void insertMovieInMovieHash(MovieHashRow movie) {
        int tentativas = 0;
        int insertion = 0;
        do {
            insertion = generateMovieHash(movie.getId(),tentativas);
            tentativas++;
        } while (this.movie_hashtable[insertion] != null);
        this.movie_hashtable[insertion] = movie;
    }

    private boolean todosVisitados(boolean[] visitados) {
        for (int i = 0; i < visitados.length; i++) {
            if (!(visitados[i])) {
                return false;
            }
        }
        return true;
    }

    private MovieHashRow getMovieInHash(int id) {
        int tentativas = 0;
        int insertion = 0;
        boolean[] visitados = new boolean[MOVIE_TABLESIZE];
        do {
            insertion = generateMovieHash(id,tentativas);
            tentativas++;
        } while ((this.movie_hashtable[insertion].getId() != id) && !todosVisitados(visitados));
        if (todosVisitados(visitados)) {
            return null;
        }
        return this.movie_hashtable[insertion];
    }

    /*
        FUNCOES DA TABELA HASH DE USUARIOS.

        Resolve conflitos por encadeamento.
     */
    private int generateUserHash(int userid) {
        return userid % USER_TABLESIZE;
    }

    public void insertReviewInHash(int userid, int movieid, float score) {
        int hash = generateUserHash(userid);
        if (user_hashtable[hash] != null) {
            user_hashtable[hash] = user_hashtable[hash].addReviewToUser(userid, movieid, score);
        } else {
            user_hashtable[hash] = new UserHashRow(userid);
            user_hashtable[hash].addReview(movieid,score);
        }
    }

    /*
        FUNCOES RESPONSAVEIS PELO PRINT E BUSCA DE FILMES POR PREFIXO.
        Comando: movie <prefix>.
     */

    //Printa detalhes.
    public void printMovieQueryDetails(MovieHashRow row) {
        String genres = "";
        for (int i = 0; i < row.getGenres().length; i++) {
            if (!row.getGenres()[i].equals("")) {
                genres += row.getGenres()[i] + "|";
            } else {
                break;
            }
        }
        String name = row.getName();
        if (name.length() > 80) {
            name = name.substring(0,77);
            name += "...";
        }
        genres = genres.substring(0,genres.length()-1);
        System.out.printf("|%7s %80s %60s %10s %6s|\n",Integer.toString(row.getId()),name,genres,row.getAverage(),row.getReviews());
    }

    //Busca prefixo.
    public void searchWithPrefix(TrieNode base) {
        for (int i = 0; i < movies.getTAMANHO(); i++) {
            if (base.getNext()[i] == null) {
                //Verifica se esse eh realmente um filme que existe.
                if (base.getMovieid() != -1) {
                    printMovieQueryDetails(getMovieInHash(base.getMovieid()));
                }
                return;
            } else {
                searchWithPrefix(base.getNext()[i]);
            }
        }
    }

    //Processa print da tabela e chamadas de busca.
    public void getAllMoviesWith(String prefix) {
        TrieNode current = movies;
        String search = prefix.toUpperCase();
        //Print de referencia pro output:
        System.out.printf("\n|=======================================================================================================================================================================|\n");
        System.out.printf("|%7s %80s %60s %10s %6s|\n","MovieID","Title","Genres","Rating","Count");
        System.out.printf("|=======================================================================================================================================================================|\n");
        while (search.length() > 0) {
            for (int i = 0; i < movies.getTAMANHO(); i++) {
                if (current.getNext()[i] == null) {
                    return; //Caso nao haja nenhum filme com o prefixo.
                }
                if (current.getNext()[i].getContent() == search.charAt(0)) {
                    current = current.getNext()[i];
                    search = search.substring(1);
                    break;
                }
            }
        }
        //Aqui o prefixo ja foi encontrado e precisamos apenas printar todos os nodos referentes a esse prefixo.
        searchWithPrefix(current);
        //Print de fechamento
        System.out.printf("|=======================================================================================================================================================================|\n\n");
    }

    /*
        FUNCOES RESPONSAVEIS PELO PRINT E BUSCA DE USUARIOS POR ID.
        Comando: user <id>.
     */

    //Printa os detalhes das reviews do usuario.
    public void printUserReviewDetails(UserHashRow user) {
        Review review = user.getReviews();
        MovieHashRow row = null;
        String name = null;
        while (review != null) {
            row = getMovieInHash(review.getMovieid());
            name = row.getName();
            if (name.length() > 80) {
                name = name.substring(0,77);
                name += "...";
            }
            System.out.printf("|%12s %80s %14s %6s|\n",review.getNota(),name,row.getAverage(),row.getReviews());
            review = review.getNext();
        }
    }

    //Cuida dos prints da tabela e chamadas de funcao.
    public void getAllFromUser(int userid) {
        UserHashRow usuario = user_hashtable[generateUserHash(userid)];
        System.out.printf("\n|===================================================================================================================|\n");
        System.out.printf("|%12s %80s %14s %6s|\n","User Rating","Title","Global Rating","Count");
        System.out.printf("|===================================================================================================================|\n");
        while (usuario != null) {
            if (usuario.getUserid() == userid && usuario.getReviews() != null) {
                printUserReviewDetails(usuario);
                System.out.printf("|===================================================================================================================|\n");
                return;
            } else {
                usuario = usuario.getNext();
            }
        }
    }

    /*
        FUNCOES RESPONSAVEIS PELO PRINT E BUSCA DE FILMES TOP DE UM GENERO.
        Comando: top<N> <genre>.
     */

    //AUXILIAR: Faz shift right dos elementos ate a posicao start_index.
    private void shiftArray(MovieHashRow array[], int start_index) {
        for (int i = array.length-2; i >= start_index; i--) {
            array[i+1] = array[i];
        }
    }

    //AUXILIAR: Insere elemento em um determinado indice do array.
    private void insertInPlace(MovieHashRow array[], MovieHashRow row) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] != null) {
                if (row.getAverage() > array[i].getAverage()) {
                    shiftArray(array, i);
                    array[i] = row;
                    break;
                }
            } else {
                array[i] = row;
                break;
            }
        }
    }

    //Faz print dos top movies.
    public void printTopMovies(MovieHashRow order[]) {
        if (order[0] != null) {
            for (int i = 0; i < order.length; i++) {
                if (order[i] == null) {
                    break;
                }
                String genres = "";
                for (int j = 0; j < order[i].getGenres().length; j++) {
                    if (!order[i].getGenres()[j].equals("")) {
                        genres += order[i].getGenres()[j] + "|";
                    } else {
                        break;
                    }
                }
                String name = order[i].getName();
                if (name.length() > 80) {
                    name = name.substring(0,77);
                    name += "...";
                }
                System.out.printf("|%80s %60s %10s %6s|\n",name,genres,order[i].getAverage(),order[i].getReviews());
            }
            System.out.printf("|===============================================================================================================================================================|\n");
        }
    }

    //Busca os top movies do genero especificado.
    public void getTopNMoviesWithGenre(int number, String genre) {
        MovieHashRow review_order[] = new MovieHashRow[number];
        java.util.Arrays.fill(review_order,null);
        System.out.printf("\n|===============================================================================================================================================================|\n");
        System.out.printf("|%80s %60s %10s %6s|\n","Title","Genres","Rating","Count");
        System.out.printf("|===============================================================================================================================================================|\n");
        for (int i = 0; i < MOVIE_TABLESIZE; i++) {
            MovieHashRow movie = movie_hashtable[i];
            int j = 0;
            if (movie != null) {
                if (movie.getReviews() >= 1000 && java.util.Arrays.asList(movie.getGenres()).contains(genre)) {
                    if (review_order[review_order.length-1] == null) {
                        insertInPlace(review_order,movie);
                    } else if (movie.getAverage() > review_order[review_order.length-1].getAverage()) {
                        insertInPlace(review_order,movie);
                    }
                }
            }
        }
        printTopMovies(review_order);
    }

    /*
        FUNCOES RESPONSAVEIS PELO PRINT E BUSCA DOS FILMES COM AS TAGS.
        Comando: tags <tag_list>.
     */

    //Transforma a lista encadeada list em um array de ids.
    public int[] addMoviesToArray(MovieList list) {
        int moviecount = 0;
        MovieList pointer = list;
        //Para evitar redimensionamento do array de saida, percorremos uma vez a lista encadeada para ver o tamanho.
        while (pointer != null) {
            moviecount++;
            pointer = pointer.getNext();
        }
        //Aqui ele transfere os ids.
        int ids[] = new int[moviecount];
        int i = 0;
        pointer = list;
        while (pointer != null) {
            ids[i] = pointer.getMovieid();
            i++;
            pointer = pointer.getNext();
        }
        //Retorna lista.
        return ids;
    }

    //AUXILIAR: Testa se numero esta no array.
    private boolean isInArray(int[] array, int number) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == number) {
                return true;
            }
        }
        return false;
    }

    //AUXILIAR: Retorna array com numeros que estao em ambos os arrays de entrada.
    private int[] inBothArrays(int[] array1, int[] array2) {
        int[] result = new int[0];
        for (int i = 0; i < array1.length; i++) {
            if (isInArray(array2,array1[i])) {
                int[] new_result = new int[result.length + 1];
                System.arraycopy(result, 0, new_result, 0, result.length);
                new_result[new_result.length-1] = array1[i];
                result = new_result;
            }
        }
        return result;
    }

    //Printa e busca filmes que contenham todas as tags.
    public void getAllWithTags(String[] tags) {
        if (tags.length > 0) {
            System.out.printf("\n|===============================================================================================================================================================|\n");
            System.out.printf("|%80s %60s %10s %6s|\n", "Title", "Genres", "Rating", "Count");
            System.out.printf("|===============================================================================================================================================================|\n");
            int filmes_validos[] = null;
            //Faz primeira busca da trie para inicializar o array de filmes validos.
            filmes_validos = addMoviesToArray(this.tags.searchAllWithTag(tags[0]));
            for (int i = 1; i < tags.length; i++) {
                //Busca todos os filmes com outra tag.
                int outra_tag[] = addMoviesToArray(this.tags.searchAllWithTag(tags[i]));
                //Elimina as tags que nao estao em ambas as listas.
                filmes_validos = inBothArrays(filmes_validos, outra_tag);
            }
            if (filmes_validos.length > 0) {
                for (int i = 0; i < filmes_validos.length; i++) {
                    String genres = "";
                    MovieHashRow row = getMovieInHash(filmes_validos[i]);
                    for (int j = 0; j < row.getGenres().length; j++) {
                        if (!row.getGenres()[j].equals("")) {
                            genres += row.getGenres()[j] + "|";
                        } else {
                            break;
                        }
                    }
                    String name = row.getName();
                    if (name.length() > 80) {
                        name = name.substring(0, 77);
                        name += "...";
                    }
                    System.out.printf("|%80s %60s %10s %6s|\n", name, genres.substring(0,genres.length()-1), row.getAverage(), row.getReviews());
                }
                System.out.printf("|===============================================================================================================================================================|\n");
            }
        } else {
            System.out.println("No valid tags found! Tags must be inserted between \' like in this example: \'tag\'");
        }
    }

    /*
        CONSTRUCAO DA BASE DE DADOS.
        Inicializa e faz estruturas de dados.
     */

    public boolean buildDatabase(String moviepath, String tagpath, String ratingpath) {
        movies = new TrieNode(' '); //Base da arvore trie.
        tags = new TagTrieNode(' '); //Base da arvore trie.
        long startTime = System.currentTimeMillis();
        /*
            MOVIE.CSV
         */
        System.out.println("Lendo movie.csv");
        try {
            Scanner scanner = new Scanner(new File(moviepath));
            scanner.nextLine();
            String[] lista = new String[4];
            String[] moviegens = new String[10];
            while (scanner.hasNextLine()) {
                /*
                    Aqui fica o procedimento que lida com todas as estruturas de dados que necessitam do movie.csv.
                    Isso torna a leitura do arquivo mais eficiente, uma vez que nao eh necessario percorrer o mesmo arquivo mais de uma vez.
                 */
                String linha = scanner.nextLine();
                lista = linha.split("\"");
                int movieid = Integer.parseInt(lista[0].substring(0, lista[0].length() - 1));
                String moviename = lista[1];
                moviegens = lista[3].split("\\|");

                //Insere filme na arvore Trie.
                movies.insertString(moviename,movieid);

                //Insere filme na tabela hash (Dados incompletos, serao completados com o rating.csv).
                MovieHashRow filme = new MovieHashRow(movieid,moviename,moviegens,0,0);
                insertMovieInMovieHash(filme);
            }
        } catch (IOException ex) {
            System.err.println("An IO Exception was caught!");
            ex.printStackTrace();
            return false;
        }

        /*
            RATING.CSV
         */
        System.out.println("Lendo rating.csv (isso pode demorar)");
        try {
            Scanner scanner = new Scanner(new File(ratingpath));
            scanner.nextLine();
            String[] lista = new String[3];
            while (scanner.hasNextLine()) {
                /*
                    Aqui fica o procedimento que lida com todas as estruturas de dados que necessitam do rating.csv.
                    Isso torna a leitura do arquivo mais eficiente, uma vez que nao eh necessario percorrer o mesmo arquivo mais de uma vez.
                 */
                String linha = scanner.nextLine();
                lista = linha.split(",");
                int userid = Integer.parseInt(lista[0]);
                int movieid = Integer.parseInt(lista[1]);
                float rating = Float.parseFloat(lista[2]);

                //Complementa reviews na hash table de filmes.
                MovieHashRow referencia = getMovieInHash(movieid);
                referencia.addReview(rating);

                //Insercao na tabela hash de usuarios.
                insertReviewInHash(userid,movieid,rating);
            }
        } catch (IOException ex) {
            System.err.println("An IO Exception was caught!");
            ex.printStackTrace();
            return false;
        }

        /*
            TAG.CSV
         */
        System.out.println("Lendo tag.csv");
        try {
            Scanner scanner = new Scanner(new File(tagpath));
            scanner.nextLine();
            String[] lista = new String[4];
            String[] moviegens = new String[10];
            while (scanner.hasNextLine()) {
                /*
                    Aqui fica o procedimento que lida com todas as estruturas de dados que necessitam do tag.csv.
                    Isso torna a leitura do arquivo mais eficiente, uma vez que nao eh necessario percorrer o mesmo arquivo mais de uma vez.
                 */
                String linha = scanner.nextLine();
                lista = linha.split(",");
                int userid = Integer.parseInt(lista[0]);
                int movieid = Integer.parseInt(lista[1]);
                String tag = lista[2];

                //Insere filme na arvore Trie.
                tags.insertMovie(tag,movieid);
            }
        } catch (IOException ex) {
            System.err.println("An IO Exception was caught!");
            ex.printStackTrace();
            return false;
        }
        long stopTime = System.currentTimeMillis();
        System.out.println("Base de dados construida em: " + String.valueOf((stopTime - startTime)/1000.0) + "s");
        return true;
    }
}
