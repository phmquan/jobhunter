package vn.uit.jobhunter.controller;



import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import vn.uit.jobhunter.domain.Company;
import vn.uit.jobhunter.domain.response.ResultPaginationDTO;
import vn.uit.jobhunter.service.CompanyService;
import vn.uit.jobhunter.util.anotation.ApiMessage;
import vn.uit.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1/companies")
@RequiredArgsConstructor
public class CompanyController {
    private final CompanyService companyService;

    @GetMapping("")
    @ApiMessage("Get all company")
    public ResponseEntity<ResultPaginationDTO> getAllCompany(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue ="10") int size,
        @RequestParam(defaultValue = "updatedAt,desc") String sort
    ){
        return ResponseEntity.ok(companyService.getAllCompany(page, size,sort));
    }
    @GetMapping("{id}")
    @ApiMessage("Get Company By id")
    public ResponseEntity<?> getCompanyById(@PathVariable Long id){
        return companyService.handleFindCompanyById(id);
    }

    @PostMapping("")
    @ApiMessage("Create Company")
    public ResponseEntity<?> createCompany(@Valid @RequestBody Company postCompany){
        if(companyService.findCompanyByName(postCompany)){
            return ResponseEntity.badRequest().body("Công ty đã tồn tại");
        }
        else{
            return ResponseEntity.ok(companyService.handleCreateCompany(postCompany));
        }
    }
    @PutMapping("")
    @ApiMessage("Update Company")
    public ResponseEntity<?> updateCompany(
        @Valid @RequestBody Company postCompany
    )throws IdInvalidException{
        return companyService.handleUpdateCompany(postCompany);
    }
    @DeleteMapping("{id}")
    @ApiMessage("Delete Company")
    public ResponseEntity<?> deleteCompany(@PathVariable("id") Long id){
        return companyService.handleDeleteById(id);
    }
}
