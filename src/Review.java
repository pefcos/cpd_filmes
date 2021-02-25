public class Review {
    private float nota;
    private int movieid;
    private Review next;

    /*
        A classe review eh uma lista simplesmente encadeada de reviews feitas por um usuario.
        Essa classe eh utilizada em conjunto com a ABPNode, que se encarrega dos ids dos usuarios.
     */
    Review(int movieid, float nota) {
        this.movieid = movieid;
        this.nota = nota;
        this.next = null;
    }

    public float getNota() {
        return nota;
    }

    public int getMovieid() {
        return movieid;
    }

    public Review getNext() {
        return next;
    }

    public void setNext(Review next) {
        this.next = next;
    }
}
