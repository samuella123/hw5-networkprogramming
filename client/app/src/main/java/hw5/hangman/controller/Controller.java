package hw5.hangman.controller;

import java.lang.*;
import java.util.regex.*;

import hw5.hangman.SafeOutput;
import hw5.hangman.net.*;
import hw5.hangman.common.*;

/**
 * The Controller class is responsible for handling input commands passed from
 * <code>client.view.Interpreter</code>. Then it calls <code>client.net.ServerConnection</code>
 * to send the processed texts to the seerver. It compares input with types of valid commands which are
 * available in <code>client.controller.CmdType</code>. If its not matched, an error message will be shown.
 * for the commands that needs communication with the server we use <code>common.MsgType</code>
 * We have <code>client.view.SafeOutput</code> reference for printing error and exceptions.
 * defiend message types.
 */
public class Controller
{
  private final ServerConnection serverCon = new ServerConnection();
  private SafeOutput safeOutput;

  /**
   * The constructor saves <code>client.view.SafeOutput</code> reference.
   */
  public Controller(SafeOutput safe) {safeOutput = safe;}

  public SafeOutput getSafeOutput() {return safeOutput;}

  public void setNewSafeOutput(SafeOutput newSafe) throws Exception
  {
      safeOutput = newSafe;
      serverCon.setNewSafeOutput(safeOutput);
  }

  /**
   * Validates the input IP by a regular expression.
   * @param inp It is the input String containing the IP.
   * @return returns a boolean.
   */
  private Boolean validateIP(String inp)
  {
    Pattern p = Pattern.compile("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");
    Matcher m = p.matcher(inp);
    return m.matches();
  }

  /**
   * Validates the input Port by boundings from 1024 to 65535
   * @param inp It is the input String containing the Port.
   * @return returns a boolean.
   */
  private Boolean validatePORT(String inp)
  {
    try
    {
      int prt = Integer.parseInt(inp);
      if( (prt < 1024) || (prt > 65535) )
        throw new Exception();
    }
    catch (Exception e)
    {
      return false;
    }
    return true;
  }

  /**
   * Splites the input string and compares first part with <code>client.controller.CmdType</code>
   * types. and communicates with the <code>client.net.ServerConnection</code> by <code>common.MsgType</code>.
   */
  public void connectServer(String ipStr, String portStr) throws Exception
  {
    if(serverCon.getConnected())
        throw new Exception("You are already connected to a game server, Disconnect first.");

    if(!validateIP(ipStr))
        throw new Exception("IPv4 format is not correct in: " + ipStr);

    if(!validatePORT(portStr))
        throw new Exception("PORT is not correct in: " + portStr);

    //connect code
    serverCon.connect(ipStr,Integer.parseInt(portStr),this.safeOutput);
  }

  public void startGame() throws Exception
  {
    if(!serverCon.getConnected())
        throw new Exception("First you should connect to the server.");

    //start code
    serverCon.sendMessage(MsgType.START.toString());
  }

  public void finishGame() throws Exception
  {
    if(!serverCon.getConnected())
        throw new Exception("First you should connect to the server.");

    //finish code
    serverCon.sendMessage(MsgType.FINISH.toString());
  }

  public void disconnectServer() throws Exception
  {
    if(!serverCon.getConnected())
        throw new Exception("You are not connected to any server.");

    //finish code
    serverCon.disconnect();
  }

  public void guessGame(String guessStr) throws Exception
  {
    if(!serverCon.getConnected())
        throw new Exception("First you should connect to the server.");


    //guess code
    serverCon.sendMessage(MsgType.GUESS.toString()+Constants.MSG_DELIMETER+guessStr);
  }

}
