package be.pxl.ja2.examen.rest;

import be.pxl.ja2.examen.rest.resources.RegistreerBezoekerResource;
import be.pxl.ja2.examen.service.BezoekersService;
import be.pxl.ja2.examen.util.exception.BezoekersAppException;
import be.pxl.ja2.examen.util.exception.OngeldigTijdstipException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping(path = "bezoekers")
public class BezoekersRest {

	private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHH:mm");
	private static final Logger LOGGER = LogManager.getLogger(BezoekersRest.class);

	private final BezoekersService bezoekersService;

	public BezoekersRest(BezoekersService bezoekersService) {
		this.bezoekersService = bezoekersService;
	}

	@PostMapping
	public ResponseEntity<Long> registreerBezoeker(@RequestBody @Valid RegistreerBezoekerResource registreerBezoekerResource) {
		try {
			Long result = bezoekersService.registreerBezoeker(registreerBezoekerResource);
			return new ResponseEntity<>(result, HttpStatus.CREATED);
		} catch (BezoekersAppException | OngeldigTijdstipException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	@GetMapping
	@RequestMapping("{bezoekerId}/{entranceTimestamp}")
	public ResponseEntity<Void> controleerBezoek(@PathVariable("bezoekerId") Long bezoekerId, @PathVariable("entranceTimestamp") String entranceTimestamp) {
		try {
			bezoekersService.controleerBezoek(bezoekerId, LocalDateTime.parse(entranceTimestamp, TIMESTAMP_FORMAT));
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (BezoekersAppException | OngeldigTijdstipException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}

	}

}
