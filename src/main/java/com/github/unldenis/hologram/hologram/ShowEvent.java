package com.github.unldenis.hologram.hologram;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface ShowEvent {

    void onShow(@NotNull Player player);

}
