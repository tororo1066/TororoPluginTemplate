package tororo1066.man10farming.data

import org.bukkit.Material
import org.bukkit.block.BlockFace

fun sideFaces() = listOf(BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST)
fun upFaces() = listOf(BlockFace.UP)
enum class CropType(val material: Material, val allowedDirections: List<BlockFace>) {
    WHEAT(Material.WHEAT, upFaces()),
    CARROT(Material.CARROTS, upFaces()),
    POTATO(Material.POTATOES, upFaces()),
    BEETROOT(Material.BEETROOTS, upFaces()),
    NETHER_WART(Material.NETHER_WART, upFaces()),
    PUMPKIN(Material.PUMPKIN_STEM, upFaces()),
    MELON(Material.MELON_STEM, upFaces()),
    COCOA(Material.COCOA, sideFaces()),
    BERRY(Material.SWEET_BERRY_BUSH, upFaces());
}
