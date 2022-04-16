package mashko.hospital.dao;

import mashko.hospital.entity.PageResult;
import mashko.hospital.entity.Procedure;

import java.util.Optional;

public interface ProceduresDao {
    int create(Procedure procedure) throws DaoException;

    Optional<Procedure> findByName(String name) throws DaoException;

    Optional<Procedure> findById(int id) throws DaoException;

    Optional<Procedure> updateCost(int id, int cost) throws DaoException;

    Optional<Procedure> updateEnabledStatus(int id, boolean isEnabled) throws DaoException;

    PageResult<Procedure> findAllByNamePartPaging(String namePart, int page) throws DaoException;
}
