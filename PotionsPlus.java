/*
Copyright (c) 2012, Mushroom Hostage
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of the <organization> nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package me.exphc.PotionsPlus;

import java.util.Collections;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;
import java.util.Iterator;
import java.util.logging.Logger;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Formatter;
import java.lang.Byte;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.io.*;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.*;
import org.bukkit.event.*;
import org.bukkit.event.block.*;
import org.bukkit.event.player.*;
import org.bukkit.event.entity.*;
import org.bukkit.Material.*;
import org.bukkit.material.*;
import org.bukkit.block.*;
import org.bukkit.entity.*;
import org.bukkit.command.*;
import org.bukkit.inventory.*;
import org.bukkit.configuration.*;
import org.bukkit.configuration.file.*;
import org.bukkit.scheduler.*;
import org.bukkit.enchantments.*;
import org.bukkit.potion.*;
import org.bukkit.*;

import net.minecraft.server.CraftingManager;

import org.bukkit.craftbukkit.enchantments.CraftEnchantment;
import org.bukkit.craftbukkit.inventory.CraftItemStack;

class PotionsPlusListener implements Listener {
    PotionsPlus plugin;

    public PotionsPlusListener(PotionsPlus plugin) {
        this.plugin = plugin;

        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    // EntityPotion
    //      List list = Item.POTION.b(this) = MCP Item.potion.getEffects(potionDamage)
    // Event only fires if there are any effects:
    //      if (list != null && !list.isEmpty()) {
    // ItemPotion has a MCP 'private HashMap effectsCache', Bukkit 'private HashMap a()', public List b(int i)
    // builds from MCP PotionHelper.getPotionEffects() = Bukkit PotionBrewer
    /* Interesting list of potion loc strings: (doesn't seem to be use, or even unused! Clear but no Milky..)
    private static final String potionPrefixes[] =
    {
        "potion.prefix.mundane", "potion.prefix.uninteresting", "potion.prefix.bland", "potion.prefix.clear", "potion.prefix.milky", "potion.prefix.diffuse", "potion.prefix.artless", "potion.prefix.thin", "potion.prefix.awkward", "potion.prefix.flat",
        "potion.prefix.bulky", "potion.prefix.bungling", "potion.prefix.buttered", "potion.prefix.smooth", "potion.prefix.suave", "potion.prefix.debonair", "potion.prefix.thick", "potion.prefix.elegant", "potion.prefix.fancy", "potion.prefix.charming",
        "potion.prefix.dashing", "potion.prefix.refined", "potion.prefix.cordial", "potion.prefix.sparkling", "potion.prefix.potent", "potion.prefix.foul", "potion.prefix.odorless", "potion.prefix.rank", "potion.prefix.harsh", "potion.prefix.acrid",
        "potion.prefix.gross", "potion.prefix.stinky"
    };
    */

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled=false) //true) 
    public void onPotionSplash(PotionSplashEvent event) {
        plugin.log.info("Splash  "+event);

        ThrownPotion potion = event.getPotion();

        plugin.log.info("pot = " + potion);

        Collection<PotionEffect> potionEffects = potion.getEffects();

        Collection<LivingEntity> hits = event.getAffectedEntities();

        for (LivingEntity hit: hits) {
            for (PotionEffect potionEffect: potionEffects) {
                plugin.log.info("pe = " + potionEffect);

                plugin.log.info(" amp="+potionEffect.getAmplifier());
                plugin.log.info(" dur="+potionEffect.getDuration());
                plugin.log.info(" type="+potionEffect.getType());
            }
        }
    }
}

public class PotionsPlus extends JavaPlugin implements Listener {
    Logger log = Logger.getLogger("Minecraft");


    public void onEnable() {
        new PotionsPlusListener(this);

        HashMap map = getEffectsCache();

        for (Object key: map.keySet()) {
            Object value = map.get(key);

            log.info("k " + key + " = " + value);
        }

/*
        for (int i = 0; i < 100; i += 1) {
            List list = net.minecraft.server.Item.POTION.b(i);
            log.info("effect " + i + " = " + list);
        }
*/
    }

    /** Get the internal map of potion effects. */
    HashMap getEffectsCache() {
        try {
            Field effectsCacheField = net.minecraft.server.ItemPotion.class.getDeclaredField("a");
            effectsCacheField.setAccessible(true);

            Object obj = effectsCacheField.get(net.minecraft.server.Item.POTION);
            if (obj instanceof HashMap) {
                return (HashMap)obj;
            } else {
                throw new RuntimeException("Unable to access effects cache, unexpected type: " + obj);
            }
        } catch (Exception e) {
            log.severe("Reflection failed, nothing will work: " + e);
            throw new RuntimeException(e);
            // TODO: disable plugin
        }
    }

    public void onDisable() {
    }

}
