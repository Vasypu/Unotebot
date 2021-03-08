package unotebot.botapi.handlers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import unotebot.botapi.BotState;
import unotebot.botapi.InputMessageHandler;
import unotebot.botapi.SendCustomMessages;
import unotebot.cache.UserDataCache;
import unotebot.model.Note;
import unotebot.model.User;
import unotebot.service.MainMenuService;
import unotebot.service.ReplyMessagesService;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Component
public class MainHandler implements InputMessageHandler {
    private UserDataCache userDataCache;
    private ReplyMessagesService messagesService;
    private MainMenuService mainMenuService;

    public MainHandler(UserDataCache userDataCache, ReplyMessagesService messagesService, MainMenuService mainMenuService) {
        this.userDataCache = userDataCache;
        this.messagesService = messagesService;
        this.mainMenuService = mainMenuService;
    }

    @Override
    public SendMessage handle(Message message) {
        if (userDataCache.getUsersCurrentBotState(message.getFrom().getId()).equals(BotState.ADD_NOTE)) {
            userDataCache.setUsersCurrentBotState(message.getFrom().getId(), BotState.ADD_NOTE);
        }
        return processUsersInput(message);
    }

    @Override
    public BotState getHandlerName() {
        return BotState.ADD_NOTE;
    }

    private SendMessage processUsersInput(Message inputMsg) {
        String usersAnswer = inputMsg.getText();
        int userId = inputMsg.getFrom().getId();
        long chatId = inputMsg.getChatId();

        User userData = userDataCache.getUserProfileData(userId);
        BotState botState = userDataCache.getUsersCurrentBotState(userId);

        SendMessage replyToUser = null;

        if (botState.equals(BotState.ADD_NOTE)) {
            replyToUser = messagesService.getReplyMessage(chatId, "reply.noteAdded");
            userData.addNote(usersAnswer);
        }

        if (botState.equals(BotState.ALL_NOTES)) {
            LinkedHashMap<String, Note> notes = userData.getNotes();
            String text = "";
            int index = 0;

            if (!notes.isEmpty()) {
                text = "\uD83D\uDDD2   ВСЕ ЗАМЕТКИ:\n\n";
            } else {
                text = "У вас пока отсутствуют заметки \uD83E\uDD14";
            }

            for (Map.Entry<String, Note> pair : notes.entrySet()) {
                Note value = pair.getValue();
                text = text + (++index + ". " + value.getmNote() + "\nДата создания:" + value.getmDate() + "\n\n");
            }

            replyToUser = new SendMessage(chatId, text);
        }

        if (botState.equals(BotState.EDIT_NOTE)) {
            LinkedHashMap<String, Note> notes = userData.getNotes();
            int index = 0;
            SendCustomMessages sendCustomMessages = new SendCustomMessages();

            if (!notes.isEmpty()) {
                sendCustomMessages.sendEvent(inputMsg.getChatId(), "✏️   РЕДАКТИРОВАНИЕ ЗАМЕТОК:", false);
            } else {
                sendCustomMessages.sendEvent(inputMsg.getChatId(), "У вас пока отсутствуют заметки \uD83E\uDD14", false);
            }

            for (Map.Entry<String, Note> pair : notes.entrySet()) {
                Note value = pair.getValue();
                sendCustomMessages.sendEvent(inputMsg.getChatId(), ++index + ". " + value.getmNote() + "\nДата создания: " + value.getmDate() + "\n\n", true);
            }
            replyToUser = null;
        }

        if (botState.equals(BotState.SHOW_COMMANDS)) {
            replyToUser = new SendMessage(chatId, "КОМАНДЫ БОТА: \n" +
                    "/show_all_commands - Показать все команды\n" +
                    "/show_menu_buttons - Показать кнопки меню\n" +
                    "/show_list - Показать все заметки\n" +
                    "/edit_list - Редактировать заметки\n" +
                    "/delete_all_list - Удалить все заметки\n");


        }

        if (botState.equals(BotState.DELETE_ALL_NOTES)) {
            User user = userDataCache.getUserProfileData(userId);
            if (user != null) {
                user.removeAllNotes();
            }
            replyToUser = new SendMessage(chatId, "✅ Все заметки удалены");
        }

        if (botState.equals(BotState.SHOW_MENU_BUTTONS)) {
            replyToUser = mainMenuService.getMainMenuMessage(chatId, "Чтобы эффективно пользоваться ботом, воспользуйтесь кнопками меню ↘️");
        }

        userDataCache.setUsersCurrentBotState(userId, BotState.ADD_NOTE);
        userDataCache.saveUserProfileData(userId, userData);

        return replyToUser;
    }
}