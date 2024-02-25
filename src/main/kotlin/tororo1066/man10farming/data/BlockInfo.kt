package tororo1066.man10farming.data

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.Skull
import org.bukkit.block.data.BlockData
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.configuration.serialization.SerializableAs
import java.net.URI
import java.util.Base64
import java.util.UUID

@SerializableAs("BlockInfo")
data class BlockInfo(
    var material: Material,
    var blockData: BlockData? = null,
    private var skullURL: String? = null
) : ConfigurationSerializable {

    private var skullProfile = if (skullURL != null) {
        Bukkit.createProfile(UUID.randomUUID())
            .apply {
                val texture = textures
                texture.skin = URI(skullURL!!).toURL()
                setTextures(texture)
            }
    } else null

    fun setSkullURL(url: String?) {
        skullURL = url
        skullProfile = if (url != null) {
            Bukkit.createProfile(UUID.randomUUID())
                .apply {
                    val texture = textures
                    texture.skin = URI(url).toURL()
                    setTextures(texture)
                }
        } else null
    }

    fun applyTo(block: Block) {
        block.type = material
        blockData?.let { block.blockData = it }
        val state = block.state
        if (state is Skull && skullProfile != null) {
            state.setPlayerProfile(skullProfile!!)
            state.update()
        }
    }

    override fun serialize(): MutableMap<String, Any> {
        val map = mutableMapOf<String, Any>()
        map["material"] = material.name
        blockData?.let { map["blockData"] = it.asString }
        skullURL?.let { map["skullURL"] = it }
        return map
    }

    companion object {
        private fun getURLFromBase64(base64: String): String? {
            return try {
                Base64.getDecoder().decode(base64).toString(Charsets.UTF_8)
            } catch (e: Exception) {
                null
            }
        }

        @JvmStatic
        fun deserialize(map: Map<String, Any>): BlockInfo {
            val material = Material.valueOf(map["material"] as String)
            val blockData = map["blockData"]?.let { Bukkit.createBlockData(it as String) }
            val skullURL = map["skullURL"] as String?
            return BlockInfo(material, blockData, skullURL)
        }
    }
}
