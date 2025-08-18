package pro.sky.telegrambot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.entity.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class NotificationTaskSchedulerService {

    @Autowired
    private NotificationTaskRepository notificationTaskRepository;

    @Autowired
    private TelegramMessageSender telegramMessageSender;

    @Scheduled(cron = "0 * * * * *") // каждую минуту в начале минуты
    public void checkAndSendNotifications() {
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        System.out.println("[Scheduler] Текущее время (минуты): " + now);
        List<NotificationTask> tasks = notificationTaskRepository.findByNotificationDateTime(now);
        System.out.println("[Scheduler] Найдено задач: " + tasks.size());
        tasks.forEach(task -> {
            System.out.println("[Scheduler] Отправка напоминания в чат " + task.getChatId() + ": " + task.getMessage());
            telegramMessageSender.sendMessage(task.getChatId(), task.getMessage());
        });
    }
}
