package se.sundsvall.billingdatapolling.api;

import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.problem.Problem;
import org.zalando.problem.violations.ConstraintViolationProblem;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import se.sundsvall.billingdatapolling.api.model.PollingRequest;
import se.sundsvall.billingdatapolling.service.scheduler.accesscard.AccessCardSchedulerService;

@RestController
@Validated
@RequestMapping("/pollings")
@Tag(name = "Pollings", description = "Polling resources")
public class PollingResource {

	@Autowired
	private AccessCardSchedulerService accessCardService;

	@PostMapping(path = "/access-cards", produces = { APPLICATION_PROBLEM_JSON_VALUE })
	@Operation(summary = "Poll access card orders and performs the necessary processing.")
	@ApiResponse(responseCode = "204", description = "Successful operation", content = @Content(schema = @Schema(implementation = Void.class)))
	@ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(oneOf = { Problem.class, ConstraintViolationProblem.class })))
	@ApiResponse(responseCode = "500", description = "Internal Server error", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	@ApiResponse(responseCode = "502", description = "Bad Gateway", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	public ResponseEntity<Void> pollAccessCards(@RequestBody @Valid @NotNull final PollingRequest body) {

		accessCardService.execute(body.getFromDate(), body.getToDate());

		return ResponseEntity.noContent().build();
	}
}
