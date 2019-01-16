package pa1;

public class Accumulator {

    private long did;
    private double score;

    
    public Accumulator(long did) {
        this.did = did;
    }

    public Accumulator(long did, double score) {
        this.did = did;
        this.score = score;
    }

    public long getDId() {
        return did;
    }

    public void setDId(long did) {
        this.did = did;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
    
}
