package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.*;

public class TelegramBot extends TelegramLongPollingBot {

    private static final String addExpense_btn = "Add Expense";
    private static final String showCategories_btn = "Show Categories";
    private static final String showExpenses_btn = "Show Expenses";

    private static final String idleState = "Idle";
    public static final String awaitsCategory_bth = "AwaitsCategory";
    public static final String awaitsExpense_bth = "AwaitsExpense";

    private static String currentState = "IdleState";

    //хранение данных в виде ключ-значение
    private static final Map <String,List<Integer>> EXPENSES =  new HashMap<>();


    @Override
    public String getBotUsername() {
        return "counting expenses";
    }

    @Override
    public String getBotToken() {
        return "7898393973:AAH6kHfzV-riYQZma6PMlSYu25fwtEFMgoM";
    }

    @Override
    public void onUpdateReceived(final Update update) {
        if (!update.hasMessage()|| !update.getMessage().hasText()){
            System.out.println("Unsupported update");
            return;
        }

            Message message = update.getMessage();

            User from = update.getMessage().getFrom();
            String text = update.getMessage().getText();
            String logMessage = from.getUserName() +": " + text;
            System.out.println(logMessage);

            switch (currentState){
                case idleState: handleIdle(message);
                case awaitsCategory_bth:
                    System.out.println(currentState);
                case awaitsExpense_bth:
                    System.out.println(currentState);
            }

    }

    private void handleIdle (Message incomingMessage){
        String incomingText = incomingMessage.getText();
        Long chatId = incomingMessage.getChatId();

        final List<String> defaultButtons = List.of(addExpense_btn,showExpenses_btn,showCategories_btn);

            switch (incomingText){
                case showCategories_btn -> changeState(idleState,chatId,getFormatedCategories(),defaultButtons);
                case showExpenses_btn -> sendMessage.setText(getFormatedExpenses());
                case addExpense_btn -> sendMessage.setText("Введи имя категории и сумму через пробел");
                default -> {
                    String[] expense = incomingText.split(" ");
                    if (expense.length == 2){
                        String category = expense[0];
                       if (!EXPENSES.containsKey(category)){
                           EXPENSES.put(category,new ArrayList<>());
                       }
                       Integer sum = Integer.parseInt(expense[1]);
                       EXPENSES.get(category).add(sum);
                    }else{
                        sendMessage.setText("Похоже на неверный ввод траты");
                    }
                }
            }
    }

    private void changeState(String newState, Long chatId, String messageText, List<String> buttonNames){
        currentState = newState;
        //ответ бота на ответ пользователя
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(messageText);

        ReplyKeyboardMarkup replyKeyboardMarkup = buildKeyboard(buttonNames);

        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            System.out.println("Error");
            System.out.println(e);
        }
    }

    private ReplyKeyboardMarkup buildKeyboard(List<String> buttonNames){
        //создание списка кнопок
        List<KeyboardRow> rows = new ArrayList<>();
        for(String buttonName:buttonNames){
            final KeyboardRow row = new KeyboardRow();
            row.add(buttonName);
            rows.add(row);
        }
        ReplyKeyboardMarkup keyBoard = new ReplyKeyboardMarkup();
        keyBoard.setKeyboard(rows);
        return keyBoard;
    }

    private String getFormatedCategories(){
        Set<String> categories = EXPENSES.keySet();
        if (categories.isEmpty()) return "Пока нет ни одной категории";
        return String.join("\n",EXPENSES.keySet());

    }

    private String getFormatedExpenses() {
        Set<Map.Entry<String,List<Integer>>> expensesPerCategories = EXPENSES.entrySet();
        if (expensesPerCategories.isEmpty()) return "Пока нет ни одной категории";
        String formatedResult = " ";
       for (Map.Entry<String,List<Integer>> category : EXPENSES.entrySet()){
            String categoryExpenses = "";
            for (Integer expense : category.getValue()){
                categoryExpenses += expense + " ";
            }
            formatedResult += (category.getKey() + ": " + categoryExpenses + "\n");
       }
            return formatedResult;
    }
}
