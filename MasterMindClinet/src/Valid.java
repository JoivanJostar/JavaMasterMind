public class Valid {
    public boolean check(String s){
        if(s == null){
            return false;
        }
        else{
            if(s.length() != 4){
                return false;
            }
            for(int i = 0; i < s.length(); i++){
                boolean flag = false;
                for(int j = 0; j < GameConfiguration.colors.length; j++){
                    if(GameConfiguration.colors[j].charAt(0) == s.charAt(i)){
                        flag = true;
                    }
                }
                if(!flag){
                    return false;
                }
            }
            return true;
        }
    }
}
