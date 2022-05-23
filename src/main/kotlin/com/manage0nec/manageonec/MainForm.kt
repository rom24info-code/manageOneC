package com.manage0nec.manageonec

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.image.Image
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import javafx.scene.layout.VBox
import javafx.stage.FileChooser
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.StageStyle
import kotlinx.coroutines.*
import kotlinx.coroutines.javafx.JavaFx
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import java.io.File


class MainForm {

    @FXML
    private lateinit var debugLabel: Label

    @FXML
    private lateinit var form: VBox

    @FXML
    private lateinit var split: SplitPane

    @FXML
    private lateinit var tableServers: TableView<server>

    @FXML
    private lateinit var  tableServersColumnCheckBox: TableColumn<server, CheckBox>

    @FXML
    private lateinit var tableServersColumnIp: TableColumn<server, String>

    @FXML
    private lateinit var tableServersColumnName: TableColumn<server, String>

    @FXML
    private lateinit var tableServersColumnCheckNameBase1c: TableColumn<server, String>

    @FXML
    private lateinit var tableServersCheckBoxAll: CheckBox

    @FXML
    private lateinit var labelNameConf1c: Label

    @FXML
    fun initialize() {
        tableServers.setStyle("-fx-selection-bar: green;")
        labelNameConf1c.text = ""

        form.addEventFilter(MouseEvent.MOUSE_PRESSED) {
            isPaused = true
        }
        form.addEventFilter(MouseEvent.MOUSE_RELEASED) {
            isPaused = false
        }
        form.addEventFilter(KeyEvent.KEY_PRESSED) {
            isPaused = true
        }
        form.addEventFilter(KeyEvent.KEY_RELEASED) {
            isPaused = false
        }

        tableServers.items = servers
        tableServersColumnIp.cellValueFactory = PropertyValueFactory("ip")
        tableServersColumnName.cellValueFactory = PropertyValueFactory("name")
        tableServersColumnCheckNameBase1c.cellValueFactory = PropertyValueFactory("nameBase1c")
        tableServersColumnCheckBox.cellValueFactory = PropertyValueFactory("checkBox")
        val jsonArrayServers = Main.getServers1C()
        for (item in jsonArrayServers) {
            val lItem = item as JSONObject

            val name = lItem[Main.JSON_KEY_NAME_SERVER1C] as String
            val ip = lItem[Main.JSON_KEY_IP_SERVER1C] as String
            val nameBase1c = lItem[Main.JSON_KEY_NAME_BASE1C] as String
            val chBox = CheckBox()
            chBox.focusTraversableProperty().value = false
            chBox.onAction = funUncheck

            servers.add(MainForm.server(false, ip, name, nameBase1c, chBox))
        }
        manageMainCheckBox(tableServersCheckBoxAll)

        jobTableServers = CoroutineScope(Dispatchers.JavaFx).launch {
            var int: Int = 0
            while (true) {
                delay(Main.REFRESH_FORM)
                if (isPaused)
                    continue
                int ++
                debugLabel.text = int.toString()
                tableServers.refresh()

            }
        }

        if (!servers.isEmpty()) {
            tableServers.selectionModel.select(0)
        }
    }

    @FXML
    fun addServer(event: ActionEvent) {
        var stage = Stage()
        val fxmlLoader = FXMLLoader(Main::class.java.getResource("forms/ServerEditForm.fxml"))
        val scene = Scene(fxmlLoader.load(), 320.0, 240.0)

        val image = Image(Main::class.java.getResourceAsStream("images/1cIcon.png"))
        fxmlLoader.getController<ServerEditForm>().parent = this
        stage.getIcons().add(image)
        stage.title = "Редактировние записи"
        stage.scene = scene
        stage.initStyle(StageStyle.UTILITY)
        stage.resizableProperty().value = false
        stage.initModality(Modality.APPLICATION_MODAL)
        stage.show()
    }

    @FXML
    fun removeServer(event: ActionEvent) {
        removeRow(tableServers.selectionModel.selectedIndex)
    }

