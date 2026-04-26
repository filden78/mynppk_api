package ru.filden.dao;

import ru.filden.db.dbConnection;
import ru.filden.entity.Role;
import ru.filden.impl.BaseDAO;
import ru.filden.utils.Cache;

import java.sql.*;
import java.util.*;

public class RoleDAO implements BaseDAO<Role, Integer> {
    private final dbConnection dataSource;
    private final Cache<Role> cache;

    public RoleDAO(dbConnection dataSource) {
        this.dataSource = dataSource;
        this.cache = new Cache<>();
        loadAllToCache();
    }

    private void loadAllToCache() {
        String sql = "SELECT * FROM role";
        Map<Integer, Role> roleMap = new HashMap<>();
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Role role = mapRowToRole(rs);
                roleMap.put(role.getId(), role);
            }
            cache.loadCache(roleMap);
        } catch (SQLException e) {
            throw new RuntimeException("Error loading roles to cache", e);
        }
    }

    @Override
    public Role save(Role role) {
        String sql = "INSERT INTO role (name) VALUES (?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, role.getName());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    role.setId(rs.getInt(1));
                    // Обновляем кеш
                    Map<Integer, Role> currentCache = cache.getFromCache();
                    if (currentCache != null) {
                        currentCache.put(role.getId(), role);
                        cache.loadCache(currentCache);
                    }
                }
            }
            return role;
        } catch (SQLException e) {
            throw new RuntimeException("Error saving role", e);
        }
    }

    @Override
    public Optional<Role> findById(Integer id) {
        // Сначала проверяем кеш
        if (cache.isCacheLoaded() && cache.containsKey(id)) {
            return Optional.ofNullable(cache.getById(id));
        }

        String sql = "SELECT * FROM role WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToRole(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding role by id", e);
        }
    }

    @Override
    public List<Role> findAll() {
        // Возвращаем из кеша, если он загружен
        if (cache.isCacheLoaded()) {
            return new ArrayList<>(cache.getFromCache().values());
        }

        String sql = "SELECT * FROM role";
        List<Role> roles = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                roles.add(mapRowToRole(rs));
            }
            return roles;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all roles", e);
        }
    }

    @Override
    public Role update(Role role) {
        String sql = "UPDATE role SET name = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, role.getName());
            stmt.setInt(2, role.getId());
            stmt.executeUpdate();

            // Обновляем кеш
            if (cache.isCacheLoaded()) {
                Map<Integer, Role> currentCache = cache.getFromCache();
                currentCache.put(role.getId(), role);
                cache.loadCache(currentCache);
            }
            return role;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating role", e);
        }
    }

    @Override
    public void deleteById(Integer id) {
        String sql = "DELETE FROM role WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();

            // Удаляем из кеша
            if (cache.isCacheLoaded()) {
                Map<Integer, Role> currentCache = cache.getFromCache();
                currentCache.remove(id);
                cache.loadCache(currentCache);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting role", e);
        }
    }

    @Override
    public boolean existsById(Integer id) {
        if (cache.isCacheLoaded() && cache.containsKey(id)) {
            return true;
        }

        String sql = "SELECT COUNT(*) FROM role WHERE id = ?";
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
            throw new RuntimeException("Error checking role existence", e);
        }
    }

    public void refreshCache() {
        loadAllToCache();
    }

    private Role mapRowToRole(ResultSet rs) throws SQLException {
        return new Role(
                rs.getInt("id"),
                rs.getString("name")
        );
    }
}
