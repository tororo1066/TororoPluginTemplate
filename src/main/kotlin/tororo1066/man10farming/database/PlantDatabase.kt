package tororo1066.man10farming.database

import org.bukkit.Bukkit
import org.bukkit.Location
import tororo1066.man10farming.Man10Farming
import tororo1066.man10farming.data.PlantCropData
import tororo1066.tororopluginapi.SJavaPlugin
import tororo1066.tororopluginapi.database.SDBCondition
import tororo1066.tororopluginapi.database.SDBVariable
import tororo1066.tororopluginapi.database.SDatabase
import tororo1066.tororopluginapi.database.SDatabase.Companion.toSQLVariable
import tororo1066.tororopluginapi.utils.LocType
import tororo1066.tororopluginapi.utils.toDate
import tororo1066.tororopluginapi.utils.toLocString
import java.util.UUID
import java.util.concurrent.CompletableFuture

class PlantDatabase {

    var sDatabase = SDatabase.newInstance(SJavaPlugin.plugin)

    init {
        sDatabase.createTable(
            "plant_list",
            mapOf(
                "id" to SDBVariable(SDBVariable.Int, autoIncrement = true),
                "crop" to SDBVariable(SDBVariable.Text),
                "location" to SDBVariable(SDBVariable.Text),
                "planter_uuid" to SDBVariable(SDBVariable.VarChar, 36),
                "planter_name" to SDBVariable(SDBVariable.VarChar, 16),
                "planted_at" to SDBVariable(SDBVariable.DateTime),
            )
        )
    }

    fun load(): CompletableFuture<Void> {
        Man10Farming.plantedCrops.clear()
        return sDatabase.asyncSelect("plant_list").thenAcceptAsync { result ->
            result.forEach { data ->
                val cropData = Man10Farming.crops[data.getString("crop")] ?: return@forEach
                val location = data.getString("location").let { locString ->
                    val split = locString.split(",")
                    val world = Bukkit.getWorld(split[0]) ?: return@forEach
                    Location(world, split[1].toDouble(), split[2].toDouble(), split[3].toDouble())
                }
                val planterUUID = UUID.fromString(data.getString("planter_uuid"))
                val planterName = data.getString("planter_name")
                val plantedAt = if (sDatabase.isMongo) data.getDate("planted_at") else data.getLocalDateTime("planted_at").toDate()
                Man10Farming.plantedCrops[location] = PlantCropData(cropData, location, planterUUID, planterName, plantedAt)
            }
        }
    }

    fun insert(data: PlantCropData): CompletableFuture<Boolean> {
        return sDatabase.asyncInsert(
            "plant_list",
            mapOf(
                "crop" to data.cropData.internalName,
                "location" to data.location.toLocString(LocType.WORLD_BLOCK_COMMA),
                "planter_uuid" to data.planterUUID.toString(),
                "planter_name" to data.planterName,
                "planted_at" to if (sDatabase.isMongo) data.plantedAt else data.plantedAt.toSQLVariable(SDBVariable.DateTime)
            )
        )
    }

    fun delete(location: Location): CompletableFuture<Boolean> {
        return sDatabase.asyncDelete("plant_list", SDBCondition().equal("location", location.toLocString(LocType.WORLD_BLOCK_COMMA)))
    }
}
