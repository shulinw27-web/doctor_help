package com.doctorhelp.patient.domain;

import java.time.LocalDate;

public record Patient(
        String id,
        String name,
        String gender,
        LocalDate birthDate,
        String visitId,
        String attendingDoctorId,
        String visitDate,
        String status,
        String queueNumber,
        String appointmentStatus
) {
}
