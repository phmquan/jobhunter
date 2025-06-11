package vn.uit.jobhunter.service.validation;

import org.springframework.stereotype.Component;


import lombok.AllArgsConstructor;



@Component
@AllArgsConstructor
public class ItemValidator {
    public <T> boolean hasItem(T item){
        return item==null?false:true;
    }
    
}
