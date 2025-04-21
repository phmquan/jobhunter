package vn.uit.jobhunter.domain.response;

import java.time.Instant;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
import vn.uit.jobhunter.util.constant.Gender;

@Getter
@Setter
public class ResUpdateUserDTO {

    private UUID id;
    private String name;
    private int age;
    private Instant updatedAt;
    private String address;
    private Gender gender;
}
