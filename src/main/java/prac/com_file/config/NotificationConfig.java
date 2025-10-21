package prac.com_file.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "notification")
public class NotificationConfig {

    private String email;
    private int interval;
    private String cronExpiringSoon;

    // Getters and Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public String getCronExpiringSoon() {
        return cronExpiringSoon;
    }

    public void setCronExpiringSoon(String cronExpiringSoon) {
        this.cronExpiringSoon = cronExpiringSoon;
    }
}