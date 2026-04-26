package ru.filden.dao;

import ru.filden.db.dbConnection;
import ru.filden.entity.Duty_History;
import ru.filden.impl.BaseDAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DutyHistoryDAO implements BaseDAO<Duty_History, Integer> {
    private final dbConnection dataSource;

    public DutyHistoryDAO(dbConnection dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Duty_History save(Duty_History dutyHistory) {
        String sql = "INSERT INTO duty_history (f_student_id, s_student_id, group_id) VALUES (?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, dutyHistory.getFirstStudentId());
            if (dutyHistory.getSecondStudentId() != null) {
                stmt.setInt(2, dutyHistory.getSecondStudentId());
            } else {
                stmt.setNull(2, Types.INTEGER);
            }
            stmt.setInt(3, dutyHistory.getGroupId());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    dutyHistory.setId(rs.getInt(1));
                }
            }
            return dutyHistory;
        } catch (SQLException e) {
            throw new RuntimeException("Error saving duty history", e);
        }
    }

    @Override
    public Optional<Duty_History> findById(Integer id) {
        String sql = "SELECT * FROM duty_history WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToDutyHistory(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding duty history by id", e);
        }
    }

    @Override
    public List<Duty_History> findAll() {
        String sql = "SELECT * FROM duty_history ORDER BY id DESC";
        List<Duty_History> histories = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                histories.add(mapRowToDutyHistory(rs));
            }
            return histories;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all duty histories", e);
        }
    }

    @Override
    public Duty_History update(Duty_History dutyHistory) {
        String sql = "UPDATE duty_history SET f_student_id = ?, s_student_id = ?, group_id = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, dutyHistory.getFirstStudentId());
            if (dutyHistory.getSecondStudentId() != null) {
                stmt.setInt(2, dutyHistory.getSecondStudentId());
            } else {
                stmt.setNull(2, Types.INTEGER);
            }
            stmt.setInt(3, dutyHistory.getGroupId());
            stmt.setInt(4, dutyHistory.getId());
            stmt.executeUpdate();
            return dutyHistory;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating duty history", e);
        }
    }

    @Override
    public void deleteById(Integer id) {
        String sql = "DELETE FROM duty_history WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting duty history", e);
        }
    }

    @Override
    public boolean existsById(Integer id) {
        String sql = "SELECT COUNT(*) FROM duty_history WHERE id = ?";
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
            throw new RuntimeException("Error checking duty history existence", e);
        }
    }

    public List<Duty_History> findByGroupId(int groupId) {
        String sql = "SELECT * FROM duty_history WHERE group_id = ? ORDER BY id DESC";
        List<Duty_History> histories = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, groupId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    histories.add(mapRowToDutyHistory(rs));
                }
            }
            return histories;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding duty histories by group", e);
        }
    }

    public List<Duty_History> findByStudentId(int studentId) {
        String sql = "SELECT * FROM duty_history WHERE f_student_id = ? OR s_student_id = ? ORDER BY id DESC";
        List<Duty_History> histories = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            stmt.setInt(2, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    histories.add(mapRowToDutyHistory(rs));
                }
            }
            return histories;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding duty histories by student", e);
        }
    }

    public List<Duty_History> getLatestDutyForGroup(int groupId, int limit) {
        String sql = "SELECT TOP (?) * FROM duty_history WHERE group_id = ? ORDER BY id DESC";
        List<Duty_History> histories = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            stmt.setInt(2, groupId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    histories.add(mapRowToDutyHistory(rs));
                }
            }
            return histories;
        } catch (SQLException e) {
            throw new RuntimeException("Error getting latest duty for group", e);
        }
    }

    private Duty_History mapRowToDutyHistory(ResultSet rs) throws SQLException {
        Duty_History history = new Duty_History();
        history.setId(rs.getInt("id"));
        history.setFirstStudentId(rs.getInt("f_student_id"));

        int secondStudentId = rs.getInt("s_student_id");
        if (!rs.wasNull()) {
            history.setSecondStudentId(secondStudentId);
        }

        history.setGroupId(rs.getInt("group_id"));
        return history;
    }
}
