package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.service.MessageProcessingService;
import pro.sky.telegrambot.service.TelegramMessageSender;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    @Autowired
    private TelegramBot telegramBot;

    @Autowired
    private MessageProcessingService messageProcessingService;

    @Autowired
    private TelegramMessageSender telegramMessageSender;

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing update: {}", update);
            if (update.message() != null &&
                update.message().text() != null) {
                String text = update.message().text();
                Long chatId = update.message().chat().id();
                String response = messageProcessingService.processIncomingMessage(chatId, text);
                if (response != null && !response.isEmpty()) {
                    telegramMessageSender.sendMessage(chatId, response);
                }
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

}
