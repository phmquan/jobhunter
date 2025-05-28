package vn.uit.jobhunter.domain.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResEmbeddingJobDes {
    Long id;
    String name;
    String level;
    String description;

}
