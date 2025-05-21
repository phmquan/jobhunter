package vn.uit.jobhunter.domain.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.uit.jobhunter.util.constant.ResumeStateEnum;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResUpdateResumeDTO {
    private ResumeStateEnum status;
}
