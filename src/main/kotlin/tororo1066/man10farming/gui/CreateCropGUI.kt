package tororo1066.man10farming.gui

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.meta.BlockDataMeta
import org.bukkit.inventory.meta.SkullMeta
import tororo1066.man10farming.Man10Farming
import tororo1066.man10farming.data.BlockInfo
import tororo1066.man10farming.data.CropData
import tororo1066.man10farming.data.CropType
import tororo1066.tororopluginapi.SJavaPlugin
import tororo1066.tororopluginapi.defaultMenus.LargeSInventory
import tororo1066.tororopluginapi.sInventory.SInventory
import tororo1066.tororopluginapi.sInventory.SInventoryItem
import tororo1066.tororopluginapi.sItem.SItem

class CreateCropGUI(val data: CropData = CropData("changeme")) : SInventory(SJavaPlugin.plugin, "CreateCrop", 5) {

    override var savePlaceItems = true

    init {
        inv.setItem(22, data.cropItem)
    }

    override fun renderMenu(p: Player): Boolean {
        fillItem(
            SInventoryItem(Material.BLACK_STAINED_GLASS_PANE)
                .setDisplayName(" ")
                .setCanClick(false)
        )

        removeItem(22)

        setItem(
            2,
            createInputItem(
                SItem(Material.OAK_SIGN).setDisplayName("§a内部名を設定する")
                    .addLore("§d現在の値: ${data.internalName}"),
                String::class.java, "/<内部名>", action = { str, _ ->
                    data.internalName = str
                }
            )
        )

        setItem(
            6,
            createInputItem(
                SItem(Material.EMERALD_BLOCK).setDisplayName("§b成長確率を設定する")
                    .addLore("§d現在の値: ${data.growChance}"),
                Double::class.java,
                "/<成長確率>",
                action = { double, _ ->
                    data.growChance = double
                }
            )
        )

        setItem(
            19,
            SInventoryItem(Material.WHEAT).setDisplayName("§6作物の表示を設定する")
                .addLore("§d現在の値: ${data.cropType.name}")
                .setCanClick(false)
                .setClickEvent {
                    val inv = object : LargeSInventory("§6作物の表示を設定する") {
                        override fun renderMenu(p: Player): Boolean {
                            val items = ArrayList<SInventoryItem>()
                            CropType.values().forEach {
                                items.add(
                                    SInventoryItem(it.material)
                                        .setDisplayName("§a${it.name}")
                                        .setCanClick(false)
                                        .setClickEvent { _ ->
                                            data.cropType = it
                                            p.closeInventory()
                                        }
                                )
                            }
                            setResourceItems(items)
                            return true
                        }
                    }

                    moveChildInventory(inv, p)
                }
        )

        setItem(
            25,
            createNullableInputItem(
                SItem(Material.MELON).setDisplayName("§e成長時のブロックを設定する")
                    .addLore("§d現在の値: ${data.formBlock?.material?.name}"),
                String::class.java,
                "/<ブロックを手に持って適当なコマンド実行>",
                action = { _, _ ->
                    val item = p.inventory.itemInMainHand
                    if (!item.type.isBlock) {
                        p.sendMessage("§cブロックを手に持ってください")
                        return@createNullableInputItem
                    }

                    val meta = item.itemMeta as? BlockDataMeta
                    val blockData = meta?.getBlockData(item.type)
                    val skullMeta = item.itemMeta as? SkullMeta
                    val texture = skullMeta?.playerProfile?.textures?.skin

                    if (data.formBlock == null) {
                        data.formBlock = BlockInfo(item.type, blockData, texture?.toString())
                    } else {
                        data.formBlock!!.apply {
                            this.material = item.type
                            this.blockData = blockData
                            this.setSkullURL(texture?.toString())
                        }
                    }
                }
            )
        )

        setItem(
            38,
            SInventoryItem(Material.BEETROOT).setDisplayName("§cドロップ品を設定する")
                .setCanClick(false).setClickEvent {
                    val inv = object : LargeSInventory("§cドロップ品を設定する") {
                        override fun renderMenu(p: Player): Boolean {
                            val items = ArrayList<SInventoryItem>()
                            data.drops.forEach { (item, chance, amountRange) ->
                                items.add(
                                    SInventoryItem(item)
                                        .addLore(
                                            "§aドロップ確率: ${chance * 100.0}%",
                                            "§a最小: ${amountRange.first} 最大: ${amountRange.last}",
                                            "§cシフト右クリックで削除"
                                        )
                                        .setCanClick(false)
                                        .setClickEvent {
                                            if (it.click == ClickType.SHIFT_RIGHT) {
                                                data.drops.remove(Triple(item, chance, amountRange))
                                                allRenderMenu(p)
                                            }
                                        }
                                )
                            }

                            items.add(
                                createInputItem(
                                    SItem(Material.EMERALD_BLOCK).setDisplayName("§a追加"),
                                    Double::class.java, "/<確率(1.0が最大)>", invOpenCancel = true,
                                    action = { chance, _ ->
                                        SJavaPlugin.sInput.sendInputCUI(
                                            p, IntRange::class.java,
                                            "/<最小>..<最大>(アイテムを手に持って)",
                                            action = { range ->
                                                val item = p.inventory.itemInMainHand
                                                if (item.type.isAir) {
                                                    p.sendMessage("§cアイテムを手に持ってください")
                                                    open(p)
                                                    return@sendInputCUI
                                                }
                                                data.drops.add(Triple(item, chance, range))
                                                open(p)
                                            }
                                        )
                                    }
                                )
                            )

                            setResourceItems(items)
                            return true
                        }
                    }

                    moveChildInventory(inv, p)
                }
        )

        setItem(
            42,
            SInventoryItem(Material.STONE_HOE).setDisplayName("§5採取可能なアイテムを設定する")
                .setCanClick(false).setClickEvent {
                    val inv = object : LargeSInventory("§5採取可能なアイテムを設定する") {
                        override fun renderMenu(p: Player): Boolean {
                            val items = ArrayList<SInventoryItem>()
                            data.harvestableItems.forEach {
                                items.add(
                                    SInventoryItem(it)
                                        .addLore("§cシフト右クリックで削除")
                                        .setCanClick(false)
                                        .setClickEvent { e ->
                                            if (e.click == ClickType.SHIFT_RIGHT) {
                                                data.harvestableItems.remove(it)
                                                allRenderMenu(p)
                                            }
                                        }
                                )
                            }

                            items.add(
                                createNullableInputItem(
                                    SItem(Material.EMERALD_BLOCK).setDisplayName("§a追加"),
                                    String::class.java, "/<アイテムを手に持って適当なコマンド実行>", action = { _, _ ->
                                        val item = p.inventory.itemInMainHand
                                        if (item.type.isAir) {
                                            p.sendMessage("§cアイテムを手に持ってください")
                                            return@createNullableInputItem
                                        }
                                        data.harvestableItems.add(item)
                                    }
                                )
                            )

                            setResourceItems(items)
                            return true
                        }
                    }

                    moveChildInventory(inv, p)
                }
        )

        setItem(
            40,
            SInventoryItem(Material.LIME_STAINED_GLASS_PANE).setDisplayName("§a保存")
                .setCanClick(false)
                .setClickEvent {
                    val cropItem = getItem(22)
                    if (cropItem == null || cropItem.type.isAir) {
                        p.sendMessage("§c種を設定してください")
                        return@setClickEvent
                    }
                    data.cropItem = cropItem
                    data.save()
                    Man10Farming.crops[data.internalName] = data
                    p.closeInventory()
                    p.sendMessage("§a保存しました")
                }
        )

        return true
    }
}
