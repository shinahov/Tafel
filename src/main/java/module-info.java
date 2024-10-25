module org.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.slf4j;
    requires org.xerial.sqlitejdbc;
    requires static lombok;

    opens org.example.Aplication to javafx.fxml;
    exports org.example.Aplication;
}