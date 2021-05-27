package be.pxl.ja2.examen.service;

import be.pxl.ja2.examen.dao.BezoekersDao;
import be.pxl.ja2.examen.dao.PatientDao;
import be.pxl.ja2.examen.model.Bezoeker;
import be.pxl.ja2.examen.model.Patient;
import be.pxl.ja2.examen.model.PatientBuilder;
import be.pxl.ja2.examen.rest.resources.RegistreerBezoekerResource;
import be.pxl.ja2.examen.util.exception.BezoekersAppException;
import be.pxl.ja2.examen.util.exception.OngeldigTijdstipException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BezoekerServiceRegistreerBezoekerTest {
	private static final String PATIENT_CODE = "P001";
	@Mock
	private BezoekersDao bezoekerDao;
	@Mock
	private PatientDao patientDao;
	@InjectMocks
	private BezoekersService bezoekersService;
	@Captor
	private ArgumentCaptor<Bezoeker> bezoekerArgumentCaptor;
	private RegistreerBezoekerResource registreerBezoekerResource;


	@BeforeEach
	public void init(){
		registreerBezoekerResource = new RegistreerBezoekerResource();
		registreerBezoekerResource.setNaam("Vanaken");
		registreerBezoekerResource.setVoornaam("Max");
		registreerBezoekerResource.setTelefoonnummer("0489787458");
		registreerBezoekerResource.setTijdstip(LocalTime.of(15,30));
		registreerBezoekerResource.setPatientCode(PATIENT_CODE);
	}

	@Test
	public void throwsBezoekersAppExceptionWhenPatientCodeInvalid(){
		when(patientDao.findById(PATIENT_CODE)).thenReturn(Optional.empty());
		assertThrows(BezoekersAppException.class, () -> bezoekersService.registreerBezoeker(registreerBezoekerResource));
	}

	@Test
	public void throwsBezoekersAppExceptionWhenPatientAlreadyHasVisitor() {
		when(patientDao.findById(PATIENT_CODE)).thenReturn(Optional.of(PatientBuilder.aPatient().withCode(PATIENT_CODE).build()));
		when(bezoekerDao.findBezoekerByPatient_Code(PATIENT_CODE)).thenReturn(new Bezoeker());
		assertThrows(BezoekersAppException.class, () -> bezoekersService.registreerBezoeker(registreerBezoekerResource));
	}

	@Test
	public void bezoekerIsSavedCorrectly() {
		Patient patient = PatientBuilder.aPatient().withCode(PATIENT_CODE).build();
		when(patientDao.findById(PATIENT_CODE)).thenReturn(Optional.of(patient));
		when(bezoekerDao.findBezoekerByPatient_Code(PATIENT_CODE)).thenReturn(null);
		when(bezoekerDao.save(any())).thenAnswer(AdditionalAnswers.returnsFirstArg());

		bezoekersService.registreerBezoeker(registreerBezoekerResource);

		verify(bezoekerDao).save(bezoekerArgumentCaptor.capture());
		Bezoeker bezoeker = bezoekerArgumentCaptor.getValue();

		assertEquals(patient, bezoeker.getPatient());
		assertEquals(registreerBezoekerResource.getTijdstip(), bezoeker.getTijdstip());
		assertNull(bezoeker.getAanmelding());
		assertEquals(registreerBezoekerResource.getVoornaam(), bezoeker.getVoornaam());
		assertEquals(registreerBezoekerResource.getNaam(), bezoeker.getNaam());
	}

}
