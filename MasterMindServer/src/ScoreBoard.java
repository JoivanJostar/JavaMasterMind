import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class ScoreBoard implements Serializable {
    static final long serialVersionUID= 983334665L;
    List<ScoreBoardItem> scores;
    public ScoreBoard() {
        this.scores = new LinkedList<>();
    }
    public void addItem(ScoreBoardItem item){
        this.scores.add(item);
    }
    public void clear(){
        this.scores.clear();
    }

}
