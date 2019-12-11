package FORKIDS;

public class CellStackTESTER {
	public static void main(String[] args) {
		CellStack s = new CellStack();
		for(int i=0; i<10; i++){
			s.push( new MazeCell(i,i,0));
			System.out.println(s.peek()+": "+s.size());
		}
		System.out.println("~~~~~~~~~~~~~~~~~REMOVING~~~~~~~~~~~");
		while(s.size()>0){
			System.out.println(s.pop()+": "+s.size());
		}
		System.out.println("EMPTY");

	}

}
