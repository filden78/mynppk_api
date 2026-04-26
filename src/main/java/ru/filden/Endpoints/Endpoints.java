package ru.filden.Endpoints;

import ru.filden.dao.*;
import ru.filden.entity.*;
import ru.filden.utils.JsonUtil;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Spark;

import java.util.List;
import java.util.Map;

public class Endpoints {
    private static final Logger logger = LoggerFactory.getLogger(Endpoints.class);

    private final RoleDAO roleDAO;
    private final UserDAO userDAO;
    private final StudentDAO studentDAO;
    private final GroupDAO groupDAO;
    private final TeacherDAO teacherDAO;
    private final DutyHistoryDAO dutyHistoryDAO;
    private final TeacherGroupDAO teacherGroupDAO;

    public Endpoints(RoleDAO roleDAO, UserDAO userDAO, StudentDAO studentDAO,
                         GroupDAO groupDAO, TeacherDAO teacherDAO,
                         DutyHistoryDAO dutyHistoryDAO, TeacherGroupDAO teacherGroupDAO) {
        this.roleDAO = roleDAO;
        this.userDAO = userDAO;
        this.studentDAO = studentDAO;
        this.groupDAO = groupDAO;
        this.teacherDAO = teacherDAO;
        this.dutyHistoryDAO = dutyHistoryDAO;
        this.teacherGroupDAO = teacherGroupDAO;
    }

    public void registerEndpoints() {
        registerRoleEndpoints();
        registerUserEndpoints();
        registerStudentEndpoints();
        registerGroupEndpoints();
        registerTeacherEndpoints();
        registerDutyHistoryEndpoints();
        registerTeacherGroupEndpoints();

        // Health check endpoint
        Spark.get("/api/health", (req, res) -> {
            logger.info("GET /api/health - Health check");
            JsonUtil.setJsonResponse(res);
            JsonObject response = new JsonObject();
            response.addProperty("status", "OK");
            response.addProperty("timestamp", System.currentTimeMillis());
            return JsonUtil.toJson(response);
        });
    }

    private void registerRoleEndpoints() {
        // GET /api/roles - получить все роли
        Spark.get("/api/roles", (req, res) -> {
            logger.info("GET /api/roles - Fetching all roles");
            JsonUtil.setJsonResponse(res);
            try {
                List<Role> roles = roleDAO.findAll();
                res.status(200);
                return JsonUtil.successResponse(roles);
            } catch (Exception e) {
                logger.error("Error fetching roles", e);
                res.status(500);
                return JsonUtil.errorResponse("Database error: " + e.getMessage(), 500);
            }
        });

        // GET /api/roles/:id - получить роль по ID
        Spark.get("/api/roles/:id", (req, res) -> {
            int id = Integer.parseInt(req.params(":id"));
            logger.info("GET /api/roles/{} - Fetching role by ID", id);
            JsonUtil.setJsonResponse(res);
            try {
                java.util.Optional<Role> role = roleDAO.findById(id);
                if (role.isPresent()) {
                    res.status(200);
                    return JsonUtil.successResponse(role.get());
                } else {
                    res.status(404);
                    return JsonUtil.errorResponse("Role not found with id: " + id, 404);
                }
            } catch (Exception e) {
                logger.error("Error fetching role by id: {}", id, e);
                res.status(500);
                return JsonUtil.errorResponse("Database error: " + e.getMessage(), 500);
            }
        });

        // POST /api/roles - создать новую роль
        Spark.post("/api/roles", (req, res) -> {
            logger.info("POST /api/roles - Creating new role");
            JsonUtil.setJsonResponse(res);
            try {
                Role roleData = JsonUtil.fromJson(req.body(), Role.class);

                if (roleData.getName() == null || roleData.getName().isEmpty()) {
                    res.status(400);
                    return JsonUtil.errorResponse("Role name is required", 400);
                }

                Role created = roleDAO.save(roleData);
                res.status(201);
                return JsonUtil.successResponse(created);
            } catch (Exception e) {
                logger.error("Error creating role", e);
                res.status(500);
                return JsonUtil.errorResponse("Database error: " + e.getMessage(), 500);
            }
        });

        // PUT /api/roles/:id - обновить роль
        Spark.put("/api/roles/:id", (req, res) -> {
            int id = Integer.parseInt(req.params(":id"));
            logger.info("PUT /api/roles/{} - Updating role", id);
            JsonUtil.setJsonResponse(res);
            try {
                Role roleData = JsonUtil.fromJson(req.body(), Role.class);
                roleData.setId(id);

                if (roleData.getName() == null || roleData.getName().isEmpty()) {
                    res.status(400);
                    return JsonUtil.errorResponse("Role name is required", 400);
                }

                Role updated = roleDAO.update(roleData);
                res.status(200);
                return JsonUtil.successResponse(updated);
            } catch (Exception e) {
                logger.error("Error updating role: {}", id, e);
                res.status(500);
                return JsonUtil.errorResponse("Database error: " + e.getMessage(), 500);
            }
        });

        // DELETE /api/roles/:id - удалить роль
        Spark.delete("/api/roles/:id", (req, res) -> {
            int id = Integer.parseInt(req.params(":id"));
            logger.info("DELETE /api/roles/{} - Deleting role", id);
            JsonUtil.setJsonResponse(res);
            try {
                roleDAO.deleteById(id);
                res.status(200);
                return JsonUtil.successResponse(Map.of("message", "Role deleted successfully"));
            } catch (Exception e) {
                logger.error("Error deleting role: {}", id, e);
                res.status(500);
                return JsonUtil.errorResponse("Database error: " + e.getMessage(), 500);
            }
        });

        // POST /api/roles/cache/refresh - обновить кеш ролей
        Spark.post("/api/roles/cache/refresh", (req, res) -> {
            logger.info("POST /api/roles/cache/refresh - Refreshing role cache");
            JsonUtil.setJsonResponse(res);
            try {
                roleDAO.refreshCache();
                res.status(200);
                return JsonUtil.successResponse(Map.of("message", "Role cache refreshed successfully"));
            } catch (Exception e) {
                logger.error("Error refreshing role cache", e);
                res.status(500);
                return JsonUtil.errorResponse("Error refreshing cache: " + e.getMessage(), 500);
            }
        });
    }

