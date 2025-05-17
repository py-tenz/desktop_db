package org.example.demo1;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;

public class HelloApplication extends Application {
    public static Connection conn;
    protected static String WAY = "jdbc:mysql://localhost:3306/test";
    public static String NAME = "root";
    public static String PASS = "Rodef2007";
    public static Stage stage;
    public static Scene mainScene;
    static {
        try {
            conn = DriverManager.getConnection(WAY, NAME, PASS);
            System.out.println("Подключение успешно установлено");
        }
        catch (SQLException e) {
            System.out.println("Ошибка подключения к БД: " + e.getMessage());
        }
    }
    public void showMessage(String message) {
        Text text = new Text(message);
        Button back = new Button("<-Назад");
        back.setOnAction(event1 -> {
            stage.setScene(mainScene);
        });

        VBox vbox2 = new VBox(text, back);
        vbox2.setAlignment(Pos.CENTER);
        Scene scene = new Scene(vbox2, 400, 300);
        stage.setScene(scene);
    }
    @Override
    public void start(Stage stage) {
        Button btnShowTables = new Button("1. Вывести все таблицы из БД");
        btnShowTables.setOnAction(new HandleShowTables());
        Button btnCreateTable = new Button("2. Создать таблицу в БД");
        btnCreateTable.setOnAction(new HandleCreateTable());
        Button btnEnterList = new Button("3. Ввести список и сохранить в MySQL");
        btnEnterList.setOnAction(new HandleEnterList());
        Button btnDeleteById = new Button("4. Удалить элемент по ID");
        btnDeleteById.setOnAction(new HandleDeleteById());
        Button btnExportToExcel = new Button("5. Экспорт данных в Excel");
        btnExportToExcel.setOnAction(new HandleExportToExcel());
        HelloApplication.stage = stage;
        VBox vbox = new VBox(15);
        vbox.getChildren().addAll(
                btnShowTables,
                btnCreateTable,
                btnEnterList,
                btnDeleteById,
                btnExportToExcel
        );

        vbox.setAlignment(Pos.CENTER);
        mainScene = new Scene(vbox, 400, 300);

        stage.setTitle("Управление Базой Данных");
        stage.setScene(mainScene);
        stage.show();
    }
    class HandleShowTables implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            String sqlQuery = "SHOW TABLES";
            try {
                PreparedStatement ps = conn.prepareStatement(sqlQuery);
                ResultSet rs = ps.executeQuery();

                VBox vbox = new VBox(10);
                vbox.setAlignment(Pos.CENTER);
                while (rs.next()) {
                    String tableName = rs.getString(1);
                    Text text = new Text(tableName);
                    vbox.getChildren().add(text);
                }
                Button back = new Button("<-Назад");
                back.setOnAction(event1 -> {
                    stage.setScene(mainScene);
                });
                vbox.getChildren().add(back);

                ScrollPane scrollPane = new ScrollPane(vbox);
                scrollPane.setFitToWidth(true);
                scrollPane.setPrefHeight(300);

                Scene scene = new Scene(scrollPane, 400, 300);
                stage.setScene(scene);
            } catch (SQLException e) {
                System.out.println("Ошибка при выводе таблиц: " + e.getMessage());
            }
        }
    }

    class HandleCreateTable implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            Label label = new Label("Введите название для новой таблицы: ");
            TextField tfield = new TextField();
            tfield.setPrefWidth(150);
            Button btn = new Button("Создать");
            VBox vbox = new VBox(10, label, tfield, btn);
            vbox.setAlignment(Pos.CENTER);
            Scene scene = new Scene(vbox, 400, 300);
            stage.setScene(scene);

            btn.setOnAction(event1 -> {
                String tableName = tfield.getText().trim();

                // Проверка имени таблицы
                if (tableName.isEmpty()) {
                    showMessage("Имя таблицы не может быть пустым.");
                    return;
                }
                if (!tableName.matches("^[a-zA-Z_][a-zA-Z0-9_]*$")) {
                    showMessage("Имя таблицы должно начинаться с буквы/подчёркивания и содержать только латинские буквы, цифры и подчёркивания.");
                    return;
                }

                String sqlQuery = "CREATE TABLE `" + tableName + "` (" +
                        "ID INT PRIMARY KEY," +
                        "STRING_LIST VARCHAR(50)," +
                        "INTEGER_LIST VARCHAR(1001))";

                try (Statement stmt = conn.createStatement()) {
                    stmt.executeUpdate(sqlQuery);
                    showMessage("Таблица " + tableName + " успешно создана!");
                } catch (SQLException e) {
                    showMessage("Ошибка при создании таблицы: " + e.getMessage());
                }
            });
        }


