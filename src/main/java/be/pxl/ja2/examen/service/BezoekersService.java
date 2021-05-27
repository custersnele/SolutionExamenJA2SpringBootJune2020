package be.pxl.ja2.examen.service;

import be.pxl.ja2.examen.dao.BezoekersDao;
import be.pxl.ja2.examen.dao.PatientDao;
import be.pxl.ja2.examen.model.Bezoeker;
import be.pxl.ja2.examen.model.Patient;
import be.pxl.ja2.examen.rest.resources.BezoekResource;
import be.pxl.ja2.examen.rest.resources.RegistreerBezoekerResource;
import be.pxl.ja2.examen.util.exception.BezoekersAppException;
import be.pxl.ja2.examen.util.exception.BezoekerstijdstipUtil;
import be.pxl.ja2.examen.util.exception.OngeldigTijdstipException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BezoekersService {
	private static final Logger LOGGER = LogManager.getLogger(BezoekersService.class);

	public static final int BEZOEKERS_PER_TIJDSTIP_PER_AFDELING = 2;

	private final BezoekersDao bezoekersDao;
	private final PatientDao patientDao;

	public BezoekersService(BezoekersDao bezoekersDao, PatientDao patientDao) {
		this.bezoekersDao = bezoekersDao;
		this.patientDao = patientDao;
	}

	public Long registreerBezoeker(RegistreerBezoekerResource registreerBezoekerResource) {
		BezoekerstijdstipUtil.controleerBezoekerstijdstip(registreerBezoekerResource.getTijdstip());
		Patient patient = patientDao.findById(registreerBezoekerResource.getPatientCode()).orElse(null);
		if (patient == null) {
			throw new BezoekersAppException("Patient [" + registreerBezoekerResource.getPatientCode() + "] onbekend.");
		}
		Bezoeker existingBezoeker = bezoekersDao.findBezoekerByPatient_Code(patient.getCode());
		if (existingBezoeker != null) {
			throw new BezoekersAppException("Er is reeds een bezoeker geregistreerd voor patient [" + registreerBezoekerResource.getPatientCode() + "]");
		}
		List<Bezoeker> bezoekersByTijdstipAndAfdeling = bezoekersDao.findBezoekersByTijdstipAndPatient_Afdeling(registreerBezoekerResource.getTijdstip(), patient.getAfdeling());
		if (bezoekersByTijdstipAndAfdeling.size() >= BEZOEKERS_PER_TIJDSTIP_PER_AFDELING) {
			throw new BezoekersAppException("Kies een ander tijdstip.");
		}
		Bezoeker bezoeker = new Bezoeker();
		bezoeker.setPatient(patient);
		bezoeker.setNaam(registreerBezoekerResource.getNaam());
		bezoeker.setVoornaam(registreerBezoekerResource.getVoornaam());
		bezoeker.setTelefoonnummer(registreerBezoekerResource.getTelefoonnummer());
		bezoeker.setTijdstip(registreerBezoekerResource.getTijdstip());
		bezoeker = bezoekersDao.save(bezoeker);
		return bezoeker.getId();
	}

	public void controleerBezoek(Long bezoekerId, LocalDateTime aanmelding) throws BezoekersAppException, OngeldigTijdstipException {
		Bezoeker bezoeker = bezoekersDao.findById(bezoekerId).orElse(null);
		if (bezoeker == null) {
			LOGGER.error("Geen bezoeker gevonden met id [" + bezoekerId + "]");
			throw new BezoekersAppException("Er deed zich een fout voor. Wend u tot de bezoekersbalie.");
		}
		if (bezoeker.isReedsAangemeld(aanmelding.toLocalDate())) {
			LOGGER.error("Bezoeker is vandaag reeds aangemeld.");
			throw new BezoekersAppException("Bezoeker is vandaag reeds aangemeld.");
		}
		BezoekerstijdstipUtil.controleerAanmeldingstijdstip(aanmelding, bezoeker.getTijdstip());
		LOGGER.info("Bezoeker voor patient [" + bezoeker.getPatient().getCode() + "] toegelaten.");
		bezoeker.setAanmelding(aanmelding);
		bezoekersDao.save(bezoeker);
	}

	public List<Bezoeker> getBezoekersVoorAfdeling(String afdelingCode) {
		return bezoekersDao.findBezoekersByPatient_Afdeling_Code(afdelingCode);
	}
}