    private void registerUserEndpoints() {
        // GET /api/users - получить всех пользователей
        Spark.get("/api/users", (req, res) -> {
            logger.info("GET /api/users - Fetching all users");
            JsonUtil.setJsonResponse(res);
            try {
                List<User> users = userDAO.findAll();
                res.status(200);
                return JsonUtil.successResponse(users);
            } catch (Exception e) {
                logger.error("Error fetching users", e);
                res.status(500);
                return JsonUtil.errorResponse("Database error: " + e.getMessage(), 500);
            }
        });

        // GET /api/users/:id - получить пользователя по ID
        Spark.get("/api/users/:id", (req, res) -> {
            int id = Integer.parseInt(req.params(":id"));
            logger.info("GET /api/users/{} - Fetching user by ID", id);
            JsonUtil.setJsonResponse(res);
            try {
                java.util.Optional<User> user = userDAO.findById(id);
                if (user.isPresent()) {
                    res.status(200);
                    return JsonUtil.successResponse(user.get());
                } else {
                    res.status(404);
                    return JsonUtil.errorResponse("User not found with id: " + id, 404);
                }
            } catch (Exception e) {
                logger.error("Error fetching user by id: {}", id, e);
                res.status(500);
                return JsonUtil.errorResponse("Database error: " + e.getMessage(), 500);
            }
        });

        // GET /api/users/login/:login - получить пользователя по логину
        Spark.get("/api/users/login/:login", (req, res) -> {
            String login = req.params(":login");
            logger.info("GET /api/users/login/{} - Fetching user by login", login);
            JsonUtil.setJsonResponse(res);
            try {
                java.util.Optional<User> user = userDAO.findByLogin(login);
                if (user.isPresent()) {
                    res.status(200);
                    return JsonUtil.successResponse(user.get());
                } else {
                    res.status(404);
                    return JsonUtil.errorResponse("User not found with login: " + login, 404);
                }
            } catch (Exception e) {
                logger.error("Error fetching user by login: {}", login, e);
                res.status(500);
                return JsonUtil.errorResponse("Database error: " + e.getMessage(), 500);
            }
        });

        // POST /api/users - создать нового пользователя
        Spark.post("/api/users", (req, res) -> {
            logger.info("POST /api/users - Creating new user");
            JsonUtil.setJsonResponse(res);
            try {
                User userData = JsonUtil.fromJson(req.body(), User.class);

                if (userData.getLogin() == null || userData.getPassword() == null) {
                    res.status(400);
                    return JsonUtil.errorResponse("Login and password are required", 400);
                }

                if (userData.getRole() == null) {
                    userData.setRole(1);
                }

                User created = userDAO.save(userData);
                res.status(201);
                return JsonUtil.successResponse(created);
            } catch (Exception e) {
                logger.error("Error creating user", e);
                if (e.getMessage().contains("duplicate")) {
                    res.status(409);
                    return JsonUtil.errorResponse("User with this login already exists", 409);
                }
                res.status(500);
                return JsonUtil.errorResponse("Database error: " + e.getMessage(), 500);
            }
        });

        // PUT /api/users/:id - обновить пользователя
        Spark.put("/api/users/:id", (req, res) -> {
            int id = Integer.parseInt(req.params(":id"));
            logger.info("PUT /api/users/{} - Updating user", id);
            JsonUtil.setJsonResponse(res);
            try {
                User userData = JsonUtil.fromJson(req.body(), User.class);
                userData.setId(id);

                User updated = userDAO.update(userData);
                res.status(200);
                return JsonUtil.successResponse(updated);
            } catch (Exception e) {
                logger.error("Error updating user: {}", id, e);
                res.status(500);
                return JsonUtil.errorResponse("Database error: " + e.getMessage(), 500);
            }
        });

        // DELETE /api/users/:id - удалить пользователя
        Spark.delete("/api/users/:id", (req, res) -> {
            int id = Integer.parseInt(req.params(":id"));
            logger.info("DELETE /api/users/{} - Deleting user", id);
            JsonUtil.setJsonResponse(res);
            try {
                userDAO.deleteById(id);
                res.status(200);
                return JsonUtil.successResponse(Map.of("message", "User deleted successfully"));
            } catch (Exception e) {
                logger.error("Error deleting user: {}", id, e);
                res.status(500);
                return JsonUtil.errorResponse("Database error: " + e.getMessage(), 500);
            }
        });

        // POST /api/users/authenticate - аутентификация
        Spark.post("/api/users/authenticate", (req, res) -> {
            logger.info("POST /api/users/authenticate - Authenticating user");
            JsonUtil.setJsonResponse(res);
            try {
                JsonObject authData = JsonUtil.fromJson(req.body(), JsonObject.class);
                String login = authData.get("login").getAsString();
                String password = authData.get("password").getAsString();

                boolean authenticated = userDAO.authenticate(login, password);
                if (authenticated) {
                    java.util.Optional<User> user = userDAO.findByLogin(login);
                    res.status(200);
                    return JsonUtil.successResponse(Map.of(
                            "authenticated", true,
                            "user", user.orElse(null)
                    ));
                } else {
                    res.status(401);
                    return JsonUtil.errorResponse("Invalid credentials", 401);
                }
            } catch (Exception e) {
                logger.error("Error during authentication", e);
                res.status(500);
                return JsonUtil.errorResponse("Authentication error: " + e.getMessage(), 500);
            }
        });
    }

