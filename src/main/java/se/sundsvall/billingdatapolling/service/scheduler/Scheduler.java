package se.sundsvall.billingdatapolling.service.scheduler;

import static java.util.Locale.ENGLISH;

import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import it.burning.cron.CronExpressionDescriptor;
import it.burning.cron.CronExpressionParser.Options;
import se.sundsvall.billingdatapolling.api.model.SchedulerInformation;
import se.sundsvall.billingdatapolling.service.scheduler.configuration.SchedulerProperties;

@Component
public class Scheduler {

	@Autowired
	private SchedulerProperties properties;

	@Scheduled(cron = "${scheduler.cron.expression}")
	public void execute() {
		throw new NotImplementedException("I'm not there yet!");
	}

	public SchedulerInformation getSchedulerInformation() {
		return SchedulerInformation.create()
			.withDescription(CronExpressionDescriptor.getDescription(properties.expression(), options()))
			.withEnabled(properties.enabled())
			.withExpression(properties.expression());
	}

	private static Options options() {
		 var options = new Options();
		options.setVerbose(true);
		options.setUseJavaEeScheduleExpression(true);
		options.setLocale(ENGLISH);
		return options;
	}
}
