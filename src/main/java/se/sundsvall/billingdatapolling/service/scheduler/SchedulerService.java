package se.sundsvall.billingdatapolling.service.scheduler;

import se.sundsvall.billingdatapolling.api.model.SchedulerInformation;

public interface SchedulerService {

	void execute();

	SchedulerInformation getScheduleInformation();

	String getExpression();
}
