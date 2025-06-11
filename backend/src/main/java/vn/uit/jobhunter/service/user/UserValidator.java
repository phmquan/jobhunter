package vn.uit.jobhunter.service.user;

import org.springframework.stereotype.Component;


import lombok.AllArgsConstructor;
import vn.uit.jobhunter.domain.Company;
import vn.uit.jobhunter.domain.Role;

@Component
@AllArgsConstructor
public class UserValidator {
    public boolean hasCompany(Company company){
        return company==null?false:true;
    }
    public boolean hasRole(Role role){
        return role==null?false:true;
    }
}
