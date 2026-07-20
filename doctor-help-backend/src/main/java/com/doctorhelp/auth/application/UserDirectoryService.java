package com.doctorhelp.auth.application;

import com.doctorhelp.auth.api.dto.LoginResponse;
import com.doctorhelp.user.api.dto.CreateSystemUserRequest;
import com.doctorhelp.user.api.dto.OaSyncResultResponse;
import com.doctorhelp.user.api.dto.SystemUserResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class UserDirectoryService {
    private static final DateTimeFormatter SYNC_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final String[] DEPARTMENTS = {"普通门诊", "检验科", "影像科", "呼吸内科", "心内科", "信息科", "医务处", "院办公室"};
    private static final String[] TITLES = {"住院医师", "主治医师", "副主任医师", "主管技师", "科室秘书", "信息工程师"};
    private static final String[] FAMILY_NAMES = {"李", "王", "刘", "陈", "杨", "黄", "赵", "周", "吴", "徐", "孙", "马"};
    private static final String[] GIVEN_NAMES = {"明", "静", "强", "芳", "晨", "悦", "博", "宁", "欣", "航", "婷", "杰"};

    private final Map<String, LoginResponse.UserProfile> users = new LinkedHashMap<>();
    private final Map<String, SystemUserResponse.PersonnelProfile> profiles = new LinkedHashMap<>();

    public UserDirectoryService() {
        createProfile("doctor-001", "zhangyisheng", "张医生", "DOCTOR", "普通门诊", "主治医师", "女", "CSH-2026-0001", "OA-0001");
        createProfile("leader-001", "yuanlingdao", "王院长", "LEADER", "院办公室", "院领导", "男", "CSH-2026-0002", "OA-0002");
        createProfile("admin-001", "admin", "系统管理员", "ADMIN", "信息科", "管理员", "男", "CSH-2026-0003", "OA-0003");
        for (int index = 1; index <= 120; index++) {
            String department = DEPARTMENTS[index % DEPARTMENTS.length];
            String role = index % 25 == 0 ? "LEADER" : "DOCTOR";
            String title = role.equals("LEADER") ? "科室负责人" : TITLES[index % TITLES.length];
            String name = FAMILY_NAMES[index % FAMILY_NAMES.length] + GIVEN_NAMES[index % GIVEN_NAMES.length] + (index % 4 == 0 ? "医生" : "");
            createProfile(
                    "staff-" + String.format("%03d", index), "staff" + String.format("%03d", index), name, role, department, title,
                    index % 2 == 0 ? "女" : "男", "CSH-2026-" + String.format("%04d", index + 3), "OA-" + String.format("%04d", index + 3)
            );
        }
    }

    public synchronized LoginResponse.UserProfile findByUsername(String username) { return users.get(username); }
    public synchronized LoginResponse.UserProfile findById(String id) { return users.values().stream().filter(user -> user.id().equals(id)).findFirst().orElse(null); }
    public synchronized List<SystemUserResponse> listUsers() { return users.values().stream().map(this::toResponse).toList(); }

    public synchronized SystemUserResponse updateRole(String id, String role) {
        LoginResponse.UserProfile user = requireUser(id);
        LoginResponse.UserProfile updated = new LoginResponse.UserProfile(user.id(), user.username(), user.name(), role, user.departmentName(), user.title());
        users.put(updated.username(), updated);
        return toResponse(updated);
    }

    public synchronized SystemUserResponse createUser(CreateSystemUserRequest request) {
        if (users.containsKey(request.username().trim())) throw new IllegalArgumentException("账号已存在");
        String employeeNo = "MANUAL-" + LocalDate.now().getYear() + "-" + String.format("%04d", users.size() + 1);
        String id = "manual-" + UUID.randomUUID();
        LoginResponse.UserProfile user = new LoginResponse.UserProfile(id, request.username().trim(), request.name().trim(), request.role(), request.departmentName().trim(), request.title().trim());
        add(user, createPersonnelProfile(employeeNo, request.gender(), request.mobile(), request.email(), request.title(), "OA-MANUAL-" + UUID.randomUUID().toString().substring(0, 8), request.departmentName()));
        return toResponse(user);
    }

    public synchronized OaSyncResultResponse syncFromOa() {
        int added = 0;
        int updated = 0;
        for (OaRecord record : simulatedOaRecords()) {
            LoginResponse.UserProfile existing = users.values().stream()
                    .filter(user -> record.oaUserId().equals(profiles.get(user.id()).oaUserId()))
                    .findFirst().orElse(null);
            if (existing == null) {
                LoginResponse.UserProfile user = new LoginResponse.UserProfile("oa-" + record.oaUserId(), record.username(), record.name(), record.role(), record.departmentName(), record.title());
                add(user, createPersonnelProfile(record.employeeNo(), record.gender(), record.mobile(), record.email(), record.title(), record.oaUserId(), record.departmentName()));
                added++;
            } else {
                LoginResponse.UserProfile updatedUser = new LoginResponse.UserProfile(existing.id(), existing.username(), record.name(), record.role(), record.departmentName(), record.title());
                users.put(updatedUser.username(), updatedUser);
                profiles.put(updatedUser.id(), createPersonnelProfile(record.employeeNo(), record.gender(), record.mobile(), record.email(), record.title(), record.oaUserId(), record.departmentName()));
                updated++;
            }
        }
        return new OaSyncResultResponse(added, updated, users.size(), now());
    }

    private LoginResponse.UserProfile requireUser(String id) {
        LoginResponse.UserProfile user = findById(id);
        if (user == null) throw new IllegalArgumentException("用户不存在");
        return user;
    }

    private SystemUserResponse toResponse(LoginResponse.UserProfile user) { return SystemUserResponse.from(user, profiles.get(user.id())); }
    private void add(LoginResponse.UserProfile user) { add(user, createPersonnelProfile("CSH-" + user.id(), "未设置", "未设置", "未设置", user.title(), "OA-" + user.id(), user.departmentName())); }
    private void add(LoginResponse.UserProfile user, SystemUserResponse.PersonnelProfile profile) { users.put(user.username(), user); profiles.put(user.id(), profile); }

    private LoginResponse.UserProfile createProfile(String id, String username, String name, String role, String department, String title, String gender, String employeeNo, String oaUserId) {
        LoginResponse.UserProfile user = new LoginResponse.UserProfile(id, username, name, role, department, title);
        add(user, createPersonnelProfile(employeeNo, gender, mobileFor(employeeNo), username + "@hospital.demo", title, oaUserId, department));
        return user;
    }

    private SystemUserResponse.PersonnelProfile createPersonnelProfile(String employeeNo, String gender, String mobile, String email, String title, String oaUserId, String department) {
        return new SystemUserResponse.PersonnelProfile(employeeNo, gender, mobile, email, "在职", "2024-" + String.format("%02d", Math.max(1, Math.abs(employeeNo.hashCode()) % 12 + 1)) + "-01", oaUserId, "OA-" + Math.abs(department.hashCode() % 9000 + 1000), title, "正常", now());
    }

    private String mobileFor(String source) { return "139" + String.format("%08d", Math.abs(source.hashCode()) % 100_000_000); }
    private String now() { return LocalDateTime.now().format(SYNC_TIME_FORMATTER); }

    private List<OaRecord> simulatedOaRecords() {
        return List.of(
                new OaRecord("OA-0004", "staff001", "李明", "DOCTOR", "普通门诊", "主治医师", "男", "CSH-2026-0004", "13900000004", "staff001@hospital.demo"),
                new OaRecord("OA-0008", "staff005", "黄晨", "DOCTOR", "影像科", "副主任医师", "女", "CSH-2026-0008", "13900000008", "staff005@hospital.demo"),
                new OaRecord("OA-SIM-1001", "oauser1001", "周敏", "DOCTOR", "检验科", "主管技师", "女", "OA-STAFF-1001", "13900001001", "zhoumin@hospital.demo"),
                new OaRecord("OA-SIM-1002", "oauser1002", "钱程", "DOCTOR", "心内科", "主治医师", "男", "OA-STAFF-1002", "13900001002", "qiancheng@hospital.demo"),
                new OaRecord("OA-SIM-1003", "oauser1003", "宋颖", "LEADER", "医务处", "科室负责人", "女", "OA-STAFF-1003", "13900001003", "songying@hospital.demo")
        );
    }

    private record OaRecord(String oaUserId, String username, String name, String role, String departmentName, String title, String gender, String employeeNo, String mobile, String email) {
    }
}
