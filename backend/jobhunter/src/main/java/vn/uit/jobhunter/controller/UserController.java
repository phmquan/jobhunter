package vn.uit.jobhunter.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.uit.jobhunter.domain.User;
import vn.uit.jobhunter.domain.response.ResCreateUserDTO;
import vn.uit.jobhunter.domain.response.ResUserDTO;
import vn.uit.jobhunter.domain.response.ResultPaginationDTO;
import vn.uit.jobhunter.service.UserService;
import vn.uit.jobhunter.util.annotation.ApiMessage;
import vn.uit.jobhunter.util.error.IdInvalidException;

import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;



@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    UserController(UserService userService,PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder=passwordEncoder;
    }
    
    @GetMapping
    @ApiMessage("Get all user")
    public ResponseEntity<ResultPaginationDTO> getAllUser(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue ="10") int size,
        @RequestParam(defaultValue = "updatedAt,desc") String sort,
        @Filter Specification<User> spec
    ) {
        String[] sortParams = sort.split(",");
        String sortBy = sortParams[0];
        Sort.Direction direction = sortParams.length > 1 && sortParams[1].equalsIgnoreCase("desc")
        ? Sort.Direction.DESC
        : Sort.Direction.ASC;
        Pageable pageable=PageRequest.of(page-1, size,Sort.by(direction,sortBy));
        return ResponseEntity.ok(userService.fetchAllUser(spec,pageable));
    }

    @PutMapping
    @ApiMessage("Update User")
    public ResponseEntity<ResUserDTO> updateUser( @RequestBody User postUser ){
        return userService.handleUpdateUser(postUser);
    }

    @PostMapping
    @ApiMessage("Create User")
    public ResponseEntity<ResCreateUserDTO> createUser(@Valid @RequestBody User postUser) throws IdInvalidException {
        return userService.handleCreateUserAdmin(postUser);
    }

    @DeleteMapping("{id}")
    @ApiMessage("Delete User")
    public ResponseEntity<?> deleteUser(@PathVariable Long id){
        return userService.handleDeleteUserById(id);
    }
    
}
