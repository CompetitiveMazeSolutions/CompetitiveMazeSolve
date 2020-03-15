package consts;

public enum Player
{
	P1, P2, P3, P4;
	public Player teammate() {
		return Player.values()[this.ordinal()^1];
	}
	public boolean isEnemyOf(Player other) {
		if (other == null) return false;
		return this.ordinal() >> 1 != other.ordinal() >> 1; 
	}
}
