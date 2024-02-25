package tororo1066.man10farming.logic

import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.event.Event
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import tororo1066.man10farming.Man10Farming
import tororo1066.man10farming.data.CropType
import tororo1066.man10farming.data.PlantCropData
import tororo1066.tororopluginapi.annotation.SEventHandler
import tororo1066.tororopluginapi.utils.addY
import java.util.Date

class PlantLogic {

    @SEventHandler
    fun onClick(e: PlayerInteractEvent) {
        if (e.hand != EquipmentSlot.HAND) return
        if (e.action != Action.RIGHT_CLICK_BLOCK) return
        if (e.useInteractedBlock() == Event.Result.DENY) return
        val item = e.item ?: return
        val cropData = Man10Farming.crops.values.find { it.cropItem?.isSimilar(item) == true } ?: return
        val block = e.clickedBlock ?: return
        if (!cropData.cropType.allowedDirections.contains(e.blockFace)) return

        when(cropData.cropType) {
            CropType.COCOA -> {
                if (block.type != Material.JUNGLE_LOG) return
            }
            CropType.BERRY -> {
                if (block.type != Material.GRASS_BLOCK) return
            }
            else -> {
                if (block.type != Material.FARMLAND) return
            }
        }

        e.isCancelled = true

        val placeLocation = block.location.add(e.blockFace.direction)

        if (Man10Farming.plantedCrops.containsKey(placeLocation)) return

        val plantCropData = PlantCropData(cropData, placeLocation, e.player.uniqueId, e.player.name, Date())
        Man10Farming.plantedCrops[placeLocation] = plantCropData
        item.amount -= 1
        placeLocation.block.type = cropData.cropType.material
        e.player.swingMainHand()

        Man10Farming.database.insert(plantCropData)
    }
}
