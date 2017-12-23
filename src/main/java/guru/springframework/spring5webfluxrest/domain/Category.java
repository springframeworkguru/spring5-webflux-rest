package guru.springframework.spring5webfluxrest.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by jt on 12/23/17.
 */
@Data
@Document
public class Category {

    @Id
    private String id;

    private String description;
}
