package tororo1066.man10farming.logic

import org.bukkit.block.data.Ageable
import org.bukkit.event.block.BlockBreakEvent
import tororo1066.man10farming.Man10Farming
import tororo1066.tororopluginapi.annotation.SEventHandler

class HarvestLogic {

    @SEventHandler
    fun onHarvest(e: BlockBreakEvent) {
        if (e.isCancelled) return
        val plantCropData = Man10Farming.plantedCrops[e.block.location] ?: return
        val cropData = plantCropData.cropData
        var harvest = true
        val ageable = e.block.blockData as? Ageable
        if (ageable != null) {
            if (ageable.age != ageable.maximumAge) {
                harvest = false
            }
        }

        if (harvest) {
            val harvestableItems = cropData.harvestableItems
            if (harvestableItems.isNotEmpty()) {
                val item = e.player.inventory.itemInMainHand
                if (!harvestableItems.any { it.isSimilar(item) }) {
                    e.isCancelled = true
                    return
                }
            }
        }

        CursorLogic.previousHolograms[e.player.uniqueId]?.delete()

        e.isDropItems = false
        Man10Farming.plantedCrops.remove(e.block.location)
        Man10Farming.database.delete(plantCropData.location)
        if (harvest) {
            plantCropData.cropData.drops.forEach { (item, chance, range) ->
                if (chance < Math.random()) return@forEach
                val amount = range.random()
                e.block.world.dropItemNaturally(e.block.location, item.clone().apply { this.amount = amount })
            }
        } else {
            e.block.world.dropItemNaturally(e.block.location, cropData.cropItem!!.clone())
        }
    }
}
