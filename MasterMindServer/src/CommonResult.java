
import java.io.Serializable;

public class CommonResult implements Serializable {
    public enum Flag{
        Quit,REGIST,RULE,BROWSE,JOIN,GAME_START,GAMING,GAME_END,BOARD
    }
    static final long serialVersionUID= 12345678L;
    private Flag flag;// the data type
    private Object resultData;
    private String message; //some error msg

    public CommonResult setFlag(Flag flag) {
        this.flag = flag;
        return this;
    }

    public Flag getFlag() {
        return flag;
    }

    public Object getResultData() {
        return resultData;
    }

    public CommonResult setResultData(Object resultData) {
        this.resultData = resultData;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public CommonResult setMessage(String message) {
        this.message = message;
        return this;
    }

}