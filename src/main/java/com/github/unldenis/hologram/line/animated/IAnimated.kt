package com.github.unldenis.hologram.line.animated

import com.github.unldenis.hologram.hologram.Hologram
import com.github.unldenis.hologram.animation.Animation
import com.github.unldenis.hologram.animation.AnimationType
import org.bukkit.entity.Player
import java.util.*

interface IAnimated {
    fun setAnimation(animation: Animation, seeingPlayers: Collection<Player>)

    fun setAnimation(animation: Animation, hologram: Hologram) {
        setAnimation(animation, hologram.seeingPlayers)
    }

    fun setAnimation(animationType: AnimationType, seeingPlayers: Collection<Player>) {
        setAnimation(animationType.type.get(), seeingPlayers)
    }

    fun setAnimation(animationType: AnimationType, hologram: Hologram) {
        setAnimation(animationType, hologram.seeingPlayers)
    }

    fun removeAnimation()

    val animation: Optional<Animation>
}
