package pro.sky.telegrambot.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity

public class NotificationTask {
    @Id
    @GeneratedValue
    private long id;
    private String textNotification;
    private LocalDateTime timeNotification;

    private Long chatId;

    public NotificationTask() {
    }

    public NotificationTask(long id, String textNotification, LocalDateTime timeNotification, Long chatId) {
        this.id = id;
        this.textNotification = textNotification;
        this.timeNotification = timeNotification;
        this.chatId = chatId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
     this.id = id;
    }

    public String getTextNotification() {
        return textNotification;
    }

    public void setTextNotification(String textNotification) {
        this.textNotification = textNotification;
    }

    public LocalDateTime getTimeNotification() {
        return timeNotification;
    }

    public void setTimeNotification(LocalDateTime timeNotification) {
        this.timeNotification = timeNotification;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotificationTask that = (NotificationTask) o;
        return id == that.id && Objects.equals(textNotification, that.textNotification) && Objects.equals(timeNotification, that.timeNotification) && Objects.equals(chatId, that.chatId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, textNotification, timeNotification, chatId);
    }
}