    private void registerStudentEndpoints() {
        // GET /api/students - получить всех студентов
        Spark.get("/api/students", (req, res) -> {
            logger.info("GET /api/students - Fetching all students");
            JsonUtil.setJsonResponse(res);
            try {
                List<Student> students = studentDAO.findAll();
                res.status(200);
                return JsonUtil.successResponse(students);
            } catch (Exception e) {
                logger.error("Error fetching students", e);
                res.status(500);
                return JsonUtil.errorResponse("Database error: " + e.getMessage(), 500);
            }
        });

        // GET /api/students/:id - получить студента по ID
        Spark.get("/api/students/:id", (req, res) -> {
            int id = Integer.parseInt(req.params(":id"));
            logger.info("GET /api/students/{} - Fetching student by ID", id);
            JsonUtil.setJsonResponse(res);
            try {
                java.util.Optional<Student> student = studentDAO.findById(id);
                if (student.isPresent()) {
                    res.status(200);
                    return JsonUtil.successResponse(student.get());
                } else {
                    res.status(404);
                    return JsonUtil.errorResponse("Student not found with id: " + id, 404);
                }
            } catch (Exception e) {
                logger.error("Error fetching student by id: {}", id, e);
                res.status(500);
                return JsonUtil.errorResponse("Database error: " + e.getMessage(), 500);
            }
        });

        // GET /api/students/group/:groupId - получить студентов по группе
        Spark.get("/api/students/group/:groupId", (req, res) -> {
            int groupId = Integer.parseInt(req.params(":groupId"));
            logger.info("GET /api/students/group/{} - Fetching students by group", groupId);
            JsonUtil.setJsonResponse(res);
            try {
                List<Student> students = studentDAO.findByGroupId(groupId);
                res.status(200);
                return JsonUtil.successResponse(students);
            } catch (Exception e) {
                logger.error("Error fetching students by group: {}", groupId, e);
                res.status(500);
                return JsonUtil.errorResponse("Database error: " + e.getMessage(), 500);
            }
        });

        // GET /api/students/duty/active - получить активных дежурных студентов
        Spark.get("/api/students/duty/active", (req, res) -> {
            logger.info("GET /api/students/duty/active - Fetching active duty students");
            JsonUtil.setJsonResponse(res);
            try {
                List<Student> dutyStudents = studentDAO.findActiveDutyStudents();
                res.status(200);
                return JsonUtil.successResponse(dutyStudents);
            } catch (Exception e) {
                logger.error("Error fetching duty students", e);
                res.status(500);
                return JsonUtil.errorResponse("Database error: " + e.getMessage(), 500);
            }
        });

        // POST /api/students - создать студента
        Spark.post("/api/students", (req, res) -> {
            logger.info("POST /api/students - Creating new student");
            JsonUtil.setJsonResponse(res);
            try {
                Student studentData = JsonUtil.fromJson(req.body(), Student.class);

                if (studentData.getName() == null || studentData.getUserId() == 0 || studentData.getGroupId() == 0) {
                    res.status(400);
                    return JsonUtil.errorResponse("Name, userId and groupId are required", 400);
                }

                if (studentData.getCountDuty() == null) {
                    studentData.setCountDuty(0);
                }

                Student created = studentDAO.save(studentData);
                res.status(201);
                return JsonUtil.successResponse(created);
            } catch (Exception e) {
                logger.error("Error creating student", e);
                res.status(500);
                return JsonUtil.errorResponse("Database error: " + e.getMessage(), 500);
            }
        });

        // PUT /api/students/:id - обновить студента
        Spark.put("/api/students/:id", (req, res) -> {
            int id = Integer.parseInt(req.params(":id"));
            logger.info("PUT /api/students/{} - Updating student", id);
            JsonUtil.setJsonResponse(res);
            try {
                Student studentData = JsonUtil.fromJson(req.body(), Student.class);
                studentData.setId(id);

                Student updated = studentDAO.update(studentData);
                res.status(200);
                return JsonUtil.successResponse(updated);
            } catch (Exception e) {
                logger.error("Error updating student: {}", id, e);
                res.status(500);
                return JsonUtil.errorResponse("Database error: " + e.getMessage(), 500);
            }
        });

        // PATCH /api/students/:id/duty/increment - увеличить счетчик дежурств
        Spark.patch("/api/students/:id/duty/increment", (req, res) -> {
            int id = Integer.parseInt(req.params(":id"));
            logger.info("PATCH /api/students/{}/duty/increment - Incrementing duty count", id);
            JsonUtil.setJsonResponse(res);
            try {
                studentDAO.incrementDutyCount(id);
                res.status(200);
                return JsonUtil.successResponse(Map.of("message", "Duty count incremented successfully"));
            } catch (Exception e) {
                logger.error("Error incrementing duty count: {}", id, e);
                res.status(500);
                return JsonUtil.errorResponse("Database error: " + e.getMessage(), 500);
            }
        });

        // PATCH /api/students/:id/duty/status - изменить статус дежурства
        Spark.patch("/api/students/:id/duty/status", (req, res) -> {
            int id = Integer.parseInt(req.params(":id"));
            logger.info("PATCH /api/students/{}/duty/status - Changing duty status", id);
            JsonUtil.setJsonResponse(res);
            try {
                JsonObject request = JsonUtil.fromJson(req.body(), JsonObject.class);
                boolean isDuty = request.get("isDuty").getAsBoolean();

                studentDAO.setDutyStatus(id, isDuty);
                res.status(200);
                return JsonUtil.successResponse(Map.of("message", "Duty status changed successfully"));
            } catch (Exception e) {
                logger.error("Error changing duty status: {}", id, e);
                res.status(500);
                return JsonUtil.errorResponse("Database error: " + e.getMessage(), 500);
            }
        });

        // DELETE /api/students/:id - удалить студента
        Spark.delete("/api/students/:id", (req, res) -> {
            int id = Integer.parseInt(req.params(":id"));
            logger.info("DELETE /api/students/{} - Deleting student", id);
            JsonUtil.setJsonResponse(res);
            try {
                studentDAO.deleteById(id);
                res.status(200);
                return JsonUtil.successResponse(Map.of("message", "Student deleted successfully"));
            } catch (Exception e) {
                logger.error("Error deleting student: {}", id, e);
                res.status(500);
                return JsonUtil.errorResponse("Database error: " + e.getMessage(), 500);
            }
        });
    }

