package mashko.hospital.dao;

import mashko.hospital.entity.Medicament;
import mashko.hospital.entity.PageResult;

import java.util.Optional;

public interface MedicamentDao {
    int create(Medicament medicament) throws DaoException;

    Optional<Medicament> findById(int id) throws DaoException;

    Optional<Medicament> findByName(String name) throws DaoException;

    Optional<Medicament> updateEnabledStatus(int id, boolean isEnabled) throws DaoException;

    PageResult<Medicament> findAllByNamePartPaging(String namePart, int page) throws DaoException;
}
