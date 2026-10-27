
package backend.controller;

import backend.entity.Student;
import backend.repository.StudentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/students")
@CrossOrigin(origins = {
    "http://localhost:5173",
    "http://localhost:5174"
})
public class StudentController {

    private final StudentRepository repository;

    public StudentController(StudentRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Student> getStudents() {
        return repository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> addStudent(
            @RequestBody Student student) {

        if (student.getName() == null ||
            student.getName().isBlank() ||
            student.getRollNumber() == null ||
            student.getRollNumber().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error",
                            "Name and roll number are required"));
        }

        if (repository.existsByRollNumber(
                student.getRollNumber().trim())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error",
                            "Roll number already exists"));
        }

        student.setName(student.getName().trim());
        student.setRollNumber(student.getRollNumber().trim());

        return ResponseEntity.ok(repository.save(student));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(
            @PathVariable Long id) {

        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
