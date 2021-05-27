package be.pxl.ja2.examen.dao;

import be.pxl.ja2.examen.model.Afdeling;
import be.pxl.ja2.examen.model.Bezoeker;
import be.pxl.ja2.examen.model.BezoekerBuilder;
import be.pxl.ja2.examen.model.Patient;
import be.pxl.ja2.examen.model.PatientBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class BezoekersDaoTest {

	@Autowired
	private TestEntityManager testEntityManager;

	@Autowired
	private BezoekersDao bezoekersDao;

	private Afdeling afdeling;

	@BeforeEach
	void init() {
		afdeling = new Afdeling("A", "Afdeling A");
		testEntityManager.persist(afdeling);
		Patient p1 = PatientBuilder.aPatient().withCode("P001").withAfdeling(afdeling).build();
		Patient p2 = PatientBuilder.aPatient().withCode("P002").withAfdeling(afdeling).build();
		testEntityManager.persist(p1);
		testEntityManager.persist(p2);
		Bezoeker bezoeker = BezoekerBuilder.aBezoeker().withPatient(p2).withTijdstip(LocalTime.of(14, 0)).build();
		testEntityManager.persist(bezoeker);
		testEntityManager.flush();
		testEntityManager.clear();
	}


	@Test
	void returnsBezoekersOpAfdelingVoorTijdstip() {
		List<Bezoeker> bezoekers = bezoekersDao.findBezoekersByTijdstipAndPatient_Afdeling(LocalTime.of(14, 0), afdeling);

		Assertions.assertNotNull(bezoekers);
		assertEquals(1, bezoekers.size());
		assertEquals(afdeling, bezoekers.get(0).getPatient().getAfdeling());
		assertEquals(LocalTime.of(14,0), bezoekers.get(0).getTijdstip());
	}

	@Test
	void returnsGeenBezoekersVoorTijdstipOpAfdeling() {
		List<Bezoeker> bezoekers = bezoekersDao.findBezoekersByTijdstipAndPatient_Afdeling(LocalTime.of(14, 10), afdeling);

		Assertions.assertNotNull(bezoekers);
		assertTrue(bezoekers.isEmpty());
	}



}
