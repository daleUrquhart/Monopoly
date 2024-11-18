module com.monopoly {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.monopoly to javafx.fxml;
    exports com.monopoly;
}
