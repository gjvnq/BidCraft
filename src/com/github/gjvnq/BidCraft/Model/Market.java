package com.github.gjvnq.BidCraft.Model;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Market {
    private ArrayList<Order> orders;
    private HashSet<Deletable> stuffToDelete;
	public static Market Main = new Market();
	// Here we store the items we want players to be able to get. (We can't transfer items when they are offline)
	protected HashMap<OfflinePlayer, ArrayList<ItemStack>> playerItems;

	public Market() {
		playerItems = new HashMap<OfflinePlayer, ArrayList<ItemStack>>();
		stuffToDelete = new HashSet<Deletable>();
		orders = new ArrayList<Order>();
	}

    void markForDeletion(Deletable stuff) {
    	this.stuffToDelete.add(stuff);
    }

    public void giveTo(OfflinePlayer player, ItemStack items) {
    	playerItems.get(player).add(items);
    }

    // true if the items were removed, false otherwise
    public boolean takeFrom(OfflinePlayer player, ItemStack items) {
	    return playerItems.get(player).remove(items);
    }

    public boolean moveItems(OfflinePlayer from, OfflinePlayer to, ItemStack items) {
    	if (!takeFrom(from, items)) {
    		return false;
	    }
	    giveTo(to, items);
    	return true;
    }

    public void getMyItems(Player player) {
    	throw new NotImplementedException();
    }
}
