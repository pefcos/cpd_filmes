public class MovieList {
    private int movieid;
    private MovieList next;

    /*
        A classe review eh uma lista simplesmente encadeada de reviews feitas por um usuario.
        Essa classe eh utilizada em conjunto com a ABPNode, que se encarrega dos ids dos usuarios.
     */
    MovieList(int movieid) {
        this.movieid = movieid;
        this.next = null;
    }

    public int getMovieid() {
        return movieid;
    }

    public void setMovieid(int movieid) {
        this.movieid = movieid;
    }

    public MovieList getNext() {
        return next;
    }

    public void setNext(MovieList next) {
        this.next = next;
    }

    //Insere um filme na lista.
    public void append(int id) {
        if (next != null) {
            //Trata inserscao da mesma tag no mesmo filme por multiplos usuarios.
            if (movieid == id) {
                return;
            } else {
                next.append(id);
            }
        } else {
            next = new MovieList(id);
        }
    }
}
