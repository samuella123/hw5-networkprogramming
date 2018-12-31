package hw5.hangman;

/**
 * Created by Samie on 2017-12-14.
 */

public interface SafeOutput {
    public void printResult(String str);
    public void terminatedConnection();
    public void statusMessage(String msg);
    public void resultMessage(String word,String remaining,String result);
}
