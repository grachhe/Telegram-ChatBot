/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Broadcast;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author octav
 */
public class BroadCastBot extends TelegramLongPollingBot {
    private final String botToken;
    private final String dbUrl;
    private final String dbUsername;
    private final String dbPassword;

    public BroadCastBot(String botToken, String dbUrl, String dbUsername, String dbPassword) {
        this.botToken = botToken;
        this.dbUrl = dbUrl;
        this.dbUsername = dbUsername;
        this.dbPassword = dbPassword;
    }

    public static void main(String[] args) {

        // Ganti dengan token bot Anda
        
        String botToken = "5902173209:AAF4Vj5wxXVNMGpTV2gvoOH_n7itFhxC8sc";

        // Ganti dengan informasi koneksi database Anda
        String dbUrl = "jdbc:mysql://localhost:3306/db_chatbot";
        String dbUsername = "root";
        String dbPassword = "";

        BroadCastBot bot = new BroadCastBot(botToken, dbUrl, dbUsername, dbPassword);
        bot.startBot();
    }

    public void startBot() {
        // Konfigurasi bot
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(this);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        // Mengabaikan pesan yang tidak terkait dengan pengiriman pesan
    }

    public void sendBroadcastMessage(String message) {
        try (Connection connection = getConnection()) {
            String query = "SELECT chat_id FROM users";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String chatId = resultSet.getString("chat_id");
                sendMessage(chatId, "geneo"); // Mengirim pesan ke setiap chat_id
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(String chatId, String message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);

        try {
            execute(sendMessage); // Mengirim pesan menggunakan Bot API Telegram
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        // Ganti dengan nama bot Anda
        return "ShavnaBot";
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
    }
    
}
