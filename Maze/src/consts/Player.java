package consts;

public enum Player
{
	P1, P2, P3, P4;
	public Player teammate() {
		return Player.values()[this.ordinal()^1];
	}
}
