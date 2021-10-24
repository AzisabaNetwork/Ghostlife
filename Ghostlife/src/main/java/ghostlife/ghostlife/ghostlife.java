package ghostlife.ghostlife;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.*;
import java.util.List;

public final class ghostlife extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        Bukkit.getServer().getPluginManager().registerEvents(this, this);
        getCommand("ghostlife").setExecutor(new command(this));
        getCommand("playerskullgive").setExecutor(new command(this));
        getCommand("adddamege").setExecutor(new command(this));
        getCommand("sellmmgui").setExecutor(new command(this));
        getCommand("smg").setExecutor(new command(this));
        saveDefaultConfig();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player player = (Player) e.getView().getPlayer();
        ItemStack slot = e.getCurrentItem();
        if (slot == null) return;
        if (e.getView().getTitle().equals(ChatColor.translateAlternateColorCodes('&', "&cSELLMMITEM MENU"))) {
            if (slot.getType() == Material.GREEN_STAINED_GLASS_PANE) {
                if (slot.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', "&aSHOPを開く"))) {
                    Inventory mirror = Bukkit.createInventory(null, 9, "§cSELLMMITEM SHOP");
                    Location loc = player.getLocation();
                    player.playSound(loc,Sound.BLOCK_CHEST_OPEN, 2, 1);
                    player.openInventory(mirror);
                }
            }else if (slot.getType() == Material.RED_STAINED_GLASS_PANE) {
                if (slot.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', "&cSHOPを閉じる"))) {
                    player.closeInventory();
                    Location loc = player.getLocation();
                    player.playSound(loc, Sound.BLOCK_CHEST_CLOSE, 2, 1);
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cSELLMMSHOP&fを閉じました"));
                }
            } else {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e){
        Inventory backpack = e.getInventory();
        Player player = (Player) e.getPlayer();
        if (e.getView().getTitle().equals(ChatColor.translateAlternateColorCodes('&', "&cSELLMMITEM SHOP"))) {
            ItemStack[] contents = backpack.getContents();
            List<String> itemDisplayNameList = new ArrayList<>();
            double totalMoney = 0;
            for (String key : getConfig().getConfigurationSection("mmitem").getKeys(false)) {
                int moneyamount = getConfig().getInt("mmitem." + key + ".sellprice");
                String ItemDisplayName = getConfig().getString("mmitem." + key + ".itemdisplay");
                for (int i = 0; i < 9; i++) {
                    ItemStack content = contents[i];
                    if (content == null) {
                        continue;
                    }
                    int amount = content.getAmount();
                    int money = amount * moneyamount;
                    if (content.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', "" + ItemDisplayName))) {
                        totalMoney += money;
                        itemDisplayNameList.add(ItemDisplayName);
                    }
                }
            }
            Location loc = player.getLocation();
            player.playSound(loc,Sound.ENTITY_PLAYER_LEVELUP, 2, 1);
            e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', itemDisplayNameList + "&fを売却し" + totalMoney + "&f円獲得しました"));
            getServer().dispatchCommand(getServer().getConsoleSender(), "eco give " + e.getPlayer().getName() + " " + totalMoney);
        }
    }

    @EventHandler
    public void onBlockbreak(BlockBreakEvent e) {
        Player player = e.getPlayer();
        Random random = new Random();
        String world = player.getWorld().getName();
        int num = random.nextInt(30);
        if(world.equals("resource")){
            if (e.getBlock().getType() == Material.OAK_LEAVES) {
                if (num <= 2) {
                    if ((Objects.requireNonNull(player.getInventory().getItemInMainHand().getItemMeta())).getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', "&cトマト採取剣"))) {
                        int emptySlot = player.getInventory().firstEmpty();
                        if (emptySlot == -1) return;
                        Location loc = player.getLocation();
                        player.playSound(loc,Sound.BLOCK_BELL_USE, 2, 1);
                        getServer().dispatchCommand(getServer().getConsoleSender(), "mm i give " + player.getName() + " tomato 2");
                    }
                }
            }
        }
    }
}