package vn.uit.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import vn.uit.jobhunter.domain.Company;
import vn.uit.jobhunter.domain.Role;
import vn.uit.jobhunter.domain.User;
import vn.uit.jobhunter.domain.response.ResUserDTO;
import vn.uit.jobhunter.domain.response.ResultPaginationDTO;
import vn.uit.jobhunter.repository.UserRepository;
import vn.uit.jobhunter.service.mapper.UserMapperDTO;

import vn.uit.jobhunter.service.pagination.PaginationHelper;
import vn.uit.jobhunter.service.validation.ItemValidator;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final CompanyService companyService;
    private final RoleService roleService;
    private final UserMapperDTO userMapperDTO;
    private final ItemValidator itemValidator;
    private final PaginationHelper paginationHelper;

    public User handleCreateUser(User user) {
        // check company
        if (itemValidator.hasItem(user.getCompany())) {
            Optional<Company> companyOptional = this.companyService.findById(user.getCompany().getId());
            user.setCompany(companyOptional.isPresent() ? companyOptional.get() : null);
        }

        // check role
        if (itemValidator.hasItem(user.getRole())) {
            Role r = this.roleService.fetchById(user.getRole().getId());
            user.setRole(r != null ? r : null);
        }

        return this.userRepository.save(user);
    }

    public void deleteUser(long id) {
        this.userRepository.deleteById(id);
    }

    public User fetchUserById(long id) {
        Optional<User> userOptional = this.userRepository.findById(id);
        if (userOptional.isPresent()) {
            return userOptional.get();
        }
        return null;
    }

    public ResultPaginationDTO fetchAllUser(Specification<User> spec, Pageable pageable) {
        Page<User> pageUser = this.userRepository.findAll(spec, pageable);
        
        List<ResUserDTO> listUser = pageUser.getContent()
                .stream().map(item -> this.userMapperDTO.convertToResUserDTO(item))
                .collect(Collectors.toList());

        return paginationHelper.convertListDTOResultPagination(pageUser, pageable, listUser);
    }

    public User updateUser(User reqUser) {
        User currentUser = this.fetchUserById(reqUser.getId());
        if (currentUser != null) {
            currentUser.setAddress(reqUser.getAddress());
            currentUser.setGender(reqUser.getGender());
            currentUser.setAge(reqUser.getAge());
            currentUser.setName(reqUser.getName());

            // check company
            if (itemValidator.hasItem(reqUser.getCompany())) {
                Optional<Company> companyOptional = this.companyService.findById(reqUser.getCompany().getId());
                currentUser.setCompany(companyOptional.isPresent() ? companyOptional.get() : null);
            }

            // check role
            if (itemValidator.hasItem(reqUser.getRole())) {
                Role r = this.roleService.fetchById(reqUser.getRole().getId());
                currentUser.setRole(r != null ? r : null);
            }
            currentUser = this.userRepository.save(currentUser);
        }
        return currentUser;
    }

    public User getUserByUserName(String username) {
        return this.userRepository.findByEmail(username);
    }

    public boolean isEmailExist(String email) {
        return this.userRepository.existsByEmail(email);
    }

    public User getUserByRefreshTokenAndEmail(String token, String email) {
        return this.userRepository.findByRefreshTokenAndEmail(token, email);
    }
}
