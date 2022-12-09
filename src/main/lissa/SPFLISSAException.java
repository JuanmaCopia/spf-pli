package lissa;

/**
 * @author Kasper Luckow
 */
public class SPFLISSAException extends RuntimeException {

  private static final long serialVersionUID = 1122L;

  public SPFLISSAException(String msg) {
    super(msg);
  }

  public SPFLISSAException(Throwable s) {
    super(s);
  }

  public SPFLISSAException(String msg, Throwable s) {
    super(msg, s);
  }
}
