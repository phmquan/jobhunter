package vn.uit.jobhunter.domain.response;

import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResResumeDTO {
    private long id;
    private Instant createdAt;
    private String createdBy;
} 