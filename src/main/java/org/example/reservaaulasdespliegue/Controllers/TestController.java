package org.example.reservaaulasdespliegue.Controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class TestController {

    @GetMapping("/ping")
    public Map<String, Object> ping() {
        Map<String, Object> out = new HashMap<>();
        out.put("ok", true);
        out.put("msg", "pong");
        return out;
    }
}
