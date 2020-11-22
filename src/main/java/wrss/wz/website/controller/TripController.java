package wrss.wz.website.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/trip")
public class TripController {

    @GetMapping()
    public ResponseEntity<?> home() {
        return new ResponseEntity<>("Sign up for the trip", HttpStatus.OK);
    }
}
