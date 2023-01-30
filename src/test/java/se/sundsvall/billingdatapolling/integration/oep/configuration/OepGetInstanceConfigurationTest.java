package se.sundsvall.billingdatapolling.integration.oep.configuration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Base64;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import feign.RequestInterceptor;
import se.sundsvall.dept44.configuration.feign.FeignMultiCustomizer;

@ExtendWith(MockitoExtension.class)
class OepGetInstanceConfigurationTest {

	@Mock
	private OepGetInstanceProperties propertiesMock;

	@Spy
	private FeignMultiCustomizer feignMultiCustomizerSpy;

	@Captor
	private ArgumentCaptor<RequestInterceptor> requestInterceptorCaptor;

	@InjectMocks
	private OepGetInstanceConfiguration configuration;

	@Test
	void testFeignBuilderHelper() {

		final var connectTimeout = 123;
		final var readTimeout = 321;
		final var username = "username";
		final var password = "password";

		when(propertiesMock.connectTimeout()).thenReturn(connectTimeout);
		when(propertiesMock.readTimeout()).thenReturn(readTimeout);
		when(propertiesMock.username()).thenReturn(username);
		when(propertiesMock.password()).thenReturn(password);

		// Mock static FeignMultiCustomizer to enable spy and to verify that static method is being called
		try (MockedStatic<FeignMultiCustomizer> feignMultiCustomizerMock = Mockito.mockStatic(FeignMultiCustomizer.class)) {
			feignMultiCustomizerMock.when(FeignMultiCustomizer::create).thenReturn(feignMultiCustomizerSpy);

			configuration.feignBuilderCustomizer(propertiesMock);

			feignMultiCustomizerMock.verify(FeignMultiCustomizer::create);
		}

		// Verifications
		verify(propertiesMock).connectTimeout();
		verify(propertiesMock).readTimeout();
		verify(propertiesMock).username();
		verify(propertiesMock).password();
		verify(feignMultiCustomizerSpy).withRequestInterceptor(requestInterceptorCaptor.capture());
		verify(feignMultiCustomizerSpy).withRequestTimeoutsInSeconds(connectTimeout, readTimeout);
		verify(feignMultiCustomizerSpy).composeCustomizersToOne();

		// Assert ErrorDecoder
		assertThat(requestInterceptorCaptor.getValue())
			.isInstanceOf(RequestInterceptor.class)
			.hasFieldOrPropertyWithValue("headerValue", "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes()));

	}
}
