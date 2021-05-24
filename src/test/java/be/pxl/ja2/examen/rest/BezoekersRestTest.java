package be.pxl.ja2.examen.rest;

import be.pxl.ja2.examen.rest.resources.RegistreerBezoekerResource;
import be.pxl.ja2.examen.service.BezoekersService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(value = BezoekersRest.class)
public class BezoekersRestTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private BezoekersService bezoekersService;

	@Captor
	private ArgumentCaptor<RegistreerBezoekerResource> registreerBezoekerResourceArgumentCaptor;

	@Test
	void badRequestWhenPhonenumberNotGiven() throws Exception {

		mockMvc.perform(post("/bezoekers")
				.contentType(MediaType.APPLICATION_JSON)
				.content(ResourceFileReader.getAsString("request/bezoekersrequest_without_phonenumber.json")))
				.andExpect(status().isBadRequest());
	}

	@Test
	void returnsBezoekerIdWhenRequestValid() throws Exception {

		Mockito.when(bezoekersService.registreerBezoeker(Mockito.any())).thenReturn(112L);

		mockMvc.perform(post("/bezoekers")
				.contentType(MediaType.APPLICATION_JSON)
				.content(ResourceFileReader.getAsString("request/bezoekersrequest_valid.json")))
				.andExpect(status().isCreated())
				.andExpect(content().string("112"));

		Mockito.verify(bezoekersService).registreerBezoeker(registreerBezoekerResourceArgumentCaptor.capture());

		RegistreerBezoekerResource resource = registreerBezoekerResourceArgumentCaptor.getValue();

		// TODO validate values
	}
}
