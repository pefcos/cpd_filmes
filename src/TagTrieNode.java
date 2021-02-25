public class TagTrieNode {
    //Constants
    private static final int TAMANHO = 40; //Define quantidade de nodos filhos de cada nodo da arvore Trie.

    //Variables
    private char content;
    private TagTrieNode[] next = new TagTrieNode[TAMANHO];
    private MovieList movie_list;

    //Constructor
    TagTrieNode(char letter) {
        this.movie_list = null;
        for (int i = 0; i < TAMANHO; i++) {
            this.next[i] = null;
        }
        this.content = letter;
    }

    public void setContent(char content) {
        this.content = content;
    }

    public char getContent() {
        return content;
    }

    public TagTrieNode[] getNext() {
        return next;
    }

    public void setNext(TagTrieNode[] next) {
        this.next = next;
    }

    public MovieList getMovie_list() {
        return movie_list;
    }

    public void setMovie_list(MovieList movie_list) {
        this.movie_list = movie_list;
    }

    public void appendMovie(int id) {
        if (movie_list != null) {
            movie_list.append(id);
        } else {
            movie_list = new MovieList(id);
        }
    }

    /*
        insertMovie eh utilizado para inserir um filme com a tag escolhida na arvore trie, colocando seu ID como folha.
        name: Nome da tag;
        id: Id do filme.
     */
    public void insertMovie(String name, int id) {
        name = name.toUpperCase().substring(1,name.length()-1);
        TagTrieNode current = this;
        while (name.length() > 0) {
            for (int i = 0; i < TAMANHO; i++) {
                if (current.next[i] == null) {
                    current.next[i] = new TagTrieNode(name.charAt(0));
                    current = current.next[i];
                    break;
                }
                if (current.next[i].getContent() == name.charAt(0)) {
                    current = current.next[i];
                    break;
                }
            }
            name = name.substring(1);
        }
        current.appendMovie(id);
    }

    /*
        searchAllWithTag pega todos os filmes de uma mesma tag.
     */
    public MovieList searchAllWithTag(String tag) {
        String name = tag.toUpperCase();
        TagTrieNode current = this;
        while (name.length() > 0) {
            for (int i = 0; i < TAMANHO; i++) {
                if (current.next[i] == null) {
                    current.next[i] = new TagTrieNode(name.charAt(0));
                    current = current.next[i];
                    break;
                }
                if (current.next[i].getContent() == name.charAt(0)) {
                    current = current.next[i];
                    break;
                }
            }
            name = name.substring(1);
        }
        return current.getMovie_list();
    }
}
