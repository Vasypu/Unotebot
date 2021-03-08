package unotebot.cache;

import unotebot.botapi.BotState;
import unotebot.model.User;

public interface DataCache {
    void setUsersCurrentBotState(int userId, BotState botState);

    BotState getUsersCurrentBotState(int userId);

    User getUserProfileData(int userId);

    void saveUserProfileData(int userId, User user);
}
