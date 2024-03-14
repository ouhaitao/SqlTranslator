package exception;

/**
 * @author parry 2024/03/14
 */
public class SqlTranslateException extends Exception {
    
    public SqlTranslateException() {
        super();
    }
    
    public SqlTranslateException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public SqlTranslateException(String message) {
        super(message);
    }
    
    public SqlTranslateException(Throwable cause) {
        super(cause == null ? null : cause.getMessage(), cause);
    }
    
}
