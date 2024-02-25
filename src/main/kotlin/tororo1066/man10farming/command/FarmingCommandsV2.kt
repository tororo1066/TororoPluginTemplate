package tororo1066.man10farming.command

import tororo1066.commandapi.argumentType.StringArg
import tororo1066.man10farming.Man10Farming
import tororo1066.man10farming.gui.CreateCropGUI
import tororo1066.tororopluginapi.annotation.SCommandV2Body
import tororo1066.tororopluginapi.sCommand.v2.SCommandV2

class FarmingCommandsV2 : SCommandV2("mfarm", "man10farming.user") {



    @SCommandV2Body
    val createCrop = command {
        literal("createCrop") {
            setPlayerFunctionExecutor { sender, _, _ ->
                CreateCropGUI().open(sender)
            }

            argument("edit", StringArg.greedyPhrase()) {
                suggest { _, _, _ ->
                    Man10Farming.crops.keys.map { it toolTip null }
                }

                setPlayerFunctionExecutor { sender, _, args ->
                    CommandFunctions.invokeFunction(sender) {
                        createCrop(args.getNullableArgument("edit", String::class.java))
                    }
                }
            }
        }
    }
}
