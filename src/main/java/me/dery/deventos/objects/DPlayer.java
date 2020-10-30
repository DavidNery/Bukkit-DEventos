package me.dery.deventos.objects;


public class DPlayer {
	
	private String p;
	private int vitorias;
	private int derrotas;
	private int participacoes;
	
	public DPlayer(final String p, final int vitorias, final int derrotas, final int participacoes) {
		this.p = p;
		this.vitorias = vitorias;
		this.derrotas = derrotas;
		this.participacoes = participacoes;
	}
	
	public void setVitorias(int vitorias) {
		this.vitorias = vitorias;
	}
	
	public void setDerrotas(int derrotas) {
		this.derrotas = derrotas;
	}
	
	public void setParticipacoes(int participacoes) {
		this.participacoes = participacoes;
	}
	
	public String getPlayer() {
		return p;
	}
	
	public int getVitorias() {
		return vitorias;
	}
	
	public int getDerrotas() {
		return derrotas;
	}
	
	public int getParticipacoes() {
		return participacoes;
	}

}
