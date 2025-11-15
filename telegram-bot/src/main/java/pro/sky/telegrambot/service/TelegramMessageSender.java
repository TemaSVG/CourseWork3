package pro.sky.telegrambot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TelegramMessageSender {

	@Autowired
	private TelegramBot telegramBot;

	public void sendMessage(Long chatId, String text) {
		if (chatId == null || text == null || text.isEmpty()) {
			return;
		}
		telegramBot.execute(new SendMessage(chatId, text));
	}
}


