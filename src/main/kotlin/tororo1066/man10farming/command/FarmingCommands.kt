package tororo1066.man10farming.command

import tororo1066.tororopluginapi.annotation.SCommandBody
import tororo1066.tororopluginapi.sCommand.SCommand
import tororo1066.tororopluginapi.sCommand.SCommandArg

class FarmingCommands: SCommand("mfarm", "man10farming.user") {

    @SCommandBody
    val createCrop = command()
        .addArg(SCommandArg("createCrop"))
        .setPlayerFunction { sender, _, _, _ ->
            CommandFunctions.invokeFunction(sender) {
                createCrop(null)
            }
        }

    @SCommandBody
    val createCropEdit = command()
        .addArg(SCommandArg("createCrop"))
        .addArg(SCommandArg("edit"))
        .setPlayerFunction { sender, _, _, args ->
            CommandFunctions.invokeFunction(sender) {
                createCrop(args[1])
            }
        }
}