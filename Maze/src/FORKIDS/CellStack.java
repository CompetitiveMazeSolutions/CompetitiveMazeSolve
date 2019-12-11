package FORKIDS;
import java.util.ArrayList;
import java.util.EmptyStackException;

public class CellStack {

	private ArrayList<MazeCell> base;
	public CellStack(){
		base = new ArrayList<MazeCell>();
	}
	public MazeCell peek(){
		if(base.isEmpty()){
			throw new EmptyStackException();
		}else{
			return base.get(base.size()-1);}}
	public MazeCell pop(){
		if(base.isEmpty()){
			throw new EmptyStackException();
		}else{
			return base.remove(base.size()-1);}}
	public void push(MazeCell shove){
		base.add(shove);}
	public int size(){return base.size();}

	public boolean isEmpty(){return base.isEmpty();}
}
