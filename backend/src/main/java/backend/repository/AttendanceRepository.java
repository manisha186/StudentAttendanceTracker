
package backend.repository;

import backend.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface AttendanceRepository
        extends JpaRepository<Attendance, Long> {

    List<Attendance> findByAttendanceDate(LocalDate date);

    Attendance findByStudent_IdAndAttendanceDate(
            Long studentId, LocalDate date);
}
