package vn.uit.jobhunter.service;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;


import vn.uit.jobhunter.domain.Company;
import vn.uit.jobhunter.domain.response.RestResponse;
import vn.uit.jobhunter.domain.response.ResultPaginationDTO;
import vn.uit.jobhunter.repository.CompanyRepository;
import vn.uit.jobhunter.util.error.IdInvalidException;

@Service
public class CompanyService {
    @Autowired
    private CompanyRepository companyRepository;
    public ResultPaginationDTO getAllCompany(int page, int size,String sort) {
        String[] sortParams = sort.split(",");
    String sortBy = sortParams[0];
    Sort.Direction direction = sortParams.length > 1 && sortParams[1].equalsIgnoreCase("desc")
        ? Sort.Direction.DESC
        : Sort.Direction.ASC;
       Pageable pageable = PageRequest.of(page-1,size,Sort.by(direction, sortBy));
       Page<Company> companyPage=companyRepository.findAll(pageable);

       ResultPaginationDTO.Meta meta=new ResultPaginationDTO.Meta();
       meta.setPage(page);
       meta.setPageSize(size);
       meta.setPages(companyPage.getTotalPages());
       meta.setTotal(companyPage.getTotalElements());

       return new ResultPaginationDTO(meta,companyPage.getContent());
    }
    public Company handleCreateCompany(Company postCompany) {
        return companyRepository.save(postCompany);
    }
    public boolean findCompanyByName(Company postCompany) {
        return companyRepository.existsByName(postCompany.getName());
    }
    public ResponseEntity<?> handleUpdateCompany(Company postCompany) throws IdInvalidException{
        
        if(companyRepository.findById(postCompany.getId()).isPresent()){
            Company updateCompany=companyRepository.findById(postCompany.getId()).isPresent()?companyRepository.findById(postCompany.getId()).get():null;
            updateCompany.setName(postCompany.getName());
            updateCompany.setAddress(postCompany.getAddress());
            updateCompany.setDescription(postCompany.getDescription());
            return ResponseEntity.ok(companyRepository.save(updateCompany));
        }
        else{
            throw new IdInvalidException("Id không tồn tại, không thể cập nhật");
        }
    }
    public ResponseEntity<?> handleFindCompanyById(Long id) {
        if(companyRepository.findById(id).isPresent()){
            return ResponseEntity.ok(companyRepository.findById(id));
        }
        else{
            return ResponseEntity.badRequest().body("Không tìm thấy công ty");
        }
    }
	public ResponseEntity<?> handleDeleteById(long id) {
        companyRepository.deleteById(id);
        RestResponse<Void> resp = new RestResponse<>();
        resp.setStatusCode(HttpStatus.OK.value());
        resp.setMessage("Xóa Company thành công");
        resp.setData(null);
		return ResponseEntity.ok().body(resp);
	}
    
}
