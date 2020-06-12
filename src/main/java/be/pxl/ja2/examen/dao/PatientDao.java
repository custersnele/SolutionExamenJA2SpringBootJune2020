package be.pxl.ja2.examen.dao;

import be.pxl.ja2.examen.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientDao extends JpaRepository<Patient, String> {

}
