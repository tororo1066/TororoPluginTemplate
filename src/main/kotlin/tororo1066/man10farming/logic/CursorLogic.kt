package tororo1066.man10farming.logic

import me.filoghost.holographicdisplays.api.hologram.Hologram
import me.filoghost.holographicdisplays.api.hologram.VisibilitySettings
import org.bukkit.Location
import org.bukkit.event.player.PlayerMoveEvent
import tororo1066.man10farming.Man10Farming
import tororo1066.tororopluginapi.annotation.SEventHandler
import java.util.UUID

class CursorLogic {

    private val holo = Man10Farming.holoAPI

    companion object {
        val previousHolograms = mutableMapOf<UUID, Hologram>()
    }

    @SEventHandler
    fun onMove(e: PlayerMoveEvent) {
        if (holo == null) return
        val block = e.player.getTargetBlockExact(5)
        if (block != null && block.location.toBlockLocation() in Man10Farming.plantedCrops) {
            val location = block.location.toBlockLocation()
            val plantCropData = Man10Farming.plantedCrops[location] ?: return
            val previous = previousHolograms[e.player.uniqueId]
            if (previous != null && previous.position.toLocation().offset() == location.offset()) {
                return
            }
            previous?.delete()
            val cropData = plantCropData.cropData
            val hologram = holo.createHologram(location.offset())
            hologram.visibilitySettings.globalVisibility = VisibilitySettings.Visibility.HIDDEN
            hologram.visibilitySettings.setIndividualVisibility(e.player, VisibilitySettings.Visibility.VISIBLE)
            hologram.lines.insertItem(0, cropData.cropItem!!)
            hologram.lines.insertText(1, cropData.cropItem?.itemMeta?.displayName ?: "Error")
            hologram.lines.insertText(2, "§dPlanter: ${plantCropData.planterName}")

            previousHolograms[e.player.uniqueId] = hologram
        } else {
            previousHolograms[e.player.uniqueId]?.delete()
            previousHolograms.remove(e.player.uniqueId)
        }
    }

    private fun Location.offset(): Location {
        return this.clone().add(0.5, 2.0, 0.5)
    }
}
