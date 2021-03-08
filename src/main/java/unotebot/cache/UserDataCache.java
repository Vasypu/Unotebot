package unotebot.cache;

import org.springframework.stereotype.Component;
import unotebot.botapi.BotState;
import unotebot.model.User;

import java.util.HashMap;
import java.util.Map;

@Component
public class UserDataCache implements DataCache {
    private Map<Integer, BotState> usersBotStates = new HashMap<>();
    private Map<Integer, User> usersProfileData = new HashMap<>();

    @Override
    public void setUsersCurrentBotState(int userId, BotState botState){
        usersBotStates.put(userId, botState);
    }

    @Override
    public BotState getUsersCurrentBotState(int userId){
        BotState botState = usersBotStates.get(userId);
        if(botState == null){
            botState = BotState.FIRST_START;
        }
        return botState;
    }

    @Override
    public User getUserProfileData(int userId) {
        User userProfileData = usersProfileData.get(userId);
        if(userProfileData == null){
            userProfileData = new User(userId);
        }
        return userProfileData;
    }

    @Override
    public void saveUserProfileData(int userId, User user) {
        usersProfileData.put(userId, user);
    }

}
