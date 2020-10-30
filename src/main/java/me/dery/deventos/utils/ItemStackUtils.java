package me.dery.deventos.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

public class ItemStackUtils {

	private final Random r;

	public ItemStackUtils() { r = new Random(); }

	@SuppressWarnings("deprecation")
	public ItemStack criarItem(String string) {

		String[] partes = string.split(" ");
		Object item = null;

		try {

			for (String parte : partes) {

				if (parte.toLowerCase().startsWith("item:")) {
					String[] id = parte.split("item:");

					if (id[1].contains(",")) {
						String sorteado = id[1].split(",")[r.nextInt(id[1].split(",").length)];
						if (sorteado.contains(":"))
							item = new ItemStack(
								Material.getMaterial(Integer.parseInt(sorteado.split(":")[0])), 1,
								Byte.parseByte(id[1].split(":")[1]));
						else
							item = new ItemStack(
								Material.getMaterial(Integer.parseInt(sorteado)), 1);
					} else {
						if (id[1].contains(":"))
							item = new ItemStack(
								Material.getMaterial(Integer.parseInt(id[1].split(":")[0])), 1,
								Byte.parseByte(id[1].split(":")[1]));
						else
							item = new ItemStack(
								Material.getMaterial(Integer.parseInt(id[1])), 1);
					}
				} else if (parte.toLowerCase().startsWith("pocao:")) {
					String[] id = parte.split("pocao:");
					String tipo;
					if (id[1].contains(","))
						tipo = traduzirPocao(
							id[1].toLowerCase().split(",")[r.nextInt(id[1].split(",").length)]);
					else
						tipo = traduzirPocao(id[1].toLowerCase());

					item = new Potion(PotionType.getByEffect(PotionEffectType.getByName(tipo)));

					int tempo = 1, amplificador = 0;
					boolean splash = false;

					for (String p : partes) {

						if (p.toLowerCase().startsWith("splash:")) {
							splash = Boolean.parseBoolean(p.toLowerCase().split("splash:")[1]);
							if (splash)
								((Potion) item).splash();
						} else if (p.toLowerCase().startsWith("duracao:")) {
							tempo = Integer.parseInt(p.toLowerCase().split("duracao:")[1]);
						} else if (p.toLowerCase().startsWith("amplificador:")) {
							amplificador = Integer
								.parseInt(p.toLowerCase().split("amplificador:")[1]);
						}
					}

					item = ((Potion) item).toItemStack(1);
					PotionMeta pm = (PotionMeta) ((ItemStack) item).getItemMeta();
					pm.addCustomEffect(new PotionEffect(PotionEffectType.getByName(tipo), tempo * 20, amplificador - 1),
						splash);

					((ItemStack) item).setItemMeta(pm);
				} else if (parte.toLowerCase().startsWith("cabeca:")) {
					String[] id = parte.split("cabeca:");
					item = new ItemStack(
						Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
					SkullMeta meta = (SkullMeta) ((ItemStack) item).getItemMeta();
					if (id[1].contains(","))
						meta.setOwner(id[1].split(",")[r.nextInt(id[1].split(",").length)]);
					else
						meta.setOwner(id[1]);
					((ItemStack) item).setItemMeta(meta);
				} else if (parte.toLowerCase().startsWith("enchants:")) {
					String[] enchants = parte.split("enchants:");

					for (String enchant : enchants[1].split(",")) {
						String[] partesenchant = enchant.split(":");
						((ItemStack) item)
							.addUnsafeEnchantment(
								Enchantment.getByName(
									traduzirEnchant(
										partesenchant[0])),
								Integer.parseInt(partesenchant[1]));
					}
				} else if (parte.toLowerCase().startsWith("nome:")) {
					ItemMeta im = ((ItemStack) item).getItemMeta();
					im.setDisplayName(parte.split("nome:")[1].replace("_", " ").replace("&", "§"));
					((ItemStack) item).setItemMeta(im);
				} else if (parte.toLowerCase().startsWith("lore:")) {
					List<String> lore = new ArrayList<String>();

					for (String l : parte.split("(?i)lore:")[1].split("@")) {
						lore.add(l.replace("_", " ").replace("&", "§"));
					}
					ItemMeta im = ((ItemStack) item).getItemMeta();
					im.setLore(lore);
					((ItemStack) item).setItemMeta(im);
				} else if (parte.toLowerCase().startsWith("qnt:")) {
					int qnt = Integer.parseInt(parte.toLowerCase().split("qnt:")[1]);

					if (item.getClass().equals(ItemStack.class)) {
						((ItemStack) item).setAmount(qnt);
					} else {
						((Potion) item).toItemStack(qnt);
					}
				} else if (parte.toLowerCase().startsWith("chance:")) {

					if ((Math.random() * 100) > Integer
						.parseInt(parte.toLowerCase().split("chance:")[1])) {
						return new ItemStack(Material.AIR);
					}
				}
			}

			return (ItemStack) item;

		} catch (Exception e) {
			e.printStackTrace();
			return new ItemStack(Material.AIR);
		}

	}

	private String traduzirEnchant(String enchant) {

		String en = "";

		switch (enchant.toLowerCase()) {
			case "protecao":
				en = "PROTECTION_ENVIRONMENTAL";
				break;
			case "protecao_fogo":
				en = "PROTECTION_FIRE";
				break;
			case "protecao_queda":
				en = "PROTECTION_FALL";
				break;
			case "protecao_explosao":
				en = "PROTECTION_EXPLOSIONS";
				break;
			case "protecao_flecha":
				en = "PROTECTION_PROJECTILE";
				break;
			case "respiracao":
				en = "OXYGEN";
				break;
			case "afinidade_aquatica":
				en = "WATER_WORKER";
				break;
			case "espinhos":
				en = "THORNS";
				break;
			case "afiada":
				en = "DAMAGE_ALL";
				break;
			case "julgamento":
				en = "DAMAGE_UNDEAD";
				break;
			case "ruina_artropodes":
				en = "DAMAGE_ARTHROPODS";
				break;
			case "repulsao":
				en = "KNOCKBACK";
				break;
			case "aspecto_flamejante":
				en = "FIRE_ASPECT";
				break;
			case "pilhagem":
				en = "LOOT_BONUS_MOBS";
				break;
			case "eficiencia":
				en = "DIG_SPEED";
				break;
			case "toque_suave":
				en = "SILK_TOUCH";
				break;
			case "inquebravel":
				en = "DURABILITY";
				break;
			case "fortuna":
				en = "LOOT_BONUS_BLOCKS";
				break;
			case "forca":
				en = "ARROW_DAMAGE";
				break;
			case "impacto":
				en = "ARROW_KNOCKBACK";
				break;
			case "chama":
				en = "ARROW_FIRE";
				break;
			case "infinidade":
				en = "ARROW_INFINITE";
				break;
		}

		return en;

	}

	public String traduzirPocao(String pocao) {

		String po = "";

		switch (pocao.toLowerCase()) {
			case "velocidade":
				po = "SPEED";
				break;
			case "forca":
				po = "INCREASE_DAMAGE";
				break;
			case "lentidao":
				po = "SLOW";
				break;
			case "escavar-rapido":
				po = "FAST_DIGGING";
				break;
			case "escavar-lento":
				po = "SLOW_DIGGING";
				break;
			case "vida-instantanea":
				po = "HEAL";
				break;
			case "dano-instantaneo":
				po = "HARM";
				break;
			case "pulo":
				po = "JUMP";
				break;
			case "nausea":
				po = "CONFUSION";
				break;
			case "regeneracao":
				po = "REGENERATION";
				break;
			case "resistencia":
				po = "DAMAGE_RESISTANCE";
				break;
			case "resistencia-fogo":
				po = "FIRE_RESISTANCE";
				break;
			case "resistencia-agua":
				po = "WATER_BREATHING";
				break;
			case "invisibilidade":
				po = "INVISIBILITY";
				break;
			case "cegueira":
				po = "BLINDNESS";
				break;
			case "visao-noturna":
				po = "NIGHT_VISION";
				break;
			case "fome":
				po = "HUNGER";
				break;
			case "fraqueza":
				po = "WEAKNESS";
				break;
			case "veneno":
				po = "POISON";
				break;
		}

		return po;

	}

}
