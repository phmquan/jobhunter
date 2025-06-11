package vn.uit.jobhunter.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import vn.uit.jobhunter.domain.response.ResultPaginationDTO;

@Component
public class PaginationHelper {
   public <T,U> ResultPaginationDTO convertResultPagination(Page<T> pageItem,Pageable pageable, List<U> listItem){
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pageItem.getTotalPages());
        mt.setTotal(pageItem.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(listItem);
        return rs;
   }
}
