module chapter5 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;

    opens chapter5 to javafx.fxml;
    exports chapter5;
}
