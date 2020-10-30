package me.dery.deventos.commands.subcommands.impl;

import java.io.IOException;
import java.lang.reflect.Field;
import java.text.NumberFormat;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.dery.deventos.DEventos;
import me.dery.deventos.commands.subcommands.abstracts.SubCommand;
import me.dery.deventos.enums.eventos.EventoProperty;
import me.dery.deventos.enums.subcommands.SubCommands;
import me.dery.deventos.exceptions.BooleanException;
import me.dery.deventos.exceptions.ValidNumberException;
import me.dery.deventos.managers.EventosManager;
import me.dery.deventos.objects.Evento;

public class SubCmdSet extends SubCommand {

	public SubCmdSet(SubCommands type) { super(type); }

	@Override
	public boolean exec(DEventos instance, CommandSender sender, String[] args) {

		EventoProperty eventoProperty = EventoProperty.valueOf(type.name().replace("SET", ""));

		if (sender.hasPermission("deventos." + type.permissao) || sender.hasPermission("deventos.admin")) {

			if (args.length <= (eventoProperty.fieldRequiredType == Location.class ? 1 : 2)) {
				sendArgsError(instance, sender);
				return true;
			}

			Object value;

			if (eventoProperty.fieldRequiredType == Location.class) {
				if (!(sender instanceof Player)) {
					instance.getConfig().getStringList("Mensagem.Comandos_Console")
						.forEach(msg -> sender.sendMessage(msg.replace("&", "§")));
					return true;
				} else {
					value = instance.getLocationUtils().serializeLocation(((Player) sender).getLocation(), true);
				}
			} else {
				try {
					value = convertToSetType(instance, eventoProperty, args[2]);
				} catch (NumberFormatException e) {
					sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_E_Numero")
						.replace("&", "§"));
					return true;
				} catch (BooleanException e) {
					sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Utilize_True_Ou_False")
						.replace("&", "§"));
					return true;
				} catch (ValidNumberException e) {
					sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Numero_Valido")
						.replace("&", "§"));
					return true;
				}
			}

			EventosManager eventosManager = instance.getEventosManager();

			Evento evento = eventosManager.getEventoByName(args[1]);

			if (evento == null) {
				sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Evento_Invalido")
					.replace("&", "§").replace("{evento}", args[1]));
				return true;
			} else if (type == SubCommands.SETESPECTADOR && !evento.ativarEspectador()) {
				sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Espectador_Desativado")
					.replace("&", "§").replace("{evento}", args[1]));
				return true;
			} else if (type == SubCommands.SETLOBBY && !evento.ativarLobby()) {
				sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Lobby_Desativado")
					.replace("&", "§").replace("{evento}", args[1]));
				return true;
			}

			if (eventoProperty.fieldName != null) {
				try {
					Field eventoField = evento.getClass().getDeclaredField(eventoProperty.fieldName);
					eventoField.setAccessible(true);

					eventoField.set(evento, value);
				} catch (NoSuchFieldException | SecurityException | IllegalArgumentException
					| IllegalAccessException e) {
					e.printStackTrace();

					sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Erro_SetProperty")
						.replace("&", "§").replace("{evento}", evento.getNome())
						.replace("{property}", eventoProperty.name()));
					return true;
				}
			}

			FileConfiguration config = eventosManager.getEventoConfig(evento);
			config.set(eventoProperty.keyInConfig, value);
			try {
				config.save(evento.getFileEvento());
			} catch (IOException e) {
				e.printStackTrace();

				sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Erro_SetProperty").replace("&", "§")
					.replace("{evento}", evento.getNome()).replace("{property}", eventoProperty.name()));
				return true;
			}

			if (value instanceof Number) {

				sender.sendMessage(instance.getConfig()
					.getString("Mensagem.Sucesso." + eventoProperty.keyInConfig + "_Setado")
					.replace("&", "§").replace("{evento}", evento.getNome())
						.replace("{quantidade}", NumberFormat.getNumberInstance().format(value)));

			} else if (value instanceof Boolean) {

				sender.sendMessage(instance.getConfig()
					.getString("Mensagem.Sucesso." + eventoProperty.keyInConfig + "_"
						+ (((Boolean) value) ? "Ativado" : "Desativado"))
					.replace("&", "§").replace("{evento}", evento.getNome()));

			} else if (value instanceof Location) {

				sender.sendMessage(instance.getConfig()
					.getString(
						"Mensagem.Sucesso." + eventoProperty.keyInConfig.replace("Locations.", "") + "_Setado")
					.replace("&", "§").replace("{evento}", evento.getNome()));

			} else { // Por enquanto, só o SETTAG usa String

				sender.sendMessage(instance.getConfig()
					.getString("Mensagem.Sucesso." + eventoProperty.keyInConfig + "_Setado")
					.replace("{value}", String.valueOf(value)).replace("&", "§")
					.replace("{evento}", evento.getNome()));

			}

		} else {
			sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Permissao")
				.replace("&", "§").replace("{1}", args[0]));
			return true;
		}

		return false;
	}

	private Object convertToSetType(DEventos instance, EventoProperty property, String arg)
		throws BooleanException, ValidNumberException {
		if (property.fieldRequiredType == Boolean.class)
			return convertBoolean(arg);
		else if (property.fieldRequiredType == Integer.class)
			return convertInteger(arg);
		else if (property.fieldRequiredType == Double.class)
			return convertDouble(arg);
		else
			return arg;
	}

	private Integer convertInteger(String arg) throws ValidNumberException {
		Integer integer = Integer.parseInt(arg);

		if (integer < 0)
			throw new ValidNumberException();

		return integer;
	}

	private Double convertDouble(String arg) throws ValidNumberException {
		Double doubleNumber = Double.parseDouble(arg);

		if (doubleNumber < 0)
			throw new ValidNumberException();

		return doubleNumber;
	}

	private Boolean convertBoolean(String arg) throws BooleanException {
		if (arg.equalsIgnoreCase("true"))
			return true;
		else if (arg.equalsIgnoreCase("false"))
			return false;
		else
			throw new BooleanException();
	}

}
