package vn.uit.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import vn.uit.jobhunter.domain.Role;
import vn.uit.jobhunter.domain.User;
import vn.uit.jobhunter.domain.response.ResCreateUserDTO;
import vn.uit.jobhunter.domain.response.ResRegisterUserDTO;
import vn.uit.jobhunter.domain.response.ResUpdateUserDTO;
import vn.uit.jobhunter.domain.response.ResUserDTO;
import vn.uit.jobhunter.domain.response.RestResponse;
import vn.uit.jobhunter.domain.response.ResultPaginationDTO;
import vn.uit.jobhunter.domain.response.ResultPaginationDTO.Meta;
import vn.uit.jobhunter.repository.CompanyRepository;
import vn.uit.jobhunter.repository.RoleRepository;
import vn.uit.jobhunter.repository.UserRepository;
import vn.uit.jobhunter.util.constant.AccountStatus;
import vn.uit.jobhunter.util.error.IdInvalidException;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CompanyRepository companyRepository;

    public UserService(UserRepository userRepository, RoleRepository roleRepository,CompanyRepository companyRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.companyRepository=companyRepository;
    }

    public User handleCreateUser(User user) {
        return this.userRepository.save(user);
    }

    public ResponseEntity<RestResponse<Void>> handleDeleteUserById(UUID id) {
        this.userRepository.deleteById(id);
        RestResponse<Void> resp=new RestResponse<>();
        resp.setStatusCode(HttpStatus.OK.value());
        resp.setMessage("Xoa Thành Công");
        resp.setData(null);
        return ResponseEntity.ok().body(resp);
    }

    public User fetchUserById(UUID id) {
        Optional<User> userOptional = this.userRepository.findById(id);
        if (userOptional.isPresent()) {
            return userOptional.get();
        }
        return null;
    }

    public ResultPaginationDTO fetchAllUser(Specification<User> spec, Pageable pageable) {
        Page<User> pageUser = this.userRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pageUser.getTotalPages());
        mt.setTotal(pageUser.getTotalElements());

        rs.setMeta(mt);

        // remove sensitive data
        List<ResUserDTO> listUser = pageUser.getContent()
                .stream().map(item -> new ResUserDTO(
                item.getId(),
                item.getEmail(),
                item.getName(),
                item.getGender(),
                item.getAddress(),
                item.getAge(),
                item.getCompany(),
                item.getUpdatedAt(),
                item.getCreatedAt()))
                .collect(Collectors.toList());

        rs.setResult(listUser);

        return rs;
    }

    public ResponseEntity<ResUserDTO> handleUpdateUser(User reqUser) {
        User currentUser = this.fetchUserById(reqUser.getId());
        if (currentUser != null) {
            currentUser.setAddress(reqUser.getAddress());
            currentUser.setGender(reqUser.getGender());
            currentUser.setAge(reqUser.getAge());
            currentUser.setName(reqUser.getName());
            currentUser.setCompany(reqUser.getCompany()==null?null:reqUser.getCompany());

            // update
            currentUser = this.userRepository.save(currentUser);
        }
        ResUserDTO updateUser=new ResUserDTO();
        updateUser.setAddress(currentUser.getAddress());
        updateUser.setGender(currentUser.getGender());
        updateUser.setAge(currentUser.getAge());
        updateUser.setName(currentUser.getName());
        updateUser.setCompany(currentUser.getCompany());
        return ResponseEntity.ok(updateUser);
    }

    public User handleGetUserByUsername(String username) {
        return this.userRepository.findByEmail(username);
    }

    public boolean isEmailExist(String email) {
        return this.userRepository.existsByEmail(email);
    }

    public ResCreateUserDTO convertToResCreateUserDTO(User user) {
        ResCreateUserDTO res = new ResCreateUserDTO();
        res.setId(user.getId());
        res.setEmail(user.getEmail());
        res.setName(user.getName());
        res.setAge(user.getAge());
        res.setCreatedAt(user.getCreatedAt());
        res.setGender(user.getGender());
        res.setAddress(user.getAddress());
        return res;
    }

    public ResUpdateUserDTO convertToResUpdateUserDTO(User user) {
        ResUpdateUserDTO res = new ResUpdateUserDTO();
        res.setId(user.getId());
        res.setName(user.getName());
        res.setAge(user.getAge());
        res.setUpdatedAt(user.getUpdatedAt());
        res.setGender(user.getGender());
        res.setAddress(user.getAddress());
        return res;
    }

    public ResUserDTO convertToResUserDTO(User user) {
        ResUserDTO res = new ResUserDTO();
        res.setId(user.getId());
        res.setEmail(user.getEmail());
        res.setName(user.getName());
        res.setAge(user.getAge());
        res.setUpdatedAt(user.getUpdatedAt());
        res.setCreatedAt(user.getCreatedAt());
        res.setGender(user.getGender());
        res.setAddress(user.getAddress());
        return res;
    }

    public void updateUserToken(String token, String email) {
        User currentUser = this.handleGetUserByUsername(email);
        if (currentUser != null) {

            currentUser.setRefreshToken(token);
            this.userRepository.save(currentUser);

        }
    }

    public ResRegisterUserDTO convertToResRegisterUserDTO(User user) {
        ResRegisterUserDTO res = new ResRegisterUserDTO();
        res.setId(user.getId());
        res.setEmail(user.getEmail());
        res.setName(user.getName());

        res.setCreatedAt(user.getCreatedAt());

        return res;
    }

    public User getUserByRefreshTokenAndEmail(String token, String email) {
        return this.userRepository.findByRefreshTokenAndEmail(token, email);
    }

    public Role getRoleByName(String string) {
        return this.roleRepository.findByName(string);
    }

    public User getUserByEmail(String email) {
        return this.userRepository.findByEmail(email);
    }

    public Long countExceptRole(Role roleName) {
        return this.userRepository.countByRoleNot(roleName);
    }

    public List<User> getAllUser(Pageable pageable) {
        return this.userRepository.findAll(pageable).getContent();
    }

    public User getUserById(String id) {
        return this.userRepository.findById(UUID.fromString(id)).get();
    }

    public void banUser(String id) {
        User user = this.getUserById(id);
        user.setStatus(AccountStatus.BANNED);
        this.userRepository.save(user);
    }
    public ResponseEntity<ResCreateUserDTO> handleCreateUserAdmin(User postUser) throws IdInvalidException{
        if(!userRepository.existsByEmail(postUser.getEmail())){
            postUser.setEmailVerified(true);
            User createUser=userRepository.save(postUser);

            ResCreateUserDTO createUserDTO=new ResCreateUserDTO();
            createUserDTO.setId(createUser.getId());
            createUserDTO.setEmail(createUser.getEmail());
            createUserDTO.setName(createUser.getName());
            createUserDTO.setAge(createUser.getAge());
            createUserDTO.setCreatedAt(createUser.getCreatedAt());
            createUserDTO.setGender(createUser.getGender());
            createUserDTO.setAddress(createUser.getAddress());
            if(postUser.getCompany()!=null){
                createUserDTO.setCompany(companyRepository.findById(postUser.getCompany().getId()).get());
            }
            else{
                createUserDTO.setCompany(null);
            }
            return ResponseEntity.ok(createUserDTO);
        }
        else{
            throw new IdInvalidException("Email đã tồn tại");
        }

    }
}
