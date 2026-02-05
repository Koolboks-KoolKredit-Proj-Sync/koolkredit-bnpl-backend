package com.koolboks.creditProject.controller.internal;

import com.koolboks.creditProject.scheduler.PaymentReminderScheduler;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/scheduler")
//@Profile("dev") // 🔐 only available in dev
public class SchedulerTestController {

    private final PaymentReminderScheduler scheduler;

    public SchedulerTestController(PaymentReminderScheduler scheduler) {
        this.scheduler = scheduler;
    }

    @PostMapping("/payment-reminders/run")
    public String runPaymentReminderNow() {
        scheduler.checkAndSendPaymentReminders();
        return "✅ Payment reminder scheduler executed manually";
    }
}
