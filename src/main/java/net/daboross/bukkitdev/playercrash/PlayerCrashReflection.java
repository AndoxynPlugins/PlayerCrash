/*
 * Copyright (C) 2013-2014 Dabo Ross <http://www.daboross.net/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.daboross.bukkitdev.playercrash;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.bukkit.entity.Player;

public class PlayerCrashReflection {
    // Reflection class names

    private static final String CRAFT_PLAYER_CLASS = "org.bukkit.craftbukkit.v%s.entity.CraftPlayer";
    private static final String ENTITY_PLAYER_CLASS = "net.minecraft.server.v%s.EntityPlayer";
    private static final String ENTITY_LIVING_CLASS = "net.minecraft.server.v%s.EntityLiving";
    private static final String PACKET_MOB_SPAWN_CLASS = "net.minecraft.server.v%s.PacketPlayOutSpawnEntityLiving";
    private static final String PLAYER_CONNECTION_CLASS = "net.minecraft.server.v%s.PlayerConnection";
    // Reflection method names
    private static final String CRAFT_PLAYER_GET_HANDLER_METHOD = "getHandle";
    private static final String PLAYER_CONNECTION_SEND_PACKET_METHOD = "sendPacket";
    private static final String ENTITY_PLAYER_PLAYER_CONNECTION_FIELD = "playerConnection";
    // Minecraft version
    private static final String MC_VERSION = "1_7_R1";
    // Runtime classes / methods for reflection
    private final Class<?> craftPlayerClass;
    private final Class<?> entityPlayerClass;
    private final Class<?> entityLivingClass;
    private final Class<?> packetMobSpawnClass;
    private final Class<?> playerConnectionClass;
    private final Method playerConnectionSendPacketMethod;
    private final Method craftPlayerGetHandlerMethod;
    private final Constructor<?> packetMobSpawnConstructor;
    private final Field entityPlayerPlayerConnectionField;

    public PlayerCrashReflection() throws ClassNotFoundException, NoSuchMethodException, SecurityException, NoSuchFieldException {
        // Classes
        craftPlayerClass = Class.forName(String.format(CRAFT_PLAYER_CLASS, MC_VERSION));
        entityPlayerClass = Class.forName(String.format(ENTITY_PLAYER_CLASS, MC_VERSION));
        packetMobSpawnClass = Class.forName(String.format(PACKET_MOB_SPAWN_CLASS, MC_VERSION));
        playerConnectionClass = Class.forName(String.format(PLAYER_CONNECTION_CLASS, MC_VERSION));
        entityLivingClass = Class.forName(String.format(ENTITY_LIVING_CLASS, MC_VERSION));
        // Methods
        craftPlayerGetHandlerMethod = craftPlayerClass.getMethod(CRAFT_PLAYER_GET_HANDLER_METHOD);
        playerConnectionSendPacketMethod = playerConnectionClass.getMethod(PLAYER_CONNECTION_SEND_PACKET_METHOD);
        // Constructors
        packetMobSpawnConstructor = packetMobSpawnClass.getConstructor(entityLivingClass);
        // Fields
        entityPlayerPlayerConnectionField = entityPlayerClass.getField(ENTITY_PLAYER_PLAYER_CONNECTION_FIELD);
    }

    public void crash(Player toCrash) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
        Object entityPlayer = craftPlayerGetHandlerMethod.invoke(toCrash);
        Object playerConnection = entityPlayerPlayerConnectionField.get(entityPlayer);
        Object mobSpawnPacket = packetMobSpawnConstructor.newInstance(entityPlayer);
        playerConnectionSendPacketMethod.invoke(playerConnection, mobSpawnPacket);
    }
}
