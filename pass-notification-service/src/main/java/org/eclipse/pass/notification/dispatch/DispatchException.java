/*
 * Copyright 2018 Johns Hopkins University
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.eclipse.pass.notification.dispatch;

import org.eclipse.pass.notification.model.Notification;

/**
 * @author Elliot Metsger (emetsger@jhu.edu)
 */
public class DispatchException extends RuntimeException {

    private final Notification notification;

    public DispatchException(String message, Notification notification) {
        super(message);
        this.notification = notification;
    }

    public DispatchException(String message, Throwable cause, Notification notification) {
        super(message, cause);
        this.notification = notification;
    }

    public Notification getNotification() {
        return notification;
    }

}
