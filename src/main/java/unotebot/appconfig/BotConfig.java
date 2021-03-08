package unotebot.appconfig;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.ApiContext;
import unotebot.MyUnotebot;
import unotebot.botapi.TelegeamFacade;

@Configuration
@ConfigurationProperties(prefix = "telegrambot")
public class BotConfig {

    private String botUserName;
    private String botToken;
    private String webHookPath;

    @Bean
    public MyUnotebot myUnotebot(TelegeamFacade telegramFacade){
        DefaultBotOptions options = ApiContext.getInstance(DefaultBotOptions.class);

        MyUnotebot myUnotebot = new MyUnotebot(options, telegramFacade);
        myUnotebot.setBotUserName(botUserName);
        myUnotebot.setBotToken(botToken);
        myUnotebot.setWebHookPath(webHookPath);

        return myUnotebot;
    }

    @Bean
    public MessageSource messageSource(){
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();

        messageSource.setBasename("classpath:message");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }
}