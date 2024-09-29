module com.example.chatappfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires jbcrypt;


    opens com.example.chatappfx to javafx.fxml;
    exports com.example.chatappfx;
}