/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.uit.jobhunter.domain.response;

import java.time.Instant;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
import vn.uit.jobhunter.domain.Company;
import vn.uit.jobhunter.util.constant.Gender;

@Getter
@Setter
public class ResCreateUserDTO {

    private UUID id;
    private String name;
    private String email;
    private Gender gender;
    private String address;
    private int age;
    private Company company;
    private Instant createdAt;
}
