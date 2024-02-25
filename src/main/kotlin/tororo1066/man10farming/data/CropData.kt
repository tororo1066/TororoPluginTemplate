package tororo1066.man10farming.data

import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.ItemStack
import tororo1066.tororopluginapi.SJavaPlugin
import java.io.File

data class CropData(
    var internalName: String,
    var cropItem: ItemStack? = null,
//    var growableBlocks: List<Material>? = null,
    var growChance: Double = 1.0,
    var cropType: CropType = CropType.WHEAT,
    var formBlock: BlockInfo? = null,
    val drops: ArrayList<Triple<ItemStack, Double, IntRange>> = arrayListOf(),
    var harvestableItems: ArrayList<ItemStack> = arrayListOf()
) {

    fun save() {
        val yaml = YamlConfiguration()
        yaml.set("cropItem", cropItem)
        yaml.set("growChance", growChance)
        yaml.set("cropType", cropType.name)
        yaml.set("formBlock", formBlock)
        yaml.set(
            "drops",
            drops.map {
                mapOf(
                    "item" to it.first,
                    "chance" to it.second,
                    "min" to it.third.first,
                    "max" to it.third.last
                )
            }
        )
        yaml.set("harvestableItems", harvestableItems)

        Thread {
            yaml.save(File(SJavaPlugin.plugin.dataFolder, "crops/$internalName.yml"))
        }.start()
    }

    companion object {
        fun loadFromYml(file: File): CropData {
            val yml = YamlConfiguration.loadConfiguration(file)
            val internalName = file.nameWithoutExtension
            val cropItem = yml.getItemStack("cropItem")
            val growChance = yml.getDouble("growChance", 1.0)
            val cropType = CropType.valueOf(yml.getString("cropType")!!)
            val formBlock = yml.getSerializable("formBlock", BlockInfo::class.java)

            val drops = ArrayList<Triple<ItemStack, Double, IntRange>>()
            yml.getMapList("drops").forEach {
                val item = it["item"] as ItemStack
                val chance = it["chance"] as Double
                val range = (it["min"] as Int)..(it["max"] as Int)
                drops.add(Triple(item, chance, range))
            }

            val harvestableItems = ArrayList(yml.getList("harvestableItems")?.map { it as ItemStack } ?: listOf())

            return CropData(
                internalName = internalName,
                cropItem = cropItem,
                growChance = growChance,
                cropType = cropType,
                formBlock = formBlock,
                drops = drops,
                harvestableItems = harvestableItems
            )
        }
    }
}
