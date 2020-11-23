package me.dery.deventos.enums.eventos;

public enum EventStopReason {

	TEMPOATINGIDO("Tempo_Atingido"),
	POUCOSPLAYERS("Poucos_Players"),
	STAFFCANCELOU("Staff_Cancelou"),
	SEMVENCEDOR("Sem_Vencedor");

	public final String propertyInConfig;

	private EventStopReason(String propertyInConfig) { this.propertyInConfig = propertyInConfig; }

}
