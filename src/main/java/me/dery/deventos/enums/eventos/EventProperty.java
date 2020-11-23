package me.dery.deventos.enums.eventos;

import me.dery.deventos.objects.Event;
import org.bukkit.Location;
import org.bukkit.Material;

public enum EventProperty {

	// Strings
	NOME("nome", "Nome", String.class),
	TAG("tag", "TAG", String.class),
	PERMISSION("permissao", "Permissao", String.class),
	PERMISSIONBYPASS("permissaoByPass", "Permissao_ByPass", String.class),
	PERMISSIONESPECTAR("permissaoEspectar", "Permissao_Espectar", String.class),
	LASTWINNER("lastWinner", "Vencedor", String.class),

	// Strings (locations)
	SPAWN("spawn", "Locations.Spawn", Location.class),
	LOBBY("lobby", "Locations.Lobby", Location.class),
	EXIT("exit", "Locations.Exit", Location.class),
	ESPECTATOR("espectador", "Locations.Espectador", Location.class),

	// Double
	PRIZE("premio", "Premio", Double.class),

	// Booleans
	DISABLEPVP("desativarPvp", "Desativar_PvP", Boolean.class),
	DISABLEDAMAGE("desativarDamage", "Desativar_Damage", Boolean.class),
	DISABLEFOME("desativarFome", "Desativar_Fome", Boolean.class),
	DISABLEFF("desativarFF", "Desativar_FF", Boolean.class),
	EMPTYINV("requireInvVazio", "Inv_Vazio", Boolean.class),
	ALLOWBYPASSMAXPLAYERS("byPassMax", "Allow_ByPass_Max_Players", Boolean.class),
	LASTALIVEWIN("ultimoVivoGanha", "Ultimo_Vivo_Ganha", Boolean.class),
	LASTEVENTWIN("ultimoEventoGanha", "Ultimo_Player_No_Evento_Ganha", Boolean.class),
	SAVEINV("salvarInv", "Salvar_Inv", Boolean.class),
	ENABLEESPECTATOR("ativarEspectador", "Ativar_Espectador", Boolean.class),
	ENABLELOBBY("ativarLobby", "Ativar_Lobby", Boolean.class),
	WALKBLOCK(null, "Passar_Bloco", Boolean.class),
	GIVETAG(null, "Dar_TAG", Boolean.class),

	// Inteiros
	ANNOUNCEMENTTIME("tAnuncios", "Tempo_Anuncios", Integer.class),
	EVENTSTOPTIME("tAcabar", "Tempo_Acabar_Evento", Integer.class),
	MINPLAYERS("minPlayers", "Min_Players", Integer.class),
	MAXPLAYERS("maxPlayers", "Max_Players", Integer.class),
	ANNOUNCEMENTS(null, "Anuncios", Integer.class),

	// Material
	BLOCKWALK("block", "Bloco_Passar", Material.class),

	COMMANDSONSTART(null, "Comandos_Ao_Iniciar", null),
	COMMANDSWINNER(null, "Comandos_Vencedor", null),
	ALLOWEDCOMMANDS(null, "Comandos_Liberados", null),
	EFFECTSONSTART(null, "Efeitos_Ao_Iniciar", null),

	MSGWINNER(null, "Mensagem.Vencedor", null),
	MSGEVENTSTARTING(null, "Mensagem.Evento_Iniciando", null),
	MSGEVENTSTARTED(null, "Mensagem.Evento_Iniciado", null),
	MSGEVENTCANCELLED(null, "Mensagem.Evento_Cancelado", null),

	BANS(null, "Bans", null),
	CHESTS(null, "Baus", null);

	public final String fieldName, keyInConfig;

	public final Class<?> fieldRequiredType;

	/**
	 * @param fieldName nome do atributo na classe {@link Event}. Utilizar <strong>null</strong> se for necessário
	 *            atualizar diretamente na config
	 * @param propertyInConfig a propriedade como está na config
	 * @param fieldRequiredType o tipo do atributo na classe {@link Event}
	 */
	EventProperty(String fieldName, String propertyInConfig, Class<?> fieldRequiredType) {
		this.fieldName = fieldName;
		this.keyInConfig = propertyInConfig;
		this.fieldRequiredType = fieldRequiredType;
	}

}