    private void registerGroupEndpoints() {
        // GET /api/groups - получить все группы
        Spark.get("/api/groups", (req, res) -> {
            logger.info("GET /api/groups - Fetching all groups");
            JsonUtil.setJsonResponse(res);
            try {
                List<Group> groups = groupDAO.findAll();
                res.status(200);
                return JsonUtil.successResponse(groups);
            } catch (Exception e) {
                logger.error("Error fetching groups", e);
                res.status(500);
                return JsonUtil.errorResponse("Database error: " + e.getMessage(), 500);
            }
        });

        // GET /api/groups/:id - получить группу по ID
        Spark.get("/api/groups/:id", (req, res) -> {
            int id = Integer.parseInt(req.params(":id"));
            logger.info("GET /api/groups/{} - Fetching group by ID", id);
            JsonUtil.setJsonResponse(res);
            try {
                java.util.Optional<Group> group = groupDAO.findById(id);
                if (group.isPresent()) {
                    res.status(200);
                    return JsonUtil.successResponse(group.get());
                } else {
                    res.status(404);
                    return JsonUtil.errorResponse("Group not found with id: " + id, 404);
                }
            } catch (Exception e) {
                logger.error("Error fetching group by id: {}", id, e);
                res.status(500);
                return JsonUtil.errorResponse("Database error: " + e.getMessage(), 500);
            }
        });

        // GET /api/groups/name/:name - получить группу по имени
        Spark.get("/api/groups/name/:name", (req, res) -> {
            String name = req.params(":name");
            logger.info("GET /api/groups/name/{} - Fetching group by name", name);
            JsonUtil.setJsonResponse(res);
            try {
                java.util.Optional<Group> group = groupDAO.findByName(name);
                if (group.isPresent()) {
                    res.status(200);
                    return JsonUtil.successResponse(group.get());
                } else {
                    res.status(404);
                    return JsonUtil.errorResponse("Group not found with name: " + name, 404);
                }
            } catch (Exception e) {
                logger.error("Error fetching group by name: {}", name, e);
                res.status(500);
                return JsonUtil.errorResponse("Database error: " + e.getMessage(), 500);
            }
        });

        // POST /api/groups - создать группу
        Spark.post("/api/groups", (req, res) -> {
            logger.info("POST /api/groups - Creating new group");
            JsonUtil.setJsonResponse(res);
            try {
                Group groupData = JsonUtil.fromJson(req.body(), Group.class);

                if (groupData.getName() == null || groupData.getName().isEmpty()) {
                    res.status(400);
                    return JsonUtil.errorResponse("Group name is required", 400);
                }

                Group created = groupDAO.save(groupData);
                res.status(201);
                return JsonUtil.successResponse(created);
            } catch (Exception e) {
                logger.error("Error creating group", e);
                if (e.getMessage().contains("duplicate")) {
                    res.status(409);
                    return JsonUtil.errorResponse("Group with this name already exists", 409);
                }
                res.status(500);
                return JsonUtil.errorResponse("Database error: " + e.getMessage(), 500);
            }
        });

        // PUT /api/groups/:id - обновить группу
        Spark.put("/api/groups/:id", (req, res) -> {
            int id = Integer.parseInt(req.params(":id"));
            logger.info("PUT /api/groups/{} - Updating group", id);
            JsonUtil.setJsonResponse(res);
            try {
                Group groupData = JsonUtil.fromJson(req.body(), Group.class);
                groupData.setId(id);

                Group updated = groupDAO.update(groupData);
                res.status(200);
                return JsonUtil.successResponse(updated);
            } catch (Exception e) {
                logger.error("Error updating group: {}", id, e);
                res.status(500);
                return JsonUtil.errorResponse("Database error: " + e.getMessage(), 500);
            }
        });

        // DELETE /api/groups/:id - удалить группу
        Spark.delete("/api/groups/:id", (req, res) -> {
            int id = Integer.parseInt(req.params(":id"));
            logger.info("DELETE /api/groups/{} - Deleting group", id);
            JsonUtil.setJsonResponse(res);
            try {
                groupDAO.deleteById(id);
                res.status(200);
                return JsonUtil.successResponse(Map.of("message", "Group deleted successfully"));
            } catch (Exception e) {
                logger.error("Error deleting group: {}", id, e);
                res.status(500);
                return JsonUtil.errorResponse("Database error: " + e.getMessage(), 500);
            }
        });

        // POST /api/groups/cache/refresh - обновить кеш групп
        Spark.post("/api/groups/cache/refresh", (req, res) -> {
            logger.info("POST /api/groups/cache/refresh - Refreshing group cache");
            JsonUtil.setJsonResponse(res);
            try {
                groupDAO.refreshCache();
                res.status(200);
                return JsonUtil.successResponse(Map.of("message", "Group cache refreshed successfully"));
            } catch (Exception e) {
                logger.error("Error refreshing group cache", e);
                res.status(500);
                return JsonUtil.errorResponse("Error refreshing cache: " + e.getMessage(), 500);
            }
        });
    }

