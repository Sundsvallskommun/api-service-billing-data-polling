package se.sundsvall.billingdatapolling.service.scheduler;

import se.sundsvall.billingdatapolling.api.model.SchedulerInformation;

public interface ScheduleService {

	void execute();

	SchedulerInformation getScheduleInformation();

	String getExpression();
}
