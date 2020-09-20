package space.ifel.directory;

import space.ifel.core.models.Login;
import space.ifel.config.Config;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import space.ifel.core.servers.ServerTask;

import java.beans.PropertyChangeEvent;
import java.net.InetAddress;
import java.net.URL;
import java.util.*;

public class Controller implements Initializable {

    private Config config;
    private ServerTask readOnlyServer;
    private ServerTask writableServer;

    public Controller() {
        System.out.println("Registering controller shutdown hook.");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> System.out.println("Shutting down controller")));

        System.out.println("Controller shutdown hook registered.");
        this.config = new Config();
    }

    // DASHBOARD TAB
    @FXML
    private Button startSystem;
    @FXML
    private TextArea dashboardLog;
    //

    // WRITE SERVER TAB
    @FXML
    private ListView<String> writeServerIpList;
    @FXML
    private TextField addWhitelistIp;
    @FXML
    private Button writeServerAddButton;
    @FXML
    private Button writeServerRemoveButton;
    @FXML
    private Slider writeServerThreadSlider;
    @FXML
    private TextField writeServerAvailableTextField;
    @FXML
    private TextField writeServerActiveTextField;
    @FXML
    private TextField writeServerPortTextField;
    @FXML
    private TextArea writeServerLog;
    //

    // READ SERVER TAB
    @FXML
    private ListView<String> readServerIpList;
    @FXML
    private TextField addBlacklistIp;
    @FXML
    private Button readServerAddButton;
    @FXML
    private Button readServerRemoveButton;
    @FXML
    private Slider readServerThreadSlider;
    @FXML
    private TextField readServerAvailableTextField;
    @FXML
    private TextField readServerActiveTextField;
    @FXML
    private TextField readServerPortTextField;
    @FXML
    private TextArea readServerLog;
    //

    // MSSQL TAB
    @FXML
    private TextField mssqlServerAddressTextField;
    @FXML
    private TextField mssqlServerDatabaseTextField;
    @FXML
    private TextField mssqlServerUsernameTextField;
    @FXML
    private TextField mssqlServerPasswordTextField;
    @FXML
    private TextField mssqlPortTextField;
    @FXML
    private Slider mssqlConnectionPoolSlider;
    @FXML
    private TextField mssqlAvailableTextField;
    @FXML
    private TextField mssqlActiveTextField;
    @FXML
    private TextArea mssqlLog;
    //

    // MYSQL TAB
    @FXML
    private TextField mysqlServerAddressTextField;
    @FXML
    private TextField mysqlServerDatabaseTextField;
    @FXML
    private TextField mysqlServerUsernameTextField;
    @FXML
    private TextField mysqlServerPasswordTextField;
    @FXML
    private TextField mysqlPortTextField;
    @FXML
    private Slider mysqlConnectionPoolSlider;
    @FXML
    private TextField mysqlAvailableTextField;
    @FXML
    private TextField mysqlActiveTextField;
    @FXML
    private TextArea mysqlLog;
    //

    // LOGIN TAB
    @FXML
    private TableView<Login> loginTable;
    @FXML
    private TableColumn<String, Login> colAlias = new TableColumn<>("Account Alias");
    @FXML
    private TableColumn<String, Login> colIp = new TableColumn<>("Account Alias");
    @FXML
    private TableColumn<String, Login> colWhen = new TableColumn<>("Account Alias");

    // SETTINGS TAB
    @FXML
    private CheckBox settingAllOnSslCheckbox;
    @FXML
    private CheckBox settingHeartBeatsCheckbox;
    //

    private boolean connected = false;

    @FXML
    public void connect() {
        if (!connected) {
            setFieldDisable(true);
            startSystem.setText("Stop");
            this.runServers();
            connected = true;
            return;
        }

        setFieldDisable(false);
        startSystem.setText("Start");
        this.stopServers();
        connected = false;
    }

    private ArrayList<InetAddress> getWhitelist() {
        return getInetAddresses(writeServerIpList);
    }

    private ArrayList<InetAddress> getBlacklist() {
        return getInetAddresses(readServerIpList);
    }

    private ArrayList<InetAddress> getInetAddresses(ListView<String> serverIpList) {
        ArrayList<InetAddress> inetList = new ArrayList<>();
        serverIpList.getItems().forEach(stringAddress -> {
            try {
                inetList.add(InetAddress.getByName(stringAddress));
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        });

        return inetList;
    }

    public void runServers() {
        this.readOnlyServer = new ServerTask(Integer.parseInt(readServerPortTextField.getText()), getBlacklist(), "read");
        this.readOnlyServer.addPropertyChangeListener(this::handleMessage);
        this.readOnlyServer.execute();

        this.writableServer = new ServerTask(Integer.parseInt(writeServerPortTextField.getText()), getWhitelist(), "write");
        this.writableServer.addPropertyChangeListener(this::handleMessage);
        this.writableServer.execute();
    }

    public void stopServers() {
        this.readOnlyServer.cancel();
        this.writableServer.cancel();
    }

    /// FORM ENABLE/DISABLE

    public void setFieldDisable(Boolean disable) {
        // WRITE SERVER TAB
        writeServerIpList.setDisable(disable);
        addWhitelistIp.setDisable(disable);
        writeServerAddButton.setDisable(disable);
        writeServerRemoveButton.setDisable(disable);
        writeServerThreadSlider.setDisable(disable);
        writeServerPortTextField.setDisable(disable);

        // READ SERVER TAB
        readServerIpList.setDisable(disable);
        addBlacklistIp.setDisable(disable);
        readServerAddButton.setDisable(disable);
        readServerRemoveButton.setDisable(disable);
        readServerThreadSlider.setDisable(disable);
        readServerPortTextField.setDisable(disable);

        // MSSQL TAB
        mssqlServerAddressTextField.setDisable(disable);
        mssqlServerDatabaseTextField.setDisable(disable);
        mssqlServerUsernameTextField.setDisable(disable);
        mssqlServerPasswordTextField.setDisable(disable);
        mssqlConnectionPoolSlider.setDisable(disable);
        mssqlPortTextField.setDisable(disable);

        // MYSQL TAB
        mysqlServerAddressTextField.setDisable(disable);
        mysqlServerDatabaseTextField.setDisable(disable);
        mysqlServerUsernameTextField.setDisable(disable);
        mysqlServerPasswordTextField.setDisable(disable);
        mysqlConnectionPoolSlider.setDisable(disable);
        mysqlPortTextField.setDisable(disable);

        // SETTINGS TAB
        settingAllOnSslCheckbox.setDisable(disable);
        settingHeartBeatsCheckbox.setDisable(disable);
    }

    /// MESSAGE HANDLING

    private void handleMessage(PropertyChangeEvent evt) {
        if (!evt.getPropertyName().equals("state")) {
            processMessage(evt.getPropertyName(), evt.getNewValue().toString());
        }
    }

    public void processMessage(String eventName, String eventMessage) {
        if (eventName.contains("DASHBOARD")) {
            updateLog("dashboard", eventMessage);
        }

        if (eventName.contains("WRITESERVER")) {
            if (eventName.contains("WRITESERVER-ACTIVE")) {
                int current = Integer.parseInt(writeServerActiveTextField.getText());
                if (eventMessage.equals("ADD")) {
                    writeServerActiveTextField.setText(String.valueOf(++current));
                } else {
                    writeServerActiveTextField.setText(String.valueOf(--current));
                }
            }
            if (eventName.contains("WRITESERVER-LOG")) {
                updateLog("write", eventMessage);
            }
        }

        if (eventName.contains("READSERVER")) {
            if (eventName.contains("READSERVER-ACTIVE")) {
                int current = Integer.parseInt(readServerActiveTextField.getText());
                if (eventMessage.equals("ADD")) {
                    readServerActiveTextField.setText(String.valueOf(++current));
                } else {
                    readServerActiveTextField.setText(String.valueOf(--current));
                }
            }
            if (eventName.contains("READSERVER-LOG")) {
                updateLog("read", eventMessage);
            }
        }

        if (eventName.contains("MSSQL")) {
            if (eventName.contains("MSSQLSERVER-ACTIVE")) {
                int current = Integer.parseInt(mssqlActiveTextField.getText());
                if (eventMessage.equals("ADD")) {
                    mssqlActiveTextField.setText(String.valueOf(++current));
                } else {
                    mssqlActiveTextField.setText(String.valueOf(--current));
                }
            }
            if (eventName.contains("MSSQLSERVER-LOG")) {
                updateLog("mssql", eventMessage);
            }
        }

        if (eventName.contains("MYSQL")) {
            if (eventName.contains("MYSQLSERVER-ACTIVE")) {
                int current = Integer.parseInt(mysqlActiveTextField.getText());
                if (eventMessage.equals("ADD")) {
                    mysqlActiveTextField.setText(String.valueOf(++current));
                } else {
                    mysqlActiveTextField.setText(String.valueOf(--current));
                }
            }
            if (eventName.contains("MYSQLSERVER-LOG")) {
                updateLog("mysql", eventMessage);
            }
        }

        if (eventName.contains("LOGIN")) {
            Login login = new Login(eventMessage);
            System.out.println(login.getAlias() + " " + login.getIp() + " " + login.getWhen());
            loginTable.getItems().add(login);
        }
    }

    public void updateLog(String logType, String message) {
        switch (logType) {
            case "dashboard":
                dashboardLog.setText(
                        dashboardLog.getText() + '\n' + message
                );
                break;
            case "write":
                writeServerLog.setText(
                        writeServerLog.getText() + '\n' + message
                );
                break;
            case "read":
                readServerLog.setText(
                        readServerLog.getText() + '\n' + message
                );
                break;
            case "mssql":
                mssqlLog.setText(
                        mssqlLog.getText() + '\n' + message
                );
                break;
            case "mysql":
                mysqlLog.setText(
                        mysqlLog.getText() + '\n' + message
                );
                break;
            default:
                // Ignore
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Read Server
        readServerPortTextField.setText(String.valueOf(config.get().getReadServer().getPort()));
        readServerThreadSlider.setValue(config.get().getReadServer().getThreadLimit());
        readServerAvailableTextField.setText(String.valueOf(config.get().getReadServer().getThreadLimit()));
        readServerIpList.setItems(FXCollections.observableList(config.get().getReadServer().getBlacklist()));

        // Write Server
        writeServerPortTextField.setText(String.valueOf(config.get().getWriteServer().getPort()));
        writeServerThreadSlider.setValue(config.get().getWriteServer().getThreadLimit());
        writeServerAvailableTextField.setText(String.valueOf(config.get().getWriteServer().getThreadLimit()));
        writeServerIpList.setItems(FXCollections.observableList(config.get().getWriteServer().getWhitelist()));

        // Mssql Server
        mssqlServerAddressTextField.setText(config.get().getMssql().getServerAddress());
        mssqlServerDatabaseTextField.setText(config.get().getMssql().getDatabaseName());
        mssqlServerUsernameTextField.setText(config.get().getMssql().getDatabaseUsername());
        mssqlServerPasswordTextField.setText(config.get().getMssql().getDatabasePassword());
        mssqlPortTextField.setText(config.get().getMssql().getServerPort().toString());
        mssqlConnectionPoolSlider.setValue(config.get().getMssql().getConnectionPoolLimit());

        // Mysql Server
        mysqlServerAddressTextField.setText(config.get().getMysql().getServerAddress());
        mysqlServerDatabaseTextField.setText(config.get().getMysql().getDatabaseName());
        mysqlServerUsernameTextField.setText(config.get().getMysql().getDatabaseUsername());
        mysqlServerPasswordTextField.setText(config.get().getMysql().getDatabasePassword());
        mysqlPortTextField.setText(config.get().getMysql().getServerPort().toString());
        mysqlConnectionPoolSlider.setValue(config.get().getMysql().getConnectionPoolLimit());

        // Login
        colAlias.setCellValueFactory(new PropertyValueFactory<>("alias"));
        colIp.setCellValueFactory(new PropertyValueFactory<>("ip"));
        colWhen.setCellValueFactory(new PropertyValueFactory<>("when"));

        // Settings
        settingAllOnSslCheckbox.setSelected(config.get().getSettings().getAllOnSsl());
        settingHeartBeatsCheckbox.setSelected(config.get().getSettings().getHeartbeats());

        setupBindings();
    }

    private void setupBindings() {
        readServerAvailableTextField.textProperty().bind(
                Bindings.format(
                        "%.0f",
                        readServerThreadSlider.valueProperty()
                )
        );

        writeServerAvailableTextField.textProperty().bind(
                Bindings.format(
                        "%.0f",
                        writeServerThreadSlider.valueProperty()
                )
        );

        mssqlAvailableTextField.textProperty().bind(
                Bindings.format(
                        "%.0f",
                        mssqlConnectionPoolSlider.valueProperty()
                )
        );

        mysqlAvailableTextField.textProperty().bind(
                Bindings.format(
                        "%.0f",
                        mysqlConnectionPoolSlider.valueProperty()
                )
        );

        readServerPortTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.equals("") && isNumeric(newValue)) {
                config.set().getReadServer().setPort(Integer.parseInt(newValue));
            }
        });
        readServerThreadSlider.valueProperty().addListener((observable, oldValue, newValue) -> config.set().getReadServer().setThreadLimit(newValue.intValue()));
        readServerAddButton.setOnAction(event -> {
            String ip = addBlacklistIp.getText();
            if (ip != null && !ip.equals("")) {
                addBlacklistIp.clear();
                readServerIpList.getItems().add(ip);
                config.set().getReadServer().setBlacklist(readServerIpList.getItems());
            }
        });
        readServerRemoveButton.setOnAction(event -> {
            int index = readServerIpList.getSelectionModel().getSelectedIndex();
            if (index > -1) {
                readServerIpList.getSelectionModel().clearSelection();
                readServerIpList.getItems().remove(index);
                config.set().getReadServer().setBlacklist(readServerIpList.getItems());
            }
        });

        writeServerPortTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.equals("") && isNumeric(newValue)) {
                config.set().getWriteServer().setPort(Integer.parseInt(newValue));
            }
        });
        writeServerThreadSlider.valueProperty().addListener((observable, oldValue, newValue) -> config.set().getWriteServer().setThreadLimit(newValue.intValue()));
        writeServerAddButton.setOnAction(event -> {
            String ip = addWhitelistIp.getText();
            if (ip != null && !ip.equals("")) {
                writeServerIpList.getItems().add(ip);
                addWhitelistIp.clear();
                config.set().getWriteServer().setWhitelist(writeServerIpList.getItems());
            }
        });
        writeServerRemoveButton.setOnAction(event -> {
            int index = writeServerIpList.getSelectionModel().getSelectedIndex();
            if (index > -1) {
                writeServerIpList.getSelectionModel().clearSelection();
                writeServerIpList.getItems().remove(index);
                config.set().getWriteServer().setWhitelist(writeServerIpList.getItems());
            }
        });

        mssqlServerAddressTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.equals("")) {
                config.set().getMssql().setServerAddress(newValue);
            }
        });
        mssqlServerDatabaseTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.equals("")) {
                config.set().getMssql().setDatabaseName(newValue);
            }
        });
        mssqlServerUsernameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.equals("")) {
                config.set().getMssql().setDatabaseUsername(newValue);
            }
        });
        mssqlServerPasswordTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.equals("")) {
                config.set().getMssql().setDatabasePassword(newValue);
            }
        });
        mssqlPortTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.equals("")) {
                config.set().getMssql().setServerPort(Integer.parseInt(newValue));
            }
        });
        mssqlConnectionPoolSlider.valueProperty().addListener((observable, oldValue, newValue) -> config.set().getMssql().setConnectionPoolLimit(newValue.intValue()));

        mysqlServerAddressTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.equals("")) {
                config.set().getMysql().setServerAddress(newValue);
            }
        });
        mysqlServerDatabaseTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.equals("")) {
                config.set().getMysql().setDatabaseName(newValue);
            }
        });
        mysqlServerUsernameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.equals("")) {
                config.set().getMysql().setDatabaseUsername(newValue);
            }
        });
        mysqlServerPasswordTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.equals("")) {
                config.set().getMysql().setDatabasePassword(newValue);
            }
        });
        mysqlPortTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.equals("")) {
                config.set().getMysql().setServerPort(Integer.parseInt(newValue));
            }
        });
        mysqlConnectionPoolSlider.valueProperty().addListener((observable, oldValue, newValue) -> config.set().getMysql().setConnectionPoolLimit(newValue.intValue()));

        settingAllOnSslCheckbox.selectedProperty().addListener((observable, oldValue, newValue) -> config.set().getSettings().setAllOnSsl(newValue));
        settingHeartBeatsCheckbox.selectedProperty().addListener((observable, oldValue, newValue) -> config.set().getSettings().setHeartbeats(newValue));
    }

    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}