    private void registerTeacherEndpoints() {
        // GET /api/teachers - получить всех преподавателей
        Spark.get("/api/teachers", (req, res) -> {
            logger.info("GET /api/teachers - Fetching all teachers");
            JsonUtil.setJsonResponse(res);
            try {
                List<Teacher> teachers = teacherDAO.findAll();
                res.status(200);
                return JsonUtil.successResponse(teachers);
            } catch (Exception e) {
                logger.error("Error fetching teachers", e);
                res.status(500);
                return JsonUtil.errorResponse("Database error: " + e.getMessage(), 500);
            }
        });

        // GET /api/teachers/:id - получить преподавателя по ID
        Spark.get("/api/teachers/:id", (req, res) -> {
            int id = Integer.parseInt(req.params(":id"));
            logger.info("GET /api/teachers/{} - Fetching teacher by ID", id);
            JsonUtil.setJsonResponse(res);
            try {
                java.util.Optional<Teacher> teacher = teacherDAO.findById(id);
                if (teacher.isPresent()) {
                    res.status(200);
                    return JsonUtil.successResponse(teacher.get());
                } else {
                    res.status(404);
                    return JsonUtil.errorResponse("Teacher not found with id: " + id, 404);
                }
            } catch (Exception e) {
                logger.error("Error fetching teacher by id: {}", id, e);
                res.status(500);
                return JsonUtil.errorResponse("Database error: " + e.getMessage(), 500);
            }
        });

        // GET /api/teachers/user/:userId - получить преподавателя по ID пользователя
        Spark.get("/api/teachers/user/:userId", (req, res) -> {
            int userId = Integer.parseInt(req.params(":userId"));
            logger.info("GET /api/teachers/user/{} - Fetching teacher by user ID", userId);
            JsonUtil.setJsonResponse(res);
            try {
                java.util.Optional<Teacher> teacher = teacherDAO.findByUserId(userId);
                if (teacher.isPresent()) {
                    res.status(200);
                    return JsonUtil.successResponse(teacher.get());
                } else {
                    res.status(404);
                    return JsonUtil.errorResponse("Teacher not found for user id: " + userId, 404);
                }
            } catch (Exception e) {
                logger.error("Error fetching teacher by user id: {}", userId, e);
                res.status(500);
                return JsonUtil.errorResponse("Database error: " + e.getMessage(), 500);
            }
        });

        // POST /api/teachers - создать преподавателя
        Spark.post("/api/teachers", (req, res) -> {
            logger.info("POST /api/teachers - Creating new teacher");
            JsonUtil.setJsonResponse(res);
            try {
                Teacher teacherData = JsonUtil.fromJson(req.body(), Teacher.class);

                if (teacherData.getName() == null || teacherData.getUserId() == 0) {
                    res.status(400);
                    return JsonUtil.errorResponse("Name and userId are required", 400);
                }

                Teacher created = teacherDAO.save(teacherData);
                res.status(201);
                return JsonUtil.successResponse(created);
            } catch (Exception e) {
                logger.error("Error creating teacher", e);
                res.status(500);
                return JsonUtil.errorResponse("Database error: " + e.getMessage(), 500);
            }
        });

        // PUT /api/teachers/:id - обновить преподавателя
        Spark.put("/api/teachers/:id", (req, res) -> {
            int id = Integer.parseInt(req.params(":id"));
            logger.info("PUT /api/teachers/{} - Updating teacher", id);
            JsonUtil.setJsonResponse(res);
            try {
                Teacher teacherData = JsonUtil.fromJson(req.body(), Teacher.class);
                teacherData.setId(id);

                Teacher updated = teacherDAO.update(teacherData);
                res.status(200);
                return JsonUtil.successResponse(updated);
            } catch (Exception e) {
                logger.error("Error updating teacher: {}", id, e);
                res.status(500);
                return JsonUtil.errorResponse("Database error: " + e.getMessage(), 500);
            }
        });

        // DELETE /api/teachers/:id - удалить преподавателя
        Spark.delete("/api/teachers/:id", (req, res) -> {
            int id = Integer.parseInt(req.params(":id"));
            logger.info("DELETE /api/teachers/{} - Deleting teacher", id);
            JsonUtil.setJsonResponse(res);
            try {
                teacherDAO.deleteById(id);
                res.status(200);
                return JsonUtil.successResponse(Map.of("message", "Teacher deleted successfully"));
            } catch (Exception e) {
                logger.error("Error deleting teacher: {}", id, e);
                res.status(500);
                return JsonUtil.errorResponse("Database error: " + e.getMessage(), 500);
            }
        });
    }

