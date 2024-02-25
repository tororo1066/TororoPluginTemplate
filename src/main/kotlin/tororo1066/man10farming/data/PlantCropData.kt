package tororo1066.man10farming.data

import org.bukkit.Location
import java.util.Date
import java.util.UUID

data class PlantCropData(
    val cropData: CropData,
    var location: Location,
    var planterUUID: UUID,
    var planterName: String,
    var plantedAt: Date
)
