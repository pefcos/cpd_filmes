/*
    Tabela hash com resolucao de conflitos por encadeamento.
 */

public class UserHashRow {
    private int userid;
    private Review reviews;
    private UserHashRow next;

    UserHashRow(int userid) {
        this.userid = userid;
        this.reviews = null;
        this.next = null;
    }

    public int getUserid() {
        return userid;
    }

    public Review getReviews() {
        return reviews;
    }

    public UserHashRow getNext() {
        return next;
    }

    public void setNext(UserHashRow next) {
        this.next = next;
    }

    public UserHashRow addReviewToUser(int userid, int movieid, float score) {
        if (this.getUserid() == userid) {
            this.addReview(movieid,score);
        } else {
            if (this.getNext() != null) {
                this.setNext(getNext().addReviewToUser(userid, movieid, score));
            } else {
                UserHashRow linked = new UserHashRow(userid);
                linked.addReview(movieid,score);
                this.setNext(linked);
            }
        }
        return this;
    }

    public void addReview(int movieid, float score) {
        Review current = reviews;
        if (current != null) {
            while (current.getNext() != null) {
                current = current.getNext();
            }
            current.setNext(new Review(movieid,score));
        } else {
            reviews = new Review(movieid,score);
        }

    }
}
