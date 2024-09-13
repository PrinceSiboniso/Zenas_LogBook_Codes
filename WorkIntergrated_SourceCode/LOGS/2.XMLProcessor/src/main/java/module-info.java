module org.example.xmlprocessor {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
	requires javafx.graphics;
	requires java.xml;
	requires javafx.base;
    // requires javax;

    opens org.example.xmlprocessor to javafx.fxml;
    exports org.example.xmlprocessor;
}