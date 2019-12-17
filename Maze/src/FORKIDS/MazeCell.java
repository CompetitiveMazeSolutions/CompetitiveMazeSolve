//package Phase1;
package FORKIDS;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;

import javax.swing.JPanel;
public class MazeCell extends JPanel{
	private boolean[] borders;
	public static final int UP = 0, RIGHT = 1, DOWN=2, LEFT = 3;
	public static final int BLANK=0, VISITED=1, DEAD=2, PATH=3;
	public static final int p1=0, p2=1;

	private int wallThickness = 2;
	private int padding = 2;
	private Stroke str;
	private Color lineColor = Color.WHITE;
	private Color textColor = Color.WHITE.darker();
	private Color defaultBG = Color.GRAY;
	private Color[] colors = {defaultBG, Color.BLUE, Color.BLACK, Color.CYAN};
	private Color[] colorsP = {Color.YELLOW, Color.GREEN, Color.YELLOW.darker(), Color.ORANGE};
	private boolean pHead;
	private int ply;
	private Color plyCo;
	private int needPly;
	private boolean hide;

	private int status;
	private boolean pStat;
	private int row, col;

	public MazeCell(int r, int c, int mode){
		super();
		str = new BasicStroke(wallThickness);
		row =r;
		col = c;
		borders = new boolean[4];
		for(int i=0; i<4; i++)
			borders[i] = true;
		this.setBackground(defaultBG);
		status = BLANK;
		pStat= false;
		pHead = false;
		needPly=mode;
		hide = true;
	}

	public int row(){return row;}
	public int col(){return col;}

	public void setStatus(int x){
		status = x;
		//this.setBackground(colors[status]);
		this.paint(this.getGraphics());
	}
	public void setPStat(boolean trü){
		if(pHead)pHead=false;
		if(pStat&&trü)pHead=true;
		pStat = trü;
		this.paint(this.getGraphics());
	}
	public void setPly(int x, Color colour){
		ply = x;
		plyCo = colour;
		this.paint(this.getGraphics());
	}
	public int getPly(){return ply;}
	public void setGrad(Color beep){
		status = 3;
		colors[status]=beep;
		colorsP[status]=beep;
		this.paint(this.getGraphics());
	}
	public void go(){
		hide = false;
		this.paint(this.getGraphics());
	}

	public boolean isBlank(){return status==BLANK;}
	public boolean isVisited(){return status==VISITED;}
	public boolean isDead(){return status==DEAD;}
	public int getStatus(){return status;}


	public boolean isBlockedUp(){return borders[UP];}
	public boolean isBlockedDown(){return borders[DOWN];}
	public boolean isBlockedLeft(){return borders[LEFT];}
	public boolean isBlockedRight(){return borders[RIGHT];}
	public boolean isBlockedDir(int dir){return borders[dir];}


	public void clearWallUp(){borders[UP] = false; repaint();}
	public void clearWallDown(){borders[DOWN] = false; repaint();}
	public void clearWallRight(){borders[RIGHT] = false; repaint();}
	public void clearWallLeft(){borders[LEFT] = false; repaint();}
	public void clearWallDir(int dir){ borders[dir] = false; repaint();}

	public void paintComponent(Graphics g){
		super.paintComponent(g);

		Color c;
		if(needPly==1){
		if(!pStat){
			c = new Color( colors[status].getRed(), colors[status].getGreen(), colors[status].getBlue(), 150);
		}else{
			c = new Color( colorsP[status].getRed(), colorsP[status].getGreen(), colorsP[status].getBlue(), 150);}
		g.setColor(c);
		if(pHead){c=Color.ORANGE;pHead=false;g.setColor(c);}
		}else{
			if(ply!=0){
				c = new Color(plyCo.getRed(),plyCo.getGreen(),plyCo.getBlue(),200);
				g.setColor(c);}
		}
		int roll = (int)(Math.random()*35)+130;

		if(status==BLANK && !(pStat) && ply==0){
			if(hide){
				g.setColor(Color.WHITE);
			}else{
				g.setColor(new Color(roll,roll,roll));
			}
		}
		g.fillRect( 0, 0, this.getWidth(), this.getHeight());

		//g.fillOval(this.getWidth()/3,this.getHeight()/3,this.getWidth()/3,this.getHeight()/3 );

		g.setColor(lineColor);
		((Graphics2D)g).setStroke(str);
		if(borders[UP])
			g.drawLine(0,0,this.getWidth(),0);
		if(borders[RIGHT])
			g.drawLine(this.getWidth()-padding,0,this.getWidth()-padding,this.getHeight());
		if(borders[DOWN])
			g.drawLine(0,this.getHeight()-padding,this.getWidth(),this.getHeight()-padding);
		if(borders[LEFT])
			g.drawLine(0,0,0,this.getHeight());

		g.setColor(textColor);
		g.drawString(this.toString(), 5, 15);
	}

	public String toString(){
		//return "("+row+", "+col+")";
		char yees = (char)((int)(Math.random()*94)+27);
		return ""+yees;
	}

}
