package com.kirazium.petkeys;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Replaces MCPets signal-stick activation with Minecraft's swap-hands key.
 */
public final class KiraziumPetKeysPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        final MCPetsBridge bridge;
        try {
            bridge = MCPetsBridge.create(getLogger());
        } catch (ReflectiveOperationException exception) {
            getLogger().severe("MCPets API bulunamadi veya uyumsuz. Eklenti devre disi birakiliyor: "
                    + exception.getMessage());
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        getServer().getPluginManager().registerEvents(new PetKeyListener(bridge), this);
        getLogger().info("Pet yetenek tusu etkin: F (el degistirme tusu).");
    }
}
