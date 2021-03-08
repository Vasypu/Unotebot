package unotebot.botapi;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import unotebot.cache.UserDataCache;
import unotebot.model.User;
import unotebot.service.MainMenuService;

@Setter
@Getter
@Component
@Slf4j
public class TelegeamFacade {

    private BotStateContext botStateContext;
    private UserDataCache userDataCache;
    private MainMenuService mainMenuService;

    public TelegeamFacade(BotStateContext botStateContext, UserDataCache userDataCache, MainMenuService mainMenuService) {
        this.botStateContext = botStateContext;
        this.userDataCache = userDataCache;
        this.mainMenuService = mainMenuService;
    }

    public BotApiMethod<?> handleUpdate(Update update) {
        SendMessage replyMessage = null;
        Logger LOGGER = LogManager.getLogger(TelegeamFacade.class);


        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            LOGGER.info("New callbackQuery from User: {}, userId: {}, with data: {}", update.getCallbackQuery().getFrom().getUserName(),
                    callbackQuery.getFrom().getId(), update.getCallbackQuery().getData());
            return processCallbackQuery(callbackQuery);
        }


        Message message = update.getMessage();
        if (message != null && message.hasText()) {
            LOGGER.info("New message from User:{}, chatId: {},  with text: {}", message.getFrom().getUserName(), message.getChatId(), message.getText());
            replyMessage = handleInputMessage(message);
        }

        return replyMessage;
    }

    public SendMessage handleInputMessage(Message message) {
        String inputMsg = message.getText();
        int userId = message.getFrom().getId();
        BotState botState;
        SendMessage replyMessage;

        switch (inputMsg) {
            case "/start":
                botState = BotState.FIRST_START;
//                LOGGER.info("FIRST_START");
                break;
            case "/show_all_commands":
                botState = BotState.SHOW_COMMANDS;
                break;
            case "/show_menu_buttons":
                botState = BotState.SHOW_MENU_BUTTONS;
                break;
            case "/show_list":
            case "\uD83D\uDDD2   Мои заметки":
                botState = BotState.ALL_NOTES;
                break;
            case "/edit_list":
            case "✏️   Редактировать":
                botState = BotState.EDIT_NOTE;
                break;
            case "/delete_all_list":
                botState = BotState.DELETE_ALL_NOTES;
                break;
            default:
                botState = BotState.ADD_NOTE;
                break;
        }

        userDataCache.setUsersCurrentBotState(userId, botState);

        replyMessage = botStateContext.processInputMessage(botState, message);

        return replyMessage;
    }

    private BotApiMethod<?> processCallbackQuery(CallbackQuery buttonQuery) {
        final long chatId = buttonQuery.getMessage().getChatId();
        final int userId = buttonQuery.getFrom().getId();
        BotApiMethod<?> callBackAnswer = null;

        if (buttonQuery.getData().equals("buttonYes")) {
            callBackAnswer = mainMenuService.getMainMenuMessage(chatId, "Чтобы эффективно пользоваться ботом, воспользуйтесь кнопками меню ↘️");
        } else if (buttonQuery.getData().equals("buttonNo")) {
            callBackAnswer = sendAnswerCallbackQuery("Возвращайтесь, когда будете готовы", false, buttonQuery);
        } else if (buttonQuery.getData().equals("buttonDelete")) {
            SendCustomMessages sendCustomMessages = new SendCustomMessages();
            sendCustomMessages.sendEmptyMessage(chatId, buttonQuery.getMessage().getMessageId());
            String text = buttonQuery.getMessage().getText().substring(3, buttonQuery.getMessage().getText().length() - 30);
            callBackAnswer = sendAnswerCallbackQuery("Заметка успешна удалена", true, buttonQuery);
            User user = userDataCache.getUserProfileData(userId);
            user.removeNote(text);
            userDataCache.setUsersCurrentBotState(userId, BotState.EDIT_NOTE);
        }
        return callBackAnswer;
    }

    private AnswerCallbackQuery sendAnswerCallbackQuery(String text, boolean alert, CallbackQuery callbackQuery) {
        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
        answerCallbackQuery.setCallbackQueryId(callbackQuery.getId());
        answerCallbackQuery.setShowAlert(alert);
        answerCallbackQuery.setText(text);
        return answerCallbackQuery;
    }
}
