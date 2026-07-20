package com.doctorhelp.patient.infrastructure;

import com.doctorhelp.patient.domain.Patient;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class DemoPatientRepository {

    private static final String DEMO_DOCTOR_ID = "doctor-001";

    private final List<Patient> patients = createPatients();

    public List<Patient> findByDoctorId(String doctorId) {
        return patients.stream()
                .filter(patient -> doctorId.equals(patient.attendingDoctorId()))
                .toList();
    }

    public Optional<Patient> findById(String patientId) {
        return patients.stream()
                .filter(patient -> patientId.equals(patient.id()))
                .findFirst();
    }

    private List<Patient> createPatients() {
        List<Patient> result = new ArrayList<>(List.of(
                new Patient("patient-001", "陈静", "女", LocalDate.of(1986, 5, 12), "visit-001", DEMO_DOCTOR_ID, "2026-07-18 09:20", "待查看", "A01", "待叫号"),
                new Patient("patient-002", "李明", "男", LocalDate.of(1972, 11, 3), "visit-002", DEMO_DOCTOR_ID, "2026-07-18 09:45", "存在异常", "A02", "候诊中"),
                new Patient("patient-003", "王芳", "女", LocalDate.of(1994, 2, 20), "visit-003", DEMO_DOCTOR_ID, "2026-07-18 10:10", "已汇总", "A03", "已就诊"),
                new Patient("patient-004", "赵强", "男", LocalDate.of(1961, 8, 8), "visit-004", DEMO_DOCTOR_ID, "2026-07-18 10:35", "待查看", "A04", "待签到")
        ));
        for (int index = 5; index <= 150; index++) {
            String status = index % 11 == 0 ? "存在异常" : index % 4 == 0 ? "已汇总" : "待查看";
            String appointmentStatus = index % 4 == 0 ? "已就诊" : index % 3 == 0 ? "候诊中" : index % 5 == 0 ? "待签到" : "待叫号";
            result.add(new Patient(
                    "patient-" + String.format("%03d", index), "模拟患者" + String.format("%03d", index), index % 2 == 0 ? "女" : "男",
                    LocalDate.of(1955 + index % 45, index % 12 + 1, index % 27 + 1), "visit-" + String.format("%03d", index), DEMO_DOCTOR_ID,
                    "2026-07-18 " + String.format("%02d:%02d", 8 + index % 10, index % 6 * 10), status, "A" + String.format("%03d", index), appointmentStatus
            ));
        }
        return List.copyOf(result);
    }
}
