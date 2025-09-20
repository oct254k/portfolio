package com.flow.test.controller;

import com.flow.test.domain.Extension;
import com.flow.test.service.ExtensionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 파일 확장자 차단 설정을 위한 API
@Slf4j
@RestController
@RequestMapping("/api/extensions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // CORS 허용
public class ExtensionController {
    
    private final ExtensionService extensionService;
    
    // 체크박스랑 라벨 타입 확장자 전부 가져오기
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllExtensions() {
        try {
            List<Extension> checkExtensions = extensionService.getCheckExtensions();
            List<Extension> labelExtensions = extensionService.getLabelExtensions();
            
            Map<String, Object> response = new HashMap<>();
            response.put("checkExtensions", checkExtensions);
            response.put("labelExtensions", labelExtensions);
            response.put("success", true);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("확장자 조회 중 오류 발생", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "message", "확장자 조회 중 오류가 발생했습니다."));
        }
    }
    
    // 고정 확장자 목록 가져오기 (체크박스용)
    @GetMapping("/check")
    public ResponseEntity<List<Extension>> getCheckExtensions() {
        try {
            List<Extension> extensions = extensionService.getCheckExtensions();
            return ResponseEntity.ok(extensions);
        } catch (Exception e) {
            log.error("체크박스 확장자 조회 중 오류 발생", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    // 커스텀 확장자 목록 가져오기 (태그 형태로 보여줄 것들)
    @GetMapping("/label")
    public ResponseEntity<List<Extension>> getLabelExtensions() {
        try {
            List<Extension> extensions = extensionService.getLabelExtensions();
            return ResponseEntity.ok(extensions);
        } catch (Exception e) {
            log.error("라벨 확장자 조회 중 오류 발생", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    // 새로운 확장자 추가하기
    @PostMapping
    public ResponseEntity<Map<String, Object>> addExtension(@RequestBody Map<String, String> request) {
        try {
            String name = request.get("name");
            String type = request.get("type");
            String regId = request.getOrDefault("regId", "system"); // 기본값: system
            
            // 빈값 체크
            if (name == null || name.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "확장자명을 입력해주세요."));
            }
            
            if (type == null || (!type.equals(Extension.Type.CHECK) && !type.equals(Extension.Type.LABEL))) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "올바른 확장자 타입을 선택해주세요."));
            }
            
            if (!extensionService.isValidExtension(name)) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "영어와 숫자만 입력 가능합니다. (1-20자, 공백 불가)"));
            }
            
            // DB에 저장
            Extension extension = extensionService.addExtension(name, type, regId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "확장자가 성공적으로 추가되었습니다.");
            response.put("extension", extension);
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            log.warn("확장자 추가 실패: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            log.error("확장자 추가 중 오류 발생", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "message", "확장자 추가 중 오류가 발생했습니다."));
        }
    }
    
    // 확장자 삭제 (x 버튼 클릭시 호출됨)
    @DeleteMapping("/{name}")
    public ResponseEntity<Map<String, Object>> removeExtension(@PathVariable String name) {
        try {
            int deletedCount = extensionService.removeExtension(name);
            
            if (deletedCount > 0) {
                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "message", "확장자가 성공적으로 삭제되었습니다."
                ));
            } else {
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            log.error("확장자 삭제 중 오류 발생", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "message", "확장자 삭제 중 오류가 발생했습니다."));
        }
    }
    
    // 확장자 중복 체크용
    @GetMapping("/exists/{name}")
    public ResponseEntity<Map<String, Object>> checkExtensionExists(@PathVariable String name) {
        try {
            boolean exists = extensionService.existsExtension(name);
            return ResponseEntity.ok(Map.of("exists", exists));
        } catch (Exception e) {
            log.error("확장자 존재 여부 확인 중 오류 발생", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("exists", false, "error", "확인 중 오류가 발생했습니다."));
        }
    }
}
