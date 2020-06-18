package io.incubator.wazx.edge.controller;

import io.incubator.common.pojo.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Description:
 *
 * @author yubb
 */
@RestController
@RequestMapping("/edge")
public class EdgeController {

    @PostMapping
    public ResponseEntity<String> create() {
        return ResponseEntity.ok("Edge create.");
    }

    @PutMapping
    public ResponseEntity<String> modify() {
        return ResponseEntity.ok("Edge modify.");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> remove(@PathVariable Long id) {
        return ResponseEntity.ok("Edge remove: " + id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> findById(@PathVariable Long id) {
        return ResponseEntity.ok("Edge findById: " + id);
    }

}
