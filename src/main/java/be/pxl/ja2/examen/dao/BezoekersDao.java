package be.pxl.ja2.examen.dao;

import be.pxl.ja2.examen.model.Afdeling;
import be.pxl.ja2.examen.model.Bezoeker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;

@Repository
public interface BezoekersDao extends JpaRepository<Bezoeker, Long> {

	List<Bezoeker> findBezoekersByTijdstipAndPatient_Afdeling(LocalTime tijdstip, Afdeling afdeling);
	Bezoeker findBezoekerByPatient_Code(String patientCode);
	List<Bezoeker> findBezoekersByPatient_Afdeling_Code(String afdelingCode);
}
