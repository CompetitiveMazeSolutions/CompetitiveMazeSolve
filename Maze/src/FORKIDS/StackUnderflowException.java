package FORKIDS;

//StackUnderflowException class

public class StackUnderflowException extends Exception{
    private static final long serialVersionUID = 1L;
   
    public StackUnderflowException(){
        super();
    }
   
    public StackUnderflowException(String msg){
        super(msg);
    }
   
    public StackUnderflowException(Throwable t){
        super(t);
    }
   
    public StackUnderflowException(String msg, Throwable t){
        super(msg, t);
    }
}

