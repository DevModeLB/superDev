package com.devmode.superdev.Controllers;

import javafx.fxml.FXML;
import javafx.stage.Stage;
import com.devmode.superdev.utils.AuthUtils;

public class CategoryController {


    @FXML
    public void initialize() {
        AuthUtils authUtils = new AuthUtils();
        authUtils.checkAuthentication();
    }

}
