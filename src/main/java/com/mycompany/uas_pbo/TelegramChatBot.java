package com.mycompany.uas_pbo;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.sql.*;
import javax.swing.JOptionPane;

public class TelegramChatBot extends TelegramLongPollingBot {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/db_chatbot";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    public static void main(String[] args) throws TelegramApiException {
        TelegramChatBot bot = new TelegramChatBot();
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(bot);
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            if (messageText.startsWith("/simpanUser")) {
                    // Menangani perintah /register
                    String[] parts = messageText.split(" ");
                    if (parts.length == 2) {
                        String username = parts[1];
                        String RegisterUser = registerUser(chatId, username);
                        sendMessage(chatId, RegisterUser);
                    } else {
                        sendMessage(chatId, "Invalid command format. Please use /simpanUser (username).");
                    }
                }
            if (isAuthorizedUser(chatId)) {
                // Pengguna telah terotorisasi
                if (messageText.startsWith("/start")) {
                // Memulai percakapan dengan pengguna
                startConversation(chatId);
                } else if (messageText.startsWith("/halo")) {
                    sapa(chatId, "Halo, aku bot Shavna! Senang berkenalan denganmu " );
                } else if (messageText.startsWith("/info")) {
                    // Mendapatkan informasi pengguna dari database
                    String userInfo = getUserInfo(chatId);
                    sendMessage(chatId, userInfo);
                }else if (messageText.startsWith("Presiden")) {
                    // Mendapatkan informasi dari database
                    saveMessage(chatId, messageText);
                    sendMessage(chatId, "Ir. Joko Widodo");
                }else if (messageText.startsWith("Author")) {
                    // Mendapatkan informasi dari database
                    saveMessage(chatId, messageText);
                    sendMessage(chatId, "Epiphany Shavna Gracia");
                }else if (messageText.startsWith("Kampus Author")) {
                    // Mendapatkan informasi dari database
                    saveMessage(chatId, messageText);
                    sendMessage(chatId, "Universitas Dian Nuswantoro");
                }else if (messageText.startsWith("Umur Author")) {
                    // Mendapatkan informasi dari database
                    saveMessage(chatId, messageText);
                    sendMessage(chatId, "20");
                }else if (messageText.startsWith("Jurusan Kuliah Author")) {
                    // Mendapatkan informasi dari database
                    saveMessage(chatId, messageText);
                    sendMessage(chatId, "Teknik Informatika");
                }else if (messageText.startsWith("Instagram Author")) {
                    // Mendapatkan informasi dari database
                    saveMessage(chatId, messageText);
                    sendMessage(chatId, "@grachhe");
                }
                else {
                    // Menyimpan pesan pengguna ke database
                    saveMessage(chatId, messageText);
                    sendMessage(chatId, "Bot tidak mengenali perintah ");
                }
            }else{
                sendMessage(chatId, "Anda belum melakukan register. Silahkan registrasi dengan command /simpanUser (username)");
            }         
        }
    }

    private void startConversation(long chatId) {
        String welcomeMessage = "Welcome to the ChatBot! "
                + "\nAnda dapat menggunakan beberapa perintah di bawah"
                + "\n=================================="
                + "\n /simpanUser (username) untuk register user"
                + "\n /info untuk melihat username anda"
                +"\n==================================="
                + "\n chat anda akan direkam dan dimasukkan kedalam database";
        sendMessage(chatId, welcomeMessage);
    }

    private void saveMessage(long chatId, String message) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "INSERT INTO messages (chat_id, message) VALUES (?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setLong(1, chatId);
            statement.setString(2, message);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private String registerUser(long chatId, String username) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT * FROM users WHERE chat_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setLong(1, chatId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return "User Sudah mendaftar";
            } else {
                String query1 = "INSERT INTO users (chat_id, username) VALUES (?, ?)";
                PreparedStatement statement1 = connection.prepareStatement(query1);
                statement1.setLong(1, chatId);
                statement1.setString(2, username);
                statement1.executeUpdate();
                return "Regsitrasi Berhasil";
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getUserInfo(long chatId) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT * FROM users WHERE chat_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setLong(1, chatId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String username = resultSet.getString("username");
                return "User Info:\nUsername: " + username;
            } else {
                return "User not found!";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void sendMessage(long chatId, String message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    
    private void sapa(long chatId, String message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    
    private boolean isAuthorizedUser(long chatId) {

    ResultSet resultSet = null;
        
    try(Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
        String query = "SELECT * FROM users WHERE chat_id = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setLong(1, chatId);
        resultSet = statement.executeQuery();

        return resultSet.next();
    } catch (SQLException e) {
        e.printStackTrace();
    } 
    return false;
    }
    
    public void sendBroadcast(String message) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            
            String query = "SELECT chat_id FROM users";
            
            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                String chatId = resultSet.getString("chat_id");

                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(chatId);
                sendMessage.setText(message);

                try {
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally{
            JOptionPane.showMessageDialog(null, "Broadcast berhasil dikirim");
        }
    }

    @Override
    public String getBotUsername() {
        // Ganti dengan username bot Anda
        return "ShavnaBot";
    }

    @Override
    public String getBotToken() {
        // Ganti dengan token bot Anda
        return "5902173209:AAF4Vj5wxXVNMGpTV2gvoOH_n7itFhxC8sc";
    }
}
