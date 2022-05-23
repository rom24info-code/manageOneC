package com.manage0nec.manageonec

import java.net.URL
import java.util.ResourceBundle
import javafx.fxml.FXML
import javafx.scene.control.Label

class SourceConfig1c {

    @FXML
    private lateinit var resources: ResourceBundle

    @FXML
    private lateinit var location: URL

    @FXML
    private lateinit var label: Label

    @FXML
    fun initialize() {
        assert(label != null) {"fx:id=\"label\" was not injected: check your FXML file 'SourceConfig1c.fxml'." }
        label.text = Main.settingsApp["sourceConfConnectionString"] as String
    }

}