    private void registerDutyHistoryEndpoints() {
        // GET /api/duty-histories - получить все истории дежурств
        Spark.get("/api/duty-histories", (req, res) -> {
            logger.info("GET /api/duty-histories - Fetching all duty histories");
            JsonUtil.setJsonResponse(res);
            try {
                List<Duty_History> histories = dutyHistoryDAO.findAll();
                res.status(200);
                return JsonUtil.successResponse(histories);
            } catch (Exception e) {
                logger.error("Error fetching duty histories", e);
                res.status(500);
                return JsonUtil.errorResponse("Database error: " + e.getMessage(), 500);
            }
        });

        // GET /api/duty-histories/:id - получить историю дежурства по ID
        Spark.get("/api/duty-histories/:id", (req, res) -> {
            int id = Integer.parseInt(req.params(":id"));
            logger.info("GET /api/duty-histories/{} - Fetching duty history by ID", id);
            JsonUtil.setJsonResponse(res);
            try {
                java.util.Optional<Duty_History> history = dutyHistoryDAO.findById(id);
                if (history.isPresent()) {
                    res.status(200);
                    return JsonUtil.successResponse(history.get());
                } else {
                    res.status(404);
                    return JsonUtil.errorResponse("Duty history not found with id: " + id, 404);
                }
            } catch (Exception e) {
                logger.error("Error fetching duty history by id: {}", id, e);
                res.status(500);
                return JsonUtil.errorResponse("Database error: " + e.getMessage(), 500);
            }
        });

        // GET /api/duty-histories/group/:groupId - получить историю дежурств по группе
        Spark.get("/api/duty-histories/group/:groupId", (req, res) -> {
            int groupId = Integer.parseInt(req.params(":groupId"));
            logger.info("GET /api/duty-histories/group/{} - Fetching duty histories by group", groupId);
            JsonUtil.setJsonResponse(res);
            try {
                List<Duty_History> histories = dutyHistoryDAO.findByGroupId(groupId);
                res.status(200);
                return JsonUtil.successResponse(histories);
            } catch (Exception e) {
                logger.error("Error fetching duty histories by group: {}", groupId, e);
                res.status(500);
                return JsonUtil.errorResponse("Database error: " + e.getMessage(), 500);
            }
        });

        // GET /api/duty-histories/student/:studentId - получить историю дежурств студента
        Spark.get("/api/duty-histories/student/:studentId", (req, res) -> {
            int studentId = Integer.parseInt(req.params(":studentId"));
            logger.info("GET /api/duty-histories/student/{} - Fetching duty histories by student", studentId);
            JsonUtil.setJsonResponse(res);
            try {
                List<Duty_History> histories = dutyHistoryDAO.findByStudentId(studentId);
                res.status(200);
                return JsonUtil.successResponse(histories);
            } catch (Exception e) {
                logger.error("Error fetching duty histories by student: {}", studentId, e);
                res.status(500);
                return JsonUtil.errorResponse("Database error: " + e.getMessage(), 500);
            }
        });

        // POST /api/duty-histories - создать историю дежурства
        Spark.post("/api/duty-histories", (req, res) -> {
            logger.info("POST /api/duty-histories - Creating new duty history");
            JsonUtil.setJsonResponse(res);
            try {
                Duty_History historyData = JsonUtil.fromJson(req.body(), Duty_History.class);

                if (historyData.getFirstStudentId() == 0 || historyData.getGroupId() == 0) {
                    res.status(400);
                    return JsonUtil.errorResponse("firstStudentId and groupId are required", 400);
                }

                Duty_History created = dutyHistoryDAO.save(historyData);
                res.status(201);
                return JsonUtil.successResponse(created);
            } catch (Exception e) {
                logger.error("Error creating duty history", e);
                res.status(500);
                return JsonUtil.errorResponse("Database error: " + e.getMessage(), 500);
            }
        });

        // PUT /api/duty-histories/:id - обновить историю дежурства
        Spark.put("/api/duty-histories/:id", (req, res) -> {
            int id = Integer.parseInt(req.params(":id"));
            logger.info("PUT /api/duty-histories/{} - Updating duty history", id);
            JsonUtil.setJsonResponse(res);
            try {
                Duty_History historyData = JsonUtil.fromJson(req.body(), Duty_History.class);
                historyData.setId(id);

                Duty_History updated = dutyHistoryDAO.update(historyData);
                res.status(200);
                return JsonUtil.successResponse(updated);
            } catch (Exception e) {
                logger.error("Error updating duty history: {}", id, e);
                res.status(500);
                return JsonUtil.errorResponse("Database error: " + e.getMessage(), 500);
            }
        });

        // DELETE /api/duty-histories/:id - удалить историю дежурства
        Spark.delete("/api/duty-histories/:id", (req, res) -> {
            int id = Integer.parseInt(req.params(":id"));
            logger.info("DELETE /api/duty-histories/{} - Deleting duty history", id);
            JsonUtil.setJsonResponse(res);
            try {
                dutyHistoryDAO.deleteById(id);
                res.status(200);
                return JsonUtil.successResponse(Map.of("message", "Duty history deleted successfully"));
            } catch (Exception e) {
                logger.error("Error deleting duty history: {}", id, e);
                res.status(500);
                return JsonUtil.errorResponse("Database error: " + e.getMessage(), 500);
            }
        });
    }

