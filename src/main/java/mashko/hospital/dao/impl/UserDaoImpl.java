package mashko.hospital.dao.impl;

import mashko.hospital.connection.ConnectionException;
import mashko.hospital.connection.ConnectionPool;
import mashko.hospital.dao.DaoException;
import mashko.hospital.dao.UserDao;
import mashko.hospital.entity.Role;
import mashko.hospital.entity.User;
import mashko.hospital.entity.UserDetails;
import mashko.hospital.connection.ProxyConnection;
import mashko.hospital.entity.table.RolesFieldName;
import mashko.hospital.entity.table.UsersDetailsFieldName;
import mashko.hospital.entity.table.UsersFieldName;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * {@code UserDaoImpl} implementation of {@link UserDao}.
 * Implements all required methods for work with the {@link User} database entity
 * and included in {@code User} object element of enum {@link Role}.
 * <p>
 * All methods get connection from {@code ConnectionPool}
 * and it is object of type {@code ProxyConnection}. It is a wrapper of really
 * {@code Connection}, which different only in methods {@code close}
 * and {@code reallyClose}.
 *
 * @see ConnectionPool
 * @see ProxyConnection
 * @see Connection
 */

public class UserDaoImpl implements UserDao {
    /**
     * Sql {@code String} object for call stored procedure
     * {@code CreateClientWithUserDetails}.
     * Written for the MySQL dialect.
     */
    private static final String SP_CREATE_CLIENT_WITH_USER_DETAILS =
            "CALL CreateClientWithUserDetails(?,?,?,?,?,?,?,?,?,?)";
    /**
     * Sql {@code String} object for call stored procedure
     * {@code FindUserByLogin}.
     * Written for the MySQL dialect.
     */
    private static final String SP_FIND_USER_BY_LOGIN =
            "CALL FindUserByLogin(?)";
    /**
     * Sql {@code String} object for call stored procedure
     * {@code FindUserWithUserDetailsByLogin}.
     * Written for the MySQL dialect.
     */
    private static final String SP_FIND_USER_WITH_USER_DETAILS_BY_LOGIN =
            "CALL FindUserWithUserDetailsByLogin(?)";
    /**
     * Sql {@code String} object for call stored procedure
     * {@code FindUserById}.
     * Written for the MySQL dialect.
     */
    private static final String SP_FIND_USER_BY_ID =
            "CALL FindUserById(?)";
    /**
     * Sql {@code String} object for call stored procedure
     * {@code FindUserWithUserDetailsById}.
     * Written for the MySQL dialect.
     */
    private static final String SP_FIND_USER_WITH_USER_DETAILS_BY_ID =
            "CALL FindUserWithUserDetailsById(?)";
    /**
     * Sql {@code String} object for call stored procedure
     * {@code FindUserWithUserDetailsByPassportData}.
     * Written for the MySQL dialect.
     */
    private static final String SP_FIND_USER_WITH_USER_DETAILS_BY_PASSPORT_DATA =
            "CALL FindUserWithUserDetailsByPassportData(?,?,?,?)";
    /**
     * Sql {@code String} object for call stored procedure {@code FindUserRolesByLogin}.
     * Written for the MySQL dialect.
     */
    private static final String SP_FIND_USER_ROLES_BY_LOGIN =
            "CALL FindUserRolesByLogin(?)";
    /**
     * Sql {@code String} object for call stored procedure
     * {@code AddUserRole}.
     * Written for the MySQL dialect.
     */
    private static final String SP_ADD_USER_ROLE =
            "CALL AddUserRole(?,?)";
    /**
     * Sql {@code String} object for call stored procedure
     * {@code DeleteUserRole}.
     * Written for the MySQL dialect.
     */
    private static final String SP_DELETE_USER_ROLE =
            "CALL DeleteUserRole(?,?)";
    /**
     * Sql {@code String} object for call stored procedure
     * {@code UpdateUserLoginAndPassword}.
     * Written for the MySQL dialect.
     */
    private static final String SP_UPDATE_LOGIN_AND_PASSWORD =
            "CALL UpdateUserLoginAndPassword(?,?,?)";

