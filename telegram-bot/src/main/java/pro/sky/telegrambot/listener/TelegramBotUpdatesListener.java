package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.entity.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    @Autowired
    private TelegramBot telegramBot;

    @Autowired
    private NotificationTaskRepository notificationTaskRepository;

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
                String text = update.message().text().trim();
                Long chatId = update.message().chat().id();
                if ("/start".equals(text)) {
                    telegramBot.execute(new com.pengrad.telegrambot.request.SendMessage(
                            chatId,
                            "Добро пожаловать в Telegram-бот!"));
                } else {
                    Pattern pattern = Pattern.compile("^(\\d{2}\\.\\d{2}\\.\\d{4} \\d{2}:\\d{2})\\s+(.+)$");
                    Matcher matcher = pattern.matcher(text);
                    if (matcher.matches()) {
                        String dateTimeString = matcher.group(1);
                        String message = matcher.group(2);
                        try {
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
                            LocalDateTime notificationDateTime = LocalDateTime.parse(dateTimeString, formatter);
                            NotificationTask task = new NotificationTask();
                            task.setChatId(chatId);
                            task.setMessage(message);
                            task.setNotificationDateTime(notificationDateTime);
                            notificationTaskRepository.save(task);
                            telegramBot.execute(new com.pengrad.telegrambot.request.SendMessage(
                                    chatId,
                                    "Напоминание успешно сохранено!"));
                        } catch (DateTimeParseException e) {
                            telegramBot.execute(new com.pengrad.telegrambot.request.SendMessage(
                                    chatId,
                                    "Ошибка в формате даты и времени. Используйте формат: дд.мм.гггг чч:мм"));
                        }
                    }
                }
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

}
