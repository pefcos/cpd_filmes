public class MovieHashRow {
    //Variaveis.
    private int id;
    private String name;
    private String[] genres;
    private float average;
    private int reviews;

    MovieHashRow(int id, String name, String[] genres, float average, int reviews) {
        this.average = average;
        this.name = name;
        this.id = id;
        this.reviews = reviews;
        this.genres = new String[genres.length];
        for (int i = 0; i < genres.length; i++) {
            this.genres[i] = genres[i];
        }
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String[] getGenres() {
        return genres;
    }

    public float getAverage() {
        return average;
    }

    public void addReview(float new_score) {
        float total = this.average * this.reviews;
        (this.reviews)++;
        total = total + new_score;
        this.average = total/this.reviews;
    }

    public int getReviews() {
        return reviews;
    }
}
