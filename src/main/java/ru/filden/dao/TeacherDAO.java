package ru.filden.dao;

import ru.filden.db.dbConnection;
import ru.filden.entity.Teacher;
import ru.filden.impl.BaseDAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TeacherDAO implements BaseDAO<Teacher, Integer> {
    private final dbConnection dataSource;

    public TeacherDAO(dbConnection dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Teacher save(Teacher teacher) {
        String sql = "INSERT INTO teachers (user_id, name) VALUES (?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, teacher.getUserId());
            stmt.setString(2, teacher.getName());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    teacher.setId(rs.getInt(1));
                }
            }
            return teacher;
        } catch (SQLException e) {
            throw new RuntimeException("Error saving teacher", e);
        }
    }

    @Override
    public Optional<Teacher> findById(Integer id) {
        String sql = "SELECT * FROM teachers WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToTeacher(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding teacher by id", e);
        }
    }

    @Override
    public List<Teacher> findAll() {
        String sql = "SELECT * FROM teachers";
        List<Teacher> teachers = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                teachers.add(mapRowToTeacher(rs));
            }
            return teachers;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all teachers", e);
        }
    }

    @Override
    public Teacher update(Teacher teacher) {
        String sql = "UPDATE teachers SET user_id = ?, name = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, teacher.getUserId());
            stmt.setString(2, teacher.getName());
            stmt.setInt(3, teacher.getId());
            stmt.executeUpdate();
            return teacher;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating teacher", e);
        }
    }

    @Override
    public void deleteById(Integer id) {
        String sql = "DELETE FROM teachers WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting teacher", e);
        }
    }

    @Override
    public boolean existsById(Integer id) {
        String sql = "SELECT COUNT(*) FROM teachers WHERE id = ?";
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
            throw new RuntimeException("Error checking teacher existence", e);
        }
    }

    public Optional<Teacher> findByUserId(int userId) {
        String sql = "SELECT * FROM teachers WHERE user_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToTeacher(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding teacher by user id", e);
        }
    }

    private Teacher mapRowToTeacher(ResultSet rs) throws SQLException {
        return new Teacher(
                rs.getInt("id"),
                rs.getInt("user_id"),
                rs.getString("name")
        );
    }
}
