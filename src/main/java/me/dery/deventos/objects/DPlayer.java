package me.dery.deventos.objects;


public class DPlayer {
	
	private String p;
	private int wins;
	private int loses;
	private int participations;
	
	public DPlayer(final String p, final int wins, final int loses, final int participations) {
		this.p = p;
		this.wins = wins;
		this.loses = loses;
		this.participations = participations;
	}
	
	public void setWins(int wins) {
		this.wins = wins;
	}
	
	public void setLoses(int loses) {
		this.loses = loses;
	}
	
	public void setParticipations(int participations) {
		this.participations = participations;
	}
	
	public String getPlayer() {
		return p;
	}
	
	public int getWins() {
		return wins;
	}
	
	public int getLoses() {
		return loses;
	}
	
	public int getParticipations() {
		return participations;
	}

}
