import bank.PrivateBank;
import bank.exceptions.AccountAlreadyExistsException;
import bank.exceptions.AccountDoesNotExistException;
import bank.exceptions.NumericValueInvalidException;
import bank.exceptions.TransactionDoesNotExistException;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Optional;


public class FxController {
    @FXML
    ListView accountsList;
    @FXML
    Button testButton;
    @FXML
    Menu accountsMenu;

    private PrivateBank bank = new PrivateBank("Meine Bank", 0.2, 0.2);

    public FxController() throws NumericValueInvalidException {

    }

    public void onSelect(ActionEvent event, String accountName) throws IOException {
        System.out.println("Ausgewählt");
        changeToAccountView(event, accountName);
    }

    public void onDelete(ActionEvent event, String accountName) {
        System.out.println("Loesche: " + accountName);


        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Löschen bestätigen");
        alert.setContentText("Möchten Sie diesen Account wirklich löschen?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    bank.deleteAccount(accountName);
                    accountsList.getItems().remove(accountName);
                } catch (AccountDoesNotExistException | IOException  e) {
                    e.printStackTrace();
                    FxApplication.showError(e);
                }
            }
        });
        //accountsList.getItems().remove(accountName);
    }

    public List<String> fetchAllAccounts() {
        return bank.getAllAccounts();
    }

    @FXML
    public void initialize() {
        try {
            bank.setDirectoryName("testData");
            bank.readAccounts();
            accountsList.getItems().addAll(fetchAllAccounts());


            accountsMenu.getItems().clear();
            MenuItem item = new MenuItem("Neues Konto");
            item.setOnAction(event -> {
                try {
                    TextInputDialog dialog = new TextInputDialog();
                    dialog.setTitle("Neues Konto Anlegen");
                    dialog.setHeaderText("Hier können Sie ein neues Konto anlegen");
                    dialog.setContentText("Der Name des Kontos:");
                    Optional<String> result = dialog.showAndWait();

                    result.ifPresent(name -> {
                        try {
                            bank.createAccount(name);
                            accountsList.getItems().add(name);
                            bank.writeAccount(name);
                        } catch (AccountAlreadyExistsException |  IOException e) {
                            FxApplication.showError(e);
                            e.printStackTrace();
                        }

                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            accountsMenu.getItems().add(item);

            // Binding Cells to ContextMenu
            accountsList.setCellFactory(lv -> {

                ListCell<String> cell = new ListCell<>();
                ContextMenu contextMenu = new ContextMenu();

                MenuItem selectItem = new MenuItem();
                selectItem.textProperty().bind(Bindings.format("Auswahlen \"%s\"", cell.itemProperty()));
                selectItem.setOnAction(event -> {
                    try {
                        onSelect(event, cell.getItem());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });

                MenuItem deleteItem = new MenuItem();
                deleteItem.textProperty().bind(Bindings.format("Loeschen \"%s\"", cell.itemProperty()));
                deleteItem.setOnAction(event -> {
                    onDelete(event, cell.getItem());
                });

                contextMenu.getItems().addAll(selectItem, deleteItem);

                cell.textProperty().bind(cell.itemProperty());
                cell.emptyProperty().addListener((obs, wasEmpty, isNowEmpty) -> {
                    if (isNowEmpty) {
                        cell.setContextMenu(null);
                    } else {
                        cell.setContextMenu(contextMenu);
                    }
                });
                return cell;
            });


        } catch (Exception e) {
           e.printStackTrace();
            FxApplication.showError(e);

        }
    }

    @FXML
    public void btnTest(ActionEvent event) throws IOException {
        System.out.println("Test");
    }

    private void changeToAccountView(ActionEvent event, String accountName) throws IOException {
        System.out.println("changeToAccountView");
        System.out.println(accountName);

        Stage primaryStage = (Stage) accountsList.getScene().getWindow();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("AccountView.fxml"));
        Parent accountView = loader.load();

        AccountViewController controller = loader.getController();
        try {
            controller.setAccount(accountName, bank);
        } catch (Exception e) {
            FxApplication.showError(e);
            e.printStackTrace();
        }

        Scene accountViewScene = new Scene(accountView);
        primaryStage.setScene(accountViewScene);
    }

}
