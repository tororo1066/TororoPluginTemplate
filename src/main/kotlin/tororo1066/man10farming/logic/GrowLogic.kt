package tororo1066.man10farming.logic

import org.bukkit.Bukkit
import org.bukkit.block.data.Ageable
import org.bukkit.event.block.BlockGrowEvent
import tororo1066.man10farming.Man10Farming
import tororo1066.tororopluginapi.SJavaPlugin
import tororo1066.tororopluginapi.annotation.SEventHandler

class GrowLogic {

    @SEventHandler
    fun onGrow(e: BlockGrowEvent) {
        val plantCropData = Man10Farming.plantedCrops[e.block.location] ?: return
        val cropData = plantCropData.cropData
        if (cropData.growChance < Math.random()) {
            e.isCancelled = true
            return
        }
        val state = e.newState.blockData as? Ageable ?: return
        if (state.age == state.maximumAge) {
            if (cropData.formBlock != null) {
                Bukkit.getScheduler().runTask(
                    SJavaPlugin.plugin,
                    Runnable {
                        cropData.formBlock!!.applyTo(e.block)
                    }
                )
            }
        }
    }
}
