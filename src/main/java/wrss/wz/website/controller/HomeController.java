package wrss.wz.website.controller;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping()
    public ResponseEntity<?> home() {

        JSONObject json = new JSONObject().put("application", "WRSS WZ Website")
                                          .put("documentation", "/api.html");

        return new ResponseEntity<>(json.toString(), HttpStatus.OK);
    }
}
