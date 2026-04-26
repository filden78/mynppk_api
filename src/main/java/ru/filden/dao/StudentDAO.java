package ru.filden.dao;

import ru.filden.db.dbConnection;
import ru.filden.entity.Student;
import ru.filden.impl.BaseDAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StudentDAO implements BaseDAO<Student, Integer> {
    private final dbConnection dataSource;

    public StudentDAO(dbConnection dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Student save(Student student) {
        String sql = "INSERT INTO students (user_id, name, group_h, is_duty, count_duty) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, student.getUserId());
            stmt.setString(2, student.getName());
            stmt.setInt(3, student.getGroupId());
            stmt.setBoolean(4, student.isDuty());
            if (student.getCountDuty() != null) {
                stmt.setInt(5, student.getCountDuty());
            } else {
                stmt.setNull(5, Types.INTEGER);
            }
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    student.setId(rs.getInt(1));
                }
            }
            return student;
        } catch (SQLException e) {
            throw new RuntimeException("Error saving student", e);
        }
    }

    @Override
    public Optional<Student> findById(Integer id) {
        String sql = "SELECT * FROM students WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToStudent(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding student by id", e);
        }
    }

    @Override
    public List<Student> findAll() {
        String sql = "SELECT * FROM students";
        List<Student> students = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                students.add(mapRowToStudent(rs));
            }
            return students;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all students", e);
        }
    }

    @Override
    public Student update(Student student) {
        String sql = "UPDATE students SET user_id = ?, name = ?, group_h = ?, is_duty = ?, count_duty = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, student.getUserId());
            stmt.setString(2, student.getName());
            stmt.setInt(3, student.getGroupId());
            stmt.setBoolean(4, student.isDuty());
            if (student.getCountDuty() != null) {
                stmt.setInt(5, student.getCountDuty());
            } else {
                stmt.setNull(5, Types.INTEGER);
            }
            stmt.setInt(6, student.getId());
            stmt.executeUpdate();
            return student;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating student", e);
        }
    }

    @Override
    public void deleteById(Integer id) {
        String sql = "DELETE FROM students WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting student", e);
        }
    }

    @Override
    public boolean existsById(Integer id) {
        String sql = "SELECT COUNT(*) FROM students WHERE id = ?";
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
            throw new RuntimeException("Error checking student existence", e);
        }
    }

    public List<Student> findByGroupId(int groupId) {
        String sql = "SELECT * FROM students WHERE group_h = ?";
        List<Student> students = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, groupId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    students.add(mapRowToStudent(rs));
                }
            }
            return students;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding students by group", e);
        }
    }

    public List<Student> findActiveDutyStudents() {
        String sql = "SELECT * FROM students WHERE is_duty = 1";
        List<Student> students = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                students.add(mapRowToStudent(rs));
            }
            return students;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding active duty students", e);
        }
    }

    public void incrementDutyCount(int studentId) {
        String sql = "UPDATE students SET count_duty = COALESCE(count_duty, 0) + 1 WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error incrementing duty count", e);
        }
    }

    public void setDutyStatus(int studentId, boolean isDuty) {
        String sql = "UPDATE students SET is_duty = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBoolean(1, isDuty);
            stmt.setInt(2, studentId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating duty status", e);
        }
    }

    private Student mapRowToStudent(ResultSet rs) throws SQLException {
        Student student = new Student();
        student.setId(rs.getInt("id"));
        student.setUserId(rs.getInt("user_id"));
        student.setName(rs.getString("name"));
        student.setGroupId(rs.getInt("group_h"));
        student.setDuty(rs.getBoolean("is_duty"));

        int countDuty = rs.getInt("count_duty");
        if (!rs.wasNull()) {
            student.setCountDuty(countDuty);
        }

        return student;
    }
}
