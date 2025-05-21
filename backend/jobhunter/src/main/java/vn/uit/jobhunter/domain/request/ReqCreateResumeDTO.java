package vn.uit.jobhunter.domain.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import vn.uit.jobhunter.util.constant.ResumeStateEnum;

@Getter
@Setter
public class ReqCreateResumeDTO {
    // @NotBlank(message = "Email không được để trống")
    // @Email(message = "Email không đúng định dạng")
    // private String email;

    @NotBlank(message = "URL không được để trống")
    private String url;

    private ResumeStateEnum status = ResumeStateEnum.PENDING;

    @Valid
    @NotNull(message = "User không được để trống")
    private UserDTO user;

    @Valid
    @NotNull(message = "Job không được để trống")
    private JobDTO job;

    @Getter
    @Setter
    public static class UserDTO {
        @NotNull(message = "User ID không được để trống")
        private String id;
    }

    @Getter
    @Setter
    public static class JobDTO {
        @NotNull(message = "Job ID không được để trống")
        private String id;
    }
} 