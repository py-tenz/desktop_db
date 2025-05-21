package org.example.demo2;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
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
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.ss.usermodel.*;
import java.sql.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;

import javafx.concurrent.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HelloApplication extends Application {
    public static Connection conn;
    protected static String WAY = "jdbc:mysql://localhost:3306/test";
    public static String NAME = "root";
    public static String PASS = "root";
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
        Label errorLabel = new Label(message);
        errorLabel.setWrapText(true);
        errorLabel.setMaxWidth(800);
        errorLabel.setAlignment(Pos.CENTER);
        errorLabel.setStyle("-fx-text-alignment: center; -fx-font-size: 18px; -fx-font-weight: 600;");

        Button back = new Button("← Назад");
        back.getStyleClass().add("back");
        back.setMinWidth(160);
        back.setMaxWidth(220);
        back.setStyle("-fx-font-size: 16px;");

        back.setOnAction(event1 -> stage.setScene(mainScene));

        VBox card = new VBox(30, errorLabel, back);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(40, 40, 40, 40));
        card.setMaxWidth(700);
        card.setStyle(
                "-fx-background-color: #fff;" +
                        "-fx-background-radius: 14;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 14, 0, 0, 2);"
        );

        StackPane root = new StackPane(card);
        root.setAlignment(Pos.CENTER);

        Scene scene = new Scene(root, 1000, 800);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        stage.setScene(scene);
    }

    @Override
    public void start(Stage stage) {
        HelloApplication.stage = stage;
        stage.setWidth(1000);
        stage.setHeight(800);
        stage.setMinWidth(1000);
        stage.setMinHeight(800);
        stage.setMaxWidth(1000);
        stage.setMaxHeight(800);
        stage.setResizable(false);

        // Заголовки
        Text title = new Text("Управление базой данных");
        title.getStyleClass().add("title");

        Text subtitle = new Text("Выберите действие из списка ниже");
        subtitle.getStyleClass().add("subtitle");

        // Кнопки
        Button btnShowTables = new Button("Вывести все таблицы из БД");
        Button btnCreateTable = new Button("Создать таблицу в БД");
        Button btnEnterList = new Button("Ввести список и сохранить в MySQL");
        Button btnDeleteById = new Button("Удалить элемент по ID");
        Button btnExportToExcel = new Button("Экспорт данных в Excel");

        btnShowTables.setOnAction(new HandleShowTables());
        btnCreateTable.setOnAction(new HandleCreateTable());
        btnEnterList.setOnAction(new HandleEnterList());
        btnDeleteById.setOnAction(new HandleDeleteById());
        btnExportToExcel.setOnAction(new HandleExportToExcel());

        for (Button btn : Arrays.asList(btnShowTables, btnCreateTable, btnEnterList, btnDeleteById, btnExportToExcel)) {
            btn.setMaxWidth(Double.MAX_VALUE);
        }

        VBox vbox = new VBox(16, title, subtitle);
        vbox.setAlignment(Pos.TOP_CENTER);
        vbox.setPadding(new Insets(40, 60, 40, 60));

        VBox buttonsBox = new VBox(10, btnShowTables, btnCreateTable, btnEnterList, btnDeleteById, btnExportToExcel);
        buttonsBox.setAlignment(Pos.CENTER);
        buttonsBox.setPadding(new Insets(20, 0, 0, 0));

        vbox.getChildren().add(buttonsBox);

        mainScene = new Scene(vbox, 1000, 800); // <= исправлено
        mainScene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        stage.setScene(mainScene);
        stage.setTitle("Управление Базой Данных");
        stage.show();
    }


    class HandleShowTables implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            Task<Void> showTask = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    String sqlQuery = "SHOW TABLES";
                    try {
                        PreparedStatement ps = conn.prepareStatement(sqlQuery);
                        ResultSet rs = ps.executeQuery();

                        // Заголовок
                        Label title = new Label("Список таблиц в базе данных");
                        title.getStyleClass().add("title");
                        title.setAlignment(Pos.CENTER);

                        // Список таблиц
                        VBox tablesBox = new VBox(10);
                        tablesBox.setAlignment(Pos.TOP_CENTER);

                        boolean anyTable = false;
                        while (rs.next()) {
                            String tableName = rs.getString(1);
                            Label tableLabel = new Label(tableName);
                            tableLabel.getStyleClass().add("table-list-item");
                            tableLabel.setMaxWidth(Double.MAX_VALUE);
                            tableLabel.setAlignment(Pos.CENTER_LEFT);
                            tablesBox.getChildren().add(tableLabel);
                            anyTable = true;
                        }

                        if (!anyTable) {
                            Label info = new Label("Нет созданных таблиц в базе данных.");
                            info.getStyleClass().add("error-label");
                            tablesBox.getChildren().add(info);
                        }

                        ScrollPane scrollPane = new ScrollPane(tablesBox);
                        scrollPane.setFitToWidth(true);
                        scrollPane.setPrefHeight(400);
                        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

                        Button back = new Button("← Назад");
                        back.getStyleClass().add("back");
                        back.setOnAction(event1 -> stage.setScene(mainScene));

                        VBox vbox = new VBox(30, title, scrollPane, back);
                        vbox.setAlignment(Pos.TOP_CENTER);
                        vbox.setPadding(new Insets(40, 60, 40, 60));
                        vbox.setSpacing(20);

                        Platform.runLater(() -> {
                            Scene scene = new Scene(vbox, 1000, 800);
                            scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
                            stage.setScene(scene);
                        });
                    } catch (SQLException e) {
                        Platform.runLater(() -> {
                            showMessage("Ошибка при выводе таблиц: " + e.getMessage());
                            stage.sizeToScene();
                        });
                    }
                    return null;
                }
            };
            Thread showThread = new Thread(showTask);
            showThread.setDaemon(true);
            showThread.start();
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
            Scene scene = new Scene(vbox, 1000, 800);
            stage.sizeToScene();
            scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
            stage.setScene(scene);

            btn.setOnAction(event1 -> {
                String tableName = tfield.getText().trim();

                // Проверка имени таблицы
                if (tableName.isEmpty()) {
                    showMessage("Имя таблицы не может быть пустым.");
                    stage.sizeToScene();
                    return;
                }
                if (!tableName.matches("^`?[a-zA-Z_][a-zA-Z0-9_]*`?$")) {
                    showMessage("Имя таблицы должно начинаться с буквы/подчёркивания и содержать только латинские буквы, цифры и подчёркивания.");
                    stage.setHeight(600);
                    stage.setWidth(800);
                    return;
                }

                Task<Void> CreateTask = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        String sqlQuery = "CREATE TABLE `" + tableName + "` (" +
                                "ID INT PRIMARY KEY," +
                                "STRING_LIST TEXT," +
                                "INTEGER_LIST TEXT)";

                        try (Statement stmt = conn.createStatement()) {
                            stmt.executeUpdate(sqlQuery);
                            Platform.runLater(() -> {
                                showMessage("Таблица " + tableName + " успешно создана!");
                            });
                        } catch (SQLException e) {
                            Platform.runLater(() -> {
                                showMessage("Ошибка при создании таблицы: " + e.getMessage());
                                stage.sizeToScene();
                            });
                        }
                        return null;
                    }
                };

                Thread createThread = new Thread(CreateTask);
                createThread.setDaemon(true);
                createThread.start();
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

    class HandleEnterList implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            Label tableLabel = new Label("Введите название таблицы:");
            tableLabel.setWrapText(true);
            tableLabel.setMaxWidth(400);

            TextField tableField = new TextField();
            tableField.setPrefWidth(400);

            Label stringsLabel = new Label("Введите 10 строк:");
            stringsLabel.setWrapText(true);
            stringsLabel.setMaxWidth(400);

            // Поля для 10 строк
            VBox inputFieldsBox = new VBox(5);
            inputFieldsBox.setPadding(new Insets(0, 0, 0, 0));
            List<TextField> stringFields = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                TextField tf = new TextField();
                tf.setPromptText("Строка " + (i + 1));
                tf.setPrefWidth(400);
                stringFields.add(tf);
                inputFieldsBox.getChildren().add(tf);
                inputFieldsBox.setAlignment(Pos.CENTER);
            }

            Label errorLabel = new Label();
            errorLabel.getStyleClass().add("error-label");
            errorLabel.setWrapText(true);
            errorLabel.setMaxWidth(400);

            Button btnSave = new Button("Сохранить");
            btnSave.setMaxWidth(200);

            VBox vbox = new VBox(15);
            vbox.setAlignment(Pos.CENTER);
            vbox.setPadding(new Insets(30, 0, 30, 0));
            vbox.setMaxWidth(500); // ограничение ширины контейнера
            vbox.getChildren().addAll(tableLabel, tableField, stringsLabel, inputFieldsBox, errorLabel, btnSave);

            StackPane root = new StackPane(vbox);
            root.setPadding(new Insets(0, 0, 0, 0));

            Scene scene = new Scene(root, 1000, 800);
            scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
            stage.setScene(scene);

            btnSave.setOnAction(e -> {
                String tableName = tableField.getText().trim();

                if (tableName.isEmpty()) {
                    errorLabel.setText("Имя таблицы не может быть пустым!");
                    return;
                }

                List<String> inputList = new ArrayList<>();
                for (TextField tf : stringFields) {
                    inputList.add(tf.getText().trim());
                }

                boolean allFilled = inputList.stream().allMatch(s -> !s.isEmpty());
                if (!allFilled) {
                    errorLabel.setText("Пожалуйста, заполните все 10 строк!");
                    return;
                }
                errorLabel.setText("");

                Listik listik = new Listik();
                List<Integer> randomList = listik.random();

                Task<Void> saveTask = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        String sqlInsert = "INSERT INTO `" + tableName + "` (ID, STRING_LIST, INTEGER_LIST) VALUES (?, ?, ?)";
                        try (PreparedStatement ps = conn.prepareStatement(sqlInsert)) {
                            ps.setInt(1, (int) (Math.random() * 10000));
                            ps.setString(2, String.join(",", inputList));
                            ps.setString(3, randomList.toString());
                            ps.executeUpdate();

                            Platform.runLater(() -> {
                                showMessage("Данные успешно сохранены в таблицу " + tableName);
                            });
                        } catch (SQLException e) {
                            Platform.runLater(() -> {
                                showMessage("Ошибка при сохранении данных: " + e.getMessage());
                            });
                        }
                        return null;
                    }
                };
                Thread saveThread = new Thread(saveTask);
                saveThread.setDaemon(true);
                saveThread.start();
            });
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
            Scene scene = new Scene(vbox, 1000, 800);
            stage.sizeToScene();
            scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
            stage.setScene(scene);

            btn.setOnAction(event1 -> {
                String tableName = textField.getText().trim();

                if (tableName.isEmpty()) {
                    showMessage("Имя таблицы не может быть пустым.");
                    stage.sizeToScene();
                    return;
                }


                if (!tableName.matches("^`?[a-zA-Z_][a-zA-Z0-9_]*`?$")) {
                    showMessage("Имя таблицы должно начинаться с буквы или подчёркивания и содержать только буквы, цифры, подчёркивания.");
                    stage.sizeToScene();
                    return;
                }

                Text txt = new Text("Введите ID для удаления:");
                TextField idField = new TextField();
                Button deleteButton = new Button("Удалить");
                Label availableIdsLabel = new Label("Загрузка доступных ID...");

                VBox vbox2 = new VBox(10, availableIdsLabel, txt, idField, deleteButton);
                vbox2.setAlignment(Pos.CENTER);
                Scene scene2 = new Scene(vbox2, 1000, 800);
                stage.sizeToScene();
                scene2.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
                stage.setScene(scene2);

                Task<Void> listIdsTask = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        String sql = "SELECT ID FROM `" + tableName + "`";
                        try (Statement stmt = conn.createStatement();
                             ResultSet rs = stmt.executeQuery(sql)) {

                            StringBuilder ids = new StringBuilder();
                            int count = 0;
                            while (rs.next()) {
                                ids.append(rs.getInt("ID")).append("\n");
                                count++;
                            }

                            String resultMessage;
                            if (count > 0) {
                                resultMessage = "Доступные ID:\n" + ids;
                            } else {
                                resultMessage = "Нет доступных ID для удаления.";
                            }

                            final String labelText = resultMessage;
                            Platform.runLater(() -> {
                                availableIdsLabel.setText(labelText);
                            });

                        } catch (SQLException e) {
                            Platform.runLater(() -> showMessage("Ошибка при получении ID: " + e.getMessage()));
                            stage.sizeToScene();
                        }
                        return null;
                    }
                };

                Thread listThread = new Thread(listIdsTask);
                listThread.setDaemon(true);
                listThread.start();

                deleteButton.setOnAction(event2 -> {
                    String idText = idField.getText().trim();
                    if (!idText.matches("\\d+")) {
                        showMessage("ID должен быть числом.");
                        return;
                    }

                    int idToDelete = Integer.parseInt(idText);

                    Task<Void> deleteTask = new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                            String sqlDelete = "DELETE FROM `" + tableName + "` WHERE ID = ?";
                            try (PreparedStatement ps = conn.prepareStatement(sqlDelete)) {
                                ps.setInt(1, idToDelete);
                                int affectedRows = ps.executeUpdate();

                                Platform.runLater(() -> {
                                    if (affectedRows > 0) {
                                        showMessage("Запись с ID " + idToDelete + " успешно удалена.");
                                    } else {
                                        showMessage("Запись с ID " + idToDelete + " не найдена.");
                                    }
                                });
                            } catch (SQLException e) {
                                Platform.runLater(() -> showMessage("Ошибка при удалении: " + e.getMessage()));
                            }
                            return null;
                        }
                    };

                    Thread deleteThread = new Thread(deleteTask);
                    deleteThread.setDaemon(true);
                    deleteThread.start();
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
            Scene scene = new Scene(vbox, 1000, 800);
            stage.sizeToScene();
            scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
            stage.setScene(scene);

            exportButton.setOnAction(e -> {
                String tableName = tableField.getText().trim();
                if (tableName.isEmpty()) {
                    showMessage("Название таблицы не может быть пустым.");
                    stage.sizeToScene();
                    return;
                }

                Task<Void> exportTask = new Task<Void>() {
                    @Override
                    protected Void call() {
                        String sqlQuery = "SELECT * FROM `" + tableName + "`";
                        try (PreparedStatement stmt = conn.prepareStatement(sqlQuery);
                             ResultSet rs = stmt.executeQuery()) {

                            XSSFWorkbook workbook = new XSSFWorkbook();
                            XSSFSheet sheet = workbook.createSheet("Export");

                            ResultSetMetaData metaData = rs.getMetaData();
                            int columnCount = metaData.getColumnCount();

                            // Записываем заголовки столбцов
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

                            // Сохраняем рабочую книгу в файл
                            String filePath = tableName + "_export.xlsx";
                            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                                workbook.write(fileOut);
                            }
                            workbook.close();

                            Platform.runLater(() -> {
                                showMessage("Экспорт завершён. Файл сохранён как:\n" + filePath);
                            });
                        } catch (SQLException ex) {
                            Platform.runLater(() -> showMessage("Ошибка SQL: " + ex.getMessage()));
                            stage.sizeToScene();
                        } catch (IOException ex) {
                            Platform.runLater(() -> showMessage("Ошибка записи в файл: " + ex.getMessage()));
                            stage.sizeToScene();
                        }
                        return null;
                    }
                };

                Thread exportThread = new Thread(exportTask);
                exportThread.setDaemon(true);
                exportThread.start();
            });
        }
    }

    public static void main(String[] args) {
        launch();

    }
}