module com.monopoly {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    
    opens com.monopoly to javafx.fxml;
    exports com.monopoly;
}
