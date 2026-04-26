package ru.filden.dao;

import ru.filden.db.dbConnection;
import ru.filden.entity.Group;
import ru.filden.impl.BaseDAO;
import ru.filden.utils.Cache;

import java.sql.*;
import java.util.*;
public class GroupDAO implements BaseDAO<Group, Integer> {
    private final dbConnection dataSource;
    private final Cache<Group> cache;

    public GroupDAO(dbConnection dataSource) {
        this.dataSource = dataSource;
        this.cache = new Cache<>();
        loadAllToCache();
    }

    private void loadAllToCache() {
        String sql = "SELECT * FROM groups";
        Map<Integer, Group> groupMap = new HashMap<>();
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Group group = mapRowToGroup(rs);
                groupMap.put(group.getId(), group);
            }
            cache.loadCache(groupMap);
        } catch (SQLException e) {
            throw new RuntimeException("Error loading groups to cache", e);
        }
    }

    @Override
    public Group save(Group group) {
        String sql = "INSERT INTO groups (name) VALUES (?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, group.getName());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    group.setId(rs.getInt(1));
                    Map<Integer, Group> currentCache = cache.getFromCache();
                    if (currentCache != null) {
                        currentCache.put(group.getId(), group);
                        cache.loadCache(currentCache);
                    }
                }
            }
            return group;
        } catch (SQLException e) {
            throw new RuntimeException("Error saving group", e);
        }
    }

    @Override
    public Optional<Group> findById(Integer id) {
        if (cache.isCacheLoaded() && cache.containsKey(id)) {
            return Optional.ofNullable(cache.getById(id));
        }

        String sql = "SELECT * FROM groups WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToGroup(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding group by id", e);
        }
    }

    @Override
    public List<Group> findAll() {
        if (cache.isCacheLoaded()) {
            return new ArrayList<>(cache.getFromCache().values());
        }

        String sql = "SELECT * FROM groups";
        List<Group> groups = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                groups.add(mapRowToGroup(rs));
            }
            return groups;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all groups", e);
        }
    }

    @Override
    public Group update(Group group) {
        String sql = "UPDATE groups SET name = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, group.getName());
            stmt.setInt(2, group.getId());
            stmt.executeUpdate();


            if (cache.isCacheLoaded()) {
                Map<Integer, Group> currentCache = cache.getFromCache();
                currentCache.put(group.getId(), group);
                cache.loadCache(currentCache);
            }
            return group;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating group", e);
        }
    }

    @Override
    public void deleteById(Integer id) {
        String sql = "DELETE FROM groups WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();

            // Удаляем из кеша
            if (cache.isCacheLoaded()) {
                Map<Integer, Group> currentCache = cache.getFromCache();
                currentCache.remove(id);
                cache.loadCache(currentCache);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting group", e);
        }
    }

    @Override
    public boolean existsById(Integer id) {
        if (cache.isCacheLoaded() && cache.containsKey(id)) {
            return true;
        }

        String sql = "SELECT COUNT(*) FROM groups WHERE id = ?";
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
            throw new RuntimeException("Error checking group existence", e);
        }
    }

    public Optional<Group> findByName(String name) {
        if (cache.isCacheLoaded()) {
            return cache.getFromCache().values().stream()
                    .filter(g -> g.getName().equals(name))
                    .findFirst();
        }

        String sql = "SELECT * FROM groups WHERE name = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToGroup(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding group by name", e);
        }
    }

    public void refreshCache() {
        loadAllToCache();
    }

    private Group mapRowToGroup(ResultSet rs) throws SQLException {
        return new Group(
                rs.getInt("id"),
                rs.getString("name")
        );
    }
}