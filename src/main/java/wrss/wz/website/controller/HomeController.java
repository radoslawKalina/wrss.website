package wrss.wz.website.controller;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import wrss.wz.website.entity.Student;
import wrss.wz.website.model.request.StudentRequest;
import wrss.wz.website.repository.TestRepository;

import java.util.List;

@RestController
public class HomeController {

    @Autowired
    private TestRepository testRepository;

    @GetMapping("/")
    public ResponseEntity<?> home() {
        return new ResponseEntity<>("WRSS WZ Website", HttpStatus.OK);
    }

    @GetMapping("api/student")
    public List<Student> getStudents() {

        List<Student> students = (List<Student>) testRepository.findAll();
        return students;
    }

    @PostMapping("/api/student")
    public void addStudent(@RequestBody final StudentRequest studentRequest) {

        Student student = new ModelMapper().map(studentRequest, Student.class);
        testRepository.save(student);
    }
}
