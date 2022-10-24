package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationRepository;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private final NotificationRepository notificationRepository;
    public TelegramBotUpdatesListener(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }
    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);
    private static final String TEXT_FOR_START = "Добро пожаловать на борт, ";
    private static final String TEXT_ERROR = "Неправильный формат сообщения, ";
    private  static final String PAT = "([0-9\\.\\:\\s]{16})(\\s)([\\W+]+)";

    @Autowired
    private TelegramBot telegramBot;

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing update: {}", update);
            long chatId = update.message().chat().id();
            if (update.message().text().equals("/start") && update.message().text() != null) {
                SendResponse sendResponse = telegramBot.execute(new SendMessage(chatId, TEXT_FOR_START + update.message().chat().username() + "!"));
                if (sendResponse.isOk()) {
                    logger.info("Приветственное сообщение отправлено!");
                } else {
                    logger.info("Сообщение не отправлено" + sendResponse.errorCode());
                }
            } else {
                try {
                    treatmentMessage(update);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    public void treatmentMessage(Update update) {
        String inputText = update.message().text();
        long chatId = update.message().chat().id();
        Pattern pattern = Pattern.compile(PAT);
        Matcher matcher = pattern.matcher(inputText);
        if (matcher.find()) {
            String messageDateFromUser = matcher.group(1);
            String messageFromUser = matcher.group(3);
            LocalDateTime dateAndTime = LocalDateTime.parse(messageDateFromUser, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
            NotificationTask notificationTask = new NotificationTask();
            notificationTask.setTextNotification(messageFromUser);
            notificationTask.setChatId(chatId);
            notificationTask.setTimeNotification(dateAndTime);
            notificationRepository.save(notificationTask);

        } else {
            telegramBot.execute(new SendMessage(chatId, TEXT_ERROR + update.message().chat().username() + "!"));
        }

    }

    @Scheduled(cron = "0 0/1 * * * *")
    public void sendScheduledMessage() {
        LocalDateTime dateTimeNow = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        List<NotificationTask> savedMessage = notificationRepository.findByTimeNotificationEquals(dateTimeNow);
        for (NotificationTask message : savedMessage) {
            telegramBot.execute(new SendMessage(message.getChatId(), message.getTextNotification()));
        }
    }
}


