package com.kirazium.petkeys;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

/**
 * Keeps vanilla F behavior unless MCPets actually accepts a pet ability cast.
 */
public final class PetKeyListener implements Listener {

    private final MCPetsBridge bridge;

    PetKeyListener(final MCPetsBridge bridge) {
        this.bridge = bridge;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onSwapHands(final PlayerSwapHandItemsEvent event) {
        if (bridge.castFirstAvailableSignal(event.getPlayer())) {
            event.setCancelled(true);
        }
    }
}
