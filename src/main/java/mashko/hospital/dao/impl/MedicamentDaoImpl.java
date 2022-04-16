package mashko.hospital.dao.impl;

import mashko.hospital.connection.ConnectionException;
import mashko.hospital.connection.ConnectionPool;
import mashko.hospital.dao.DaoException;
import mashko.hospital.dao.MedicamentDao;
import mashko.hospital.entity.Medicament;
import mashko.hospital.entity.PageResult;
import mashko.hospital.entity.table.MedicationsFieldName;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class MedicamentDaoImpl implements MedicamentDao {
    private static final String SP_CREATE_MEDICAMENT = "CALL CreateMedicament(?)";
    private static final String SP_FIND_MEDICAMENT_BY_ID = "CALL FindMedicamentById(?)";
    private static final String SP_FIND_MEDICAMENT_BY_NAME = "CALL FindMedicamentByName(?)";
    private static final String SP_UPDATE_MEDICAMENT_ENABLED_STATUS = "CALL UpdateMedicamentEnabledStatus(?,?)";
    private static final String SP_FIND_ALL_MEDICATIONS_BY_NAME_PAGING = "CALL FindAllMedicationsByNamePaging(?,?,?)";

    @Override
    public int create(Medicament medicament) throws DaoException {
        Connection connection = null;
        CallableStatement statement = null;
        ResultSet resultSet = null;
        int userId;
        try {
            connection = ConnectionPool.getInstance().getConnection();
            statement = connection.prepareCall(SP_CREATE_MEDICAMENT);

            statement.setString(1, medicament.getName());

            resultSet = statement.executeQuery();
            userId = resultSet.next() ? resultSet.getInt(1) : 0;
        } catch (ConnectionException e) {
            throw new DaoException("Can not create data source.", e);
        } catch (SQLException e) {
            throw new DaoException("CreateMedicament failed.", e);
        } finally {
            ConnectionPool.closeConnection(connection, statement, resultSet);
        }
        return userId;
    }

    @Override
    public Optional<Medicament> findById(int id) throws DaoException {
        Connection connection = null;
        CallableStatement statement = null;
        ResultSet resultSet = null;
        Optional<Medicament> optionalMedicament;
        try {
            connection = ConnectionPool.getInstance().getConnection();
            statement = connection.prepareCall(SP_FIND_MEDICAMENT_BY_ID);
            statement.setInt(1, id);

            resultSet = statement.executeQuery();
            optionalMedicament = Optional.ofNullable(getMedicament(resultSet));
        } catch (ConnectionException e) {
            throw new DaoException("Can not create data source.", e);
        } catch (SQLException e) {
            throw new DaoException("FindMedicamentById failed.", e);
        } finally {
            ConnectionPool.closeConnection(connection, statement, resultSet);
        }
        return optionalMedicament;
    }

    @Override
    public Optional<Medicament> findByName(String name) throws DaoException {
        Connection connection = null;
        CallableStatement statement = null;
        ResultSet resultSet = null;
        Optional<Medicament> optionalMedicament;
        try {
            connection = ConnectionPool.getInstance().getConnection();
            statement = connection.prepareCall(SP_FIND_MEDICAMENT_BY_NAME);
            statement.setString(1, name);

            resultSet = statement.executeQuery();
            optionalMedicament = Optional.ofNullable(getMedicament(resultSet));
        } catch (ConnectionException e) {
            throw new DaoException("Can not create data source.", e);
        } catch (SQLException e) {
            throw new DaoException("FindMedicamentByName failed.", e);
        } finally {
            ConnectionPool.closeConnection(connection, statement, resultSet);
        }
        return optionalMedicament;
    }

    @Override
    public Optional<Medicament> updateEnabledStatus(int id, boolean isEnabled) throws DaoException {
        Connection connection = null;
        CallableStatement statement = null;
        ResultSet resultSet = null;
        Optional<Medicament> optionalMedicament;
        try {
            connection = ConnectionPool.getInstance().getConnection();
            statement = connection.prepareCall(SP_UPDATE_MEDICAMENT_ENABLED_STATUS);

            statement.setInt(1, id);
            statement.setBoolean(2, isEnabled);

            resultSet = statement.executeQuery();
            optionalMedicament = Optional.ofNullable(getMedicament(resultSet));
        } catch (ConnectionException e) {
            throw new DaoException("Can not create data source.", e);
        } catch (SQLException e) {
            throw new DaoException("UpdateMedicamentEnabledStatus failed.", e);
        } finally {
            ConnectionPool.closeConnection(connection, statement, resultSet);
        }
        return optionalMedicament;
    }

    @Override
    public PageResult<Medicament> findAllByNamePartPaging(String namePart, int page) throws DaoException {
        PageResult<Medicament> pageResult;
        Connection connection = null;
        CallableStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = ConnectionPool.getInstance().getConnection();
            statement = connection.prepareCall(SP_FIND_ALL_MEDICATIONS_BY_NAME_PAGING);
            statement.setString(1, namePart);
            statement.setInt(2, page);
            statement.registerOutParameter(3, Types.INTEGER);

            resultSet = statement.executeQuery();

            List<Medicament> medications = new LinkedList<>();
            while (resultSet.next()) {
                Medicament medicament = new Medicament();
                medicament.setId(resultSet.getInt(MedicationsFieldName.ID));
                medicament.setName(resultSet.getString(MedicationsFieldName.NAME));
                medicament.setEnabled(resultSet.getBoolean(MedicationsFieldName.IS_ENABLED));
                medications.add(medicament);
            }
            pageResult = PageResult.from(medications, statement.getInt(3));
        } catch (ConnectionException e) {
            throw new DaoException("Can not create data source.", e);
        } catch (SQLException e) {
            throw new DaoException("FindAllMedicationsByNamePaging failed.", e);
        } finally {
            ConnectionPool.closeConnection(connection, statement, resultSet);
        }
        return pageResult;
    }

    private Medicament getMedicament(ResultSet resultSet) throws SQLException {
        if (resultSet.next()) {
            Medicament medicament = new Medicament();
            medicament.setId(resultSet.getInt(MedicationsFieldName.ID));
            medicament.setName(resultSet.getString(MedicationsFieldName.NAME));
            medicament.setEnabled(resultSet.getBoolean(MedicationsFieldName.IS_ENABLED));
            return medicament;
        }
        return null;
    }
}
