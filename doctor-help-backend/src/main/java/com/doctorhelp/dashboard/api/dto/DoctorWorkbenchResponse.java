package com.doctorhelp.dashboard.api.dto;

import com.doctorhelp.organization.domain.Doctor;
import com.doctorhelp.patient.domain.Patient;

import java.util.List;

public record DoctorWorkbenchResponse(
        Doctor doctor,
        List<Patient> patients,
        WorkbenchMetrics metrics
) {
    public record WorkbenchMetrics(
            int todayVisits,
            int pendingReviews,
            int abnormalPatients,
            int aiSummariesGenerated
    ) {
    }
}

