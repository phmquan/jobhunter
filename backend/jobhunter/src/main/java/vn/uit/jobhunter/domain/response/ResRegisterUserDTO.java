package vn.uit.jobhunter.domain.response;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResRegisterUserDTO {
    private Long id;
    private String email;
    private String name;
    private Instant createdAt;
}