    private void registerTeacherGroupEndpoints() {
        // GET /api/teacher-groups - получить все связи преподаватель-группа
        Spark.get("/api/teacher-groups", (req, res) -> {
            logger.info("GET /api/teacher-groups - Fetching all teacher-group relations");
            JsonUtil.setJsonResponse(res);
            try {
                List<TeacherGroup> relations = teacherGroupDAO.findAll();
                res.status(200);
                return JsonUtil.successResponse(relations);
            } catch (Exception e) {
                logger.error("Error fetching teacher-group relations", e);
                res.status(500);
                return JsonUtil.errorResponse("Database error: " + e.getMessage(), 500);
            }
        });

        // GET /api/teacher-groups/:id - получить связь по ID
        Spark.get("/api/teacher-groups/:id", (req, res) -> {
            int id = Integer.parseInt(req.params(":id"));
            logger.info("GET /api/teacher-groups/{} - Fetching teacher-group relation by ID", id);
            JsonUtil.setJsonResponse(res);
            try {
                java.util.Optional<TeacherGroup> relation = teacherGroupDAO.findById(id);
                if (relation.isPresent()) {
                    res.status(200);
                    return JsonUtil.successResponse(relation.get());
                } else {
                    res.status(404);
                    return JsonUtil.errorResponse("Teacher-group relation not found with id: " + id, 404);
                }
            } catch (Exception e) {
                logger.error("Error fetching teacher-group relation by id: {}", id, e);
                res.status(500);
                return JsonUtil.errorResponse("Database error: " + e.getMessage(), 500);
            }
        });

        // GET /api/teacher-groups/teacher/:teacherId - получить все группы, которые ведет преподаватель
        Spark.get("/api/teacher-groups/teacher/:teacherId", (req, res) -> {
            int teacherId = Integer.parseInt(req.params(":teacherId"));
            logger.info("GET /api/teacher-groups/teacher/{} - Fetching groups taught by teacher", teacherId);
            JsonUtil.setJsonResponse(res);
            try {
                List<TeacherGroup> relations = teacherGroupDAO.findByTeacherId(teacherId);
                res.status(200);
                return JsonUtil.successResponse(relations);
            } catch (Exception e) {
                logger.error("Error fetching groups for teacher: {}", teacherId, e);
                res.status(500);
                return JsonUtil.errorResponse("Database error: " + e.getMessage(), 500);
            }
        });

        // GET /api/teacher-groups/group/:groupId - получить всех преподавателей, которые ведут группу
        Spark.get("/api/teacher-groups/group/:groupId", (req, res) -> {
            int groupId = Integer.parseInt(req.params(":groupId"));
            logger.info("GET /api/teacher-groups/group/{} - Fetching teachers who teach group", groupId);
            JsonUtil.setJsonResponse(res);
            try {
                List<TeacherGroup> relations = teacherGroupDAO.findByGroupId(groupId);
                res.status(200);
                return JsonUtil.successResponse(relations);
            } catch (Exception e) {
                logger.error("Error fetching teachers for group: {}", groupId, e);
                res.status(500);
                return JsonUtil.errorResponse("Database error: " + e.getMessage(), 500);
            }
        });

        // POST /api/teacher-groups - назначить преподавателя на группу
        Spark.post("/api/teacher-groups", (req, res) -> {
            logger.info("POST /api/teacher-groups - Assigning teacher to group");
            JsonUtil.setJsonResponse(res);
            try {
                TeacherGroup relationData = JsonUtil.fromJson(req.body(), TeacherGroup.class);

                if (relationData.getTeacherId() == 0 || relationData.getGroupId() == 0) {
                    res.status(400);
                    return JsonUtil.errorResponse("teacherId and groupId are required", 400);
                }

                TeacherGroup created = teacherGroupDAO.save(relationData);
                res.status(201);
                return JsonUtil.successResponse(created);
            } catch (Exception e) {
                logger.error("Error assigning teacher to group", e);
                if (e.getMessage().contains("duplicate")) {
                    res.status(409);
                    return JsonUtil.errorResponse("Teacher is already assigned to this group", 409);
                }
                res.status(500);
                return JsonUtil.errorResponse("Database error: " + e.getMessage(), 500);
            }
        });

        // PUT /api/teacher-groups/:id - обновить связь
        Spark.put("/api/teacher-groups/:id", (req, res) -> {
            int id = Integer.parseInt(req.params(":id"));
            logger.info("PUT /api/teacher-groups/{} - Updating teacher-group relation", id);
            JsonUtil.setJsonResponse(res);
            try {
                TeacherGroup relationData = JsonUtil.fromJson(req.body(), TeacherGroup.class);
                relationData.setId(id);

                TeacherGroup updated = teacherGroupDAO.update(relationData);
                res.status(200);
                return JsonUtil.successResponse(updated);
            } catch (Exception e) {
                logger.error("Error updating teacher-group relation: {}", id, e);
                res.status(500);
                return JsonUtil.errorResponse("Database error: " + e.getMessage(), 500);
            }
        });

        // DELETE /api/teacher-groups/:id - удалить связь по ID
        Spark.delete("/api/teacher-groups/:id", (req, res) -> {
            int id = Integer.parseInt(req.params(":id"));
            logger.info("DELETE /api/teacher-groups/{} - Deleting teacher-group relation", id);
            JsonUtil.setJsonResponse(res);
            try {
                teacherGroupDAO.deleteById(id);
                res.status(200);
                return JsonUtil.successResponse(Map.of("message", "Teacher removed from group successfully"));
            } catch (Exception e) {
                logger.error("Error deleting teacher-group relation: {}", id, e);
                res.status(500);
                return JsonUtil.errorResponse("Database error: " + e.getMessage(), 500);
            }
        });

        // DELETE /api/teacher-groups/teacher/:teacherId/group/:groupId - удалить назначение преподавателя на группу
        Spark.delete("/api/teacher-groups/teacher/:teacherId/group/:groupId", (req, res) -> {
            int teacherId = Integer.parseInt(req.params(":teacherId"));
            int groupId = Integer.parseInt(req.params(":groupId"));
            logger.info("DELETE /api/teacher-groups/teacher/{}/group/{} - Removing teacher from group", teacherId, groupId);
            JsonUtil.setJsonResponse(res);
            try {
                teacherGroupDAO.deleteByTeacherAndGroup(teacherId, groupId);
                res.status(200);
                return JsonUtil.successResponse(Map.of("message", "Teacher removed from group successfully"));
            } catch (Exception e) {
                logger.error("Error removing teacher from group: teacher={}, group={}", teacherId, groupId, e);
                res.status(500);
                return JsonUtil.errorResponse("Database error: " + e.getMessage(), 500);
            }
        });
    }
}