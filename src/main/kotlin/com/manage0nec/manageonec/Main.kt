package com.manage0nec.manageonec

import com.jacob.activeX.ActiveXComponent
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.stage.Stage
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.nio.charset.Charset

class Main: Application() {

    private fun initSetting() {
        var dirConfig = File("./$SETTINGS_DIR_NAME")
        if (!dirConfig.exists()) {
            dirConfig.mkdir()
        }

        val fileSettings = File("./$SETTINGS_DIR_NAME/$SETTINGS_FILE_NAME")

        if (!fileSettings.exists()){
            fileSettings.createNewFile()
            fileSettings.writeText(defaultSettingApp())
        }

        val readerSettings = FileReader("./$SETTINGS_DIR_NAME/$SETTINGS_FILE_NAME", Charset.forName("utf8"))

        val parser = JSONParser()
        settingsApp = parser.parse(readerSettings) as JSONObject
        readerSettings.close()
    }

    private fun defaultSettingApp(): String {
        val urlDefSettings = Main::class.java.getResource("defaultSettings.settings")

        return urlDefSettings.readText()
    }

    override fun start(stage: Stage) {
        initSetting()

        val fxmlLoader = FXMLLoader(Main::class.java.getResource("forms/MainForm.fxml"))
        val scene = Scene(fxmlLoader.load(), 640.0, 480.0)

        stage.title = "Управление 1С серверами"
        if (!(settingsApp["sorceConfAsFile"] as Boolean) && !(settingsApp["sorceConfFileBase"] as Boolean)) {
            stage.title = (Main.settingsApp[JSON_KEY_NAME_CON_STR_1C] as String) + " ${stage.title}"
        }
        stage.scene = scene
        stage.show()
    }


    companion object {
        @JvmStatic
        lateinit var settingsApp: JSONObject

        const val SETTINGS_DIR_NAME = "settings"
        const val SETTINGS_FILE_NAME = "app.settings"
        const val JSON_KEY_SERVERS1C = "servers1c"
        const val JSON_KEY_NAME_SERVER1C = "name"
        const val JSON_KEY_IP_SERVER1C = "ip"
        const val JSON_KEY_NAME_BASE1C = "nameBase1c"
        const val JSON_KEY_NAME_CON_STR_1C = "sourceConfConnectionString"
        const val REFRESH_FORM = 1000L

        fun getServers1C(): JSONArray {

            return settingsApp[JSON_KEY_SERVERS1C] as JSONArray
        }

        fun saveSetting() {
            val textJsonObject = settingsApp.toJSONString()
            val fileWriterSettingsApp = FileWriter("./$SETTINGS_DIR_NAME/$SETTINGS_FILE_NAME", false)
            fileWriterSettingsApp.write(textJsonObject)
            fileWriterSettingsApp.close()
        }
    }
}

fun main(args: Array<String>) {
//    val activeX = ActiveXComponent("V83.COMConnector")
//	activeX.invoke("Connect", "File=\"D:\\1C\\FileBases\\DemoBuh30\";Usr=\"АбрамовГС (директор)\";Pwd=\"\";")
    Application.launch(Main::class.java, *args)
}