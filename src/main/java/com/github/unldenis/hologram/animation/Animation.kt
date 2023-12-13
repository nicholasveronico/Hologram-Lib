/*
 * Hologram-Lib - Asynchronous, high-performance Minecraft Hologram
 * library for 1.8-1.18 servers.
 * Copyright (C) unldenis <https://github.com/unldenis>
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
package com.github.unldenis.hologram.animation

import com.github.unldenis.hologram.line.Line
import org.bukkit.entity.Player
import java.util.function.Supplier

interface Animation {
    fun delay(): Long

    fun nextFrame(player: Player, line: Line)

    fun async(): Boolean

    enum class AnimationType(val type: Supplier<Animation>) {
        CIRCLE(Supplier { CircleAnimation() })
    }
}
