package tororo1066.man10farming

import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI
import org.bukkit.Location
import org.bukkit.configuration.serialization.ConfigurationSerialization
import tororo1066.man10farming.command.FarmingCommands
import tororo1066.man10farming.command.FarmingCommandsV2
import tororo1066.man10farming.data.BlockInfo
import tororo1066.man10farming.data.CropData
import tororo1066.man10farming.data.PlantCropData
import tororo1066.man10farming.database.PlantDatabase
import tororo1066.man10farming.logic.CursorLogic
import tororo1066.tororopluginapi.SJavaPlugin
import java.io.File

class Man10Farming : SJavaPlugin(UseOption.SConfig) {

    companion object {
        val crops = HashMap<String, CropData>()
        val plantedCrops = HashMap<Location, PlantCropData>()
        lateinit var database: PlantDatabase

        var holoAPI: HolographicDisplaysAPI? = null
    }

    fun loadConfig() {
        crops.clear()
        val cropsFolder = File(dataFolder, "crops")
        if (!cropsFolder.exists()) {
            cropsFolder.mkdirs()
        }
        cropsFolder.listFiles()?.forEach { file ->
            val cropData = CropData.loadFromYml(file)
            crops[cropData.internalName] = cropData
        }
    }

    override fun onStart() {
        ConfigurationSerialization.registerClass(BlockInfo::class.java)
        if (server.pluginManager.isPluginEnabled("HolographicDisplays")) {
            holoAPI = HolographicDisplaysAPI.get(this)
        } else {
            logger.warning("HolographicDisplays is not enabled.")
        }

        loadConfig()

        database = PlantDatabase()
        database.load()

        try {
            getSNms()
            FarmingCommandsV2()
        } catch (e: Exception) {
            FarmingCommands()
        }
    }

    override fun onDisable() {
        CursorLogic.previousHolograms.forEach { it.value.delete() }
        CursorLogic.previousHolograms.clear()
    }
}
