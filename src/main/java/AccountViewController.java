import bank.*;
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
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;


public class AccountViewController {
    @FXML
    Label labelAccountOwner;

    @FXML
    Label labelAccountBalance;

    @FXML
    ListView transactionsList;

    @FXML
    Button backToHomeBtn;

    @FXML
    Button ascSortBtn;

    @FXML
    Button descSortBtn;

    @FXML
    Button posFilterBtn;

    @FXML
    Button negFilterBtn;

    @FXML
    Button addTransactionBtn;

    @FXML
    TextField transactionDateField;

    @FXML
    TextField transactionDescriptionField;

    @FXML
    TextField transactionAmountField;

    @FXML
    CheckBox transactionIsPaymentCheckbox;

    @FXML
    TextField transactionSenderField;

    @FXML
    TextField transactionReceiverField;

    @FXML
    TextField transactionIncomingInterestField;

    @FXML
    TextField transactionOutgoingInterestField;

    @FXML
    DialogPane createTransactionDialog;

    @FXML
    GridPane gridInterests;

    @FXML
    GridPane gridSenderReceiver;

    private List<Transaction> transactions;
    private PrivateBank bank;
    private String accountName;

    public AccountViewController() throws NumericValueInvalidException {

    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public void setBank(PrivateBank bank) {
        this.bank = bank;
    }

    public void setLabelAccountOwner(String accountOwner) {
        labelAccountOwner.setText(accountOwner);
    }

    public void setLabelAccountBalance(Double accountBalance) {
        labelAccountBalance.setText("Kontostand: " + String.valueOf(accountBalance));
    }

    public void setAccount(String accountName, PrivateBank bank) throws AccountDoesNotExistException {
        setLabelAccountOwner(accountName);
        System.out.println(bank.getAccountBalance(accountName));
        setLabelAccountBalance(bank.getAccountBalance(accountName));
        setBank(bank);
        setAccountName(accountName);

        transactions = bank.getTransactions(accountName);
        transactionsList.getItems().addAll(transactions);
    }

    private void onDelete(ActionEvent event, Transaction transaction) throws AccountDoesNotExistException, TransactionDoesNotExistException {
        System.out.println("Loesche: " + transaction);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Löschen bestätigen");
        alert.setContentText("Möchten Sie die Transaktion wirklich löschen?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    bank.removeTransaction(accountName, transaction);
                    transactions = bank.getTransactions(accountName);

                    transactionsList.getItems().clear();
                    transactionsList.getItems().addAll(transactions);
                    setLabelAccountBalance(bank.getAccountBalance(accountName));

                    bank.writeAccount(accountName);
                } catch (AccountDoesNotExistException | TransactionDoesNotExistException | IOException e) {
                    e.printStackTrace();
                    FxApplication.showError(e);

                }
            }
        });
    }

    private void addTransaction() {
        try {
            if (transactionIsPaymentCheckbox.isSelected()) {
                Payment p = new Payment(
                        transactionDateField.getText(),
                        transactionDescriptionField.getText(),
                        Double.parseDouble(transactionAmountField.getText()),
                        Double.parseDouble(transactionIncomingInterestField.getText()),
                        Double.parseDouble(transactionOutgoingInterestField.getText())
                );
                bank.addTransaction(accountName, p);
                bank.writeAccount(accountName);
                createTransactionDialog.setVisible(false);

            } else {
                if (transactionSenderField.getText().equals(accountName)) {
                    OutgoingTransfer t = new OutgoingTransfer(
                            transactionDateField.getText(),
                            transactionDescriptionField.getText(),
                            Double.parseDouble(transactionAmountField.getText()),
                            accountName,
                            transactionReceiverField.getText()
                    );
                    bank.addTransaction(accountName, t);
                    bank.writeAccount(accountName);
                    createTransactionDialog.setVisible(false);

                } else {
                    IncomingTransfer t = new IncomingTransfer(
                            transactionDateField.getText(),
                            transactionDescriptionField.getText(),
                            Double.parseDouble(transactionAmountField.getText()),
                            transactionSenderField.getText(),
                            accountName
                    );
                    bank.addTransaction(accountName, t);
                    bank.writeAccount(accountName);
                    createTransactionDialog.setVisible(false);

                }
            }

            transactions = bank.getTransactions(accountName);
            transactionsList.getItems().clear();
            transactionsList.getItems().addAll(transactions);
            setLabelAccountBalance(bank.getAccountBalance(accountName));

            cleanDialogUp();
        } catch (Exception e) {
            FxApplication.showError(e);
            e.printStackTrace();
        }
    }

    private void cleanDialogUp() {
        transactionDateField.clear();
        transactionDescriptionField.clear();
        transactionAmountField.clear();
        transactionIsPaymentCheckbox.setSelected(false);
    }

    @FXML
    public void initialize() {
        try {
            gridInterests.setVisible(false);
            createTransactionDialog.setVisible(false);

            Button btnOK = (Button) createTransactionDialog.lookupButton(ButtonType.OK);
            Button btnCancel = (Button) createTransactionDialog.lookupButton(ButtonType.CANCEL);
            btnOK.addEventFilter(
                    ActionEvent.ACTION,
                    event -> {
                        addTransaction();

                    }
            );

            btnCancel.addEventFilter(
                    ActionEvent.ACTION,
                    event -> {
                        System.out.println("Cancel");
                        createTransactionDialog.setVisible(false);
                    }
            );


            addTransactionBtn.setOnAction(event -> {
                createTransactionDialog.setVisible(true);
                cleanDialogUp();
            });


            // Binding Cells to ContextMenu
            transactionsList.setCellFactory(lv -> {

                ListCell<Transaction> cell = new ListCell<>();
                ContextMenu contextMenu = new ContextMenu();

                MenuItem deleteItem = new MenuItem();
                deleteItem.textProperty().bind(Bindings.format("Loeschen \"%s\"", cell.itemProperty()));
                deleteItem.setOnAction(event -> {
                    try {
                        onDelete(event, cell.getItem());
                    } catch (AccountDoesNotExistException e) {
                        FxApplication.showError(e);

                    } catch (TransactionDoesNotExistException e) {
                        FxApplication.showError(e);
                    }
                });

                contextMenu.getItems().addAll(deleteItem);

                cell.textProperty().bind(cell.itemProperty().asString());
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
    void onAscSortBtn(ActionEvent event) {
        transactionsList.getItems().clear();
        transactionsList.getItems().addAll(bank.getTransactionsSorted(accountName, true));
    }

    @FXML
    void onDescSortBtn(ActionEvent event) {
        transactionsList.getItems().clear();
        transactionsList.getItems().addAll(bank.getTransactionsSorted(accountName, false));
    }

    @FXML
    void onPosFilterBtn(ActionEvent event) {
        transactionsList.getItems().clear();
        transactionsList.getItems().addAll(bank.getTransactionsByType(accountName, true));
    }

    @FXML
    void onNegFilterBtn(ActionEvent event) {
        transactionsList.getItems().clear();
        transactionsList.getItems().addAll(bank.getTransactionsByType(accountName, false));
    }

    @FXML
    void onAddTransactionBtn(ActionEvent event) {
        System.out.println("Add Transaction BTN");
    }


    @FXML
    public void backToHome(ActionEvent event) throws IOException {
        System.out.println("backToHome");

        System.out.println("Test");

        Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        Parent mainView = FXMLLoader.load(getClass().getResource("MainView.fxml"));
        Scene mainViewScene = new Scene(mainView);
        primaryStage.setScene(mainViewScene);
    }

    @FXML
    public void isPaymentChanged(ActionEvent event) {
        System.out.println("isPaymentChanged");
        System.out.println(event);
        System.out.println(event.getTarget());
        if (transactionIsPaymentCheckbox.isSelected()) {
            gridInterests.setVisible(true);
            gridSenderReceiver.setVisible(false);
        } else {
            gridInterests.setVisible(false);
            gridSenderReceiver.setVisible(true);
        }
    }


}
