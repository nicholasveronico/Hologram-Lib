package com.github.unldenis.hologram.line;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.utility.MinecraftVersion;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.github.unldenis.hologram.animation.AbstractAnimation;
import com.github.unldenis.hologram.animation.AnimationType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public abstract class AbstractLine<T> {
    private final Plugin plugin;
    protected final int MINECRAFT_VERSION;
    protected final ProtocolManager protocolManager;
    protected final int entityID;
    protected Location location;

    protected T obj;

    protected Optional<AbstractAnimation> animation = Optional.empty();

    private final Collection<Player> animationPlayers;
    private int taskID = -1;

    private WrappedDataWatcher defaultDataWatcher;

    public AbstractLine(@NotNull Collection<Player> seeingPlayers, @NotNull Plugin plugin, int entityID, @NotNull T obj) {
        this.plugin = plugin;
        this.MINECRAFT_VERSION = MinecraftVersion.getCurrentVersion().getMinor();
        this.protocolManager = ProtocolLibrary.getProtocolManager();
        this.entityID = entityID;
        this.obj = obj;
        this.animationPlayers = seeingPlayers; //copy rif
        if(MINECRAFT_VERSION <9) {
            defaultDataWatcher = getDefaultWatcher(Bukkit.getWorlds().get(0));
        }

    }

    public void setLocation(@NotNull Location location) {
        this.location = location;
    }

    public void set(T newObj) {
        this.obj = newObj;
    }

    public abstract void update(@NotNull Player player);

    public void hide(@NotNull Player player) {
        PacketContainer destroyEntity = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);
        if(MINECRAFT_VERSION < 9) {
            destroyEntity.getIntegerArrays().write(0, new int[] { this.entityID });
        }else{
            destroyEntity.getIntLists().write(0, Collections.singletonList(this.entityID));
        }
        try {
            protocolManager.sendServerPacket(player, destroyEntity);
        } catch (InvocationTargetException invocationTargetException) {
            invocationTargetException.printStackTrace();
        }

    }

    public void show(@NotNull Player player) {
        /*
         * Entity Living Spawn
         */
        final PacketContainer itemPacket = protocolManager.createPacket(PacketType.Play.Server.SPAWN_ENTITY_LIVING);

        if(MINECRAFT_VERSION < 9) {
            itemPacket.getIntegers().
                    write(0, this.entityID).
                    write(1, (int) EntityType.ARMOR_STAND.getTypeId()).
                    write(2, (int) (this.location.getX() * 32)).
                    write(3, (int) (this.location.getY() * 32)).
                    write(4, (int) (this.location.getZ() * 32));
            itemPacket.getDataWatcherModifier().
                    write(0, this.defaultDataWatcher);
        }else{
            final int entityType = 1;
            final int extraData = 1;
            StructureModifier<Integer> itemInts = itemPacket.getIntegers();
            itemInts.write(0, this.entityID);
            itemInts.write(1, entityType);
            itemInts.write(2, extraData);

            StructureModifier<UUID> itemIDs = itemPacket.getUUIDs();
            itemIDs.write(0, UUID.randomUUID());

            StructureModifier<Double> itemDoubles = itemPacket.getDoubles();
            itemDoubles.write(0, this.location.getX());
            itemDoubles.write(1, this.location.getY()/*+1.2*/);
            itemDoubles.write(2, this.location.getZ());
        }

        try {
            protocolManager.sendServerPacket(player, itemPacket);
        } catch (InvocationTargetException invocationTargetException) {
            invocationTargetException.printStackTrace();
        }
    }

    public void setAnimation(@NotNull AnimationType animationType) {
        AbstractAnimation abstractAnimation = animationType.cloned();
        this.animation = Optional.of(abstractAnimation);
        abstractAnimation.setEntityID(this.entityID);
        abstractAnimation.setProtocolManager(this.protocolManager);

        BukkitTask task = Bukkit.getScheduler().runTaskTimer(plugin,
                ()->{ this.animationPlayers.forEach(abstractAnimation::nextFrame);
                }, abstractAnimation.delay(), abstractAnimation.delay());
        this.taskID = task.getTaskId();
    }

    public void removeAnimation() {
        if(taskID != -1) {
            Bukkit.getScheduler().cancelTask(taskID);
            taskID = -1;
        }
    }

    private WrappedDataWatcher getDefaultWatcher(@NotNull World world) {
        Entity entity = world.spawnEntity(new Location(world, 0, 256, 0), EntityType.ARMOR_STAND);
        WrappedDataWatcher watcher = WrappedDataWatcher.getEntityWatcher(entity).deepClone();
        entity.remove();
        return watcher;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractLine<?> that = (AbstractLine<?>) o;
        return entityID == that.entityID && Objects.equals(obj, that.obj);
    }

}
