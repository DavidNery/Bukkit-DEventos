package me.dery.deventos.integrations;

import org.bukkit.entity.Player;

import me.dery.deventos.DEventos;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;

public class SimpleClansAPI {

	private SimpleClans sc;

	public SimpleClansAPI(DEventos instance) {
		this.sc = (SimpleClans) instance.getServer().getPluginManager().getPlugin("SimpleClans");
	}

	public void disableFriendlyFire(Player p) {
		ClanPlayer cp = sc.getClanManager().getClanPlayer(p);
		if (cp != null)
			cp.setFriendlyFire(false);
	}

}
