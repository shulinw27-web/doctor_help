package com.doctorhelp.dashboard.application;

import com.doctorhelp.dashboard.api.dto.DoctorWorkbenchResponse;
import com.doctorhelp.organization.domain.Doctor;
import com.doctorhelp.patient.infrastructure.DemoPatientRepository;
import org.springframework.stereotype.Service;

@Service
public class DoctorWorkbenchService {

    private static final String DEMO_DOCTOR_ID = "doctor-001";

    private final Doctor demoDoctor = new Doctor(
            DEMO_DOCTOR_ID,
            "张医生",
            "主治医师",
            "dept-outpatient",
            "普通门诊"
    );

    private final DemoPatientRepository patientRepository;

    public DoctorWorkbenchService(DemoPatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public DoctorWorkbenchResponse getWorkbench(String doctorId) {
        if (!DEMO_DOCTOR_ID.equals(doctorId)) {
            throw new IllegalArgumentException("未找到演示医生");
        }

        return new DoctorWorkbenchResponse(
                demoDoctor,
                patientRepository.findByDoctorId(DEMO_DOCTOR_ID),
                new DoctorWorkbenchResponse.WorkbenchMetrics(12, 4, 3, 8)
        );
    }
}
