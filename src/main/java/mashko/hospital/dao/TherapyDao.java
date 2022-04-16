package mashko.hospital.dao;

import mashko.hospital.entity.CardType;
import mashko.hospital.entity.Diagnosis;
import mashko.hospital.entity.Therapy;
import mashko.hospital.entity.UserDetails;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

/**
 * A {@code TherapyDao} data access objects works with the database and
 * can be used for work with the {@link Therapy} database entity.
 */

public interface TherapyDao {
    int createTherapyWithDiagnosis(Therapy therapy, Diagnosis diagnosis, CardType cardType) throws DaoException;

    Optional<Therapy> findCurrentPatientTherapy(String doctorLogin, String patientLogin, CardType cardType) throws DaoException;

    List<Therapy> findOpenDoctorTherapies(String doctorLogin, CardType cardType) throws DaoException;

    boolean setTherapyEndDate(String doctorLogin, String patientLogin, Date date, CardType cardType) throws DaoException;

    boolean setFinalDiagnosisToTherapy(String doctorLogin, String patientLogin, CardType cardType) throws DaoException;

    List<Therapy> findPatientTherapies(UserDetails patientUserDetails, CardType cardType) throws DaoException;
}
