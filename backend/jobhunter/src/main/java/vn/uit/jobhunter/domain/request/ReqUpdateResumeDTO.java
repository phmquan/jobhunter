package vn.uit.jobhunter.domain.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import vn.uit.jobhunter.util.constant.ResumeStateEnum;

@Getter
@Setter
public class ReqUpdateResumeDTO {
    @NotNull(message = "Status không được để trống")
    private ResumeStateEnum status;
} 