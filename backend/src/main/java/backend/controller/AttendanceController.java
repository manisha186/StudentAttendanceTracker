
package backend.controller;

import backend.entity.Attendance;
import backend.entity.Student;
import backend.repository.AttendanceRepository;
import backend.repository.StudentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/attendance")
@CrossOrigin(origins = {
    "http://localhost:5173",
    "http://localhost:5174"
})
public class AttendanceController {

    private final AttendanceRepository attendanceRepo;
    private final StudentRepository studentRepo;

    public AttendanceController(
            AttendanceRepository attendanceRepo,
            StudentRepository studentRepo) {
        this.attendanceRepo = attendanceRepo;
        this.studentRepo = studentRepo;
    }

    // Get attendance for a selected date
    @GetMapping
    public ResponseEntity<?> getAttendance(
            @RequestParam String date) {
        try {
            LocalDate selectedDate = LocalDate.parse(date);
            List<Attendance> records =
                    attendanceRepo.findByAttendanceDate(selectedDate);
            return ResponseEntity.ok(records);
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid date format"));
        }
    }

    // Save or update attendance for a student on a date
    @PostMapping
    public ResponseEntity<?> saveAttendance(
            @RequestBody Map<String, String> data) {
        try {
            Long studentId =
                    Long.parseLong(data.get("studentId"));
            LocalDate date =
                    LocalDate.parse(data.get("date"));
            String status = data.get("status");

            if (!"Present".equals(status)
                    && !"Absent".equals(status)) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error",
                                "Status must be Present or Absent"));
            }

            Student student = studentRepo.findById(studentId)
                    .orElse(null);

            if (student == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Student not found"));
            }

            Attendance attendance =
                    attendanceRepo
                        .findByStudent_IdAndAttendanceDate(
                            studentId, date);

            if (attendance == null) {
                attendance = new Attendance();
                attendance.setStudent(student);
                attendance.setAttendanceDate(date);
            }

            attendance.setStatus(status);

            Attendance saved =
                    attendanceRepo.save(attendance);

            return ResponseEntity.ok(saved);

        } catch (DateTimeParseException
                | NumberFormatException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid attendance data"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("error",
                            "Failed to save attendance"));
        }
    }

    // Update an existing attendance record
    @PutMapping("/{id}")
    public ResponseEntity<?> updateAttendance(
            @PathVariable Long id,
            @RequestBody Map<String, String> data) {

        Attendance attendance = attendanceRepo.findById(id)
                .orElse(null);

        if (attendance == null) {
            return ResponseEntity.notFound().build();
        }

        String status = data.get("status");

        if (!"Present".equals(status)
                && !"Absent".equals(status)) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid status"));
        }

        attendance.setStatus(status);
        return ResponseEntity.ok(
                attendanceRepo.save(attendance));
    }

    // Delete an attendance record
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAttendance(
            @PathVariable Long id) {

        if (!attendanceRepo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        attendanceRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
