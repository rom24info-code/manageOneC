package com.manage0nec.manageonec

import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.TextField
import javafx.scene.layout.VBox
import javafx.stage.Stage

class ServerEditForm {
    lateinit var parent: MainForm
    @FXML
    private lateinit var textEditIp: TextField

    @FXML
    private lateinit var textEditNameBase: TextField

    @FXML
    private lateinit var textEditNameServer: TextField


    @FXML
    lateinit var form: VBox

    @FXML
    fun closeForm(actionEvent: ActionEvent) {
        val stage = form.scene.window as Stage
        stage.close()
    }

    @FXML
    fun actionOk(actionEvent: ActionEvent) {
        parent.addRow(textEditNameServer.text, textEditIp.text, textEditNameBase.text)
        closeForm(actionEvent)
    }

}