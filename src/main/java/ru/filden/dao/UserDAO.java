package ru.filden.dao;

import ru.filden.db.dbConnection;
import ru.filden.entity.User;
import ru.filden.impl.BaseDAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDAO implements BaseDAO<User, Integer> {
    private final dbConnection dataSource;

    public UserDAO(dbConnection dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public User save(User user) {
        String sql = "INSERT INTO users (login, password, role) VALUES (?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, user.getLogin());
            stmt.setString(2, user.getPassword());
            stmt.setInt(3, user.getRole());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    user.setId(rs.getInt(1));
                }
            }
            return user;
        } catch (SQLException e) {
            throw new RuntimeException("Error saving user", e);
        }
    }

    @Override
    public Optional<User> findById(Integer id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToUser(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding user by id", e);
        }
    }

    @Override
    public List<User> findAll() {
        String sql = "SELECT * FROM users";
        List<User> users = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                users.add(mapRowToUser(rs));
            }
            return users;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all users", e);
        }
    }

    @Override
    public User update(User user) {
        String sql = "UPDATE users SET login = ?, password = ?, role = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getLogin());
            stmt.setString(2, user.getPassword());
            stmt.setInt(3, user.getRole());
            stmt.setInt(4, user.getId());
            stmt.executeUpdate();
            return user;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating user", e);
        }
    }

    @Override
    public void deleteById(Integer id) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting user", e);
        }
    }

    @Override
    public boolean existsById(Integer id) {
        String sql = "SELECT COUNT(*) FROM users WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Error checking user existence", e);
        }
    }

    public Optional<User> findByLogin(String login) {
        String sql = "SELECT * FROM users WHERE login = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, login);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToUser(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding user by login", e);
        }
    }

    public boolean authenticate(String login, String password) {
        String sql = "SELECT COUNT(*) FROM users WHERE login = ? AND password = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, login);
            stmt.setString(2, password);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Error authenticating user", e);
        }
    }

    private User mapRowToUser(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("id"),
                rs.getString("login"),
                rs.getString("password"),
                rs.getInt("role")
        );
    }
}
