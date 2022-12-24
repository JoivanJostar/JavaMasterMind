import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class ScoreBoard implements Serializable {
    static final long serialVersionUID= 983334665L;
    List<ScoreBoardItem> scores;

    public ScoreBoard() {
        this.scores = new LinkedList<>();
    }

    @Override
    public String toString() {
        String s="GameScoreBoard:\n"+
                "---------------------------------------------------------------------------\n"+
                "id\t\tname\t\tstatus\t\tturns\t\ttime_cost\n";
        for (ScoreBoardItem score : scores) {
            s+=score.getPlayerId()+"\t\t"+score.getPlayerName()+"\t\t"+
                    score.getStatus()+"\t\t"+
                    score.getTurns()+"\t\t"+score.getTimeCost()+"s\n";
        }
        return s;
    }
    public void addItem(ScoreBoardItem item){
        this.scores.add(item);
    }
    public void clear(){
        this.scores.clear();
    }
    public String getWiner(){
        int i=0;
        long mincost=Long.MAX_VALUE;
        ScoreBoardItem winner=null;
        for(i=0;i<scores.size();++i){
            ScoreBoardItem scoreBoardItem = scores.get(i);
            if(scoreBoardItem.getStatus().equals("Finish")&&scoreBoardItem.getTimeCost()<mincost){
                mincost=scoreBoardItem.getTimeCost();
                winner=scoreBoardItem;
            }
        }
        if(winner==null)
            return "No one guesses right! No winner!";
        else{
            return "player id: "+winner.getPlayerId()+" player name: "+winner.getPlayerName()+" is Winner!!\n";
        }
    }
}