    /**
     * Create entity {@link User} with entity {@link UserDetails}
     * and with {@link Role#CLIENT} in database.
     *
     * @param user an a {@code User} entity.
     * @return auto-generated {@code User.id} field or zero if not success.
     * @throws DaoException if a database access error occurs or if
     *                      {@link ConnectionPool} throws
     *                      {@link ConnectionException}.
     */
    @Override
    public int createClientWithUserDetails(User user) throws DaoException {
        Connection connection = null;
        CallableStatement statement = null;
        ResultSet resultSet = null;
        int userId;
        try {
            connection = ConnectionPool.getInstance().getConnection();
            statement = connection.prepareCall(SP_CREATE_CLIENT_WITH_USER_DETAILS);

            statement.setString(1, user.getLogin());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getUserDetails().getPassportId());
            statement.setString(4, user.getUserDetails().getGender().name());
            statement.setString(5, user.getUserDetails().getFirstName());
            statement.setString(6, user.getUserDetails().getSurname());
            statement.setString(7, user.getUserDetails().getLastName());
            statement.setDate(8, user.getUserDetails().getBirthday());
            statement.setString(9, user.getUserDetails().getAddress());
            statement.setString(10, user.getUserDetails().getPhone());

            resultSet = statement.executeQuery();
            userId = resultSet.next() ? resultSet.getInt(1) : 0;
        } catch (ConnectionException e) {
            throw new DaoException("Can not create data source.", e);
        } catch (SQLException e) {
            throw new DaoException("CreateClientWithUserDetails failed.", e);
        } finally {
            ConnectionPool.closeConnection(connection, statement, resultSet);
        }
        return userId;
    }

    /**
     * Update entity {@code User} in database.
     *
     * @param login    {@code String} value of {@code User.login}
     *                 for find entity that need to be updated.
     * @param newValue new value of {@code User} entity.
     * @return {@code newValue} if it was updated or
     * {@code oldValue} if it wasn't of {@code User} entity.
     * @throws DaoException if a database access error occurs or if
     *                      {@link ConnectionPool} throws
     *                      {@link ConnectionException}.
     */
    @Override
    public Optional<User> updateLoginAndPassword(String login, User newValue) throws DaoException {
        Connection connection = null;
        CallableStatement statement = null;
        ResultSet resultSet = null;
        Optional<User> optionalUser = Optional.empty();
        try {
            connection = ConnectionPool.getInstance().getConnection();
            statement = connection.prepareCall(SP_UPDATE_LOGIN_AND_PASSWORD);
            statement.setString(1, login);
            statement.setString(2, newValue.getLogin());
            statement.setString(3, newValue.getPassword());

            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                newValue.setId(resultSet.getInt(UsersFieldName.ID));
                newValue.setLogin(resultSet.getString(UsersFieldName.LOGIN));
                newValue.setPassword(resultSet.getString(UsersFieldName.PASSWORD));
                newValue.setRoles(findUserRoles(newValue.getLogin()));
                optionalUser = Optional.of(newValue);
            }
        } catch (ConnectionException e) {
            throw new DaoException("Can not create data source.", e);
        } catch (SQLException e) {
            throw new DaoException("Updating user failed.", e);
        } finally {
            ConnectionPool.closeConnection(connection, statement, resultSet);
        }
        return optionalUser;
    }

    /**
     * Find {@code User} entity by {@code User.login} field.
     *
     * @param login {@code String} value of {@code User.login} field.
     * @return {@code Optional<User>} if it present
     * or an empty {@code Optional} if it isn't.
     * @throws DaoException if a database access error occurs or if
     *                      {@link ConnectionPool} throws
     *                      {@link ConnectionException}.
     * @see Optional
     */
    @Override
    public Optional<User> findByLogin(String login) throws DaoException {
        Connection connection = null;
        CallableStatement statement = null;
        ResultSet resultSet = null;
        Optional<User> optionalUser;
        try {
            connection = ConnectionPool.getInstance().getConnection();
            statement = connection.prepareCall(SP_FIND_USER_BY_LOGIN);
            statement.setString(1, login);

            resultSet = statement.executeQuery();
            optionalUser = resultSet.next() ? Optional.of(getUser(resultSet)) : Optional.empty();
        } catch (ConnectionException e) {
            throw new DaoException("Can not create data source.", e);
        } catch (SQLException e) {
            throw new DaoException("Find user failed.", e);
        } finally {
            ConnectionPool.closeConnection(connection, statement, resultSet);
        }
        return optionalUser;
    }

    /**
     * Find {@code User} entity by {@code User.login} field
     * with {@link UserDetails}.
     *
     * @param login {@code String} value of {@code User.login} field.
     * @return {@code Optional<User>} if it present
     * or an empty {@code Optional} if it isn't.
     * @throws DaoException if a database access error occurs or if
     *                      {@link ConnectionPool} throws
     *                      {@link ConnectionException}.
     * @see Optional
     */
    @Override
    public Optional<User> findByLoginWithUserDetails(String login) throws DaoException {
        Connection connection = null;
        CallableStatement statement = null;
        ResultSet resultSet = null;
        Optional<User> optionalUser;
        try {
            connection = ConnectionPool.getInstance().getConnection();
            statement = connection.prepareCall(SP_FIND_USER_WITH_USER_DETAILS_BY_LOGIN);
            statement.setString(1, login);

            resultSet = statement.executeQuery();
            optionalUser = resultSet.next() ? Optional.of(getUserWithUserDetails(resultSet)) : Optional.empty();
        } catch (ConnectionException e) {
            throw new DaoException("Can not create data source.", e);
        } catch (SQLException e) {
            throw new DaoException("Find user failed.", e);
        } finally {
            ConnectionPool.closeConnection(connection, statement, resultSet);
        }
        return optionalUser;
    }

    /**
     * Find {@code User} entity by {@code User.id} field.
     *
     * @param id {@code int} value of {@code User.id} field.
     * @return {@code Optional<User>} if it present
     * or an empty {@code Optional} if it isn't.
     * @throws DaoException if a database access error occurs or if
     *                      {@link ConnectionPool} throws
     *                      {@link ConnectionException}.
     * @see Optional
     */
    @Override
    public Optional<User> findById(int id) throws DaoException {
        Connection connection = null;
        CallableStatement statement = null;
        ResultSet resultSet = null;
        Optional<User> optionalUser;
        try {
            connection = ConnectionPool.getInstance().getConnection();
            statement = connection.prepareCall(SP_FIND_USER_BY_ID);
            statement.setInt(1, id);

            resultSet = statement.executeQuery();
            optionalUser = resultSet.next() ? Optional.of(getUser(resultSet)) : Optional.empty();
        } catch (ConnectionException e) {
            throw new DaoException("Can not create data source.", e);
        } catch (SQLException e) {
            throw new DaoException("Find user failed.", e);
        } finally {
            ConnectionPool.closeConnection(connection, statement, resultSet);
        }
        return optionalUser;
    }

    /**
     * Find {@code User} entity by {@code User.id} field
     * with {@link UserDetails}.
     *
     * @param id {@code int} value of {@code User.id} field.
     * @return {@code Optional<User>} if it present
     * or an empty {@code Optional} if it isn't.
     * @throws DaoException if a database access error occurs or if
     *                      {@link ConnectionPool} throws
     *                      {@link ConnectionException}.
     * @see Optional
     */
    @Override
    public Optional<User> findByIdWithUserDetails(int id) throws DaoException {
        Connection connection = null;
        CallableStatement statement = null;
        ResultSet resultSet = null;
        Optional<User> optionalUser;
        try {
            connection = ConnectionPool.getInstance().getConnection();
            statement = connection.prepareCall(SP_FIND_USER_WITH_USER_DETAILS_BY_ID);
            statement.setInt(1, id);

            resultSet = statement.executeQuery();
            optionalUser = resultSet.next() ? Optional.of(getUserWithUserDetails(resultSet)) : Optional.empty();
        } catch (ConnectionException e) {
            throw new DaoException("Can not create data source.", e);
        } catch (SQLException e) {
            throw new DaoException("Find user failed.", e);
        } finally {
            ConnectionPool.closeConnection(connection, statement, resultSet);
        }
        return optionalUser;
    }

    /**
     * Find entity {@code User} with {@code UserDetails}.
     *
     * @param firstName {@code String} value of
     *                  {@link UserDetails}.
     * @param surname   {@code String} value of
     *                  {@link UserDetails}.
     * @param lastName  {@code String} value of
     *                  {@link UserDetails}.
     * @param birthday  {@code Date} value of
     *                  {@link UserDetails}.
     * @return {@code Optional} of {@code User} if user exist or
     * {@link Optional#empty()} if entity {@code User} not exist.
     * @throws DaoException if a database access error occurs or if
     *                      {@link ConnectionPool} throws
     *                      {@link ConnectionException}.
     * @see Date
     * @see Optional
     */
    @Override
    public Optional<User> findUserWithUserDetailsByPassportData
    (String firstName, String surname, String lastName, Date birthday) throws DaoException {
        Connection connection = null;
        CallableStatement statement = null;
        ResultSet resultSet = null;
        Optional<User> optionalUser;
        try {
            connection = ConnectionPool.getInstance().getConnection();
            statement = connection.prepareCall(SP_FIND_USER_WITH_USER_DETAILS_BY_PASSPORT_DATA);
            statement.setString(1, firstName);
            statement.setString(2, surname);
            statement.setString(3, lastName);
            statement.setDate(4, birthday);

            resultSet = statement.executeQuery();
            optionalUser = resultSet.next() ? Optional.of(getUserWithUserDetails(resultSet)) : Optional.empty();
        } catch (ConnectionException e) {
            throw new DaoException("Can not create data source.", e);
        } catch (SQLException e) {
            throw new DaoException("Find user failed.", e);
        } finally {
            ConnectionPool.closeConnection(connection, statement, resultSet);
        }
        return optionalUser;
    }

    /**
     * Add {@link Role} to entity {@link User}
     *
     * @param login {@code String} value of {@code User.login} field.
     * @param role  enumeration element of {@link Role}.
     * @return {@code true} if it was successful and {@code false} if it wasn't.
     * @throws DaoException if a database access error occurs or if
     *                      {@link ConnectionPool} throws
     *                      {@link ConnectionException}.
     */
    @Override
    public boolean addUserRole(String login, Role role) throws DaoException {
        boolean result;
        Connection connection = null;
        CallableStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = ConnectionPool.getInstance().getConnection();
            statement = connection.prepareCall(SP_ADD_USER_ROLE);
            statement.setString(1, login);
            statement.setString(2, role.name());

            resultSet = statement.executeQuery();
            result = resultSet.next() && resultSet.getInt(1) != 0;
        } catch (ConnectionException e) {
            throw new DaoException("Can not create data source.", e);
        } catch (SQLException e) {
            throw new DaoException("Updating user roles failed.", e);
        } finally {
            ConnectionPool.closeConnection(connection, statement, resultSet);
        }
        return result;
    }

    /**
     * Delete {@link Role} from entity {@link User}
     *
     * @param login {@code String} value of {@code User.login} field.
     * @param role  enumeration element of {@link Role}.
     * @return {@code true} if it was successful and {@code false} if it wasn't.
     * @throws DaoException if a database access error occurs or if
     *                      {@link ConnectionPool} throws
     *                      {@link ConnectionException}.
     */
    @Override
    public boolean deleteUserRole(String login, Role role) throws DaoException {
        boolean result;
        Connection connection = null;
        CallableStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = ConnectionPool.getInstance().getConnection();
            statement = connection.prepareCall(SP_DELETE_USER_ROLE);
            statement.setString(1, login);
            statement.setString(2, role.name());

            resultSet = statement.executeQuery();
            result = resultSet.next() && resultSet.getInt(1) != 0;
        } catch (ConnectionException e) {
            throw new DaoException("Can not create data source.", e);
        } catch (SQLException e) {
            throw new DaoException("Updating user roles failed.", e);
        } finally {
            ConnectionPool.closeConnection(connection, statement, resultSet);
        }
        return result;
    }

    /**
     * Find roles {@code User} entity by {@code User.login} field.
     *
     * @param login {@code int} value of {@code User.login} field.
     * @return {@code List<Role>} being a {@code ArrayList<Role>}
     * object if it present  or an empty {@code List} if it isn't.
     * @throws DaoException if a database access error occurs or if
     *                      {@link ConnectionPool} throws
     *                      {@link ConnectionException}.
     * @see List
     * @see ArrayList
     * @see ConnectionException
     */
    public List<Role> findUserRoles(String login) throws DaoException {
        Connection connection = null;
        CallableStatement statement = null;
        ResultSet resultSet = null;
        ArrayList<Role> roles = new ArrayList<>();
        try {
            connection = ConnectionPool.getInstance().getConnection();
            statement = connection.prepareCall(SP_FIND_USER_ROLES_BY_LOGIN);
            statement.setString(1, login);

            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Role role = Role.valueOf(resultSet.getString(RolesFieldName.TITLE));
                roles.add(role);
            }
        } catch (ConnectionException e) {
            throw new DaoException("Can not create data source.", e);
        } catch (SQLException e) {
            throw new DaoException("Find user roles failed.", e);
        } finally {
            ConnectionPool.closeConnection(connection, statement, resultSet);
        }
        return roles;
    }


    /**
     * Get {@code User} fields from {@link ResultSet}.
     *
     * @param resultSet {@link ResultSet} entity.
     * @return new {@code User} entity.
     * @throws SQLException if a database access error occurs.
     * @throws DaoException if in {@link UserDaoImpl#findUserRoles(String)}
     *                      a database access error occurs.
     */
    private User getUser(ResultSet resultSet) throws SQLException, DaoException {
        User user = new User();
        user.setId(resultSet.getInt(UsersFieldName.ID));
        user.setLogin(resultSet.getString(UsersFieldName.LOGIN));
        user.setPassword(resultSet.getString(UsersFieldName.PASSWORD));
        user.setRoles(findUserRoles(user.getLogin()));
        return user;
    }

    /**
     * Get {@code User} and {@code UserDetails} fields from {@link ResultSet}.
     *
     * @param resultSet {@link ResultSet} entity.
     * @return new {@code User} entity.
     * @throws SQLException if a database access error occurs.
     * @throws DaoException if in {@link UserDaoImpl#findUserRoles(String)}
     *                      a database access error occurs.
     */
    private User getUserWithUserDetails(ResultSet resultSet) throws SQLException, DaoException {
        User user = new User();
        UserDetails userDetails = new UserDetails();
        user.setUserDetails(userDetails);
        user.setId(resultSet.getInt(UsersFieldName.ID));
        user.setLogin(resultSet.getString(UsersFieldName.LOGIN));
        user.setPassword(resultSet.getString(UsersFieldName.PASSWORD));

        userDetails.setPassportId(resultSet.getString(UsersDetailsFieldName.PASSPORT_ID));
        userDetails.setUserId(resultSet.getInt(UsersDetailsFieldName.USER_ID));
        userDetails.setGender(UserDetails.Gender
                .valueOf(resultSet.getString(UsersDetailsFieldName.GENDER)));
        userDetails.setFirstName(resultSet.getString(UsersDetailsFieldName.FIRST_NAME));
        userDetails.setSurname(resultSet.getString(UsersDetailsFieldName.SURNAME));
        userDetails.setLastName(resultSet.getString(UsersDetailsFieldName.LAST_NAME));
        userDetails.setBirthday(resultSet.getDate(UsersDetailsFieldName.BIRTHDAY));
        userDetails.setAddress(resultSet.getString(UsersDetailsFieldName.ADDRESS));
        userDetails.setPhone(resultSet.getString(UsersDetailsFieldName.PHONE));

        user.setRoles(findUserRoles(user.getLogin()));
        return user;
    }
}
