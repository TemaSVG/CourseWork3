package pro.sky.telegrambot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.entity.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class MessageProcessingService {

	@Autowired
	private NotificationTaskRepository notificationTaskRepository;

	private static final Pattern NOTIFICATION_PATTERN =
			Pattern.compile("^(\\d{2}\\.\\d{2}\\.\\d{4} \\d{2}:\\d{2})\\s+(.+)$");

	private static final DateTimeFormatter NOTIFICATION_DATE_FORMATTER =
			DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

	public String processIncomingMessage(Long chatId, String text) {
		if (text == null) {
			return null;
		}
		text = text.trim();
		if ("/start".equals(text)) {
			return "Добро пожаловать в Telegram-бот!";
		}

		Matcher matcher = NOTIFICATION_PATTERN.matcher(text);
		if (matcher.matches()) {
			String dateTimeString = matcher.group(1);
			String message = matcher.group(2);
			try {
				LocalDateTime notificationDateTime =
						LocalDateTime.parse(dateTimeString, NOTIFICATION_DATE_FORMATTER);
				NotificationTask task = new NotificationTask();
				task.setChatId(chatId);
				task.setMessage(message);
				task.setNotificationDateTime(notificationDateTime);
				notificationTaskRepository.save(task);
				return "Напоминание успешно сохранено!";
			} catch (DateTimeParseException e) {
				return "Ошибка в формате даты и времени. Используйте формат: дд.мм.гггг чч:мм";
			}
		}

		return null;
	}
}