//        private void showMessage(String message) {
//            Text text = new Text(message);
//            Button back = new Button("<-Назад");
//            back.setOnAction(event1 -> {
//                stage.setScene(mainScene);
//            });
//
//            VBox vbox2 = new VBox(text, back);
//            vbox2.setAlignment(Pos.CENTER);
//            Scene scene = new Scene(vbox2, 400, 300);
//            stage.setScene(scene);
//        }
    }


    static class HandleEnterList implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {

        }
    }

    class HandleDeleteById implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            // 1. Ввод имени таблицы
            Text text = new Text("Введите название таблицы:");
            TextField textField = new TextField();
            Button btn = new Button("Далее →");

            VBox vbox = new VBox(10, text, textField, btn);
            vbox.setAlignment(Pos.CENTER);
            Scene scene = new Scene(vbox, 400, 300);
            stage.setScene(scene);

            btn.setOnAction(event1 -> {
                String tableName = textField.getText().trim();

                if (tableName.isEmpty()) {
                    showMessage("Имя таблицы не может быть пустым.");
                    return;
                }

                
                if (!tableName.matches("^[a-zA-Z_][a-zA-Z0-9_]*$")) {
                    showMessage("Имя таблицы должно начинаться с буквы или подчёркивания и содержать только буквы, цифры, подчёркивания.");
                    return;
                }


                Text txt = new Text("Введите ID для удаления:");
                TextField idField = new TextField();
                Button deleteButton = new Button("Удалить");

                VBox vbox2 = new VBox(10, txt, idField, deleteButton);
                vbox2.setAlignment(Pos.CENTER);
                Scene scene2 = new Scene(vbox2, 400, 300);
                stage.setScene(scene2);

                deleteButton.setOnAction(event2 -> {
                    String idStr = idField.getText().trim();

                    if (idStr.isEmpty()) {
                        showMessage("ID не может быть пустым.");
                        return;
                    }

                    if (!idStr.matches("^-?\\d+$")) {
                        showMessage("ID должен быть целым числом.");
                        return;
                    }

                    int readyId = Integer.parseInt(idStr);

                    String sqlQuery = "DELETE FROM `" + tableName + "` WHERE ID=?";
                    try (PreparedStatement pstmt = conn.prepareStatement(sqlQuery)) {
                        pstmt.setInt(1, readyId);
                        int rows = pstmt.executeUpdate();

                        if (rows > 0) {
                            showMessage("Объект с ID " + readyId + " успешно удалён.");
                        } else {
                            showMessage("Объект с ID " + readyId + " не найден.");
                        }
                    } catch (SQLException e) {
                        showMessage("Ошибка при удалении: " + e.getMessage());
                    }
                });
            });
        }

//        private void showMessage(String message) {
//            Text msg = new Text(message);
//            Button backButton = new Button("Назад");
//            backButton.setOnAction(e -> handle(null)); // Вернуться к началу
//
//            VBox vbox = new VBox(10, msg, backButton);
//            vbox.setAlignment(Pos.CENTER);
//            Scene scene = new Scene(vbox, 400, 300);
//            stage.setScene(scene);
//        }
    }

    class HandleExportToExcel implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            Label label = new Label("Введите название таблицы для экспорта:");
            TextField tableField = new TextField();
            Button exportButton = new Button("Экспортировать в Excel");

            VBox vbox = new VBox(10, label, tableField, exportButton);
            vbox.setAlignment(Pos.CENTER);
            Scene scene = new Scene(vbox, 400, 300);
            stage.setScene(scene);

            exportButton.setOnAction(e -> {
                String tableName = tableField.getText().trim();
                if (tableName.isEmpty()) {
                    showMessage("Название таблицы не может быть пустым.");
                    return;
                }

                String sqlQuery = "SELECT * FROM `" + tableName + "`";
                try (PreparedStatement stmt = conn.prepareStatement(sqlQuery);
                     ResultSet rs = stmt.executeQuery()) {

                    XSSFWorkbook workbook = new XSSFWorkbook();
                    XSSFSheet sheet = workbook.createSheet("Export");

                    ResultSetMetaData metaData = rs.getMetaData();
                    int columnCount = metaData.getColumnCount();

                    // Заголовки
                    Row header = sheet.createRow(0);
                    for (int i = 1; i <= columnCount; i++) {
                        Cell cell = header.createCell(i - 1);
                        cell.setCellValue(metaData.getColumnName(i));
                    }

                    int rowIndex = 1;
                    while (rs.next()) {
                        Row row = sheet.createRow(rowIndex++);
                        for (int i = 1; i <= columnCount; i++) {
                            row.createCell(i - 1).setCellValue(rs.getString(i));
                        }
                    }

                    // Сохранение в файл
                    String filePath = tableName + "_export.xlsx";
                    try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                        workbook.write(fileOut);
                        workbook.close();
                    }

                    showMessage("Экспорт завершён. Файл сохранён как:\n" + filePath);

                } catch (SQLException ex) {
                    showMessage("Ошибка SQL: " + ex.getMessage());
                } catch (IOException ex) {
                    showMessage("Ошибка записи в файл: " + ex.getMessage());
                }
            });
        }
    }

    public static void main(String[] args) {
        launch();
    }
}

