package org.holoeasy.line


import org.bukkit.Location
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin
import org.holoeasy.ext.send
import org.holoeasy.packet.PacketType
import org.holoeasy.reactive.MutableState
import org.holoeasy.util.VersionEnum
import org.holoeasy.util.VersionUtil

class BlockLine(plugin: Plugin, obj: MutableState<ItemStack>) : ILine<ItemStack> {
    constructor(plugin: Plugin, obj: ItemStack) : this(plugin, MutableState(obj))


    private val line: Line = Line(plugin, EntityType.ARMOR_STAND)
    private val _mutableStateOf = obj
    private var firstRender = true


    override val plugin: Plugin
        get() = line.plugin

    override val type: ILine.Type
        get() = ILine.Type.BLOCK_LINE

    override val entityId: Int
        get() = line.entityID

    override val location: Location?
        get() = line.location

    override var obj : ItemStack
        get() = _mutableStateOf.get()
        set(value) = _mutableStateOf.set(value)

    override var pvt = ILine.PrivateConfig(this)

    override fun setLocation(value: Location) {
        line.location = value
    }

    override fun hide(player: Player) {
        line.destroy(player)
    }

    override fun teleport(player: Player) {
        line.teleport(player)
    }

    override fun show(player: Player) {
        line.spawn(player)
        PacketType.METADATA_TEXT
            .metadata(entityId, nameTag = null, invisible = true).send(player)

        this.update(player)

        if(firstRender) {
            firstRender = false
            _mutableStateOf.addObserver(pvt)
        }
    }

    override fun update(player: Player) {
        PacketType.EQUIPMENT
            .equip(entityId, helmet = _mutableStateOf.get()).send(player)
    }
}
