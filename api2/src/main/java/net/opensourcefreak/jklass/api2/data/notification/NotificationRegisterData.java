package net.opensourcefreak.jklass.api2.data.notification;

import java.util.ArrayList;
import java.util.List;

// https://api2.vklass.se/v1/notifications/push/register
public class NotificationRegisterData {
    private String device_handle;
    private String device_type;
    private List<String> activated_roles;
    private List<String> activated_toggleable_notifications;

    public NotificationRegisterData(String device_handle, List<String> activated_roles, List<String> activated_toggleable_notifications) {
        this.device_handle = device_handle;
        this.activated_roles = activated_roles;
        this.activated_toggleable_notifications = activated_toggleable_notifications;
        device_type = "ANDROID";
    }
}
