package ru.filden.dao;

import ru.filden.db.dbConnection;
import ru.filden.entity.TeacherGroup;
import ru.filden.impl.BaseDAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TeacherGroupDAO implements BaseDAO<TeacherGroup, Integer> {
    private final dbConnection dataSource;

    public TeacherGroupDAO(dbConnection dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public TeacherGroup save(TeacherGroup teacherGroup) {
        String sql = "INSERT INTO teacher_groups (teacher_id, group_id) VALUES (?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, teacherGroup.getTeacherId());
            stmt.setInt(2, teacherGroup.getGroupId());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    teacherGroup.setId(rs.getInt(1));
                }
            }
            return teacherGroup;
        } catch (SQLException e) {
            throw new RuntimeException("Error saving teacher group", e);
        }
    }

    @Override
    public Optional<TeacherGroup> findById(Integer id) {
        String sql = "SELECT * FROM teacher_groups WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToTeacherGroup(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding teacher group by id", e);
        }
    }

    @Override
    public List<TeacherGroup> findAll() {
        String sql = "SELECT * FROM teacher_groups";
        List<TeacherGroup> teacherGroups = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                teacherGroups.add(mapRowToTeacherGroup(rs));
            }
            return teacherGroups;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all teacher groups", e);
        }
    }

    @Override
    public TeacherGroup update(TeacherGroup teacherGroup) {
        String sql = "UPDATE teacher_groups SET teacher_id = ?, group_id = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, teacherGroup.getTeacherId());
            stmt.setInt(2, teacherGroup.getGroupId());
            stmt.setInt(3, teacherGroup.getId());
            stmt.executeUpdate();
            return teacherGroup;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating teacher group", e);
        }
    }

    @Override
    public void deleteById(Integer id) {
        String sql = "DELETE FROM teacher_groups WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting teacher group", e);
        }
    }

    @Override
    public boolean existsById(Integer id) {
        String sql = "SELECT COUNT(*) FROM teacher_groups WHERE id = ?";
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
            throw new RuntimeException("Error checking teacher group existence", e);
        }
    }

    public List<TeacherGroup> findByTeacherId(int teacherId) {
        String sql = "SELECT * FROM teacher_groups WHERE teacher_id = ?";
        List<TeacherGroup> teacherGroups = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, teacherId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    teacherGroups.add(mapRowToTeacherGroup(rs));
                }
            }
            return teacherGroups;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding teacher groups by teacher", e);
        }
    }

    public List<TeacherGroup> findByGroupId(int groupId) {
        String sql = "SELECT * FROM teacher_groups WHERE group_id = ?";
        List<TeacherGroup> teacherGroups = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, groupId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    teacherGroups.add(mapRowToTeacherGroup(rs));
                }
            }
            return teacherGroups;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding teacher groups by group", e);
        }
    }

    public void deleteByTeacherAndGroup(int teacherId, int groupId) {
        String sql = "DELETE FROM teacher_groups WHERE teacher_id = ? AND group_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, teacherId);
            stmt.setInt(2, groupId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting teacher group", e);
        }
    }

    private TeacherGroup mapRowToTeacherGroup(ResultSet rs) throws SQLException {
        return new TeacherGroup(
                rs.getInt("id"),
                rs.getInt("teacher_id"),
                rs.getInt("group_id")
        );
    }
}
