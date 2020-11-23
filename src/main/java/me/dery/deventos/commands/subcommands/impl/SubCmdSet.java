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
import me.dery.deventos.enums.events.EventProperty;
import me.dery.deventos.enums.subcommands.SubCommands;
import me.dery.deventos.exceptions.BooleanException;
import me.dery.deventos.exceptions.ValidNumberException;
import me.dery.deventos.managers.EventsManager;
import me.dery.deventos.objects.Event;

public class SubCmdSet extends SubCommand {

	public SubCmdSet(SubCommands type) { super(type); }

	@Override
	public boolean exec(DEventos instance, CommandSender sender, String[] args) {

		EventProperty eventProperty = EventProperty.valueOf(type.name().replace("SET", ""));

		if (sender.hasPermission("deventos." + type.permissao) || sender.hasPermission("deventos.admin")) {

			if (args.length <= (eventProperty.fieldRequiredType == Location.class ? 1 : 2)) {
				sendArgsError(instance, sender);
				return true;
			}

			Object value;

			if (eventProperty.fieldRequiredType == Location.class) {
				if (!(sender instanceof Player)) {
					instance.getConfig().getStringList("Mensagem.Comandos_Console")
						.forEach(msg -> sender.sendMessage(msg.replace("&", "§")));
					return true;
				} else {
					value = instance.getLocationUtils().serializeLocation(((Player) sender).getLocation(), true);
				}
			} else {
				try {
					value = convertToSetType(instance, eventProperty, args[2]);
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

			EventsManager eventsManager = instance.getEventosManager();

			Event event = eventsManager.getEventoByName(args[1]);

			if (event == null) {
				sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Evento_Invalido")
					.replace("&", "§").replace("{evento}", args[1]));
				return true;
			} else if (type == SubCommands.SETESPECTADOR && !event.ativarEspectador()) {
				sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Espectador_Desativado")
					.replace("&", "§").replace("{evento}", args[1]));
				return true;
			} else if (type == SubCommands.SETLOBBY && !event.ativarLobby()) {
				sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Lobby_Desativado")
					.replace("&", "§").replace("{evento}", args[1]));
				return true;
			}

			if (eventProperty.fieldName != null) {
				try {
					Field eventoField = event.getClass().getDeclaredField(eventProperty.fieldName);
					eventoField.setAccessible(true);

					eventoField.set(event, value);
				} catch (NoSuchFieldException | SecurityException | IllegalArgumentException
					| IllegalAccessException e) {
					e.printStackTrace();

					sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Erro_SetProperty")
						.replace("&", "§").replace("{evento}", event.getNome())
						.replace("{property}", eventProperty.name()));
					return true;
				}
			}

			FileConfiguration config = eventsManager.getEventoConfig(event);
			config.set(eventProperty.keyInConfig, value);
			try {
				config.save(event.getFileEvento());
			} catch (IOException e) {
				e.printStackTrace();

				sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Erro_SetProperty").replace("&", "§")
					.replace("{evento}", event.getNome()).replace("{property}", eventProperty.name()));
				return true;
			}

			if (value instanceof Number) {

				sender.sendMessage(instance.getConfig()
					.getString("Mensagem.Sucesso." + eventProperty.keyInConfig + "_Setado")
					.replace("&", "§").replace("{evento}", event.getNome())
						.replace("{quantidade}", NumberFormat.getNumberInstance().format(value)));

			} else if (value instanceof Boolean) {

				sender.sendMessage(instance.getConfig()
					.getString("Mensagem.Sucesso." + eventProperty.keyInConfig + "_"
						+ (((Boolean) value) ? "Ativado" : "Desativado"))
					.replace("&", "§").replace("{evento}", event.getNome()));

			} else if (value instanceof Location) {

				sender.sendMessage(instance.getConfig()
					.getString(
						"Mensagem.Sucesso." + eventProperty.keyInConfig.replace("Locations.", "") + "_Setado")
					.replace("&", "§").replace("{evento}", event.getNome()));

			} else { // Por enquanto, só o SETTAG usa String

				sender.sendMessage(instance.getConfig()
					.getString("Mensagem.Sucesso." + eventProperty.keyInConfig + "_Setado")
					.replace("{value}", String.valueOf(value)).replace("&", "§")
					.replace("{evento}", event.getNome()));

			}

		} else {
			sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Permissao")
				.replace("&", "§").replace("{1}", args[0]));
			return true;
		}

		return false;
	}

	private Object convertToSetType(DEventos instance, EventProperty property, String arg)
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
