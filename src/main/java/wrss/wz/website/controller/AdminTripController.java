package wrss.wz.website.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/trip")
public class AdminTripController {

    @GetMapping()
    public ResponseEntity<?> home() {
        return new ResponseEntity<>("Hello in admin panel for trip", HttpStatus.OK);
    }
}
