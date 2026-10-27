package com.repository.impl;

import java.nio.file.Path;

import com.model.Notification;

public class NotificationRepository extends AbstractCsvRepository<Notification> {
	public NotificationRepository() {
		super(Notification.class, "notifications.csv");
	}

	public NotificationRepository(Path dataDirectory) {
		super(Notification.class, dataDirectory.resolve("notifications.csv"));
	}
}
