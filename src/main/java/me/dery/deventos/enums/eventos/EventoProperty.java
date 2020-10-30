package me.dery.deventos.enums.eventos;

import org.bukkit.Location;
import org.bukkit.Material;

import me.dery.deventos.objects.Evento;

public enum EventoProperty {

	// Strings
	NOME("nome", "Nome", String.class),
	TAG("tag", "TAG", String.class),
	PERMISSAO("permissao", "Permissao", String.class),
	PERMISSAOBYPASS("permissaoByPass", "Permissao_ByPass", String.class),
	PERMISSAOESPECTAR("permissaoEspectar", "Permissao_Espectar", String.class),
	LASTWINNER("lastWinner", "Vencedor", String.class),

	// Strings (locations)
	SPAWN("spawn", "Locations.Spawn", Location.class),
	LOBBY("lobby", "Locations.Lobby", Location.class),
	EXIT("exit", "Locations.Exit", Location.class),
	ESPECTADOR("espectador", "Locations.Espectador", Location.class),

	// Double
	PREMIO("premio", "Premio", Double.class),

	// Booleans
	DESATIVARPVP("desativarPvp", "Desativar_PvP", Boolean.class),
	DESATIVARDAMAGE("desativarDamage", "Desativar_Damage", Boolean.class),
	DESATIVARFOME("desativarFome", "Desativar_Fome", Boolean.class),
	DESATIVARFF("desativarFF", "Desativar_FF", Boolean.class),
	INVVAZIO("requireInvVazio", "Inv_Vazio", Boolean.class),
	ALLOWBYPASSMAXPLAYERS("byPassMax", "Allow_ByPass_Max_Players", Boolean.class),
	ULTIMOVIVOGANHA("ultimoVivoGanha", "Ultimo_Vivo_Ganha", Boolean.class),
	ULTIMOEVENTOGANHA("ultimoEventoGanha", "Ultimo_Player_No_Evento_Ganha", Boolean.class),
	SALVARINV("salvarInv", "Salvar_Inv", Boolean.class),
	ATIVARESPECTADOR("ativarEspectador", "Ativar_Espectador", Boolean.class),
	ATIVARLOBBY("ativarLobby", "Ativar_Lobby", Boolean.class),
	PASSARBLOCO(null, "Passar_Bloco", Boolean.class),
	DARTAG(null, "Dar_TAG", Boolean.class),

	// Inteiros
	TEMPOANUNCIOS("tAnuncios", "Tempo_Anuncios", Integer.class),
	TEMPOACABAREVENTO("tAcabar", "Tempo_Acabar_Evento", Integer.class),
	MINPLAYERS("minPlayers", "Min_Players", Integer.class),
	MAXPLAYERS("maxPlayers", "Max_Players", Integer.class),
	ANUNCIOS(null, "Anuncios", Integer.class),

	// Material
	BLOCOPASSAR("block", "Bloco_Passar", Material.class),

	COMANDOSAOINICIAR(null, "Comandos_Ao_Iniciar", null),
	COMANDOSVENCEDOR(null, "Comandos_Vencedor", null),
	COMANDOSLIBERADOS(null, "Comandos_Liberados", null),
	EFEITOSAOINICIAR(null, "Efeitos_Ao_Iniciar", null),

	MSGVENCEDOR(null, "Mensagem.Vencedor", null),
	MSGEVENTOINICIANDO(null, "Mensagem.Evento_Iniciando", null),
	MSGEVENTOINICIADO(null, "Mensagem.Evento_Iniciado", null),
	MSGEVENTOCANCELADO(null, "Mensagem.Evento_Cancelado", null),

	BANS(null, "Bans", null),
	BAUS(null, "Baus", null);

	public final String fieldName, keyInConfig;

	public final Class<?> fieldRequiredType;

	/**
	 * @param fieldName nome do atributo na classe {@link Evento}. Utilizar <strong>null</strong> se for necessário
	 *            atualizar diretamente na config
	 * @param propertyinconfig a propriedade como está na config
	 * @param fieldRequiredType o tipo do atributo na classe {@link Evento}
	 */
	private EventoProperty(String fieldName, String propertyinconfig, Class<?> fieldRequiredType) {
		this.fieldName = fieldName;
		this.keyInConfig = propertyinconfig;
		this.fieldRequiredType = fieldRequiredType;
	}

}
