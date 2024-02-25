package tororo1066.man10farming.command

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import tororo1066.man10farming.Man10Farming
import tororo1066.man10farming.gui.CreateCropGUI

class CommandFunctions(private val sender: CommandSender) {

    fun createCrop(edit: String?) {
        if (sender !is Player) {
            return
        }

        if (edit != null) {
            val data = Man10Farming.crops[edit]
            if (data == null) {
                sender.sendMessage("§cデータが存在しません")
                return
            }
            CreateCropGUI(data.copy()).open(sender)
        } else {
            CreateCropGUI().open(sender)
        }
    }

    companion object {
        fun invokeFunction(sender: CommandSender, function: CommandFunctions.() -> Unit) {
            CommandFunctions(sender).apply(function)
        }
    }
}