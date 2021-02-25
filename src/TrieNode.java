public class TrieNode {
    //Constants
    private static final int TAMANHO = 40; //Define quantidade de nodos filhos de cada nodo da arvore Trie.

    //Variables
    private char content;
    private TrieNode[] next = new TrieNode[TAMANHO];
    private int movieid;

    //Constructor
    TrieNode(char letter) {
        this.movieid = -1;
        for (int i = 0; i < TAMANHO; i++) {
            this.next[i] = null;
        }
        this.content = letter;
    }

    public TrieNode[] getNext() {
        return next;
    }

    public char getContent() {
        return content;
    }

    public static int getTAMANHO() {
        return TAMANHO;
    }

    public int getMovieid() {
        return movieid;
    }

    /*
        insertString eh utilizado para inserir um filme na arvore trie, colocando seu ID como folha.
        name: Nome do filme;
        id: Id do filme.
     */
    public void insertString(String name, int id) {
        name = name.toUpperCase();
        if (name.length() > 0) {
            for (int i = 0; i < TAMANHO; i++) {
                if (next[i] == null) {
                    next[i] = new TrieNode(name.charAt(0));
                }
                if (this.next[i].content == name.charAt(0)) {
                    this.next[i].insertString(name.substring(1), id);
                    break;
                }
            }
        } else {
            this.movieid = id; //Condicao de parada da recursao.
        }
    }

    /*
        getMovieId eh utilizado para buscar o id de um filme.
        name: nome do filme.
     */
    public int searchMovieId(String name) {
        name = name.toUpperCase();

        if (name.length() > 0) {
            for (int i = 0; i < TAMANHO; i++) {
                if (this.next[i] != null) {
                    if (this.next[i].content == name.charAt(0)) {
                        return this.next[i].searchMovieId(name.substring(1));
                    }
                }
            }
            //Caso nao encontrado:
            return -1;
        } else {
            return this.movieid; //Condicao de parada da recursao.
        }
    }

    

    

}