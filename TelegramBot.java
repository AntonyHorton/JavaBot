package org.example;

import javassist.runtime.Inner;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TelegramBot extends TelegramLongPollingBot {

    private static final String addExpense = "Add Expense";
    private static final String showCategories = "Show Categories";
    private static final String showExpenses = "Show Expenses";

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

            //ответ бота на ответ пользователя
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(update.getMessage().getChatId());

            switch (text){
                case showCategories -> sendMessage.setText(getFormatedCategories());
                case showExpenses -> sendMessage.setText(getFormatedExpenses());
                case addExpense -> sendMessage.setText("Введи имя категории и сумму через пробел");
                default -> {
                    String[] expense = text.split(" ");
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


            //создание ряда из кнопок
            //одна кнопка с названием "Add Expenses"
            KeyboardRow keyboardRow1= new KeyboardRow();
            keyboardRow1.add(addExpense);

            //вторая кнопка с названием "Show Categories"
            KeyboardRow keyboardRow2 = new KeyboardRow();
            keyboardRow2.add(showCategories);

            //третья кнопка с названием "Show Expenses"
            KeyboardRow keyboardRow3 = new KeyboardRow();
            keyboardRow3.add(showExpenses);

            //создание списка кнопок
            List<KeyboardRow> keyboardRows = new ArrayList<>();

            keyboardRows.add(keyboardRow1);
            keyboardRows.add(keyboardRow2);
            keyboardRows.add(keyboardRow3);

            //установка действия этой кнопки из списка рядов кнопок
            ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
            replyKeyboardMarkup.setKeyboard(keyboardRows);
            sendMessage.setReplyMarkup(replyKeyboardMarkup);

            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                System.out.println("Error");
                System.out.println(e);
        }
    }

    private String getFormatedCategories(){
     return String.join("\n",EXPENSES.keySet());

    }

    private String getFormatedExpenses() {
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
