package se.sundsvall.billingdatapolling.integration.oep.configuration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import se.sundsvall.billingdatapolling.Application;

@SpringBootTest(classes = Application.class)
@ActiveProfiles("junit")
class OepGetInstancePropertiesTest {

	@Autowired
	private OepGetInstanceProperties properties;

	@Test
	void testProperties() {
		assertThat(properties.connectTimeout()).isEqualTo(10);
		assertThat(properties.readTimeout()).isEqualTo(20);
	}
}