    @FXML
    fun tableServersCheckBoxOnAction(event: ActionEvent?) {
        val checkBox = event?.target as CheckBox
        if (checkBox.isSelected) {
            for (item in servers) {
                item.checkBox.selectedProperty().value = true
            }
        } else {
            for (item in servers) {
                item.checkBox.selectedProperty().value = false
            }
        }
    }

    @FXML
    fun formClose(event: ActionEvent) {
        val stage = form.scene.window as Stage
        stage.close()
    }

    @FXML
    fun onActionSaveSettings(event: ActionEvent) {
        val jsonArray = (Main.settingsApp["servers1c"] as JSONArray)
        jsonArray.clear()
        for (item in servers) {
            val jsonObject = JSONObject()
            jsonObject["name"] = item.name
            jsonObject["ip"] = item.ip
            jsonObject["nameBase1c"] = item.nameBase1c
            jsonArray.add(jsonObject)
        }

        Main.saveSetting()
    }

    @FXML
    fun onActionSaveSettingsAs(event: ActionEvent) {

        val fileChooser = FileChooser()
        fileChooser.setTitle("Сохранение настроек")
        fileChooser.extensionFilters.add(FileChooser.ExtensionFilter("json", "json"))
        var chosenFile = fileChooser.showSaveDialog(form.scene.window as Stage)
        if (chosenFile == null)
            return

        if(!chosenFile.getName().contains(".")) {
            chosenFile = File(chosenFile.getAbsolutePath() + ".${fileChooser.selectedExtensionFilter.extensions[0]}");
        }

        val jsonArray = (Main.settingsApp["servers1c"] as JSONArray)
        jsonArray.clear()
        for (item in servers) {
            val jsonObject = JSONObject()
            jsonObject["name"] = item.name
            jsonObject["ip"] = item.ip
            jsonObject["nameBase1c"] = item.nameBase1c
            jsonArray.add(jsonObject)
        }

        chosenFile.writeText(jsonArray.toJSONString())
    }

    @FXML
    fun menuItemSourceConf1cOnAction(event: ActionEvent) {
        var stage = Stage()
        val fxmlLoader = FXMLLoader(Main::class.java.getResource("forms/SourceConfig1c.fxml"))
        val scene = Scene(fxmlLoader.load(), 320.0, 240.0)

        val image = Image(Main::class.java.getResourceAsStream("images/1cIcon.png"))
        stage.getIcons().add(image)
        stage.title = "Исходная конфигурация для обновления"
        stage.scene = scene
        //stage.initStyle(StageStyle.UTILITY)
        //stage.resizableProperty().value = false
        stage.initModality(Modality.APPLICATION_MODAL)
        stage.show()
    }

    private lateinit var jobTableServers: Job

    private var isPaused = false

    data class server(var select: Boolean, var ip: String, var name: String, var nameBase1c: String, val chBox: CheckBox) {
        var checkBox: CheckBox = chBox

        init {
            checkBox.selectedProperty().value = select
        }

    }

    private val servers: ObservableList<server> = FXCollections.observableArrayList()

    private fun manageMainCheckBox(checkBox: CheckBox) {
        var haveCheck = false
        var haveUncheck = false
        for (item in servers) {
            if (item.checkBox.isSelected) {
                haveCheck = true
            } else {
                haveUncheck = true
            }
        }

        if (haveUncheck and haveCheck)
            tableServersCheckBoxAll.indeterminateProperty().value = true
        else if (!haveCheck and haveUncheck) {
            tableServersCheckBoxAll.selectedProperty().value = false
            tableServersCheckBoxAll.indeterminateProperty().value = false
        }
        else {
            tableServersCheckBoxAll.selectedProperty().value = true
            tableServersCheckBoxAll.indeterminateProperty().value = false
        }
    }

    private var funUncheck = EventHandler<ActionEvent> {
        val checkBox = it.target as CheckBox
        tableServers.selectionModel.select((checkBox.parent.parent as TableRow<server>).item)
        manageMainCheckBox(checkBox)
    }

    fun addRow(name: String, ip: String, nameBase1c: String) {
        servers.add(server(name = name, ip = ip, chBox = CheckBox(), select = false, nameBase1c = nameBase1c))
    }

    fun removeRow(index: Int) {
        servers.remove(index, index + 1)
    }
}