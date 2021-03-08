package unotebot.botapi;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class BotStateContext {
    private Map<BotState, InputMessageHandler> messageHandler = new HashMap<>();

    public BotStateContext(List<InputMessageHandler> messageHandlers){
        messageHandlers.forEach(handler -> this.messageHandler.put(handler.getHandlerName(), handler));
    }

    public SendMessage processInputMessage(BotState currentState, Message message){
        InputMessageHandler currentMessageHandler = findMessageHandler(currentState);
        return currentMessageHandler.handle(message);
    }

    private InputMessageHandler findMessageHandler(BotState currentState){
        if(isStateFirstStart(currentState)){
            return messageHandler.get(BotState.FIRST_START);
        }else if(isStateAddingNotes(currentState)){
            return messageHandler.get(BotState.ADD_NOTE);
        }
        return messageHandler.get(currentState);
    }

    private boolean isStateFirstStart(BotState currentState){
        switch (currentState){
            case FIRST_START:
                return true;
            default:
                return false;
        }
    }

    private boolean isStateAddingNotes(BotState currentState){
        switch (currentState){
            case ADD_NOTE:
            case EDIT_NOTE:
            case ALL_NOTES:
            case SHOW_COMMANDS:
            case DELETE_ALL_NOTES:
            case SHOW_MENU_BUTTONS:
                return true;
            default:
            case FIRST_START:
                return false;
        }
    }
}
