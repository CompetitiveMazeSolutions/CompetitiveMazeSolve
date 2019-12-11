package FORKIDS;

import java.util.ArrayList;

/**
 *
 * @author Dharmendra Singh
 */
public class StackAL {
    private ArrayList<Integer> stack = null;
 
    public StackAL(){
        stack = new ArrayList<Integer>();
    }
 
    public void push(int value){
        System.out.println("Push " + value + " in stack");
        stack.add(value);
    }
 
    public void pop() throws StackUnderflowException{
        if(!isEmpty()){
        System.out.println("Pop " + stack.get(stack.size() - 1) + " from stack");
        stack.remove(stack.size() - 1);
        }else{
            throw new StackUnderflowException("Stack is empty !");
        }
    }
 
    public boolean isEmpty(){
        if(stack.size() == 0) return true;
        else return false;
    }
 
    public int top() throws StackUnderflowException{
        if(!isEmpty()){
            return stack.get(stack.size() - 1);
        }else{
            throw new StackUnderflowException("Stack is empty !");
        }
    }
 
    public static void main(String[] args) {
        StackAL st = new StackAL();
        st.push(1);
        st.push(2);
        st.push(3);
     
        try{
            System.out.println("Top entry in stack :" + st.top());
        }catch(StackUnderflowException e){
            System.out.println(e.getMessage());
        }
     
        try{
        st.pop();
        st.pop();
        st.pop();
        st.pop();
        }catch(StackUnderflowException e){
            System.out.println(e.getMessage());
        }
    }
}


