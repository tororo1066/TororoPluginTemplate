package tororo1066.man10farming.logic

import org.bukkit.Material
import org.bukkit.block.data.type.Farmland
import org.bukkit.event.Event
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockFadeEvent
import org.bukkit.event.block.BlockFromToEvent
import org.bukkit.event.block.MoistureChangeEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import tororo1066.man10farming.Man10Farming
import tororo1066.tororopluginapi.annotation.SEventHandler

class ProtectionLogic {

    @SEventHandler
    fun onBreak(e: BlockBreakEvent) {
        if (e.block.type == Material.FARMLAND) {
            val loc = e.block.location.add(0.0, 1.0, 0.0).toBlockLocation()
            Man10Farming.plantedCrops[loc] ?: return
            e.isCancelled = true
        }
    }

    @SEventHandler
    fun onFluid(e: BlockFromToEvent) {
        if (Man10Farming.plantedCrops.containsKey(e.block.location.toBlockLocation())) {
            e.isCancelled = true
        }
    }

    @SEventHandler
    fun onJumpOnFarmland(e: PlayerInteractEvent) {
        if (e.action != Action.PHYSICAL) return
        if (e.clickedBlock?.type == Material.FARMLAND) {
            val loc = e.clickedBlock!!.location.add(0.0, 1.0, 0.0).toBlockLocation()
            Man10Farming.plantedCrops[loc] ?: return
            e.isCancelled = true
        }
    }

    @SEventHandler
    fun onUseBoneMeal(e: PlayerInteractEvent) {
        if (e.hand != EquipmentSlot.HAND) return
        if (e.action != Action.RIGHT_CLICK_BLOCK) return
        if (e.useInteractedBlock() == Event.Result.DENY) return
        val item = e.item ?: return
        if (item.type != Material.BONE_MEAL) return
        val loc = e.clickedBlock?.location?.toBlockLocation() ?: return
        if (Man10Farming.plantedCrops.containsKey(loc)) {
            e.isCancelled = true
        }
    }

    @SEventHandler
    fun fadeDrySoil(e: BlockFadeEvent) {
        if (e.block.type == Material.FARMLAND) {
            val loc = e.block.location.add(0.0, 1.0, 0.0).toBlockLocation()
            Man10Farming.plantedCrops[loc] ?: return
            e.isCancelled = true
        }
    }
}
