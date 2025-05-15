package vn.uit.jobhunter.domain.response;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.uit.jobhunter.domain.Company;
import vn.uit.jobhunter.util.constant.Gender;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResUserDTO {
    private Long id;
    private String email;
    private String name;
    private Gender gender;
    private String address;
    private int age;
    private Company company;
    private Instant updatedAt;
    private Instant createdAt;
}
